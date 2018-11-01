package hmvv.gui.sampleList;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.MutationListFrame;
import hmvv.main.HMVVDefectReportFrame;

public class ReportFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	
	/**
	 * Create the frame.
	 */
	public ReportFrame(MutationListFrame parent, String report) {
		super("Variant Report");
		
		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*.50), (int)(bounds.height*.70));
		
		setLocationRelativeTo(parent);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);	
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);
		
		JTextArea textArea = new JTextArea(report);
		textArea.addMouseListener(new ContextMenuMouseListener());
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		JButton saveAsButton = new JButton("Save As File");
		saveAsButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
		            exportReport(textArea.getText());
		        } catch (IOException ex) {
		        	HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, ex);
		        }
			}
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1,0));
		buttonPanel.add(new JPanel());
		buttonPanel.add(new JPanel());
		buttonPanel.add(new JPanel());
		buttonPanel.add(new JPanel());
		buttonPanel.add(new JPanel());
		buttonPanel.add(saveAsButton);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(textArea);
		contentPane.add(buttonPanel, BorderLayout.NORTH);
		contentPane.add(scrollPane, BorderLayout.CENTER);
	}
	
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
