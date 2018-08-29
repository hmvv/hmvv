package hmvv.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;

import hmvv.io.DatabaseCommands;
import hmvv.model.Pipeline;
import hmvv.model.PipelineStatus;

public enum GUIPipelineProgress {
	COMPLETE("Complete", GUICommonTools.WHITE_COLOR),
	RUNNING("Running", GUICommonTools.RUNNING_COLOR),
	ERROR("ERROR", GUICommonTools.ERROR_COLOR);

	private String displayString;
	public final Color displayColor;
	GUIPipelineProgress(String displayString, Color displayColor){
		this.displayString = displayString;
		this.displayColor = displayColor;
	}

	public void setDisplayString(String displayString) {
		this.displayString = displayString;
	}

	public String getDisplayString() {
		return displayString;
	}

	public String toString() {
		return displayString;
	}
	
	public static GUIPipelineProgress getProgram(Pipeline pipeline){

		//default to RUNNING
		GUIPipelineProgress progress = GUIPipelineProgress.RUNNING;
		String status = pipeline.status;

		if (status.equals("pipelineCompleted")){
			return GUIPipelineProgress.COMPLETE;
		}else if ( pipeline.getInstrumentName().equals("proton") || pipeline.getInstrumentName().equals("pgm") || pipeline.getInstrumentName().equals("miseq")) {
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
				progress = GUIPipelineProgress.ERROR;
			}
		}else if  ( pipeline.getAssayName().equals("heme") || pipeline.getInstrumentName().equals("nextseq")) {
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
				progress = GUIPipelineProgress.ERROR;
			}
		}

		return progress;
	}

	public static int getProgress(Pipeline pipeline) throws Exception{
		float progress = 0.0f;
		float  pipelineTotalTime = DatabaseCommands.getPipelineTimeEstimate(pipeline);
		Date currentTime = new Date();
		Date pipelineStartTime = new Date();

		// if pipeline has already started then get the start time
		ArrayList<PipelineStatus> rows = new ArrayList<PipelineStatus>();
		rows = DatabaseCommands.getPipelineDetail(pipeline.queueID);
		for (PipelineStatus ps:rows) {
			if(ps.pipelineStatus.equals("started")) {
				pipelineStartTime = ps.getDateUpdated();
			}
		}

		float timeElapsed = (float)((Math.abs(currentTime.getTime() - pipelineStartTime.getTime()))/(1000.0f));
		progress = ((timeElapsed* 100.0f)/pipelineTotalTime);

		if (syncWithProgram(pipeline) == 0) {
			progress=0.0f;
		} else if (progress>= 95.0f) {
			if (syncWithProgram(pipeline) == 2) {
				progress=100.0f;
			} else {
				progress=95.0f;
			}
		} 

		return (int)(progress);
	}
	
	private static int syncWithProgram(Pipeline pipeline) {
		int isSync = 1;
		
		if (pipeline.status.startsWith("ERROR")) {
			isSync=0;// for error 
		}else if (pipeline.status.equals("pipelineCompleted")) {
			isSync=2;// for checking completion 
		}
		return isSync;
	}
}
