package hmvv.gui.adminFrames;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import hmvv.gui.GUICommonTools;
import hmvv.gui.sampleList.SampleListFrame;
import hmvv.gui.sampleList.SampleListTableModel;
import hmvv.io.DatabaseCommands;
import hmvv.io.SSHConnection;
import hmvv.model.Sample;

public class EnterSample extends JDialog {
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
	private JComboBox<String> comboBoxCoverageIDList;
	private JComboBox<String> comboBoxVariantCallerIDList;
	private JComboBox<String> comboBoxSample;
	
	private JButton btnFindRun;
	private JButton enterSampleButton;
	private JButton cancelButton;
	
	private SampleListTableModel sampleListTableModel;
	
	private static String defaultCoverageAndCallerID = "-";


	private Thread findRunThread;
	private Thread enterSampleThread;
	
	/**
	 * Create the frame.
	 */
	public EnterSample(SampleListFrame parent, SampleListTableModel sampleListTableModel) {
		super(parent, "Enter Sample");
		this.parent = parent;
		this.sampleListTableModel = sampleListTableModel;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		createComponents();
		layoutComponents();
		try {
			activateComponents();
		}catch (Exception e){}


		try{
			for(String assay : DatabaseCommands.getAllAssays()){
				comboBoxAssay.addItem(assay);
			}
		}catch(Exception e){
			JOptionPane.showMessageDialog(parent, "Error getting Assays from database: " + e.getMessage());
			dispose();
			return;
		}
		
		try {
			findInstrument();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(parent, "Error getting Instruments from database: " + e.getMessage());
			dispose();
			return;
		}
		
		pack();
		setModalityType(ModalityType.APPLICATION_MODAL);
		setAlwaysOnTop(true);
		setResizable(false);
		setLocationRelativeTo(parent);
	}
	
