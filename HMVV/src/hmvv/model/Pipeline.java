package hmvv.model;

import java.util.ArrayList;

import java.util.Date;
import java.time.Duration;
import hmvv.io.DatabaseCommands;
import hmvv.io.SSHConnection;

public class Pipeline {

	public final Integer queueID;
	public final String runID;
	public final String sampleName;
	public final String assayID;
	public final String instrumentID;
	public final String status;
	public final String runTime;
	
	
	public Pipeline (Integer queueID, String runID , String sampleName , String assayID, String instrumentID, String status, String runTime) {
        this.queueID = queueID;
        this.runID = runID;
        this.sampleName = sampleName;
        this.assayID = assayID;
        this.instrumentID = instrumentID;
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

	public String getAssayID() {
		return assayID;
	}

	public String getInstrumentID() {
		return instrumentID;
	}

	public String getStatus() {
		return status;
	}

	public String getRunTime() {
		return runTime;
	}
	
	public String getProgram(){
		
		String program = null;

		if (status.equals("pipelineCompleted")){
			return "Complete";
		}else if ( getInstrumentID().equals("proton") || getInstrumentID().equals("pgm") || getInstrumentID().equals("miseq")) {
			if (status.equals("started") || status.equals("queued") ) {
				program="0/4";
			}else if (status.equals("bedtools")) {
				program="1/4";
			}else if (status.equals("VEP") || status.equals("parseVEP")) {
				program="2/4";
			}else if (status.equals("UpdateDatabase")) {
				program="3/4";
			}else if (status.equals("UpdatingDatabase")) {
				program="4/4";
			}else if (status.startsWith("ERROR")) {
				program = "ERROR";
				
			}
		}else if  ( getAssayID().equals("heme") || getInstrumentID().equals("nextseq")) {
			if (status.equals("started") || status.equals("queued")) {
				program="0/6";
			}else if (status.equals("bcl2fastq")) {
				program="1/6";
			}else if (status.equals("varscanPE")) {
				program="2/6";
			}else if (status.equals("bedtools")) {
				program="3/6";
			}else if (status.equals("VEP")) {
				program="4/6";
			}else if (status.equals("samtools")) {
				program="5/6";
			}else if (status.equals("UpdateDatabase")) {
				program="6/6";
			}else if (status.equals("UpdatingDatabase")) {
				program="6/6";
			}else if (status.startsWith("ERROR")) {
				program = "ERROR";
			}
		} else if  ( getAssayID().equals("exome") || getInstrumentID().equals("nextseq")) {
			if (status.equals("started") || status.equals("queued")) {
				program="0/6";
			}else if (status.equals("bcl2fastq")) {
				program="1/6";
			}else if (status.equals("varscanPE")) {
				program="2/6";
			}else if (status.equals("bedtools")) {
				program="3/6";
			}else if (status.equals("VEP")) {
				program="4/6";
			}else if (status.equals("samtools")) {
				program="5/6";
			}else if (status.equals("UpdateDatabase")) {
				program="6/6";
			}else if (status.equals("UpdatingDatabase")) {
				program="6/6";
			}else if (status.startsWith("ERROR")) {
				program = "ERROR";
			}
		}
		
		return program;
	  }
	
	public int getProgress() throws Exception {
		
		float progress = 0.0f;
		float  pipelineTotalTime = DatabaseCommands.getPipelineTimeEstimate(this);
		Date currentTime = new Date();
		Date pipelineStartTime = new Date();
		
		
		// if pipeline has already started then get the start time
		ArrayList<PipelineStatus> rows = new ArrayList<PipelineStatus>();
		rows=DatabaseCommands.getPipelineDetail(this.queueID);
		for (PipelineStatus ps:rows) {
			if(ps.pipelineStatus.equals("started")) {
				pipelineStartTime = ps.getDateUpdated();
			}
		}

		float timeElapsed = (float)((Math.abs(currentTime.getTime() - pipelineStartTime.getTime()))/(1000.0f));
		progress = ((timeElapsed* 100.0f)/pipelineTotalTime);
		
		if (syncWithProgram() == 0) {
			progress=0.0f;
		} else if (progress>= 95.0f) {
			if (syncWithProgram() == 2) {
				progress=100.0f;
			} else {
				progress=95.0f;
			}
		} 
		return (int)(progress);
	}
    
	private int syncWithProgram() {
		
		int isSync=1;
		
		if (status.startsWith("ERROR")) {
			isSync=0;// for error 
		}else if (status.equals("pipelineCompleted")) {
			isSync=2;// for checking completion 
		}
		return isSync;
	}
}
