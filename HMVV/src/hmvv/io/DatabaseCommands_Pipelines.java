package hmvv.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import hmvv.model.Pipeline;
import hmvv.model.PipelineStatus;

public class DatabaseCommands_Pipelines {
	
	private static Connection databaseConnection = null;
	static void setConnection(Connection databaseConnection) {
		DatabaseCommands_Pipelines.databaseConnection = databaseConnection;
	}
	
	private static Pipeline getPipeline(ResultSet row) throws Exception{
		Pipeline pipeline = new Pipeline(
				row.getInt("queueID"),
				row.getInt("sampleID"),
				row.getString("runID"),
				row.getString("sampleName"),
				row.getString("assayName"),
				row.getString("instrumentName"),
				row.getString("plstatus"),
				row.getTimestamp("timeUpdated")
				);
		return pipeline;
	}

	static ArrayList<Pipeline> getAllPipelines() throws Exception{
		String query = "select" + 
				" queueTable.queueID, queueTable.sampleID, queueTable.runID, queueTable.sampleName, queueTable.assayName, queueTable.instrumentName," + 
				" statusTable.plStatus, statusTable.timeUpdated" + 
				" from" + 
				" (" + 
					" select pipelineQueue.queueID, samples.sampleID, samples.runID, samples.sampleName, assays.assayName, instruments.instrumentName" + 
					" from pipelineQueue" + 
					" join samples on samples.sampleID = pipelineQueue.sampleID" + 
					" join assays on assays.assayID = samples.assayID" + 
					" join instruments on instruments.instrumentID = samples.instrumentID" + 
					" where timeSubmitted >= now() - interval 10 day" + 
					" ) as queueTable" + 
				
				" left join " + 
				
				" (" + 
				" select pipelineStatus.pipelineStatusID, pipelineStatus.queueID, pipelineStatus.plStatus, pipelineStatus.timeUpdated" + 
				"   from" + 
				"    (" + 
				"     select max(pipelineStatusID) as pipelineStatusID" + 
				"     from pipelineStatus" + 
				"     where timeUpdated >= now() - interval 10 day" + 
				"     group by queueID" + 
				"    ) as maxPipelineStatusID" + 
				"   join pipelineStatus on pipelineStatus.pipelineStatusID = maxPipelineStatusID.pipelineStatusID " + 
				"   " + 
				" ) as statusTable" + 
				 
				" on queueTable.queueID = statusTable.queueID" + 
				" order by queueTable.queueID desc;";
		
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

	static ArrayList<PipelineStatus> getPipelineDetail(int queueID) throws Exception{
		PreparedStatement preparedStatement = databaseConnection.prepareStatement("select pipelineStatusID, plStatus, timeUpdated from pipelineStatus where queueID = ? order by timeupdated asc");
		preparedStatement.setInt(1, queueID);
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<PipelineStatus> rows = new ArrayList<PipelineStatus>();

		while(rs.next()){
			int pipelineStatusID = rs.getInt("pipelineStatusID");
			String plStatusString = rs.getString("plStatus");
			Timestamp timeUpdated = rs.getTimestamp("timeUpdated");

			PipelineStatus pipelineStatus = new PipelineStatus(pipelineStatusID, queueID, plStatusString, timeUpdated);
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
				"  select samples.runID, instruments.instrumentName, assays.assayName, ps1.queueID, pipelineStatus.timeUpdated as startTime, ps1.timeUpdated as completedTime, TIMESTAMPDIFF(MINUTE, pipelineStatus.timeUpdated, ps1.timeUpdated) as runtime " +
				 
				"  from pipelineStatus " +
				"  join pipelineStatus ps1 on pipelineStatus.queueID = ps1.queueID " +
				"  join pipelineQueue on ps1.queueID = pipelineQueue.queueID " +
				"  join samples on samples.sampleID = pipelineQueue.sampleID " +
				"  join assays on assays.assayID = samples.assayID " +
				"  join instruments on instruments.instrumentID = samples.instrumentID " +

				"  where pipelineStatus.plStatus = \"started\" " +
				"  and ps1.plStatus = \"pipelineCompleted\" " +
				"  and instruments.instrumentName = ? and assays.assayName = ?) temp "
		);
		
		preparedStatement.setString(1, pipeline.getInstrumentName());
		preparedStatement.setString(2, pipeline.getAssayName());

		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){
			averageRunTime = rs.getInt("averageMinutes");
		}
		preparedStatement.close();
		return (float)averageRunTime;
	}
}