	private void createComponents(){
		comboBoxAssay = new JComboBox<String>();
		comboBoxInstrument = new JComboBox<String>();
		textRunID = new JTextField();
		btnFindRun = new JButton("Find Run");
		comboBoxCoverageIDList = new JComboBox<String>();
		comboBoxVariantCallerIDList = new JComboBox<String>();
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
		mainPanel.add(new RowPanel("CoverageID", comboBoxCoverageIDList));
		mainPanel.add(new RowPanel("VariantCallerID", comboBoxVariantCallerIDList));
		mainPanel.add(new RowPanel("SampleName", comboBoxSample));
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
	
	private void activateComponents() throws Exception{
		ActionListener actionListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(e.getSource() == btnFindRun) {

						findRunThread = new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									findRun();
								} catch (Exception e) {
									JOptionPane.showMessageDialog(EnterSample.this, "Error finding run: " + e.getMessage());
								}
							}
						});
						findRunThread.start();

					}else if(e.getSource() == enterSampleButton) {

						enterSampleThread = new Thread(new Runnable() {
							@Override
							public void run() {
								enterSampleButton.setText("Processing...");
								try {
									enterData();
									enterSampleButton.setText("Completed.");

								} catch (Exception e) {
									JOptionPane.showMessageDialog(EnterSample.this, "Error entering sample data: " + e.getMessage());
								}
							}
						});
						enterSampleThread.start();

					}else if(e.getSource() == cancelButton) {
						EnterSample.this.setVisible(false);
					}
				}catch(Exception e1) {
					JOptionPane.showMessageDialog(EnterSample.this, e1.getMessage());
				}
			}
		};
		
		btnFindRun.addActionListener(actionListener);
		enterSampleButton.addActionListener(actionListener);
		cancelButton.addActionListener(actionListener);
		
		
		comboBoxAssay.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				try {
					clearAndDisableAll();
					findInstrument();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(EnterSample.this, e1.getMessage());
				}
			}
		});
		
		comboBoxInstrument.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				clearAndDisableAll();
			}
		});
		
		comboBoxVariantCallerIDList.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(comboBoxVariantCallerIDList.getSelectedItem() != null){
					try{
						fillSampleIon();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(EnterSample.this, e1.getMessage());
					}
				}
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
	
	private void findInstrument() throws Exception{
		String assay = comboBoxAssay.getSelectedItem().toString();
		comboBoxInstrument.removeAllItems();
		for(String instrument : DatabaseCommands.getInstrumentsForAssay(assay)){
			comboBoxInstrument.addItem(instrument);
		}
	}
	
	private void findRun() throws Exception{
		clearComboBoxes();
		clearFields(false);
		enableComboBoxes(false, false, false);
		
		String assay = comboBoxAssay.getSelectedItem().toString();
		String instrument = comboBoxInstrument.getSelectedItem().toString();
		String runID = textRunID.getText();
		
		try{
			Integer.parseInt(runID);
		}catch(Exception e){
			throw new Exception("Run ID must be an integer.");
		}
		
		if(instrument.equals("miseq") || instrument.equals("nextseq")){
			findRunIllumina(instrument, runID);
		}else if(instrument.equals("pgm") || instrument.equals("proton")){
			findRunIon(instrument, runID);
		}else {
			throw new Exception(String.format("Unsupported instrument/assay combination (%s,%s)", instrument, assay));
		}
	}
	
	private void findRunIllumina(String instrument, String runID) throws Exception {
		ArrayList<String> sampeIDList = SSHConnection.getSampleListIllumina(instrument, runID);
		fillSample(sampeIDList);
		enableComboBoxes(false, false, true);
	}
	
	private void findRunIon(String instrument, String runID) throws Exception {
		ArrayList<String> coverageIDList = SSHConnection.getCandidateCoverageIDs(instrument, runID);
		ArrayList<String> variantCallerIDList = SSHConnection.getCandidateVariantCallerIDs(instrument, runID);
		fillComboBoxes(coverageIDList, variantCallerIDList);
		enableComboBoxes(true, true, true);
		
		//Don't have to fillSampleIon because the comboBox listener will fire, and fillSampleIon will be called
	}
	
	private void fillSampleIon() throws Exception{
		String instrument = comboBoxInstrument.getSelectedItem().toString();
		String runID = textRunID.getText();
		String variantCallerID = comboBoxVariantCallerIDList.getSelectedItem().toString();
		
		try{
			Integer.parseInt(runID);
		}catch(Exception e){
			throw new Exception("Run ID must be an integer.");
		}
		
		ArrayList<String> sampleIDList = SSHConnection.getSampleListIon(instrument, runID, variantCallerID);
		fillSample(sampleIDList);
	}

	private void fillSample(ArrayList<String> samples){
		comboBoxSample.removeAllItems();
		for(int i =0; i < samples.size(); i++){
			comboBoxSample.addItem(samples.get(i));
		}
		sampleIDSelectionChanged();
	}

	private void fillComboBoxes(ArrayList<String> coverageID, ArrayList<String> variantCallerID){
		comboBoxCoverageIDList.removeAllItems();
		comboBoxVariantCallerIDList.removeAllItems();
		for(int i =0; i < coverageID.size(); i++){
			comboBoxCoverageIDList.addItem(coverageID.get(i));
		}
		for(int i =0; i < variantCallerID.size(); i++){
			comboBoxVariantCallerIDList.addItem(variantCallerID.get(i));
		}
	}

	private void sampleIDSelectionChanged(){
		enterSampleButton.setText("Enter Sample");
		String runID = textRunID.getText();
		String coverageID = (String)comboBoxCoverageIDList.getSelectedItem();
		String variantCallerID = (String)comboBoxVariantCallerIDList.getSelectedItem();
		String sampleName = (String)comboBoxSample.getSelectedItem();
		
		if(runID.equals("") || sampleName == null){
			updateFields("", "", "", "", "", "", "", false);
			return;
		}
		
		if(coverageID == null)
			coverageID = defaultCoverageAndCallerID;
		if(variantCallerID == null)
			variantCallerID = defaultCoverageAndCallerID;
		
		Sample sample = sampleListTableModel.getSample(runID, coverageID, variantCallerID, sampleName);
		if(sample != null){
			updateFields(sample.getLastName(), sample.getFirstName(), sample.getOrderNumber(), sample.getPathNumber(), sample.getTumorSource(), sample.getTumorPercent(), sample.getNote(), false);
		}else{
			clearFields(true);
		}
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
	
	private void clearComboBoxes(){
		comboBoxVariantCallerIDList.removeAllItems();
		comboBoxCoverageIDList.removeAllItems();
		comboBoxSample.removeAllItems();
	}
	
	private void clearAndDisableAll(){
		clearComboBoxes();
		clearFields(false);
		enableComboBoxes(false, false, false);
	}
	
	private void enableComboBoxes(boolean comboBoxCoverageIDList, boolean comboBoxVariantCallerIDList, boolean comboBoxSample) {
		this.comboBoxCoverageIDList.setEnabled(comboBoxCoverageIDList);
		this.comboBoxVariantCallerIDList.setEnabled(comboBoxVariantCallerIDList);
		this.comboBoxSample.setEnabled(comboBoxSample);
	}
	
	private void clearFields(boolean editable) {
		updateFields("", "", "", "", "", "", "", editable);
	}
	
	private void enterData() throws Exception{

		this.setEnabled(false);

		try {

			Sample sample = constructSampleFromTextFields();
			DatabaseCommands.insertDataIntoDatabase(sample);
			parent.addSample(sample);

			//call update fields in order to run the code that updates the editable status of the fields, and also the enterSampleButton
			updateFields(sample.getLastName(), sample.getFirstName(), sample.getOrderNumber(), sample.getPathNumber(), sample.getTumorSource(), sample.getTumorPercent(), sample.getNote(), false);

		}catch (Exception e){

		}finally {

			this.setEnabled(true);
		}
	}
	
	private Sample constructSampleFromTextFields(){

		if(textlastName.getText().equals("") || textFirstName.getText().equals("") || textOrderNumber.getText().equals("") ){
			JOptionPane.showMessageDialog(this,"First Name, Last Name, Order Number are required");
			return null;
		}else {

			int sampleID = -1;//This will be computed by the database when the sample is inserted
			String assay = comboBoxAssay.getSelectedItem().toString();
			String instrument = comboBoxInstrument.getSelectedItem().toString();
			String lastName = textlastName.getText();
			String firstName = textFirstName.getText();
			String orderNumber = textOrderNumber.getText();
			String pathologyNumber = textPathologyNumber.getText();
			String tumorSource = textTumorSource.getText();
			String tumorPercent = textPercent.getText();
			String runID = textRunID.getText();
			String sampleName = comboBoxSample.getSelectedItem().toString();

			String coverageID = defaultCoverageAndCallerID;
			if(comboBoxCoverageIDList.getSelectedItem() != null){
				coverageID = comboBoxCoverageIDList.getSelectedItem().toString();
			}
			String variantCallerID = defaultCoverageAndCallerID;
			if(comboBoxVariantCallerIDList.getSelectedItem() != null){
				variantCallerID = comboBoxVariantCallerIDList.getSelectedItem().toString();
			}

			String runDate = GUICommonTools.extendedDateFormat1.format(Calendar.getInstance().getTime());
			String note = textNote.getText();
			String enteredBy = SSHConnection.getUserName();

			return new Sample(sampleID, assay, instrument, lastName, firstName, orderNumber,
					pathologyNumber, tumorSource, tumorPercent, runID, sampleName, coverageID, variantCallerID, runDate, note, enteredBy);
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
