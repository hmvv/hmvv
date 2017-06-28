package hmvv.gui.adminFrames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import hmvv.gui.GUICommonTools;
import hmvv.gui.sampleList.SampleListFrame;
import hmvv.io.DatabaseCommands;
import hmvv.io.SSHConnection;
import hmvv.io.SampleEnterCommands;
import hmvv.model.CommandResponse;
import hmvv.model.Sample;

public class EnterSample extends JFrame {
	private static final long serialVersionUID = 1L;

	private SampleListFrame parent;
	private JTextField textRunID;
	private JTextField textlastName;
	private JTextField textFirstName;
	private JTextField textOrderNumber;
	private JTextField textPathologyNumber;
	private JTextField textTumorSource;
	private JTextField textPercent;
	private JTextField textNote;
	
	private JComboBox<String> comboBoxAssay;
	private JComboBox<String> comboBoxInstrument;
	private JComboBox<String> comboBoxCoverage;
	private JComboBox<String> comboBoxCaller;
	private JComboBox<String> comboBoxSample;
	
	private JButton btnFindRun;
	private JButton enterSampleButton;
	private JButton cancelButton;
	
	/**
	 * Create the frame.
	 */
	public EnterSample(SampleListFrame parent) {
		super("Enter Sample");
		this.parent = parent;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//setBounds(100, 100, 527, 571);
		
		createComponents();
		layoutComponents();
		activateComponents();
		
		try{
			for(String assay : DatabaseCommands.getAllAssays()){
				comboBoxAssay.addItem(assay);
			}
		}catch(Exception e){
			JOptionPane.showMessageDialog(parent, "Error getting Assays from database: " + e.getMessage());
			return;
		}
		findInstrument();//initialize
		
		pack();
		setAlwaysOnTop(true);
		setResizable(false);
		setLocationRelativeTo(parent);
	}
	
	private void createComponents(){
		comboBoxAssay = new JComboBox<String>();
		comboBoxInstrument = new JComboBox<String>();
		textRunID = new JTextField();
		btnFindRun = new JButton("Find Run");
		comboBoxCoverage = new JComboBox<String>();
		comboBoxCaller = new JComboBox<String>();
		comboBoxSample = new JComboBox<String>();
		textlastName = new JTextField();
		textFirstName = new JTextField();
		textOrderNumber = new JTextField();
		textPathologyNumber = new JTextField();
		textTumorSource = new JTextField();
		textPercent = new JTextField();
		textNote = new JTextField();
		enterSampleButton = new JButton("Enter Sample");
		enterSampleButton.setFont(GUICommonTools.TAHOMA_BOLD_13);
		cancelButton = new JButton("Cancel");
		cancelButton.setFont(GUICommonTools.TAHOMA_BOLD_13);
	}

	private void layoutComponents(){
		JPanel runIDPanel = new JPanel();
		GridLayout runIDGridLayout = new GridLayout(1,0);
		runIDGridLayout.setHgap(10);
		runIDPanel.setLayout(runIDGridLayout);
		runIDPanel.add(textRunID);
		runIDPanel.add(btnFindRun);
		
		JPanel mainPanel = new JPanel();
		GridLayout gridLayout = new GridLayout(0,1);
		gridLayout.setVgap(10);
		mainPanel.setLayout(gridLayout);
		mainPanel.add(new RowPanel("Assay", comboBoxAssay));
		mainPanel.add(new RowPanel("Instrument", comboBoxInstrument));
		mainPanel.add(new RowPanel("RunID", runIDPanel));
		mainPanel.add(new RowPanel("CoverageID", comboBoxCoverage));
		mainPanel.add(new RowPanel("VariantCallerID", comboBoxCaller));
		mainPanel.add(new RowPanel("SampleID", comboBoxSample));
		mainPanel.add(new RowPanel("Last Name", textlastName));
		mainPanel.add(new RowPanel("First Name", textFirstName));
		mainPanel.add(new RowPanel("Order Number", textOrderNumber));
		mainPanel.add(new RowPanel("Pathology Number", textPathologyNumber));
		mainPanel.add(new RowPanel("Tumor Source", textTumorSource));
		mainPanel.add(new RowPanel("Tumor Percent", textPercent));
		mainPanel.add(new RowPanel("Note", textNote));
		
		JPanel southPanel = new JPanel();
		GridLayout southGridLayout = new GridLayout(1,0);
		southGridLayout.setHgap(30);
		southPanel.setLayout(southGridLayout);
		southPanel.add(enterSampleButton);
		southPanel.add(cancelButton);
		
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(mainPanel, BorderLayout.CENTER);
		contentPane.add(southPanel, BorderLayout.SOUTH);
		
		contentPane.setBorder(new EmptyBorder(20, 35, 15, 35));
		setContentPane(contentPane);
	}
	
