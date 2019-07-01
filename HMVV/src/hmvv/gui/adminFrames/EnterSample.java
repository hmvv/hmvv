package hmvv.gui.adminFrames;

import hmvv.gui.GUICommonTools;
import hmvv.gui.sampleList.SampleListFrame;
import hmvv.gui.sampleList.SampleListTableModel;
import hmvv.io.DatabaseCommands;
import hmvv.io.LIS.LISConnection;
import hmvv.io.SSHConnection;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.model.Sample;
import hmvv.model.SampleExome;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Calendar;

public class EnterSample extends JDialog {

    private static final long serialVersionUID = 1L;

    private SampleListFrame parent;
    private JTextField textRunID;
    private JTextField textExomeNormalRunID;
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

    private JComboBox<String> comboBoxInstrument;
    private JComboBox<String> comboBoxAssay;
    private JComboBox<String> comboBoxCoverageIDList;
    private JComboBox<String> comboBoxVariantCallerIDList;
    private JComboBox<String> comboBoxSample;
    private JComboBox<String> comboBoxExomeNormalSample;

    private JButton btnFindRun;
    private JButton btnFindExomeNormalRun;
    private JButton enterSampleButton;
    private JButton cancelButton;

    private JPanel assayPanel;

    private SampleListTableModel sampleListTableModel;

    private static String defaultCoverageAndCallerID = "-";

    private Thread findRunThread;
    private Thread enterSampleThread;

    public EnterSample(SampleListFrame parent, SampleListTableModel sampleListTableModel) {
        super(parent, "Enter Sample");
        this.parent = parent;
        this.sampleListTableModel = sampleListTableModel;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        createComponents();
        layoutComponents();
        activateComponents();
        enableComboBoxes(false, false, false,false);

        pack();
        setModalityType(ModalityType.APPLICATION_MODAL);
        setAlwaysOnTop(true);
        setResizable(false);
        setLocationRelativeTo(parent);
    }
    private void createComponents(){
        comboBoxInstrument = new JComboBox<String>();
        try{
            for(String instrument : DatabaseCommands.getAllInstruments()){
                comboBoxInstrument.addItem(instrument);
            }
        }catch(Exception e){
            HMVVDefectReportFrame.showHMVVDefectReportFrame(EnterSample.this, e, "Error getting Instruments from database");
            dispose();
            return;
        }

        textRunID = new JTextField();
        textExomeNormalRunID = new JTextField();

        btnFindRun = new JButton("Find Run");
        btnFindExomeNormalRun = new JButton("Find Normal");
        comboBoxCoverageIDList = new JComboBox<String>();
        comboBoxVariantCallerIDList = new JComboBox<String>();
        comboBoxSample = new JComboBox<String>();

        comboBoxExomeNormalSample= new JComboBox<String>();
        comboBoxAssay = new JComboBox<String>();

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

        enterSampleButton = new JButton("Enter Sample");
        enterSampleButton.setFont(GUICommonTools.TAHOMA_BOLD_13);
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(GUICommonTools.TAHOMA_BOLD_13);

        assayPanel = new JPanel();
    }

    private void layoutComponents(){

        Container pane = getContentPane();

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
        leftPanel.add(new RowPanel("RunID", runIDPanel));
        leftPanel.add(new RowPanel("SampleName", comboBoxSample));

        leftPanel.add(new RowPanel("Assay", comboBoxAssay));
        assayPanel.setPreferredSize(new Dimension(475, 150));
        leftPanel.add(assayPanel);

        JPanel centerPanel = new JPanel();
        centerPanel.setPreferredSize(new Dimension(500, 250));
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        RowPanel barcodeRowPanel = new RowPanel("Barcode", textBarcode);
        barcodeRowPanel.left.setToolTipText(LISConnection.getBarcodeHelpText());
        centerPanel.add(barcodeRowPanel);
        centerPanel.add(new RowPanel("Pathology Number", textPathologyNumber));
        centerPanel.add(new RowPanel("Order Number", textOrderNumber));
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
        southPanel.add(enterSampleButton);
        southPanel.add(cancelButton);
        bottomPanel.add(southPanel);

        //pane.add(topPanel, BorderLayout.PAGE_START);
        pane.add(leftPanel, BorderLayout.LINE_START);
        pane.add(centerPanel, BorderLayout.CENTER);
        pane.add(rightPanel, BorderLayout.LINE_END);
        pane.add(bottomPanel, BorderLayout.PAGE_END);
    }

