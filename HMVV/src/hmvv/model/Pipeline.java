package hmvv.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import hmvv.io.DatabaseCommands;

public class Pipeline {
	
	public final int sampleID;
	public final RunFolder runFolderName;
	public final String sampleName;
	public final Assay assay;
	public final Instrument instrument;
	public final String status;
	public final Timestamp timeStatusUpdated;
	
	public final PipelineProgram pipelineProgram;
	private int progress;
	
	public Pipeline (Integer sampleTableID, RunFolder runFolderName, String sampleName, Assay assay, Instrument instrumentName, String status, Timestamp timeStatusUpdated) {
        this.sampleID = sampleTableID;
        this.runFolderName = runFolderName;
        this.sampleName = sampleName;
        this.assay = assay;
        this.instrument = instrumentName;
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

	public RunFolder getRunFolder() {
		return runFolderName;
	}

	public String getsampleName() {
		return sampleName;
	}

	public Assay getAssay() {
		return assay;
	}

	public Instrument getInstrument() {
		return instrument;
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
		PipelineProgram program = PipelineProgram.runningProgram();
		
		if (status.equals("PipelineCompleted")){
			return PipelineProgram.completeProgram();
		}
		if (status.startsWith("ERROR")) {
			return PipelineProgram.errorProgram();
		}
		program.setDisplayString(status);
		
		return program;
	}

	private int computeProgress() throws Exception {

		if (pipelineProgram.isRunningProgram()) {
			float progress = 0.0f;
			float pipelineTotalTime = DatabaseCommands.getPipelineTimeEstimate(this);
			Date currentTime = new Date();
			Date pipelineStartTime = new Date();

			// if pipeline has already started then get the start time
			ArrayList<PipelineStatus> rows = new ArrayList<PipelineStatus>();
			rows = DatabaseCommands.getPipelineDetail(this);
			for (PipelineStatus ps : rows) {
				if (ps.pipelineStatus.equals("queued")){
					pipelineStartTime = ps.dateUpdated;
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
		}else if (pipelineProgram.isCompleteProgram()){
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
