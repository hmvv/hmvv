package hmvv.model;

public class Pipeline {

	public final Integer queueID;
	public final String runID;
	public final String sampleID;
	public final String assayID;
	public final String instrumentID;
	public final String environmentID;
	public final String status;
	public final String runTime;
	
	
	public Pipeline (Integer queueID, String runID , String sampleID , String assayID, String instrumentID, String environmentID, String status, String runTime) {
        this.queueID = queueID;
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
	
	public String getProgress(){
		// TODO - need to think better way of doing this
		String progress = null;

		if (status.equals("pipelineCompleted")){
			return "Complete";
		}else if ( getInstrumentID().equals("proton") || getInstrumentID().equals("pgm") || getInstrumentID().equals("miseq")) {
			if (status.equals("started") || status.equals("queued") ) {
				progress="0/4";
			}else if (status.equals("bedtools")) {
				progress="1/4";
			}else if (status.equals("VEP") || status.equals("parseVEP")) {
				progress="2/4";
			}else if (status.equals("UpdateDatabase")) {
				progress="3/4";
			}else if (status.equals("UpdatingDatabase")) {
				progress="4/4";
			}else if (status.startsWith("ERROR")) {
				progress = "ERROR";
				
			}
		}else if  ( getAssayID().equals("heme") || getInstrumentID().equals("nextseq")) {
			if (status.equals("started") || status.equals("queued")) {
				progress="0/6";
			}else if (status.equals("bcl2fastq")) {
				progress="1/6";
			}else if (status.equals("varscanPE")) {
				progress="2/6";
			}else if (status.equals("bedtools")) {
				progress="3/6";
			}else if (status.equals("VEP")) {
				progress="4/6";
			}else if (status.equals("samtools")) {
				progress="5/6";
			}else if (status.equals("UpdateDatabase")) {
				progress="6/6";
			}else if (status.equals("UpdatingDatabase")) {
				progress="6/6";
			}else if (status.startsWith("ERROR")) {
				progress = "ERROR";
			}
		} 
		
		
		return progress;
	}
}
