package hmvv.model;

import java.util.ArrayList;

public class CommandResponse {
	public final ArrayList<String> responseLines;
	public final int exitStatus;
	
	public CommandResponse(ArrayList<String> responseLines, int exitStatus) {
		this.responseLines = responseLines;
		this.exitStatus = exitStatus;
	}	
}