	private void activateComponents(){
		comboBoxAssay.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				findInstrument();
			}
		});
		
		btnFindRun.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					findRun();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(EnterSample.this, e1.getMessage());
				}
			}
		});
		
		comboBoxCaller.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(comboBoxCaller.getSelectedItem() != null){
					try{
						fillSampleIon();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(EnterSample.this, e1.getMessage());
					}
				}
			}
		});
		
		enterSampleButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					enterData();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(EnterSample.this, e1.getMessage());
				}
			}
		});
		
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				EnterSample.this.setVisible(false);
			}

		});
	}
	
	private void findInstrument(){
		String assay = comboBoxAssay.getSelectedItem().toString();
		comboBoxInstrument.removeAllItems();
		try {
			for(String instrument : DatabaseCommands.getInstrumentsForAssay(assay)){
				comboBoxInstrument.addItem(instrument);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}
	
	private void removeAllItems(){
		comboBoxCaller.removeAllItems();
		comboBoxCoverage.removeAllItems();
		comboBoxSample.removeAllItems();
	}
	
	private void findRun() throws Exception{
		String instrument = comboBoxInstrument.getSelectedItem().toString();
		String runID = textRunID.getText();
		
		if((instrument.equals("miseq")) || (instrument.equals("nextseq"))){
			//For illumina
			String command = String.format("ls /home/%sAnalysis/*_%s_*/*.amplicon.vep.parse.filter.txt", instrument, runID);
			CommandResponse result = SSHConnection.executeCommandAndGetOutput(command);
			ArrayList<String> sampleList = sampleListIllumina(result.response);
			if(result.exitStatus == 0){
				fillSample(sampleList);
			}else{
				removeAllItems();
				JOptionPane.showMessageDialog(null, "There was a problem locating the run");
			}
		}else{
			//For Ion
			String coverageCommand = String.format("ls -d /home/%sAnalysis/*%s/coverageAnalysis_out*", instrument, runID);
			String variantCallerCommand = String.format("ls -d /home/%sAnalysis/*%s/variantCaller_out*", instrument, runID);
			CommandResponse coverageResult = SSHConnection.executeCommandAndGetOutput(coverageCommand);
			CommandResponse variantCallerResult = SSHConnection.executeCommandAndGetOutput(variantCallerCommand);
			ArrayList<String> coverageID = getAnalysisID(coverageResult.response);
			ArrayList<String> variantCallerID = getAnalysisID(variantCallerResult.response);
			if(coverageResult.exitStatus == 0 && variantCallerResult.exitStatus == 0){
				fillAnalysis(coverageID, variantCallerID);
			}else{
				removeAllItems();
				JOptionPane.showMessageDialog(null, "There was a problem locating the run");
			}
		}
	}

	private void fillSampleIon() throws Exception{
		String instrument = comboBoxInstrument.getSelectedItem().toString();
		String runID = textRunID.getText();
		String variantCallerID = comboBoxCaller.getSelectedItem().toString();
		String sampleIDCommand = String.format("ls -d /home/%sAnalysis/*%s/%s/*", instrument, runID, variantCallerID);
		CommandResponse sampleIDResult = SSHConnection.executeCommandAndGetOutput(sampleIDCommand);
		if(sampleIDResult.exitStatus == 0){
			ArrayList<String> sampleID = getAnalysisID(sampleIDResult.response);
			comboBoxSample.removeAllItems();
			for(int i =0; i < sampleID.size(); i++){
				comboBoxSample.addItem(sampleID.get(i));
			}
		}else{
			removeAllItems();
			JOptionPane.showMessageDialog(null, "There was a problem locating samples for the run");
		}
	}

	private void fillSample(ArrayList<String> samples){
		removeAllItems();
		for(int i =0; i < samples.size(); i++){
			comboBoxSample.addItem(samples.get(i));
		}
	}

	private void fillAnalysis(ArrayList<String> coverageID, ArrayList<String> variantCallerID){
		comboBoxCoverage.removeAllItems();
		comboBoxCaller.removeAllItems();
		for(int i =0; i < coverageID.size(); i++){
			comboBoxCoverage.addItem(coverageID.get(i));
		}
		for(int i =0; i < variantCallerID.size(); i++){
			comboBoxCaller.addItem(variantCallerID.get(i));
		}
	}

	private ArrayList<String> sampleListIllumina(StringBuilder result){
		String resultString = result.toString();
		ArrayList<String> sampleList = new ArrayList<String>();
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(resultString.split("\\r?\\n")));
		for(int i =0; i < list.size(); i++){
			String fileName = list.get(i).replaceAll("^.*/", "");
			String sample = fileName.replaceAll("\\..*", "");
			sampleList.add(sample);
		}
		return sampleList;
	}

	private ArrayList<String> getAnalysisID(StringBuilder result){
		ArrayList<String> IDlist = new ArrayList<String>();
		String resultString = result.toString();
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(resultString.split("\\r?\\n")));
		for(int i =0; i < list.size(); i++){
			String dirName = list.get(i).replaceAll("^.*/", "");
			IDlist.add(dirName);
		}
		return IDlist;
	}
	
	private void enterData() throws Exception{
		Sample sample = constructSampleFromTextFields();
		SampleEnterCommands.enterData(sample);
		parent.addSample(sample);
		JOptionPane.showMessageDialog(this, "Success: Sample entered");
	}
	
	private Sample constructSampleFromTextFields() throws Exception{
		int ID = -1;//This will be computed by the database when the sample is inserted
		String assay = comboBoxAssay.getSelectedItem().toString();
		String instrument = comboBoxInstrument.getSelectedItem().toString();
		String lastName = textlastName.getText();
		String firstName = textFirstName.getText();
		String orderNumber = textOrderNumber.getText();
		String pathologyNumber = textPathologyNumber.getText();
		String tumorSource = textTumorSource.getText();
		String tumorPercent = textPercent.getText();
		String runID = textRunID.getText();
		String sampleID = comboBoxSample.getSelectedItem().toString();
		String coverageID = "";
		if(comboBoxCoverage.getSelectedItem() != null){
			coverageID = comboBoxCoverage.getSelectedItem().toString();
		}
		String variantCallerID = "";
		if(comboBoxCaller.getSelectedItem() != null){
			variantCallerID = comboBoxCaller.getSelectedItem().toString();
		}
		String runDate = SampleEnterCommands.getDateString(instrument, runID);
		String note = textNote.getText();
		String enteredBy = SSHConnection.getUserName();
		
		return new Sample(ID, assay, instrument, lastName, firstName, orderNumber,
				pathologyNumber, tumorSource, tumorPercent, runID, sampleID, coverageID, variantCallerID, runDate, note, enteredBy);
	}
	
	private class RowPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		private JLabel left;
		private Component right;
		
		RowPanel(String label, Component right){
			this.left = new JLabel(label);
			this.right = right;
			layoutComponents();
		}
		
		private void layoutComponents(){
			left.setFont(GUICommonTools.TAHOMA_BOLD_14);
			left.setPreferredSize(new Dimension(150, 25));
			
			right.setPreferredSize(new Dimension(250, 25));
			
			setLayout(new BorderLayout());
			add(left, BorderLayout.WEST);
			add(right, BorderLayout.CENTER);
		}
	}
}
