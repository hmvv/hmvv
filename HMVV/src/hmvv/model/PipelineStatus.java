package hmvv.model;

import java.util.Date;

public class PipelineStatus {
	
	public final int pipelineStatusID;
	public final int queueID;
	public final String pipelineStatus;
	public final Date dateUpdated;
	
	public PipelineStatus(int pipelineStatusID, int queueID, String pipelineStatus, Date dateUpdated) {
		this.pipelineStatusID = pipelineStatusID;
		this.queueID = queueID;
		this.pipelineStatus = pipelineStatus;
		this.dateUpdated = dateUpdated;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}
	
}
