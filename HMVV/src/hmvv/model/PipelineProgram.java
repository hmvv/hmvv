package hmvv.model;

import java.awt.Color;

import hmvv.gui.GUICommonTools;

public enum PipelineProgram {
	COMPLETE("Complete", GUICommonTools.WHITE_COLOR),
	RUNNING("Running", GUICommonTools.RUNNING_COLOR),
	ERROR("ERROR", GUICommonTools.ERROR_COLOR);

	private String displayString;
	public final Color displayColor;
	
	PipelineProgram(String displayString, Color displayColor){
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
}
