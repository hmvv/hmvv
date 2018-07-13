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
				progress="0/3";
			}else if (status.equals("bedtools")) {
				progress="1/3";
			}else if (status.equals("vep") || status.equals("parseVEP")) {
				progress="2/3";
			}else if (status.equals("addingAnalysis")) {
				progress="3/3";
			}
		}else if  ( getAssayID().equals("heme") || getInstrumentID().equals("nextseq")) {
			if (status.equals("started") || status.equals("queued")) {
				progress="0/5";
			}else if (status.equals("bcl2fastq")) {
				progress="1/5";
			}else if (status.equals("varscanPE")) {
				progress="2/5";
			}else if (status.equals("bedtools")) {
				progress="3/5";
			}else if (status.equals("vep")) {
				progress="4/5";
			}else if (status.equals("samtools")) {
				progress="5/5";
			}
		}
		return progress;
	}
}