    private void activateComponents(){
        comboBoxInstrument.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                try {
                    if(e.getStateChange() == ItemEvent.SELECTED) {
                        clearAndDisableAll();
                        findAssays();
                    }
                } catch (Exception e1) {
                    HMVVDefectReportFrame.showHMVVDefectReportFrame(EnterSample.this, e1);
                }
            }
        });//

        btnFindRun.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                findRunThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            findRun(textRunID.getText(),comboBoxSample,false);
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(EnterSample.this, "Error finding run: " + e.getMessage());
                        }
                    }
                });
                findRunThread.start();
            }
        });

        btnFindExomeNormalRun.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                findRunThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            findRun(textExomeNormalRunID.getText(),comboBoxExomeNormalSample, true);
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
                if(e.getSource() == enterSampleButton) {
                    enterSampleThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                            enterSampleButton.setText("Processing...");
                            enterSampleButton.setEnabled(false);
                            try {
                                enterData();
                                enterSampleButton.setText("Completed");
                            } catch (Exception e) {
                                HMVVDefectReportFrame.showHMVVDefectReportFrame(EnterSample.this, e, "Error entering sample data");
                                enterSampleButton.setText("Enter Sample");
                                enterSampleButton.setEnabled(true);
                            }
                            setCursor(Cursor.getDefaultCursor());
                        }
                    });
                    enterSampleThread.start();
                }else if(e.getSource() == cancelButton) {
                    dispose();
                }
            }
        };

        enterSampleButton.addActionListener(actionListener);
        cancelButton.addActionListener(actionListener);

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

        comboBoxAssay.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if(arg0.getStateChange() == ItemEvent.SELECTED) {
                    assaySelectionChanged(arg0.getItem().toString());
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

        textExomeNormalRunID.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnFindExomeNormalRun.doClick();
                }
            }

            @Override
            public void keyReleased(KeyEvent arg0) {}

            @Override
            public void keyTyped(KeyEvent arg0) {}
        });

        sampleIDSelectionChanged();

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
                    clearFields(true,false);
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
        String instrument = comboBoxInstrument.getSelectedItem().toString();
        comboBoxAssay.removeAllItems();
        for(String assay : DatabaseCommands.getAssaysForInstrument(instrument)){
            comboBoxAssay.addItem(assay);
        }
    }

    private void findRun(String runID, JComboBox<String> combobox,boolean isExomeNormal) throws Exception{
        clearComboBoxes(isExomeNormal);
        clearFields(false,isExomeNormal);
        enableComboBoxes(false, false, false,false);

        String instrument = comboBoxInstrument.getSelectedItem().toString();
        try{
            Integer.parseInt(runID);
        }catch(Exception e){
            throw new Exception("Run ID must be an integer.");
        }

        if(instrument.equals("miseq") || instrument.equals("nextseq")){
            findRunIllumina(instrument, runID, combobox);
        }else if(instrument.equals("pgm") || instrument.equals("proton")){
            findRunIon(instrument, runID);
        }else {
            throw new Exception(String.format("Unsupported instrument (%s)", instrument));
        }
    }

    private void findRunIllumina(String instrument, String runID,JComboBox<String> combobox) throws Exception {
        ArrayList<String> sampeIDList = SSHConnection.getSampleListIllumina(instrument, runID);
        fillSample(sampeIDList,combobox);
        enableComboBoxes(false, false, true,true);
    }

    private void findRunIon(String instrument, String runID) throws Exception {
        ArrayList<String> coverageIDList = SSHConnection.getCandidateCoverageIDs(instrument, runID);
        ArrayList<String> variantCallerIDList = SSHConnection.getCandidateVariantCallerIDs(instrument, runID);
        fillComboBoxes(coverageIDList, variantCallerIDList);
        enableComboBoxes(true, true, true,false);

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
        fillSample(sampleIDList,comboBoxSample);
    }

    private void fillSample(ArrayList<String> samples,JComboBox<String> combobox){
        combobox.removeAllItems();
        for(int i =0; i < samples.size(); i++){
            combobox.addItem(samples.get(i));
        }
        //TODO verify impact of removing this
        //sampleIDSelectionChanged();
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
        String instrument = (String)comboBoxInstrument.getSelectedItem();
        String coverageID = (String)comboBoxCoverageIDList.getSelectedItem();
        String variantCallerID = (String)comboBoxVariantCallerIDList.getSelectedItem();
        String sampleName = (String)comboBoxSample.getSelectedItem();

        if(runID.equals("") || sampleName == null){
            updateFields("", "", "", "", "", "", "", "", "", false);
            return;
        }

        if(coverageID == null)
            coverageID = defaultCoverageAndCallerID;
        if(variantCallerID == null)
            variantCallerID = defaultCoverageAndCallerID;

        Sample sample = sampleListTableModel.getSample(instrument, runID, coverageID, variantCallerID, sampleName);
        if(sample != null){
            updateFields(sample.getLastName(), sample.getFirstName(), sample.getOrderNumber(), sample.getPathNumber(), sample.getTumorSource(), sample.getTumorPercent(), sample.getPatientHistory(), sample.getDiagnosis(), sample.getNote(), false);
        }else{
            String barcodeText = textBarcode.getText();
            clearFields(true,false);
            runLISIntegration(barcodeText);
        }
    }

    private void assaySelectionChanged(String assay) {
        if (assay.equals("gene50") || assay.equals("neuro") ) {

            assayPanel.removeAll();
            assayPanel.repaint();
            assayPanel.revalidate();

            assayPanel.add(new RowPanel("CoverageID", comboBoxCoverageIDList));
            assayPanel.add(new RowPanel("VariantCallerID", comboBoxVariantCallerIDList));
            assayPanel.repaint();
            assayPanel.revalidate();


        } else if (assay.equals("heme")) {

            assayPanel.removeAll();
            assayPanel.repaint();
            assayPanel.revalidate();

        } else if (assay.equals("exome")) {

            assayPanel.removeAll();
            assayPanel.repaint();
            assayPanel.revalidate();

            JPanel runIDPanel = new JPanel();
            GridLayout runIDGridLayout = new GridLayout(1,0);
            runIDGridLayout.setHgap(10);
            runIDPanel.setLayout(runIDGridLayout);
            runIDPanel.add(textExomeNormalRunID);
            runIDPanel.add(btnFindExomeNormalRun);

            assayPanel.add(new RowPanel("Normal-RunID", runIDPanel));
            assayPanel.add(new RowPanel("Normal-Sample", comboBoxExomeNormalSample));
            assayPanel.repaint();
            assayPanel.revalidate();
        }
    }
    private void runLISIntegration(String barcodeText) {
        String assay = (String)comboBoxAssay.getSelectedItem();
        String sampleName = (String)comboBoxSample.getSelectedItem();
        comboBoxSample.hidePopup();

        try {
            //fill order number
            String labOrderNumber = LISConnection.getLabOrderNumber(assay, barcodeText, sampleName);
            textOrderNumber.setText(labOrderNumber);
            if(labOrderNumber.equals("")) {
                return;
            }
            //fill pathology number
            ArrayList<String> pathOrderNumbers = LISConnection.getPathOrderNumbers(assay, labOrderNumber);
            if(pathOrderNumbers.size() == 0) {
                //No pathology orders found for this sample
            }else if(pathOrderNumbers.size() == 1) {
                textPathologyNumber.setText(pathOrderNumbers.get(0));
            }else {
                String[] choices = pathOrderNumbers.toArray(new String[pathOrderNumbers.size() + 20]);//add 20 to force JOptionPane into JList
                String choice = (String) JOptionPane.showInputDialog(this, "Choose the Path Number:",
                        "Choose the Path Number", JOptionPane.QUESTION_MESSAGE, null,
                        choices, // Array of choices
                        choices[0]); // Initial choice
                if(choice != null) {
                    textPathologyNumber.setText(choice);
                }
            }

            //fill patient name
            if(labOrderNumber != null) {
                String[] patientName = LISConnection.getPatientName(labOrderNumber);
                textFirstName.setText(patientName[0]);
                textlastName.setText(patientName[1]);
            }
        }catch(Exception e) {
            HMVVDefectReportFrame.showHMVVDefectReportFrame(EnterSample.this, e, "LIS Integration Error");
        }
    }

    private void updateFields(String lastName, String firstName, String orderNumber, String pathologyNumber, String tumorSource, String tumorPercent, String patientHistory, String diagnosis, String note, boolean editable){
        textBarcode.setText("");
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
        textlastName.setEditable(editable);
        textFirstName.setEditable(editable);
        textOrderNumber.setEditable(editable);
        textPathologyNumber.setEditable(editable);
        textTumorSource.setEditable(editable);
        textPercent.setEditable(editable);
        textPatientHistory.setEditable(editable);
        textDiagnosis.setEditable(editable);
        textNote.setEditable(editable);
        enterSampleButton.setEnabled(editable);
    }

    private void clearComboBoxes(Boolean isExomeNormal){
        comboBoxCoverageIDList.removeAllItems();
        comboBoxVariantCallerIDList.removeAllItems();
        if(!isExomeNormal){
            comboBoxSample.removeAllItems();
        }
        comboBoxExomeNormalSample.removeAllItems();
    }

    private void clearAllRunIDs(){
        textRunID.setText("");
        textExomeNormalRunID.setText("");
    }

    private void clearAndDisableAll(){
        clearComboBoxes(false);
        clearAllRunIDs();
        clearFields(false,false);
        enableComboBoxes(false, false, false,false);
    }

    private void enableComboBoxes(boolean comboBoxCoverageIDList, boolean comboBoxVariantCallerIDList, boolean comboBoxSample,boolean comboBoxExomeNormalSample) {
        this.comboBoxCoverageIDList.setEnabled(comboBoxCoverageIDList);
        this.comboBoxVariantCallerIDList.setEnabled(comboBoxVariantCallerIDList);
        this.comboBoxSample.setEnabled(comboBoxSample);
        this.comboBoxExomeNormalSample.setEnabled(comboBoxExomeNormalSample);
    }

    private void clearFields(boolean editable,boolean isExomeNormal) {

        if (!isExomeNormal) {
            updateFields("", "", "", "", "", "", "", "", "", editable);
        }
    }

    private void enterData() throws Exception{
        setEnabled(false);

        try {
            Sample sample = constructSampleFromTextFields();
            DatabaseCommands.insertDataIntoDatabase(sample);
            parent.addSample(sample);

            //call update fields in order to run the code that updates the editable status of the fields, and also the enterSampleButton
            updateFields(sample.getLastName(), sample.getFirstName(), sample.getOrderNumber(), sample.getPathNumber(), sample.getTumorSource(), sample.getTumorPercent(), sample.getPatientHistory(), sample.getDiagnosis(), sample.getNote(), false);
        }finally {
            setEnabled(true);
        }
    }

    private Sample constructSampleFromTextFields() throws Exception{
        if(textlastName.getText().equals("") || textFirstName.getText().equals("")  ){
            throw new Exception("First Name and Last Name are required");
        }

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
        String patientHistory = textPatientHistory.getText();
        String diagnosis = textDiagnosis.getText();
        String note = textNote.getText();
        String enteredBy = SSHConnection.getUserName();

        if (assay.equals("exome")){

            if(comboBoxExomeNormalSample.getSelectedItem().toString().equals(comboBoxSample.getSelectedItem().toString()) &&
                textRunID.getText().equals(textExomeNormalRunID.getText())) {

                throw new Exception("Tumor and Normal sample CANNOT be the same sample.");
            }

            return new SampleExome(sampleID, assay, instrument, lastName, firstName, orderNumber,
                    pathologyNumber, tumorSource, tumorPercent, runID, sampleName, coverageID, variantCallerID,
                    runDate, patientHistory, diagnosis, note, enteredBy,
                    textExomeNormalRunID.getText(),comboBoxExomeNormalSample.getSelectedItem().toString());
        }
        else{
            return new Sample(sampleID, assay, instrument, lastName, firstName, orderNumber,
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
