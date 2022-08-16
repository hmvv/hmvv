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

public class DatabaseCommands_Pipelines {
	
	private static Connection databaseConnection = null;
	static void setConnection(Connection databaseConnection) {
		DatabaseCommands_Pipelines.databaseConnection = databaseConnection;
	}
	
	private static Pipeline getPipeline(ResultSet row) throws Exception{
		Pipeline pipeline = new Pipeline(
				row.getInt("sampleID"),
				row.getString("runFolderName"),
				row.getString("sampleName"),
				Assay.getAssay(row.getString("assayName")),
				Instrument.getInstrument(row.getString("instrumentName")),
				row.getString("plstatus"),
				row.getTimestamp("timeUpdated")
				);
		return pipeline;
	}

	static ArrayList<Pipeline> getAllPipelines() throws Exception{
		String query = "select" + 
				" queueTable.instrument, queueTable.runFolderName, queueTable.sampleName, queueTable.assay, " + 
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
				"   " + 
				" ) as statusTable" + 
				 
				" on queueTable.instrument = statusTable.instrument and queueTable.runFolderName = statusTable.runFolderName and queueTable.sampleName = statusTable.sampleName" + 
				" order by queueTable.assay, queueTable.instrument, queueTable.runFolderName, queueTable.sampleName";
		
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
		PreparedStatement preparedStatement = databaseConnection.prepareStatement("select pipelineStatusID, plStatus, timeUpdated from pipelineStatus " + 
		" where instrument = ? and runFolderName = ? and sampleName = ? order by timeupdated asc");
		preparedStatement.setString(1, pipeline.instrument.instrumentName);
		preparedStatement.setString(2, pipeline.runFolderName);
		preparedStatement.setString(3, pipeline.sampleName);
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<PipelineStatus> rows = new ArrayList<PipelineStatus>();

		while(rs.next()){
			int pipelineStatusID = rs.getInt("pipelineStatusID");
			String plStatusString = rs.getString("plStatus");
			Timestamp timeUpdated = rs.getTimestamp("timeUpdated");

			PipelineStatus pipelineStatus = new PipelineStatus(pipelineStatusID, pipeline, plStatusString, timeUpdated);
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
				"  select samples.runID, instruments.instrumentName, samples.assay, ps1.queueID, pipelineStatus.timeUpdated as startTime, ps1.timeUpdated as completedTime, TIMESTAMPDIFF(MINUTE, pipelineStatus.timeUpdated, ps1.timeUpdated) as runtime " +
				 
				"  from pipelineStatus " +
				"  join pipelineStatus ps1 on pipelineStatus.queueID = ps1.queueID " +
				"  join pipelineQueue on ps1.queueID = pipelineQueue.queueID " +
				"  join samples on samples.sampleID = pipelineQueue.sampleID " +

				"  where pipelineStatus.plStatus = \"started\" " +
				"  and ps1.plStatus = \"pipelineCompleted\" " +
				"  and samples.instrument = ? and samples.assay = ?) temp "
		);
		
		preparedStatement.setString(1, pipeline.getInstrument().instrumentName);
		preparedStatement.setString(2, pipeline.getAssay().assayName);

		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){
			averageRunTime = rs.getInt("averageMinutes");
		}
		preparedStatement.close();
		return (float)averageRunTime;
	}
}
