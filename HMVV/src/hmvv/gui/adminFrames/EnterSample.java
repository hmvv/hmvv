package hmvv.gui.adminFrames;

import java.awt.Component;
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
import hmvv.io.DatabaseCommands;
import hmvv.io.SSHConnection;
import hmvv.io.SampleEnterCommands;
import hmvv.model.CommandResponse;
import hmvv.model.Sample;

public class EnterSample extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField textRunID;
	private JTextField textlastName;
	private JTextField textFirstName;
	private JTextField textOrderNumber;
	private JTextField textPathologyNumber;
	private JTextField textTumorSource;
	private JTextField textPercent;
	private JTextField textNote;
	private String assay;
	private JComboBox<String> comboBoxAssay;
	private JComboBox<String> comboBoxInstrument;
	private JComboBox<String> comboBoxCoverage;
	private JComboBox<String> comboBoxCaller;
	private JComboBox<String> comboBoxSample;
	
	/**
	 * Create the frame.
	 */
	public EnterSample(Component parent) {
		super("Enter Sample");
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		
		setContentPane(contentPane);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 527, 571);
		
		createComponents();
		setLocationRelativeTo(parent);
	}
	
	private void createComponents(){
		try{
			comboBoxAssay = new JComboBox<String>();
			for(String assay : DatabaseCommands.getAllAssays()){
				comboBoxAssay.addItem(assay);
			}
			comboBoxAssay.setBounds(196, 34, 213, 20);
			contentPane.add(comboBoxAssay);
			
			comboBoxAssay.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					assay = comboBoxAssay.getSelectedItem().toString();
					findInstrument();
				}
			});
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Error getting Assays from database: " + e.getMessage());
			return;
		}
		
		JLabel lblAssay = new JLabel("Assay");
		lblAssay.setFont(GUICommonTools.TAHOMA_BOLD_14);
		lblAssay.setBounds(41, 34, 58, 17);
		contentPane.add(lblAssay);

		JLabel lblInstrument = new JLabel("Instrument");
		lblInstrument.setFont(GUICommonTools.TAHOMA_BOLD_14);
		lblInstrument.setBounds(41, 69, 94, 17);
		contentPane.add(lblInstrument);

		comboBoxInstrument = new JComboBox<String>();
		comboBoxInstrument.setBounds(196, 69, 213, 20);
		contentPane.add(comboBoxInstrument);
		findInstrument();
		
		JLabel lblRunid = new JLabel("RunID");
		lblRunid.setFont(GUICommonTools.TAHOMA_BOLD_14);
		lblRunid.setBounds(41, 104, 94, 17);
		contentPane.add(lblRunid);

		textRunID = new JTextField();
		textRunID.setBounds(196, 104, 100, 20);
		contentPane.add(textRunID);
		textRunID.setColumns(10);
		
		JButton btnFindRun = new JButton("Find Run");
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
		btnFindRun.setFont(GUICommonTools.TAHOMA_BOLD_11);
		btnFindRun.setBounds(320, 104, 89, 20);
		contentPane.add(btnFindRun);

		JLabel lblCoverageid = new JLabel("CoverageID");
		lblCoverageid.setFont(GUICommonTools.TAHOMA_BOLD_14);
		lblCoverageid.setBounds(41, 139, 94, 17);
		contentPane.add(lblCoverageid);

		JLabel lblVariantcallerid = new JLabel("VariantCallerID");
		lblVariantcallerid.setFont(GUICommonTools.TAHOMA_BOLD_14);
		lblVariantcallerid.setBounds(41, 174, 120, 17);
		contentPane.add(lblVariantcallerid);

		comboBoxCoverage = new JComboBox<String>();
		comboBoxCoverage.setBounds(196, 139, 213, 20);
		contentPane.add(comboBoxCoverage);

		comboBoxCaller = new JComboBox<String>();
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
		comboBoxCaller.setBounds(196, 174, 213, 20);
		contentPane.add(comboBoxCaller);

		JLabel lblSampleid = new JLabel("SampleID");
		lblSampleid.setFont(GUICommonTools.TAHOMA_BOLD_14);
		lblSampleid.setBounds(41, 209, 120, 17);
		contentPane.add(lblSampleid);

		comboBoxSample = new JComboBox<String>();
		comboBoxSample.setBounds(196, 209, 213, 20);
		contentPane.add(comboBoxSample);

		JLabel lblLastName = new JLabel("Last Name");
		lblLastName.setFont(GUICommonTools.TAHOMA_BOLD_14);
		lblLastName.setBounds(41, 244, 120, 17);
		contentPane.add(lblLastName);

		JLabel lblFirstName = new JLabel("First Name");
		lblFirstName.setFont(GUICommonTools.TAHOMA_BOLD_14);
		lblFirstName.setBounds(41, 279, 120, 17);
		contentPane.add(lblFirstName);

		JLabel lblOrderNumber = new JLabel("Order Number");
		lblOrderNumber.setFont(GUICommonTools.TAHOMA_BOLD_14);
		lblOrderNumber.setBounds(41, 314, 120, 17);
		contentPane.add(lblOrderNumber);

		JLabel lblPathologyNumber = new JLabel("Pathology Number");
		lblPathologyNumber.setFont(GUICommonTools.TAHOMA_BOLD_14);
		lblPathologyNumber.setBounds(41, 349, 137, 17);
		contentPane.add(lblPathologyNumber);

		JLabel lblTumorSource = new JLabel("Tumor Source");
		lblTumorSource.setFont(GUICommonTools.TAHOMA_BOLD_14);
		lblTumorSource.setBounds(41, 384, 137, 17);
		contentPane.add(lblTumorSource);

		JLabel lblTumorPercent = new JLabel("Tumor Percent");
		lblTumorPercent.setFont(GUICommonTools.TAHOMA_BOLD_14);
		lblTumorPercent.setBounds(41, 419, 137, 17);
		contentPane.add(lblTumorPercent);

		JLabel lblNote = new JLabel("Note");
		lblNote.setFont(GUICommonTools.TAHOMA_BOLD_14);
		lblNote.setBounds(41, 454, 137, 17);
		contentPane.add(lblNote);

		textlastName = new JTextField();
		textlastName.setColumns(10);
		textlastName.setBounds(196, 244, 213, 20);
		contentPane.add(textlastName);

		textFirstName = new JTextField();
		textFirstName.setColumns(10);
		textFirstName.setBounds(196, 279, 213, 20);
		contentPane.add(textFirstName);

		textOrderNumber = new JTextField();
		textOrderNumber.setColumns(10);
		textOrderNumber.setBounds(196, 314, 213, 20);
		contentPane.add(textOrderNumber);

		textPathologyNumber = new JTextField();
		textPathologyNumber.setColumns(10);
		textPathologyNumber.setBounds(196, 349, 213, 20);
		contentPane.add(textPathologyNumber);

		textTumorSource = new JTextField();
		textTumorSource.setColumns(10);
		textTumorSource.setBounds(196, 384, 213, 20);
		contentPane.add(textTumorSource);

		textPercent = new JTextField();
		textPercent.setColumns(10);
		textPercent.setBounds(196, 419, 213, 20);
		contentPane.add(textPercent);

		textNote = new JTextField();
		textNote.setColumns(10);
		textNote.setBounds(196, 454, 238, 20);
		contentPane.add(textNote);

		JButton btnNewButton = new JButton("Enter");
		btnNewButton.setFont(GUICommonTools.TAHOMA_BOLD_12);
		btnNewButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					enterData();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(EnterSample.this, e1.getMessage());
				}
			}
		});
		btnNewButton.setBounds(138, 502, 89, 23);
		contentPane.add(btnNewButton);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				EnterSample.this.setVisible(false);
			}

		});
		btnCancel.setFont(GUICommonTools.TAHOMA_BOLD_12);
		btnCancel.setBounds(256, 502, 89, 23);
		contentPane.add(btnCancel);
	}

	private void findInstrument(){
		assay = comboBoxAssay.getSelectedItem().toString();
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
		//For illumina
		if((instrument.equals("miseq")) || (instrument.equals("nextseq"))){
			String command = String.format("ls /home/%sAnalysis/*_%s_*/*.amplicon.vep.parse.filter.txt", instrument, runID);
			CommandResponse result = SSHConnection.executeCommandAndGetOutput(command);
			ArrayList<String> sampleList = sampleListIllumina(result.response);
			if(result.exitStatus == 0){
				fillSample(sampleList);
			}else{
				removeAllItems();
				JOptionPane.showMessageDialog(null, "There was a problem locating the run");
			}
		}
		//For Ion
		else{
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
}
