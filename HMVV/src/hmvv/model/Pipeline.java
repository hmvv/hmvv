package hmvv.model;

public class Pipeline {
	
	public final Integer queueID;
	public final Integer sampleTableID;
	public final String runID;
	public final String sampleID;
	public final String assayID;
	public final String instrumentID;
	public final String environmentID;
	public final String status;
	public final String runTime;
	
	
	public Pipeline (Integer queueID, Integer sampleTableID, String runID , String sampleID , String assayID, String instrumentID, String environmentID, String status, String runTime) {
        this.queueID = queueID;
        this.sampleTableID = sampleTableID;
        this.runID = runID;
        this.sampleID = sampleID;
        this.assayID = assayID;
        this.instrumentID = instrumentID;
        this.environmentID = environmentID;
        this.status = status;
        this.runTime = runTime;
	}

	public Integer getQueueID() {
		return queueID;
	}

	public String getRunID() {
		return runID;
	}

	public String getSampleID() {
		return sampleID;
	}

	public String getAssayID() {
		return assayID;
	}

	public String getInstrumentID() {
		return instrumentID;
	}

	public String getEnvironmentID() {
		return environmentID;
	}

	public String getStatus() {
		return status;
	}

	public String getRunTime() {
		return runTime;
	}
}
