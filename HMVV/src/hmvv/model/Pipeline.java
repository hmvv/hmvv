package hmvv.model;

import hmvv.gui.GUIPipelineProgress;

public class Pipeline {
	
	public final Integer queueID;
	public final Integer sampleTableID;
	public final String runID;
	public final String sampleName;
	public final String assayName;
	public final String instrumentName;
	public final String status;
	public final String runTime;
	
	//TODO not sure the best spot for these yet. Here we are regrettably mixing model code with GUI code.
	public final GUIPipelineProgress pipelineProgress;
	private int progress;
	
	public Pipeline (Integer queueID, Integer sampleTableID, String runID , String sampleName , String assayName, String instrumentName, String status, String runTime) {
        this.queueID = queueID;
        this.sampleTableID = sampleTableID;
        this.runID = runID;
        this.sampleName = sampleName;
        this.assayName = assayName;
        this.instrumentName = instrumentName;
        this.status = status;
        this.runTime = runTime;
        pipelineProgress = GUIPipelineProgress.getProgram(this);
        try {
			progress = GUIPipelineProgress.getProgress(this);
		} catch (Exception e) {
			e.printStackTrace();
			progress = -1;
		}
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
	
	public GUIPipelineProgress getPipelineProgress() {
		return pipelineProgress;
	}
	
	public int getProgress() {
		return progress;
	}
}
