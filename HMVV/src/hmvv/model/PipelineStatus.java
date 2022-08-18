package hmvv.model;

import java.util.Date;

public class PipelineStatus {
	
	public final Pipeline pipeline;
	public final String pipelineStatus;
	public final Date dateUpdated;
	
	public PipelineStatus(Pipeline pipeline, String pipelineStatus, Date dateUpdated) {
		this.pipeline = pipeline;
		this.pipelineStatus = pipelineStatus;
		this.dateUpdated = dateUpdated;
	}
}
