package hmvv.model;

public class Pipeline {
	
	public final Integer queueID;
	public final Integer sampleTableID;
	public final String runID;
	public final String sampleName;
	public final String assayName;
	public final String instrumentName;
	public final String status;
	public final String runTime;
	
	public Pipeline (Integer queueID, Integer sampleTableID, String runID , String sampleName , String assayName, String instrumentName, String status, String runTime) {
        this.queueID = queueID;
        this.sampleTableID = sampleTableID;
        this.runID = runID;
        this.sampleName = sampleName;
        this.assayName = assayName;
        this.instrumentName = instrumentName;
        this.status = status;
        this.runTime = runTime;
	}

	public Integer getQueueID() {
		return queueID;
	}

	public String getRunID() {
		return runID;
	}

	public String getsampleName() {
		return sampleName;
	}

	public String getAssayName() {
		return assayName;
	}

	public String getInstrumentName() {
		return instrumentName;
	}

	public String getStatus() {
		return status;
	}

	public String getRunTime() {
		return runTime;
	}
}
