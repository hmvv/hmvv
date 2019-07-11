package hmvv.main;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import hmvv.gui.GUICommonTools;
import hmvv.io.InternetCommands;

public class HMVVDefectReportFrame extends JDialog{
	private static final long serialVersionUID = 1L;

	private JLabel defectLinkLabel;
	private JLabel messageLabel;
	private Exception exception;
	private String message;
	private JTextArea stackTraceTextArea;
	
	public static void showHMVVDefectReportFrame(Window parent, Exception exception) {
		showHMVVDefectReportFrame(parent, exception, "");
	}
	
	public static void showHMVVDefectReportFrame(Window parent, Exception exception, String message) {
		HMVVDefectReportFrame defectFrame = new HMVVDefectReportFrame(parent, exception, message);
		defectFrame.pack();
		defectFrame.setLocationRelativeTo(parent);
		defectFrame.setVisible(true);
	}
	
	private HMVVDefectReportFrame(Window parent, Exception exception, String message) {
		super(parent, "HMVV Defect Report Frame", Dialog.ModalityType.APPLICATION_MODAL);
		this.exception = exception;
		this.message = message;
		createComponents();
		layoutLoginComponents();
		activateComponents();
	}

	private void createComponents(){
		defectLinkLabel = new JLabel("<html>Exception Error Encountered. Please <a style=\"text-decoration:none\" href=\"\">alert the administrators</a>.</html>", SwingConstants.CENTER);
		defectLinkLabel.setFont(GUICommonTools.TAHOMA_BOLD_17);
		
		messageLabel = new JLabel(message + " " + exception.getMessage(), SwingConstants.CENTER);
		messageLabel.setFont(GUICommonTools.TAHOMA_PLAIN_13);
		
		StringWriter stringWriter = new StringWriter();
		exception.printStackTrace(new PrintWriter(stringWriter));
		stackTraceTextArea = new JTextArea(exception.getClass().getName() + "\n\n" + stringWriter.toString());
		stackTraceTextArea.setFont(GUICommonTools.TAHOMA_PLAIN_13);
		stackTraceTextArea.setEditable(false);
		stackTraceTextArea.setLineWrap(true);
		stackTraceTextArea.setWrapStyleWord(true);
		stackTraceTextArea.setPreferredSize(new Dimension(800,800));
	}
	
	private void layoutLoginComponents(){
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new GridLayout(0,1));
		northPanel.add(defectLinkLabel);
		northPanel.add(messageLabel);

		setLayout(new BorderLayout());
		add(northPanel, BorderLayout.NORTH);
		add(stackTraceTextArea, BorderLayout.CENTER);
		
		Rectangle bounds = GUICommonTools.getScreenBounds();
		setLocation(bounds.width/2-getSize().width/2, bounds.height/2-getSize().height/2);
	}
	
	private void activateComponents(){
		defectLinkLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					InternetCommands.hmvvBugsReport();
				} catch (Exception e1) {
					//Create another DefectReportFrame within this frame?
					JOptionPane.showMessageDialog(HMVVDefectReportFrame.this, "Unable to browse to the bugs report URL. " + e1.getMessage());
				}
			}
		});
		defectLinkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
}
