package hmvv.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import hmvv.model.Assay;
import hmvv.model.Instrument;
import hmvv.model.Pipeline;
import hmvv.model.PipelineStatus;
import hmvv.model.RunFolder;
import hmvv.main.Configurations;

public class DatabaseCommands_Pipelines {
	
	private static Connection databaseConnection = null;
	static void setConnection(Connection databaseConnection) {
		DatabaseCommands_Pipelines.databaseConnection = databaseConnection;
	}
	
	private static Pipeline getPipeline(ResultSet row) throws Exception{
		Pipeline pipeline = new Pipeline(
				row.getInt("sampleID"),
				new RunFolder(row.getString("runFolderName")),
				row.getString("sampleName"),
				Assay.getAssay(row.getString("assay")),
				Instrument.getInstrument(row.getString("instrument")),
				row.getString("plstatus"),
				row.getTimestamp("timeUpdated")
				);
		return pipeline;
	}

	static ArrayList<Pipeline> getAllPipelines() throws Exception{
		String query = "(select" + 
				" queueTable.sampleID, queueTable.instrument, queueTable.runFolderName, queueTable.sampleName, queueTable.assay, " + 
				" statusTable.plStatus, statusTable.timeUpdated" + 
				" from" + 
				" (" + 
					" select samples.sampleID, samples.runFolderName, samples.sampleName, samples.assay, samples.instrument" + 
					" from pipelineQueue" + 
					" join samples on samples.instrument = pipelineQueue.instrument and samples.runFolderName = pipelineQueue.runFolderName and samples.sampleName = pipelineQueue.sampleName" + 
					" where timeSubmitted >= now() - interval 10 day" + 
					" ) as queueTable" + 
				
				" left join " + 
				
				" (" + 
				" select pipelineStatus.instrument, pipelineStatus.runFolderName, pipelineStatus.sampleName, pipelineStatus.plStatus, pipelineStatus.timeUpdated" + 
				"   from" + 
				"    (" + 
				"     select max(timeUpdated) as timeUpdated, runFolderName, sampleName" + 
				"     from pipelineStatus" + 
				"     where timeUpdated >= now() - interval 10 day" + 
				"     group by runFolderName, sampleName" + 
				"    ) as maxPipelineStatusTimeUpdated" + 
				"   join pipelineStatus on pipelineStatus.timeUpdated = maxPipelineStatusTimeUpdated.timeUpdated " + 
				"   AND pipelineStatus.runFolderName = maxPipelineStatusTimeUpdated.runFolderName AND" +
				"	pipelineStatus.sampleName = maxPipelineStatusTimeUpdated.sampleName	" + 
				" ) as statusTable" + 
				 
				" on queueTable.instrument = statusTable.instrument and queueTable.runFolderName = statusTable.runFolderName and queueTable.sampleName = statusTable.sampleName" + 
				" order by queueTable.assay, queueTable.instrument, queueTable.runFolderName, queueTable.sampleName)" +

				"UNION" +

				" (SELECT tbl3.sampleID,tbl2.instrument,tbl2.runFolderName,tbl2.sampleName,tbl3.assay,tbl2.plstatus,tbl2.timeUpdated FROM " +
				" (select max(timeUpdated) as timeUpdated,runFolderName,sampleName from pipelineStatus group by runFolderName,sampleName)" + 
				" AS tbl1 left join pipelineStatus AS tbl2 ON tbl2.timeUpdated = tbl1.timeUpdated AND tbl2.runFolderName = tbl1.runFolderName" + 
				" AND tbl2.sampleName = tbl1.sampleName left JOIN samples AS tbl3 ON tbl2.runFolderName = tbl3.runFolderName" + 
				" AND tbl2.instrument = tbl3.instrument AND tbl2.sampleName = tbl3.sampleName" +
				" WHERE tbl3.sampleID IS NOT NULL AND tbl2.plStatus != 'PipelineCompleted' AND tbl2.timeUpdated < NOW() - INTERVAL 10 DAY AND tbl2.timeUpdated > NOW() - INTERVAL 60 DAY)";
		
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<Pipeline> pipelines = new ArrayList<Pipeline>();

		while(rs.next()){
			Pipeline p = getPipeline(rs);
			pipelines.add(p);
		}
		preparedStatement.close();

		return pipelines;
	}

	static ArrayList<PipelineStatus> getPipelineDetail(Pipeline pipeline) throws Exception{
		PreparedStatement preparedStatement = databaseConnection.prepareStatement("select instrument, plStatus, timeUpdated from pipelineStatus " + 
		" where instrument = ? and runFolderName = ? and sampleName = ? order by timeupdated asc");
		preparedStatement.setString(1, pipeline.instrument.instrumentName);
		preparedStatement.setString(2, pipeline.runFolderName.runFolderName);
		preparedStatement.setString(3, pipeline.sampleName);
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<PipelineStatus> rows = new ArrayList<PipelineStatus>();

		while(rs.next()){
			String plStatusString = rs.getString("plStatus");
			Timestamp timeUpdated = rs.getTimestamp("timeUpdated");

			PipelineStatus pipelineStatus = new PipelineStatus(pipeline, pipeline.instrument.instrumentName, plStatusString, timeUpdated);
			rows.add(pipelineStatus);
		}
		preparedStatement.close();

		return rows;
	}

	static float getPipelineTimeEstimate(Pipeline pipeline) throws Exception {
		int averageRunTime = 0;
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(
				" select AVG(runtime) as averageMinutes from " +
				"  ( " +
				"  select samples.runFolderName, samples.instrument, samples.assay, pipelineStatus.timeUpdated as startTime, ps1.timeUpdated as completedTime, TIMESTAMPDIFF(MINUTE, pipelineStatus.timeUpdated, ps1.timeUpdated) as runtime " +
				 
				"  from pipelineStatus " +
				"  join pipelineStatus ps1 on pipelineStatus.instrument = ps1.instrument and pipelineStatus.runFolderName = ps1.runFolderName and pipelineStatus.sampleName = ps1.sampleName " +
				"  join samples on pipelineStatus.instrument = samples.instrument and pipelineStatus.runFolderName = samples.runFolderName and pipelineStatus.sampleName = samples.sampleName " +

				"  where pipelineStatus.plStatus = ? " +
				"  and ps1.plStatus = \"PipelineCompleted\" " +
				"  and samples.instrument = ? and samples.assay = ?) temp "
		);
		
		String pipeline_instriment = pipeline.getInstrument().instrumentName;
		String estimationStep = Configurations.getPipelineFirstStep(pipeline_instriment);

		preparedStatement.setString(1, estimationStep);
		preparedStatement.setString(2, pipeline_instriment);
		preparedStatement.setString(3, pipeline.getAssay().assayName);

		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){
			averageRunTime = rs.getInt("averageMinutes");
		}
		preparedStatement.close();
		return (float)averageRunTime;
	}
}
