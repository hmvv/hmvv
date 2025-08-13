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
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.main.Configurations.REPORT_TYPE;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.model.MutationCommon;
import hmvv.model.MutationSomatic;
import hmvv.model.Sample;

public class ReportFrame extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextArea fullReportTextArea = new JTextArea("");
	private JPanel buttonPanel;
	protected JDialog parent;
	
	private Sample sample;
	private MutationList mutationList;
	private REPORT_TYPE report_type;

	private ArrayList<ReportVariantPanel> reportVariantPanels = new ArrayList<ReportVariantPanel>();

	/**
	 * Create the frame.
	 */
	public ReportFrame(JDialog parent, REPORT_TYPE report_type, Sample sample, MutationList mutationList)  throws Exception{
		super(parent, report_type.title_text, ModalityType.APPLICATION_MODAL);
		this.parent = parent;
		this.sample = sample;
		this.mutationList = mutationList;
		
		constructFrame(.50);

		fullReportTextArea.setText(compileReport());
	}
	

	protected void constructFrame(double widthPercentOfParent) throws Exception{
		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*widthPercentOfParent), (int)(bounds.height*.90));
		
		setLocationRelativeTo(parent);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		fullReportTextArea.setLineWrap(true);
		fullReportTextArea.setWrapStyleWord(true);
		JScrollPane scrollPane = new JScrollPane(fullReportTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Full Report", null, scrollPane, null);
		
		for(int i = 0; i < mutationList.getMutationCount(); i++) {
			MutationCommon mutation = mutationList.getMutation(i);
			if(!mutation.isReported()){
				continue;
			}
			ReportVariantPanel thisVariantPanel = new ReportVariantPanel(report_type, sample, (MutationSomatic)mutation);
			reportVariantPanels.add(thisVariantPanel);
			tabbedPane.addTab(thisVariantPanel.getPanelTitle(), null, thisVariantPanel, null);
		}

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);
		
		JButton saveAsButton = new JButton("Save As File");
		ActionListener listener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (e.getSource() == saveAsButton){
						String fullReport = compileReport();
						exportReport(fullReport.toString());
					}
		        } catch (Exception ex) {
		        	HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, ex);
		        }
			}
		};

		saveAsButton.addActionListener(listener);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1,0));
		buttonPanel.add(new JPanel());
		buttonPanel.add(new JPanel());
		buttonPanel.add(new JPanel());
		buttonPanel.add(new JPanel());
		buttonPanel.add(new JPanel());
		buttonPanel.add(saveAsButton);
		
		contentPane.add(buttonPanel, BorderLayout.NORTH);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
	}

	private String compileReport(){
		ArrayList<ReportVariantPanel> sortedReportVariantPanels = new ArrayList<ReportVariantPanel>(reportVariantPanels);
		Collections.sort(sortedReportVariantPanels);

		StringBuilder fullReport = new StringBuilder();
		for(int i = 0; i < sortedReportVariantPanels.size(); i++) {
			ReportVariantPanel thisPanel = sortedReportVariantPanels.get(i);
			fullReport.append(thisPanel.getSampleVariantAnnotationTextArea() + "\n\n");
		}
		return fullReport.toString();
	}
	
	protected void addButton(JButton button) {
		buttonPanel.add(button);
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
