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
		String query = " select " + 
					" sampleTable.sampleID, statusTable.instrument, statusTable.runFolderName, statusTable.sampleName, sampleTable.assay, " + 
					" statusTable.plStatus, statusTable.timeUpdated " +
					" from " +

					" (SELECT tempStatusTable.instrument, tempStatusTable.runFolderName, tempStatusTable.sampleName, " +
					" IF (errorTable.plStatus LIKE '%WARNING%' AND tempStatusTable.plStatus = 'PipelineCompleted', CONCAT(errorTable.plStatus, ' with ', tempStatusTable.plStatus), COALESCE(errorTable.plStatus,tempStatusTable.plStatus)) plStatus, " +
					" COALESCE(errorTable.timeUpdated,tempStatusTable.timeUpdated) timeUpdated " +

					" FROM " + 

					" (SELECT rowNumTable.instrument,rowNumTable.runFolderName,rowNumTable.sampleName,rowNumTable.timeUpdated,rowNumTable.plStatus,rowNumTable.row_num rowNum " + 

					" FROM " + 

					" (SELECT *, ROW_NUMBER() over (PARTITION BY runFolderName, sampleName ORDER BY timeUpdated desc) AS row_num " + 
					" FROM pipelineStatus) rowNumTable " + 
					" WHERE " + 
					" rowNumTable.row_num = 1 " + 
					" AND (timeUpdated >= now() - INTERVAL 10 DAY " + 
					" OR " + 
					" (timeUpdated < NOW() - INTERVAL 10 DAY " + 
					" AND timeUpdated > NOW() - INTERVAL 60 DAY " + 
					" AND plStatus != 'PipelineCompleted' " + 
					" )) " + 
					" GROUP BY rowNumTable.runFolderName, rowNumTable.sampleName " + 
					" ) AS  tempStatusTable" +  

					" LEFT JOIN " +

					" (SELECT * FROM pipelineStatus " +
					" WHERE plStatus LIKE '%ERROR%' OR plStatus LIKE '%WARNING%' " +
					" GROUP BY runFolderName,sampleName) as errorTable " +
					" ON tempStatusTable.runFolderName = errorTable.runFolderName AND tempStatusTable.sampleName = errorTable.sampleName " + 
					" ) AS statusTable " + 

					" LEFT JOIN " + 

					" samples sampleTable " + 
					" on sampleTable.instrument = statusTable.instrument and sampleTable.runFolderName = statusTable.runFolderName and sampleTable.sampleName = statusTable.sampleName " + 
					" order by sampleTable.assay, sampleTable.instrument, sampleTable.runFolderName, sampleTable.sampleName ";

						
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
