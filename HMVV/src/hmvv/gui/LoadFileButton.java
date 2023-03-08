package hmvv.gui;

import javax.swing.JButton;

import com.jcraft.jsch.SftpProgressMonitor;

public class LoadFileButton extends JButton implements SftpProgressMonitor{
	
	private long fileSize;
	private long cumulativeBytesRead;
	private final String defaultText;
	
	private static final long serialVersionUID = 1L;

	public LoadFileButton(String defaultText){
		this.defaultText = defaultText;
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
    	this.cumulativeBytesRead += bytesRead;
    	
    	long percent = this.cumulativeBytesRead * 100 / fileSize;
    	setText(String.format("Copying File (%d%% of " + toMB(fileSize) + " MB)", percent));
    	
        return true;
    }
	
    private long toMB(long size) {
		return size / (1024 * 1024);
	}
}