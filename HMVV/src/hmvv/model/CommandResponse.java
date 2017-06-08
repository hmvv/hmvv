package hmvv.model;

public class CommandResponse {
	public final StringBuilder response;
	public final int exitStatus;
	
	public CommandResponse(StringBuilder response, int exitStatus) {
		this.response = response;
		this.exitStatus = exitStatus;
	}	
}
