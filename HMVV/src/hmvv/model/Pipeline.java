package hmvv.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import hmvv.io.DatabaseCommands;

public class Pipeline {
	
	public final int queueID;
	public final int sampleID;
	public final String runID;
	public final String sampleName;
	public final String assayName;
	public final String instrumentName;
	public final String status;
	public final Timestamp timeStatusUpdated;
	
	public final PipelineProgram pipelineProgram;
	private int progress;
	
	public Pipeline (Integer queueID, Integer sampleTableID, String runID , String sampleName , String assayName, String instrumentName, String status, Timestamp timeStatusUpdated) {
        this.queueID = queueID;
        this.sampleID = sampleTableID;
        this.runID = runID;
        this.sampleName = sampleName;
        this.assayName = assayName;
        this.instrumentName = instrumentName;
        this.status = status;
        this.timeStatusUpdated = timeStatusUpdated;
        pipelineProgram = computeProgram();
        try {
			progress = computeProgress();
		} catch (Exception e) {
			e.printStackTrace();
			progress = -1;
		}
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

	public Timestamp getTimeStatusUpdated() {
		return timeStatusUpdated;
	}
	
	public PipelineProgram getPipelineProgress() {
		return pipelineProgram;
	}
	
	public int getProgress() {
		return progress;
	}
	
	private PipelineProgram computeProgram(){
		//default to RUNNING
		PipelineProgram program = PipelineProgram.RUNNING;

		if (status.toLowerCase().equals("pipelinecompleted")){
			return PipelineProgram.COMPLETE;
		}else if ( instrumentName.equals("proton")) {
			if (status.equals("started") || status.equals("queued") ) {
				program.setDisplayString("1/4");
			}else if (status.equals("RunningVEP")) {
				program.setDisplayString("2/4");
			}else if (status.equals("CompletedVEP")) {
				program.setDisplayString("3/4");
			}else if (status.equals("UpdatingDatabase")) {
				program.setDisplayString("4/4");
			}else if (status.startsWith("ERROR")) {
				program = PipelineProgram.ERROR;
			}
		}else if  ( assayName.equals("heme") || assayName.equals("tmb")) {
			if (status.equals("started") || status.equals("queued")) {
				program.setDisplayString("0/6");
			}else if (status.equals("bcl2fastq_running_now")) {
				program.setDisplayString("1/6");
			}else if ( status.equals("bcl2fastq_completed_now")) {
				program.setDisplayString("1/6");
			}else if ( status.equals("bcl2fastq_completed_past")) {
				program.setDisplayString("1/6");
			}else if (status.equals("bcl2fastq_wait") ) {
				program.setDisplayString("1/6");
			}else if (status.equals("Alignment")) {
				program.setDisplayString("2/6");
			}else if (status.equals("VariantCaller")) {
				program.setDisplayString("3/6");
			}else if (status.equals("RunningVEP")) {
				program.setDisplayString("4/6");
			}else if (status.equals("CompletedVEP")){
				program.setDisplayString("5/6");
			}else if (status.equals("UpdatingDatabase")) {
				program.setDisplayString("6/6");
			}else if (status.startsWith("ERROR")) {
				program = PipelineProgram.ERROR;
			}
		}
		return program;
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
				if (ps.pipelineStatus.equals("started")){
					pipelineStartTime = ps.getDateUpdated();
				}
			}

			float timeElapsed = (float)((Math.abs(currentTime.getTime() - pipelineStartTime.getTime())) / (1000.0f));

			progress = ((timeElapsed * 100.0f) / (pipelineTotalTime * 60.0f));

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
		}else if (status.equals("PipelineCompleted")) {
			isSync=2;// for checking completion 
		}
		return isSync;
	}
}
