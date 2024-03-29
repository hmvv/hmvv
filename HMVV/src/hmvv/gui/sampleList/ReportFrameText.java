package hmvv.gui.sampleList;
import java.awt.Component;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class ReportFrameText extends ReportFrame{
	private static final long serialVersionUID = 1L;
	
	private JTextArea textArea;
	
	public ReportFrameText(JDialog parent, String title, String report) {
		super(parent, title);
		
		textArea = new JTextArea(report);
		textArea.addMouseListener(new ContextMenuMouseListener());
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		constructFrame(.50);
	}

	@Override
	public Component getReport() {
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(textArea);
		return scrollPane;
	}

	@Override
	public String buildTextExtract() {
		return textArea.getText();
	}

}
