package hmvv.gui.mutationlist;

import javax.swing.JButton;

import com.jcraft.jsch.SftpProgressMonitor;

public class LoadIGVButton extends JButton implements SftpProgressMonitor{
	
	private long fileSize;
	private long bytesRead;
	private final String defaultText = "Load IGV";
	
	private static final long serialVersionUID = 1L;

	LoadIGVButton(){
		resetText();
	}
	
	public void resetText() {
		setText(defaultText);
	}
	
	@Override
    public void init(int op, String src, String dest, long fileSize){
		this.fileSize = fileSize;
    }
    
    @Override
    public void end(){
    	
    }
    
    @Override
    public boolean count(long bytesRead){
    	this.bytesRead += bytesRead;
    	
    	int progress = (int) ((((double) this.bytesRead) * 100) / (fileSize));
    	setText(String.format("Copying BAM File (%d%% of " + toMB(fileSize) + " MB)", progress));
    	
        return true;
    }
	
    private long toMB(long size) {
		return size / (1024 * 1024);
	}
}