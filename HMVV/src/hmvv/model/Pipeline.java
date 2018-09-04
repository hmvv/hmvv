package hmvv.model;

import java.util.ArrayList;
import java.util.Date;

import hmvv.io.DatabaseCommands;

public class Pipeline {
	
	public final Integer queueID;
	public final Integer sampleID;
	public final String runID;
	public final String sampleName;
	public final String assayName;
	public final String instrumentName;
	public final String status;
	public final String runTime;
	
	public final PipelineProgram pipelineProgram;
	private int progress;
	
	public Pipeline (Integer queueID, Integer sampleTableID, String runID , String sampleName , String assayName, String instrumentName, String status, String runTime) {
        this.queueID = queueID;
        this.sampleID = sampleTableID;
        this.runID = runID;
        this.sampleName = sampleName;
        this.assayName = assayName;
        this.instrumentName = instrumentName;
        this.status = status;
        this.runTime = runTime;
        pipelineProgram = computeProgram();
        try {
			progress = computeProgress();
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
	
	public PipelineProgram getPipelineProgress() {
		return pipelineProgram;
	}
	
	public int getProgress() {
		return progress;
	}
	
	private PipelineProgram computeProgram(){
		//default to RUNNING
		PipelineProgram progress = PipelineProgram.RUNNING;

		if (status.equals("pipelineCompleted")){
			return PipelineProgram.COMPLETE;
		}else if ( instrumentName.equals("proton") || instrumentName.equals("pgm") || instrumentName.equals("miseq")) {
			if (status.equals("started") || status.equals("queued") ) {
				progress.setDisplayString("0/4");
			}else if (status.equals("bedtools")) {
				progress.setDisplayString("1/4");
			}else if (status.equals("VEP") || status.equals("parseVEP")) {
				progress.setDisplayString("2/4");
			}else if (status.equals("UpdateDatabase")) {
				progress.setDisplayString("3/4");
			}else if (status.equals("UpdatingDatabase")) {
				progress.setDisplayString("4/4");
			}else if (status.startsWith("ERROR")) {
				progress = PipelineProgram.ERROR;
			}
		}else if  ( assayName.equals("heme") || instrumentName.equals("nextseq")) {
			if (status.equals("started") || status.equals("queued")) {
				progress.setDisplayString("0/6");
			}else if (status.equals("bcl2fastq_running_now")) {
				progress.setDisplayString("1/6");
			}else if ( status.equals("bcl2fastq_completed_now")) {
				progress.setDisplayString("1/6");
			}else if ( status.equals("bcl2fastq_completed_past")) {
				progress.setDisplayString("1/6");
			}else if (status.equals("bcl2fastq_wait") ) {
				progress.setDisplayString("1/6");
			}else if (status.equals("varscanPE")) {
				progress.setDisplayString("2/6");
			}else if (status.equals("bedtools")) {
				progress.setDisplayString("3/6");
			}else if (status.equals("VEP")) {
				progress.setDisplayString("4/6");
			}else if (status.equals("samtools")) {
				progress.setDisplayString("5/6");
			}else if (status.equals("UpdateDatabase")) {
				progress.setDisplayString("6/6");
			}else if (status.equals("UpdatingDatabase")) {
				progress.setDisplayString("6/6");
			}else if (status.startsWith("ERROR")) {
				progress = PipelineProgram.ERROR;
			}
		}

		return progress;
	}

	private int computeProgress() throws Exception {
		if (pipelineProgram == PipelineProgram.RUNNING) {
			float progress = 0.0f;
			float pipelineTotalTime = DatabaseCommands.getPipelineTimeEstimate(this);
			Date currentTime = new Date();
			Date pipelineStartTime = new Date();

			// if pipeline has already started then get the start time
			ArrayList<PipelineStatus> rows = new ArrayList<PipelineStatus>();
			rows = DatabaseCommands.getPipelineDetail(queueID);
			for (PipelineStatus ps : rows) {
				if (ps.pipelineStatus.equals("started")) {
					pipelineStartTime = ps.getDateUpdated();
				}
			}

			float timeElapsed = (float) ((Math.abs(currentTime.getTime() - pipelineStartTime.getTime())) / (1000.0f));
			progress = ((timeElapsed * 100.0f) / pipelineTotalTime);

			if (syncWithProgram() == 0) {
				progress = 0.0f;
			} else if (progress >= 95.0f) {
				if (syncWithProgram() == 2) {
					progress = 100.0f;
				} else {
					progress = 95.0f;
				}
			}
			return (int)(progress);
		}else if (pipelineProgram == PipelineProgram.COMPLETE){
			return 100;
		}else {
			return -1;
		}
	}

	private int syncWithProgram() {
		int isSync = 1;
		
		if (status.startsWith("ERROR")) {
			isSync=0;// for error 
		}else if (status.equals("pipelineCompleted")) {
			isSync=2;// for checking completion 
		}
		return isSync;
	}
}
