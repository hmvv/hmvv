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
import java.util.Comparator;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import hmvv.gui.GUICommonTools;
import hmvv.gui.sampleList.SampleListFrame;
import hmvv.gui.sampleList.SampleListTableModel;
import hmvv.io.DatabaseCommands;
import hmvv.io.SSHConnection;
import hmvv.io.SampleEnterCommands;
import hmvv.main.Configurations;
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
	
	private SampleListTableModel sampleListTableModel;
	/**
	 * Create the frame.
	 */
	public EnterSample(SampleListFrame parent, SampleListTableModel sampleListTableModel) {
		super("Enter Sample");
		this.parent = parent;
		this.sampleListTableModel = sampleListTableModel;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
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
		
		comboBoxSample.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				sampleIDSelectionChanged();
			}
		});
				
		
		sampleIDSelectionChanged();
			
		
	}
	
	private void sampleIDSelectionChanged(){
		String runId = textRunID.getText();
		String coverageID = (String)comboBoxCoverage.getSelectedItem();
		String variantCallerID = (String)comboBoxCaller.getSelectedItem();
		String sampleID = (String)comboBoxSample.getSelectedItem();
		
		if(runId.equals("") || sampleID == null){
			updateFields("", "", "", "", "", "", "", false);
			return;
		}
		
		if(coverageID == null || coverageID.equals(""))
			coverageID = "out.na";
		if(variantCallerID == null || variantCallerID.equals(""))
			variantCallerID = "out.na";
		
		
		Sample sample = sampleListTableModel.getSample(runId, coverageID, variantCallerID, sampleID);
		if(sample != null){
			updateFields(sample.getLastName(), sample.getFirstName(), sample.getOrderNumber(), sample.getPathNumber(), sample.getTumorSource(), sample.getTumorPercent(), sample.getNote(), false);
		}else{
			updateFields("", "", "", "", "", "", "", true);
		}
		
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
		String assay = comboBoxAssay.getSelectedItem().toString();
		String instrument = comboBoxInstrument.getSelectedItem().toString();
		String runID = textRunID.getText();
		
		try{
			Integer.parseInt(runID);
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, "Run ID must be an integer.");
			return;
		}
		
		//TODO Is the approach for each of these the best? Consider parsing sample list json files where present.
		if(instrument.equals("miseq") || instrument.equals("nextseq")){
			String command = String.format("cat /home/%s/*_%s_*/SampleSheet.csv", instrument, runID);
			CommandResponse result = SSHConnection.executeCommandAndGetOutput(command);
			ArrayList<String> sampleList = sampleListIllumina(result.responseLines);
			fillSampleCommon(result, sampleList);
		}else if(instrument.equals("pgm") || instrument.equals("proton")){
			String coverageCommand = String.format("ls /home/%s/*_%s/plugin_out/ | grep coverageAnalysis", instrument, runID);
			String variantCallerCommand = String.format("ls /home/%s/*_%s/plugin_out/ | grep variantCaller_out", instrument, runID);
			CommandResponse coverageResult = SSHConnection.executeCommandAndGetOutput(coverageCommand);
			CommandResponse variantCallerResult = SSHConnection.executeCommandAndGetOutput(variantCallerCommand);
			ArrayList<String> coverageID = coverageResult.responseLines;
			ArrayList<String> variantCallerID = variantCallerResult.responseLines;
			
			if(coverageResult.exitStatus == 0 && variantCallerResult.exitStatus == 0){
				fillAnalysis(coverageID, variantCallerID);
				//Don't have to fillSample because the comboBox listener will fire, and fillSampleIon will be called
			}else{
				removeAllItems();
				JOptionPane.showMessageDialog(this, "There was a problem locating the run");
				sampleIDSelectionChanged();
			}
		}else {
			throw new Exception(String.format("Unsupported instrument/assay combination (%s,%s)", instrument, assay));
		}
	}
	
	private void fillSampleCommon(CommandResponse result, ArrayList<String> sampleList) {
		if(result.exitStatus == 0){
			fillSample(sampleList);
		}else{
			removeAllItems();
			JOptionPane.showMessageDialog(this, "There was a problem locating the run");
			sampleIDSelectionChanged();
		}
	}
	
	private void fillSampleIon() throws Exception{
		String instrument = comboBoxInstrument.getSelectedItem().toString();
		String runID = textRunID.getText();
		String variantCallerID = comboBoxCaller.getSelectedItem().toString();
		String sampleIDCommand = String.format("ls /home/%s/*_%s/plugin_out/%s/ -F | grep / | grep Ion", instrument, runID, variantCallerID);
		CommandResponse sampleIDResult = SSHConnection.executeCommandAndGetOutput(sampleIDCommand);
		if(sampleIDResult.exitStatus == 0){
			ArrayList<String> sampleID = sampleIDResult.responseLines;
			comboBoxSample.removeAllItems();
			sampleID.sort(new Comparator<String>() {
				@Override
				public int compare(String arg0, String arg1) {
					return arg0.compareTo(arg1);
				}
			});
			for(int i =0; i < sampleID.size(); i++){
				comboBoxSample.addItem(sampleID.get(i).replaceAll("/", ""));
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
	
	private ArrayList<String> sampleListIllumina(ArrayList<String> responseLines){
		ArrayList<String> sampleList = new ArrayList<String>();		
		boolean samplesFound = false;
		for(int i = 0; i < responseLines.size(); i++){
			String fileName = responseLines.get(i).split(",")[0];
			if(fileName.equals("Sample_ID")) {
				samplesFound = true;
				continue;
			}
			if(!samplesFound) {
				continue;
			}
			sampleList.add(fileName);
		}
		sampleList.sort(new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
				return arg0.compareTo(arg1);
			}
		});
		return sampleList;
	}
	
	private void updateFields(String lastName, String firstName, String orderNumber, String pathologyNumber, String tumorSource, String tumorPercent, String note, boolean editable){
		textlastName.setText(lastName);
		textFirstName.setText(firstName);
		textOrderNumber.setText(orderNumber);
		textPathologyNumber.setText(pathologyNumber);
		textTumorSource.setText(tumorSource);
		textPercent.setText(tumorPercent);
		textNote.setText(note);
		
		textlastName.setEditable(editable);
		textFirstName.setEditable(editable);
		textOrderNumber.setEditable(editable);
		textPathologyNumber.setEditable(editable);
		textTumorSource.setEditable(editable);
		textPercent.setEditable(editable);
		textNote.setEditable(editable);
		
		
		enterSampleButton.setEnabled(editable);
        
		
	}
	
	private void enterData() throws Exception{
		
		Sample sample = constructSampleFromTextFields();
		
		SampleEnterCommands.enterData(sample);
		parent.addSample(sample);
		
		JOptionPane.showMessageDialog(this, "Success: Sample entered");
		
		//call update fields in order to run the code that updates the editable status of the fields, and also the enterSampleButton
		updateFields(sample.getLastName(), sample.getFirstName(), sample.getOrderNumber(), sample.getPathNumber(), sample.getTumorSource(), sample.getTumorPercent(), sample.getNote(), false);
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
		
		String coverageID = ".na";
		if(comboBoxCoverage.getSelectedItem() != null){
			coverageID = comboBoxCoverage.getSelectedItem().toString();
		}
		String variantCallerID = ".na";
		if(comboBoxCaller.getSelectedItem() != null){
			variantCallerID = comboBoxCaller.getSelectedItem().toString();
		}
		
		
		String runDate = SampleEnterCommands.getDateString(instrument, runID);
		String note = textNote.getText();
		String enteredBy = SSHConnection.getUserName();
		
		if(lastName.equals("") || firstName.equals("") || orderNumber.equals("") ){
			System.out.println("error");
			throw new Exception("firstName, lastName, orderNumber are required");
		}
		else {
		
		return new Sample(ID, assay, instrument, lastName, firstName, orderNumber,
				pathologyNumber, tumorSource, tumorPercent, runID, sampleID, coverageID, variantCallerID, runDate, note, enteredBy);
		}
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
