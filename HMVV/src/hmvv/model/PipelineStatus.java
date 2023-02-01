package hmvv.model;

import java.util.Date;

public class PipelineStatus {
	
	public final Pipeline pipeline;
	public final String instrument;
	public final String pipelineStatus;
	public final Date dateUpdated;
	
	public PipelineStatus(Pipeline pipeline, String instrument, String pipelineStatus, Date dateUpdated) {
		this.pipeline = pipeline;
		this.instrument = instrument;
		this.pipelineStatus = pipelineStatus;
		this.dateUpdated = dateUpdated;
	}
}
