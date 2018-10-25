package hmvv.gui.mutationlist;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.gui.sampleList.ReportFrame;
import hmvv.io.AsynchronousMutationDataIO;
import hmvv.io.DatabaseCommands;
import hmvv.io.IGVConnection;
import hmvv.io.MutationReportGenerator;
import hmvv.io.SSHConnection;
import hmvv.model.Mutation;
import hmvv.model.Sample;

public class MutationButtonPanel extends JPanel{
	private static final long serialVersionUID = 1L;


	private JButton shortReportButton;
	private JButton longReportButton;
	private JButton resetButton;
	private JButton exportButton;
	private JButton loadFilteredMutationsButton;
	private LoadIGVButton loadIGVButton;

	private MutationListFrame parent;
	private Sample sample;
	private MutationFilterPanel mutationFilterPanel;
	private MutationList mutationList;
	
	MutationButtonPanel(MutationListFrame parent, Sample sample, MutationList mutationList, MutationFilterPanel mutationFilterPanel){
		this.parent = parent;
		this.sample = sample;
		this.mutationList = mutationList;
		this.mutationFilterPanel = mutationFilterPanel;
		
		constructButtons();
		layoutComponents();
	}

	private void constructButtons(){
		shortReportButton = new JButton("Short Report");
		shortReportButton.setToolTipText("Generate a short report for the mutations marked as reported");
		shortReportButton.setFont(GUICommonTools.TAHOMA_BOLD_13);

		longReportButton = new JButton("Long Report");
		longReportButton.setToolTipText("Generate a long report for the mutations marked as reported");
		longReportButton.setFont(GUICommonTools.TAHOMA_BOLD_13);

		resetButton = new JButton("Reset Filters");
		resetButton.setToolTipText("Reset filters to defaults");
		resetButton.setFont(GUICommonTools.TAHOMA_BOLD_13);

		exportButton = new JButton("Export");
		exportButton.setToolTipText("Export the current table to file");
		exportButton.setFont(GUICommonTools.TAHOMA_BOLD_13);

		loadFilteredMutationsButton = new JButton("Load Filtered Mutations");
		loadFilteredMutationsButton.setToolTipText("Load Mutations that did not meet the quality filter metrics");
		loadFilteredMutationsButton.setFont(GUICommonTools.TAHOMA_BOLD_13);
		loadFilteredMutationsButton.setEnabled(false);//this will be enabled after the unfiltered variant data is loaded

		loadIGVButton = new LoadIGVButton();
		loadIGVButton.setToolTipText("Load the sample into IGV. IGV needs to be already opened");
		loadIGVButton.setFont(GUICommonTools.TAHOMA_BOLD_14);

		loadFilteredMutationsButton.setEnabled(false);
		loadIGVButton.setEnabled(false);

		ActionListener actionListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == shortReportButton){
					showShortReportFrame();
				}else if(e.getSource() == longReportButton){
					showLongReportFrame();
				}else if(e.getSource() == resetButton){
					mutationFilterPanel.resetFilters();
				}else if(e.getSource() == exportButton){
					try{
						exportTable();
					}catch(IOException ex){
						JOptionPane.showMessageDialog(parent, ex.getMessage());
					}
				}else if(e.getSource() == loadFilteredMutationsButton){
					loadFilteredMutationsButton.setEnabled(false);
					loadFilteredMutationsAsynchronous();
				}else if(e.getSource() == loadIGVButton) {
					new Thread(new Runnable() {
						public void run() {
							try{
								loadIGVButton.setEnabled(false);
								loadIGVAsynchronous();
							}catch(Exception ex){
								JOptionPane.showMessageDialog(parent, ex.getMessage());
							}
							loadIGVButton.setEnabled(true);
							loadIGVButton.resetText();
						}
					}).start();
				}
			}
		};

		shortReportButton.addActionListener(actionListener);
		longReportButton.addActionListener(actionListener);
		resetButton.addActionListener(actionListener);
		exportButton.addActionListener(actionListener);
		loadFilteredMutationsButton.addActionListener(actionListener);
		loadIGVButton.addActionListener(actionListener);
	}

	private void layoutComponents() {
		GridLayout buttonPanelGridLayout = new GridLayout(0,1);
		buttonPanelGridLayout.setVgap(5);
		setLayout(buttonPanelGridLayout);
		add(shortReportButton);

		add(longReportButton);
		add(resetButton);
		add(exportButton);
		add(loadFilteredMutationsButton);
		add(loadIGVButton);
	}

	private void exportTable() throws IOException{
		JFileChooser saveAs = new JFileChooser();
		saveAs.setAcceptAllFileFilterUsed(false);
		saveAs.addChoosableFileFilter(new FileFilter(){
			@Override
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().endsWith(".txt");
			}

			@Override
			public String getDescription() {
				return ".txt file";
			}
		});
		
		int returnValue = saveAs.showSaveDialog(this);
		if(returnValue == JFileChooser.APPROVE_OPTION){
			parent.exportReport(saveAs.getSelectedFile());
		}
	}

	private void showShortReportFrame(){
		try{
			String report = MutationReportGenerator.generateShortReport(mutationList);
			showReportFrame(report);
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	private void showLongReportFrame(){
		try{
			String report = MutationReportGenerator.generateLongReport(mutationList);
			showReportFrame(report);
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	private void showReportFrame(String report){
		ReportFrame reportPanel = new ReportFrame(parent, report);
		reportPanel.setVisible(true);
	}
	
	private void loadFilteredMutationsAsynchronous() {
		createLoadFilteredMutationDataThread();
	}

	private void createLoadFilteredMutationDataThread(){
		Thread loadFilteredMutationDataThread = new Thread(new Runnable(){
			@Override
			public void run() {
				disableInputForAsynchronousLoad();
				getFilteredMutationData();
				enableInputAfterAsynchronousLoad();
				loadFilteredMutationsButton.setEnabled(false);
			}
		});
		loadFilteredMutationDataThread.start();
	}
	
	//TODO disable HTTP access to BAM files
	private void loadIGVAsynchronous() throws Exception{
		loadIGVButton.setText("Finding BAM File...");
		File bamFile = SSHConnection.loadBAMForIGV(sample, loadIGVButton);
		
		loadIGVButton.setText("Loading File Into IGV...");
		String response = IGVConnection.loadFileIntoIGV(this, bamFile);

		if(response.equals("OK")) {
			JOptionPane.showMessageDialog(this, "BAM file successfully loaded into IGV");
		}else if(!response.equals("")){
			JOptionPane.showMessageDialog(this, response);
		}
	}
	
	private void getFilteredMutationData() {
		try{
			loadFilteredMutationsButton.setText("Loading...");
			ArrayList<Mutation> mutations = DatabaseCommands.getExtraMutationsBySample(sample);
			for(int i = 0; i < mutations.size(); i++) {
				if(parent.isCallbackClosed()){
					return;
				}
				loadFilteredMutationsButton.setText("Loading " + (i+1) + " of " + mutations.size());
				try{
					Mutation mutation = mutations.get(i);
					AsynchronousMutationDataIO.getMutationData(mutation);
					//no need to call parent.mutationListIndexUpdated() here these mutations are not current displayed
					mutationList.addFilteredMutation(mutation);
					
				}catch(Exception e){
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, e.getMessage() + " : " + e.getClass().getName() + ": Could not load additional mutation data.");
				}
			}
			mutationFilterPanel.applyRowFilters();
		}catch(Exception ex){
			JOptionPane.showMessageDialog(parent, ex.getMessage());
		}
		loadFilteredMutationsButton.setText("Filtered mutations loaded");
	}

	void disableInputForAsynchronousLoad() {
		String tooltip = "Disabled while data is loading";
		resetButton.setEnabled(false);
		resetButton.setToolTipText(tooltip);
	}

	void enableInputAfterAsynchronousLoad() {
		resetButton.setEnabled(true);
		resetButton.setToolTipText("");
		loadFilteredMutationsButton.setEnabled(true);
		loadIGVButton.setEnabled(true);
	}
}
