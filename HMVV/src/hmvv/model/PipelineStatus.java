package hmvv.model;

import java.util.Date;

public class PipelineStatus {
	
	public final int pipelineStatusID;
	public final Pipeline pipeline;
	public final String pipelineStatus;
	public final Date dateUpdated;
	
	public PipelineStatus(int pipelineStatusID, Pipeline pipeline, String pipelineStatus, Date dateUpdated) {
		this.pipelineStatusID = pipelineStatusID;
		this.pipeline = pipeline;
		this.pipelineStatus = pipelineStatus;
		this.dateUpdated = dateUpdated;
	}
}
