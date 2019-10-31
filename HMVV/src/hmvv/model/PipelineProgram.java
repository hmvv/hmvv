package hmvv.model;

import java.awt.Color;

import hmvv.gui.GUICommonTools;

public class PipelineProgram {
	
	private static final int COMPLETE = 0;
	private static final int RUNNING = 1;
	private static final int ERROR = 2;
	
	private String displayString;
	public final int programType;
	public static final Color DEFAULT_COLOR = GUICommonTools.WHITE_COLOR;
	
	PipelineProgram(String displayString, int programType){
		this.displayString = displayString;
		this.programType = programType;
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
	
	public Color getColor() {
		switch(programType) {
			case COMPLETE: return GUICommonTools.WHITE_COLOR;
			case RUNNING: return GUICommonTools.RUNNING_COLOR;
			case ERROR: return GUICommonTools.ERROR_COLOR;
			default: return DEFAULT_COLOR;
		}
	}
	
	public boolean isCompleteProgram() {
		return programType == COMPLETE;
	}
	
	public boolean isRunningProgram() {
		return programType == RUNNING;
	}
	
	public static PipelineProgram completeProgram() {
		return new PipelineProgram("Complete", COMPLETE);
	}
	
	public static PipelineProgram runningProgram() {
		return new PipelineProgram("Running", RUNNING);
	}
	
	public static PipelineProgram errorProgram() {
		return new PipelineProgram("Error", ERROR);
	}
}
