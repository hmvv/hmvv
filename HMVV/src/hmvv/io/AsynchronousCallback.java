package hmvv.io;

public interface AsynchronousCallback {
	public void disableInputForAsynchronousLoad();	
	public void enableInputAfterAsynchronousLoad();
	public void mutationListIndexUpdated(int index);
	public void showErrorMessage(Exception e, String message);
	public boolean isCallbackClosed();
}
