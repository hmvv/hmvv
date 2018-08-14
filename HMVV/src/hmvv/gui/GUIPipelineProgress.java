package hmvv.gui;

import java.awt.Color;

import hmvv.model.Pipeline;

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
	
	public static GUIPipelineProgress getProgress(Pipeline pipeline){
		// TODO - need to think better way of doing this
		
		//default to RUNNING
		GUIPipelineProgress progress = GUIPipelineProgress.RUNNING;
		String status = pipeline.status;
		
		if (status.equals("pipelineCompleted")){
			return GUIPipelineProgress.COMPLETE;
		}else if ( pipeline.getInstrumentID().equals("proton") || pipeline.getInstrumentID().equals("pgm") || pipeline.getInstrumentID().equals("miseq")) {
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
		}else if  ( pipeline.getAssayID().equals("heme") || pipeline.getInstrumentID().equals("nextseq")) {
			if (status.equals("started") || status.equals("queued")) {
				progress.setDisplayString("0/6");
			}else if (status.equals("bcl2fastq")) {
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
}
