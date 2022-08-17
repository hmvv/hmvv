package hmvv.gui.adminFrames;

import hmvv.gui.GUICommonTools;
import hmvv.gui.sampleList.SampleListFrame;
import hmvv.io.DatabaseCommands;
import hmvv.io.LIS.LISConnection;
import hmvv.io.SSHConnection;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.main.HMVVFrame;
import hmvv.model.Assay;
import hmvv.model.Instrument;
import hmvv.model.Patient;
import hmvv.model.RunFolder;
import hmvv.model.Sample;
import hmvv.model.TMBSample;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Calendar;

public class EnterSample extends JDialog {

    private static final long serialVersionUID = 1L;
    
    private JTextField textRunID;
    private JTextField textRunFolder;
    private JTextField textTMBNormalRunID;
    private JTextField textMRN;
    private JTextField textlastName;
    private JTextField textFirstName;
    private JTextField textOrderNumber;
    private JTextField textPathologyNumber;
    private JTextField textTumorSource;
    private JTextField textPercent;
    private JTextField textPatientHistory;
    private JTextField textDiagnosis;
    private JTextArea textNote;
    private JTextField textBarcode;

    private JComboBox<Instrument> comboBoxInstrument;
    private JComboBox<Assay> comboBoxAssay;
    private JComboBox<String> comboBoxCoverageIDList;
    private JComboBox<String> comboBoxVariantCallerIDList;
    private JComboBox<String> comboBoxSample;
    private JComboBox<String> comboBoxTMBNormalSample;

    private JButton btnFindRun;
    private JButton btnFindTMBNormalRun;
    private JButton btnEnterSample;
    private JButton btnClear;

    private JPanel samplePanel;

    private SampleListFrame sampleListFrame;

    private static String defaultCoverageAndCallerID = "-";

    private Thread findRunThread;
    private Thread enterSampleThread;

    public EnterSample(HMVVFrame parent, SampleListFrame sampleListFrame) {
        super(parent, "Enter Sample");
        this.sampleListFrame = sampleListFrame;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        createComponents();
        layoutComponents();
        activateComponents();
        clearAndDisableSamplePanel();
        clearAndDisableSampleRecords();

        pack();
        setModalityType(ModalityType.APPLICATION_MODAL);
        setAlwaysOnTop(true);
        setResizable(false);
        setLocationRelativeTo(parent);
    }
    
    private void createComponents(){
        comboBoxInstrument = new JComboBox<Instrument>();
        try{
            for(Instrument instrument : DatabaseCommands.getAllInstruments()){
                // remove instruments during sample entry, these cannot be deleted in the database
                // as we want all the samples displayed on the samplelist
                if(instrument.instrumentName.equals("pgm") || instrument.instrumentName.equals("miseq")){continue;}
                comboBoxInstrument.addItem(instrument);
            }
        }catch(Exception e){
            HMVVDefectReportFrame.showHMVVDefectReportFrame(EnterSample.this, e, "Error getting Instruments from database");
            dispose();
            return;
        }

        textRunID = new JTextField();
        textTMBNormalRunID = new JTextField();
        textRunFolder = new JTextField();
        textRunFolder.setEditable(false);

        btnFindRun = new JButton("Find Run");
        btnFindTMBNormalRun = new JButton("Find Normal");
        comboBoxCoverageIDList = new JComboBox<String>();
        comboBoxVariantCallerIDList = new JComboBox<String>();
        comboBoxSample = new JComboBox<String>();

        comboBoxTMBNormalSample= new JComboBox<String>();
        comboBoxAssay = new JComboBox<Assay>();
        try {
			findAssays();
		} catch (Exception e) {
			HMVVDefectReportFrame.showHMVVDefectReportFrame(EnterSample.this, e, "Error getting Assays from database");
            dispose();
            return;
		}
        
        textMRN = new JTextField();
        textlastName = new JTextField();
        textFirstName = new JTextField();
        textOrderNumber = new JTextField();
        textPathologyNumber = new JTextField();
        textTumorSource = new JTextField();
        textPercent = new JTextField();
        textPatientHistory = new JTextField();
        textDiagnosis = new JTextField();
        textNote = new JTextArea();
        textNote.setLineWrap(true);
        textNote.setWrapStyleWord(true);
        textNote.setColumns(5);
        textBarcode = new JTextField();

        btnEnterSample = new JButton("Enter Sample");
        btnEnterSample.setFont(GUICommonTools.TAHOMA_BOLD_13);
        btnClear = new JButton("Clear");
        btnClear.setFont(GUICommonTools.TAHOMA_BOLD_13);

        samplePanel = new JPanel();
    }

