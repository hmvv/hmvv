package hmvv.gui.sampleList;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import hmvv.gui.GUICommonTools;
import hmvv.main.HMVVDefectReportFrame;

public abstract class ReportFrame extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JPanel buttonPanel;
	protected JDialog parent;
	
	/**
	 * Create the frame.
	 */
	public ReportFrame(JDialog parent, String title) {
		super(parent, title, ModalityType.APPLICATION_MODAL);
		this.parent = parent;
	}
	
	protected void constructFrame(double widthPercentOfParent) {
		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*widthPercentOfParent), (int)(bounds.height*.90));
		
		setLocationRelativeTo(parent);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);
		
		JButton saveAsButton = new JButton("Save As File");
		saveAsButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
		            exportReport(buildTextExtract());
		        } catch (IOException ex) {
		        	HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, ex);
		        }
			}
		});
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1,0));
		buttonPanel.add(new JPanel());
		buttonPanel.add(new JPanel());
		buttonPanel.add(new JPanel());
		buttonPanel.add(new JPanel());
		buttonPanel.add(new JPanel());
		buttonPanel.add(saveAsButton);
		
		contentPane.add(buttonPanel, BorderLayout.NORTH);
		contentPane.add(getReport(), BorderLayout.CENTER);
	}
	
	protected void addButton(JButton button) {
		buttonPanel.add(button);
	}
	
	public abstract Component getReport();
	public abstract String buildTextExtract();
	
	private void exportReport(String text) throws IOException{
		JFileChooser saveAsFileChooser = new JFileChooser();
		saveAsFileChooser.setAcceptAllFileFilterUsed(false);
		saveAsFileChooser.addChoosableFileFilter(new FileFilter(){
			@Override
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().endsWith(".txt");
			}

			@Override
			public String getDescription() {
				return ".txt file";
			}			
		});
		int returnValue = saveAsFileChooser.showOpenDialog(this);
		if(returnValue == JFileChooser.APPROVE_OPTION ){
			File fileName = saveAsFileChooser.getSelectedFile();
			if(!fileName.getName().endsWith(".txt")){
				fileName = new File(fileName.toString() + ".txt");
			}
			BufferedWriter outFile = new BufferedWriter(new FileWriter(fileName));
			outFile.write(text);
			outFile.close();
		}
	}
}