    private void layoutComponents(){
        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(200, 10));
        topPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(500, 275));
        leftPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        JPanel runIDPanel = new JPanel();
        GridLayout runIDGridLayout = new GridLayout(1,0);
        runIDGridLayout.setHgap(10);
        runIDPanel.setLayout(runIDGridLayout);
        runIDPanel.add(textRunID);
        runIDPanel.add(btnFindRun);

        leftPanel.add(new RowPanel("Instrument", comboBoxInstrument));
        leftPanel.add(new RowPanel("Assay", comboBoxAssay));
        leftPanel.add(new RowPanel("RunID", runIDPanel));
        leftPanel.add(new RowPanel("RunFolder",textRunFolder ));
        leftPanel.add(new RowPanel("SampleName", comboBoxSample));
        comboBoxSample.setEnabled(false);
        samplePanel.setPreferredSize(new Dimension(475, 150));
        leftPanel.add(samplePanel);

        JPanel centerPanel = new JPanel();
        centerPanel.setPreferredSize(new Dimension(500, 325));
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        RowPanel barcodeRowPanel = new RowPanel("Barcode", textBarcode);
        barcodeRowPanel.left.setToolTipText(LISConnection.getBarcodeHelpText());
        centerPanel.add(barcodeRowPanel);
        centerPanel.add(new RowPanel("Pathology Number", textPathologyNumber));
        centerPanel.add(new RowPanel("Order Number", textOrderNumber));
        centerPanel.add(new RowPanel("MRN", textMRN));
        centerPanel.add(new RowPanel("Last Name", textlastName));
        centerPanel.add(new RowPanel("First Name", textFirstName));
        centerPanel.add(new RowPanel("Tumor Source", textTumorSource));
        centerPanel.add(new RowPanel("Tumor Percent", textPercent));
        centerPanel.add(new RowPanel("Patient History", textPatientHistory));
        centerPanel.add(new RowPanel("Diagnosis", textDiagnosis));

        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(200, 250));
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        JLabel noteLabel = new JLabel("Note");
        noteLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
        Dimension textAreaDimension = new Dimension(160, 225);
        JScrollPane textNoteScroll = new JScrollPane(textNote);
        textNoteScroll.setPreferredSize(textAreaDimension);
        rightPanel.add(noteLabel);
        rightPanel.add(textNoteScroll);
        rightPanel.add(Box.createVerticalStrut(10));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(100, 50));
        bottomPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        JPanel southPanel = new JPanel();
        GridLayout southGridLayout = new GridLayout(1,0);
        southGridLayout.setHgap(30);
        southPanel.setLayout(southGridLayout);
        southPanel.add(btnEnterSample);
        southPanel.add(btnClear);
        bottomPanel.add(southPanel);

        //add(topPanel, BorderLayout.PAGE_START);
        add(leftPanel, BorderLayout.LINE_START);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.LINE_END);
        add(bottomPanel, BorderLayout.PAGE_END);
    }

    private void activateComponents(){
        comboBoxInstrument.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                try {
                    if(e.getStateChange() == ItemEvent.SELECTED) {
                        findAssays();
                    }
                } catch (Exception e1) {
                    HMVVDefectReportFrame.showHMVVDefectReportFrame(EnterSample.this, e1);
                }
            }
        });

        btnFindRun.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                findRunThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            findRun(textRunID.getText(), comboBoxSample, false);
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(EnterSample.this, "Error finding run: " + e.getMessage());
                        }
                    }
                });
                findRunThread.start();
            }
        });

        btnFindTMBNormalRun.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                findRunThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            findRun(textTMBNormalRunID.getText(), comboBoxTMBNormalSample, true);
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(EnterSample.this, "Error finding run: " + e.getMessage());
                        }
                    }
                });
                findRunThread.start();
            }
        });

        ActionListener actionListener = new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource() == btnEnterSample) {
                    enterSampleThread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            if(textRunFolder.getText().equals("")){
                                JOptionPane.showMessageDialog(EnterSample.this, "Please select a run before entering samples.");
                                return;
                            }

                            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                            btnEnterSample.setText("Processing...");
                            btnEnterSample.setEnabled(false);
                            try {
                                enterData();
                                btnEnterSample.setText("Completed");
                            } catch (Exception e) {
                                HMVVDefectReportFrame.showHMVVDefectReportFrame(EnterSample.this, e, "Error entering sample data");
                                btnEnterSample.setText("Enter Sample");
                                btnEnterSample.setEnabled(true);
                            }
                            setCursor(Cursor.getDefaultCursor());
                        }
                    });
                    enterSampleThread.start();
                }else if (e.getSource() == btnClear){
                    updateMainPanel(true);
                    comboBoxSample.removeAllItems();
                    comboBoxSample.setEnabled(false);
                    textRunFolder.setText(null);
                    clearAndDisableSamplePanel();
                    clearAndDisableSampleRecords();
                }
            }
        };

        btnEnterSample.addActionListener(actionListener);

        btnClear.addActionListener(actionListener);

        comboBoxVariantCallerIDList.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED && comboBoxVariantCallerIDList.getSelectedItem() != null) {
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
                if(arg0.getStateChange() == ItemEvent.SELECTED) {
                    sampleIDSelectionChanged();
                }
            }
        });


        textRunID.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnFindRun.doClick();
                }
            }

            @Override
            public void keyReleased(KeyEvent arg0) {}

            @Override
            public void keyTyped(KeyEvent arg0) {}
        });

        textTMBNormalRunID.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnFindTMBNormalRun.doClick();
                }
            }

            @Override
            public void keyReleased(KeyEvent arg0) {}

            @Override
            public void keyTyped(KeyEvent arg0) {}
        });

        textBarcode.addKeyListener(new KeyListener() {
            volatile boolean isEntered = false;
            @Override
            public void keyPressed(KeyEvent arg0) {
                if(isEntered) {
                    textBarcode.setText("");
                    isEntered = false;
                }
                if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                    String barcodeText = textBarcode.getText();
                    updateFields("", "", "", "", "", "", "", "", "", "",true);
                    textBarcode.setText(barcodeText);
                    runLISIntegration(barcodeText);
                }
            }

            @Override
            public void keyReleased(KeyEvent arg0) {
                if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                    isEntered = true;
                }
            }

            @Override
            public void keyTyped(KeyEvent arg0) {}
        });
    }

    private void findAssays() throws Exception{
        Instrument instrument = (Instrument) comboBoxInstrument.getSelectedItem();
        comboBoxAssay.removeAllItems();
        for(Assay assay : DatabaseCommands.getAssaysForInstrument(instrument)){
            comboBoxAssay.addItem(assay);
        }
    }

    private void findRun(String runID, JComboBox<String> comboboxSample, boolean isTMBNormal) throws Exception{

        Instrument instrument = (Instrument) comboBoxInstrument.getSelectedItem();
        try{
            Integer.parseInt(runID);
        }catch(Exception e){
            throw new Exception("Run ID must be an integer.");
        }

        // deactivate downstream steps while finding run - only for main sample
        if (!isTMBNormal){
            comboboxSample.setEnabled(false);
            clearAndDisableSamplePanel();
            clearAndDisableSampleRecords();
        }

        RunFolder runFolder = SSHConnection.getRunFolderIon(instrument, runID);
        if(instrument.instrumentName.equals("miseq") || instrument.instrumentName.equals("nextseq") || instrument.instrumentName.equals("novaseq") ){
            findRunIllumina(instrument, runFolder, comboboxSample, isTMBNormal);
        }else if(instrument.instrumentName.equals("pgm") || instrument.instrumentName.equals("proton")){
            findRunIon(instrument, runFolder);
        }else {
            throw new Exception(String.format("Unsupported instrument (%s)", instrument));
        }


        updateMainPanel(false);
        comboboxSample.setEnabled(true);
        updateSamplePanel(true,comboBoxAssay.getSelectedItem().toString());

    }

    private void findRunIllumina(Instrument instrument, RunFolder runFolder, JComboBox<String> combobox, boolean isTMBNormal) throws Exception {
        ArrayList<String> sampeIDList = SSHConnection.getSampleListIllumina(instrument, runFolder);
        fillSample(sampeIDList,runFolder,combobox,isTMBNormal);
    }

    private void findRunIon(Instrument instrument, RunFolder runFolder) throws Exception {
        if (SSHConnection.checkProtonCopyComplete(instrument,runFolder)) {
            ArrayList<String> coverageIDList = SSHConnection.getCandidateCoverageIDs(instrument, runFolder);
            ArrayList<String> variantCallerIDList = SSHConnection.getCandidateVariantCallerIDs(instrument, runFolder);
            fillComboBoxes(coverageIDList, variantCallerIDList);
        }else{
            JOptionPane.showMessageDialog(EnterSample.this, "Please try again when the run is complete.");
        }

    }

    private void fillSampleIon() throws Exception{
        Instrument instrument = (Instrument)comboBoxInstrument.getSelectedItem();
        String runID = textRunID.getText();
        String variantCallerID = comboBoxVariantCallerIDList.getSelectedItem().toString();

        try{
            Integer.parseInt(runID);
        }catch(Exception e){
            throw new Exception("Run ID must be an integer.");
        }
        RunFolder runFolder = SSHConnection.getRunFolderIon(instrument, runID);
        ArrayList<String> sampleIDList = SSHConnection.getSampleListIon(instrument, runFolder, variantCallerID);
        fillSample(sampleIDList,runFolder, comboBoxSample,false);
    }

    private void fillSample(ArrayList<String> samples,  RunFolder runFolder,JComboBox<String> combobox,boolean isTMBNormal){
        combobox.removeAllItems();
        textRunFolder.setText(runFolder.runFolderName);
        for(int i =0; i < samples.size(); i++){

            String currentSample =samples.get(i);
            if (!isTMBNormal){
                combobox.addItem(currentSample);
            } else if(isTMBNormal && currentSample.endsWith("-N") ){
                combobox.addItem(currentSample);
            }
        }
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
        updateSamplePanel(true,comboBoxAssay.getSelectedItem().toString());
        btnEnterSample.setText("Enter Sample");
        Instrument instrument = (Instrument)comboBoxInstrument.getSelectedItem();
        RunFolder runFolder = new RunFolder(textRunFolder.getText());
        String coverageID = (String)comboBoxCoverageIDList.getSelectedItem();
        String variantCallerID = (String)comboBoxVariantCallerIDList.getSelectedItem();
        String sampleName = (String)comboBoxSample.getSelectedItem();

        if(textRunFolder.getText().equals("") || sampleName == null){
            updateFields("", "", "", "", "", "", "", "", "", "", true);
            return;
        }

        if(coverageID == null)
            coverageID = defaultCoverageAndCallerID;
        if(variantCallerID == null)
            variantCallerID = defaultCoverageAndCallerID;

        Sample sample = sampleListFrame.getSampleTabelModel().getSample(instrument, runFolder, coverageID, variantCallerID, sampleName);
        if(sample != null){
            updateFields(sample.getMRN(), sample.getLastName(), sample.getFirstName(), sample.getOrderNumber(), sample.getPathNumber(), sample.getTumorSource(), sample.getTumorPercent(), sample.getPatientHistory(), sample.getDiagnosis(), sample.getNote(), false);
        }else{
            String barcodeText = textBarcode.getText();
            updateFields("", "", "", "", "", "", "", "", "", "",true);
            runLISIntegration(barcodeText);
        }
    }

    private void runLISIntegration(String barcodeText) {
        Assay assay = (Assay)comboBoxAssay.getSelectedItem();
        String sampleName = (String)comboBoxSample.getSelectedItem();
        comboBoxSample.hidePopup();

        try {
            //fill order number
            String labOrderNumber = LISConnection.getLabOrderNumber(assay.assayName, barcodeText, sampleName);
            textOrderNumber.setText(labOrderNumber);
            if(labOrderNumber.equals("")) {
                return;
            }
            //fill pathology number
            ArrayList<String> pathOrderNumbers = LISConnection.getPathOrderNumbers(assay.assayName, labOrderNumber);
            if(pathOrderNumbers.size() == 0) {
                //No pathology orders found for this sample
            }else if(pathOrderNumbers.size() == 1) {
                textPathologyNumber.setText(pathOrderNumbers.get(0));
            }else {
//                String[] choices = pathOrderNumbers.toArray(new String[pathOrderNumbers.size() + 20]);//add 20 to force JOptionPane into JList
//                String choice = (String) JOptionPane.showInputDialog(this, "Choose the Path Number:",
//                        "Choose the Path Number", JOptionPane.QUESTION_MESSAGE, null,
//                        choices, // Array of choices
//                        choices[0]); // Initial choice
//                if(choice != null) {
//                    textPathologyNumber.setText(choice);
//                }
            }

            //fill patient name
            if(labOrderNumber != null) {
                Patient patient = LISConnection.getPatient(labOrderNumber);
                if(patient != null) {
	                textMRN.setText(patient.mrn);
	                textFirstName.setText(patient.firstName);
	                textlastName.setText(patient.lastName);
                }
            }
        }catch(Exception e) {
            HMVVDefectReportFrame.showHMVVDefectReportFrame(EnterSample.this, e, "LIS Integration Error");
        }
    }

    private void updateFields(String mrn, String lastName, String firstName, String orderNumber, String pathologyNumber, String tumorSource, String tumorPercent, String patientHistory, String diagnosis, String note, boolean editable){
        textBarcode.setText("");
        textMRN.setText(mrn);
        textlastName.setText(lastName);
        textFirstName.setText(firstName);
        textOrderNumber.setText(orderNumber);
        textPathologyNumber.setText(pathologyNumber);
        textTumorSource.setText(tumorSource);
        textPercent.setText(tumorPercent);
        textPatientHistory.setText(patientHistory);
        textDiagnosis.setText(diagnosis);
        textNote.setText(note);

        textBarcode.setEditable(editable);
        textMRN.setEditable(editable);
        textlastName.setEditable(editable);
        textFirstName.setEditable(editable);
        textOrderNumber.setEditable(editable);
        textPathologyNumber.setEditable(editable);
        textTumorSource.setEditable(editable);
        textPercent.setEditable(editable);
        textPatientHistory.setEditable(editable);
        textDiagnosis.setEditable(editable);
        textNote.setEditable(editable);
        btnEnterSample.setEnabled(editable);
    }

    private void clearAndDisableSamplePanel(){

        updateSamplePanel(false, "heme");
    }

    private void clearAndDisableSampleRecords(){

        updateFields("", "", "", "", "", "", "", "", "", "", false);
    }

    private void updateSamplePanel(boolean mode, String assay) {


        if (assay.equals("gene50") || assay.equals("neuro") ) {

            samplePanel.removeAll();
            samplePanel.repaint();
            samplePanel.revalidate();

            this.comboBoxCoverageIDList.setEnabled(mode);
            this.comboBoxVariantCallerIDList.setEnabled(mode);
            samplePanel.add(new RowPanel("CoverageID", this.comboBoxCoverageIDList));
            samplePanel.add(new RowPanel("VariantCallerID", this.comboBoxVariantCallerIDList));
            samplePanel.repaint();
            samplePanel.revalidate();


        } else if (assay.equals("heme")) {

            textTMBNormalRunID.setText("");
            this.comboBoxTMBNormalSample.removeAllItems();
            samplePanel.removeAll();
            samplePanel.repaint();
            samplePanel.revalidate();

        } else if (assay.equals("tmb")) {

            samplePanel.removeAll();
            samplePanel.repaint();
            samplePanel.revalidate();

            JPanel runIDPanel = new JPanel();
            GridLayout runIDGridLayout = new GridLayout(1,0);
            runIDGridLayout.setHgap(10);
            runIDPanel.setLayout(runIDGridLayout);

            runIDPanel.add(textTMBNormalRunID);
            runIDPanel.add(btnFindTMBNormalRun);

            this.textTMBNormalRunID.setEnabled(mode);
            this.btnFindTMBNormalRun.setEnabled(mode);
            this.comboBoxTMBNormalSample.setEnabled(mode);

            samplePanel.add(new RowPanel("Normal-RunID", runIDPanel));
            samplePanel.add(new RowPanel("Normal-Sample", this.comboBoxTMBNormalSample));
            samplePanel.repaint();
            samplePanel.revalidate();
        }
    }

    private void updateMainPanel(boolean mode){
        comboBoxInstrument.setEnabled(mode);
        comboBoxAssay.setEnabled(mode);
        textRunID.setEnabled(mode);
        if(mode){textRunID.setText("");}
        btnFindRun.setEnabled(mode);

    }
    private void enterData() throws Exception{
        setEnabled(false);
        try {
            Sample sample = constructSampleFromTextFields();
            DatabaseCommands.insertDataIntoDatabase(sample);
            sampleListFrame.addSample(sample);

            //call update fields in order to run the code that updates the editable status of the fields, and also the btnEnterSample
            updateFields(sample.getMRN(), sample.getLastName(), sample.getFirstName(), sample.getOrderNumber(), sample.getPathNumber(), sample.getTumorSource(), sample.getTumorPercent(), sample.getPatientHistory(), sample.getDiagnosis(), sample.getNote(), false);
            updateSamplePanel(false,comboBoxAssay.getSelectedItem().toString());
        }finally {
            setEnabled(true);
        }
    }

    private Sample constructSampleFromTextFields() throws Exception{
        if ( textlastName.getText().equals("") || textFirstName.getText().equals("") ){
            throw new Exception("First Name and Last Name are required");
        }

        int sampleID = -1;//This will be computed by the database when the sample is inserted
        Assay assay = (Assay) comboBoxAssay.getSelectedItem();
        Instrument instrument = (Instrument) comboBoxInstrument.getSelectedItem();
        String mrn = textMRN.getText();
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
        String patientHistory = textPatientHistory.getText();
        String diagnosis = textDiagnosis.getText();
        String note = textNote.getText();
        String enteredBy = SSHConnection.getUserName();

        RunFolder runFolder = new RunFolder(textRunFolder.getText());
        if (assay.assayName.equals("tmb")){

            if(comboBoxTMBNormalSample.getSelectedItem().toString().equals(comboBoxSample.getSelectedItem().toString()) &&
                textRunID.getText().equals(textTMBNormalRunID.getText())) {

                throw new Exception("Tumor and Normal sample CANNOT be the same sample.");
            }
             // TODO  replace comboBoxInstrument with normal sample instrument in future
             //TODO run folder
            return new TMBSample(sampleID, assay, instrument, runFolder, mrn, lastName, firstName, orderNumber,
                    pathologyNumber, tumorSource, tumorPercent, runID, sampleName, coverageID, variantCallerID,
                    runDate, patientHistory, diagnosis, note, enteredBy, comboBoxInstrument.getSelectedItem().toString(),
                    textTMBNormalRunID.getText(),comboBoxTMBNormalSample.getSelectedItem().toString());
        }
        else{
            return new Sample(sampleID, assay, instrument, runFolder, mrn, lastName, firstName, orderNumber,
                    pathologyNumber, tumorSource, tumorPercent, runID, sampleName, coverageID, variantCallerID, runDate, patientHistory, diagnosis, note, enteredBy);
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
