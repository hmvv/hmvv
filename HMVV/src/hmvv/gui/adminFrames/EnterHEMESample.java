package hmvv.gui.adminFrames;

import java.util.Collections;
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
import oracle.net.aso.l;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Calendar;

public class EnterHEMESample extends JDialog {

    private static final long serialVersionUID = 1L;
    
    private JTextField textRunID;
    private JTextField textTMBNormalRunID;
    private JTextArea textNote;
    private JTextField textRunFolder;

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

    private Thread findRunThread;
    private Thread enterSampleThread;
    

    public EnterHEMESample(HMVVFrame parent, SampleListFrame sampleListFrame) {
        super(parent, "Enter HEME Sample");
        this.sampleListFrame = sampleListFrame;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        createComponents();
        layoutComponents();
        activateComponents();
        clearAndDisableSamplePanel();
        //clearAndDisableSampleRecords();

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
            HMVVDefectReportFrame.showHMVVDefectReportFrame(EnterHEMESample.this, e, "Error getting Instruments from database");
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
			HMVVDefectReportFrame.showHMVVDefectReportFrame(EnterHEMESample.this, e, "Error getting Assays from database");
            dispose();
            return;
		}
        

        textNote = new JTextArea();
        textNote.setLineWrap(true);
        textNote.setWrapStyleWord(true);
        textNote.setColumns(5);

        btnEnterSample = new JButton("Enter HEME Samples");
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
        leftPanel.setPreferredSize(new Dimension(500, 400));
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
        leftPanel.add(new RowPanel("SampleName", textNote));
        textNote.setEnabled(false);
        samplePanel.setPreferredSize(new Dimension(10, 5));
        leftPanel.add(samplePanel);

        JLabel noteLabel = new JLabel();
        noteLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
        Dimension textAreaDimension = new Dimension(270, 150);
        JScrollPane textNoteScroll = new JScrollPane(textNote);
        textNoteScroll.setPreferredSize(textAreaDimension);
        leftPanel.add(noteLabel);
        leftPanel.add(textNoteScroll);
        leftPanel.add(Box.createVerticalStrut(10));

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
        //add(rightPanel, BorderLayout.LINE_END);
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
                    HMVVDefectReportFrame.showHMVVDefectReportFrame(EnterHEMESample.this, e1);
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
                            findRun(textRunID.getText(), comboBoxSample,false);
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(EnterHEMESample.this, "Error finding run: " + e.getMessage());
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
                            JOptionPane.showMessageDialog(EnterHEMESample.this, "Error finding run: " + e.getMessage());
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
                                JOptionPane.showMessageDialog(EnterHEMESample.this, "Please select a run before entering samples.");
                                return;
                            }
                            if(textNote.getText().equals("")){
                                JOptionPane.showMessageDialog(EnterHEMESample.this, "No samples found. Please select a valid run before entering samples.");
                                return;
                            }

                            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                            btnEnterSample.setText("Processing...");
                            btnEnterSample.setEnabled(false);
                            try {
                                enterData();
                                btnEnterSample.setText("Completed");
                            } catch (Exception e) {
                                HMVVDefectReportFrame.showHMVVDefectReportFrame(EnterHEMESample.this, e, "Error entering sample data");
                                btnEnterSample.setText("Enter HEME Samples");
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
                    textNote.setText(null);
                    textRunFolder.setText(null);
                    clearAndDisableSamplePanel();
                    //clearAndDisableSampleRecords();
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
                        JOptionPane.showMessageDialog(EnterHEMESample.this, e1.getMessage());
                    }
                }
            }
        });

        comboBoxSample.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                if(arg0.getStateChange() == ItemEvent.SELECTED) {
                    //sampleIDSelectionChanged();
                    //check
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


    }

    private void enterData() throws Exception{
        setEnabled(false);
        try {

            String sampleName = textNote.getText();
            ArrayList<String> sampleNames = new ArrayList<>();
            Collections.addAll(sampleNames,sampleName.split("\n"));


            // if the list is not empty
            if(sampleNames.size() != 0){
            for(int i =0; i < sampleNames.size(); i++){
                try{
                    String currentSample =sampleNames.get(i);
                    Sample sample = constructSampleFromTextFields(currentSample);
                    DatabaseCommands.insertDataIntoDatabase(sample);
                    sampleListFrame.addSample(sample);

                }catch(Exception e){
                    HMVVDefectReportFrame.showHMVVDefectReportFrame(EnterHEMESample.this, e);
                }
            
            }
        }      

            //call update fields in order to run the code that updates the editable status of the fields, and also the btnEnterSample
            //updateFields(sample.getMRN(), sample.getLastName(), sample.getFirstName(), sample.getOrderNumber(), sample.getPathNumber(), sample.getTumorSource(), sample.getTumorPercent(), sample.getPatientHistory(), sample.getDiagnosis(), sample.getNote(), false);
            updateSamplePanel(false,comboBoxAssay.getSelectedItem().toString());
        }finally {
            setEnabled(true);
        }
    }

    private Sample constructSampleFromTextFields(String currentSample) throws Exception{
        int sampleID = -1;//This will be computed by the database when the sample is inserted
        Assay assay = (Assay) comboBoxAssay.getSelectedItem();
        Instrument instrument = (Instrument) comboBoxInstrument.getSelectedItem();
        String runID = textRunID.getText();
        RunFolder runFolder = new RunFolder(textRunFolder.getText());
        String sampleName = currentSample;
        String runDate = GUICommonTools.extendedDateFormat1.format(Calendar.getInstance().getTime());
        String enteredBy = SSHConnection.getUserName();
        return new Sample(sampleID, assay, instrument, runFolder, "", "", "", "", "", "", "", runID, sampleName, "", "", runDate, "", "", "", enteredBy);
    }

    private void findAssays() throws Exception{
        Instrument instrument = (Instrument)comboBoxInstrument.getSelectedItem();
        comboBoxAssay.removeAllItems();
        for(Assay assay : DatabaseCommands.getAssaysForInstrument(instrument)){
            comboBoxAssay.addItem(assay);
        }
    }

    private void findRun(String runID, JComboBox<String> comboboxSample, boolean isTMBNormal) throws Exception{

        Instrument instrument = (Instrument)comboBoxInstrument.getSelectedItem();
        try{
            Integer.parseInt(runID);
        }catch(Exception e){
            throw new Exception("Run ID must be an integer.");
        }

        // deactivate downstream steps while finding run - only for main sample
        if (!isTMBNormal){
            comboboxSample.setEnabled(false);
            clearAndDisableSamplePanel();
            //clearAndDisableSampleRecords();
        }

        RunFolder runFolder = SSHConnection.getRunFolderIllumina(instrument, runID); 
        if(instrument.instrumentName.equals("miseq") || instrument.instrumentName.equals("nextseq") || instrument.instrumentName.equals("novaseq") ){
            findRunIllumina(instrument, runFolder, comboboxSample, isTMBNormal);
        }else if(instrument.equals("pgm") || instrument.equals("proton")){
            findRunIon(instrument, runFolder);
        }else {
            throw new Exception(String.format("Unsupported instrument (%s)", instrument));
        }


        updateMainPanel(false);
        comboboxSample.setEnabled(true);
        updateSamplePanel(true,comboBoxAssay.getSelectedItem().toString());

    }

    private void findRunIllumina(Instrument instrument, RunFolder runFolder, JComboBox<String> combobox,boolean isTMBNormal) throws Exception {
        ArrayList<String> sampeIDList = SSHConnection.getSampleListIllumina(instrument, runFolder);
        fillSample(sampeIDList,runFolder,textNote,isTMBNormal);
    }

    private void findRunIon(Instrument instrument, RunFolder runFolder) throws Exception {
        if (SSHConnection.checkProtonCopyComplete(instrument, runFolder)) {
            ArrayList<String> coverageIDList = SSHConnection.getCandidateCoverageIDs(instrument, runFolder);
            ArrayList<String> variantCallerIDList = SSHConnection.getCandidateVariantCallerIDs(instrument, runFolder);
            fillComboBoxes(coverageIDList, variantCallerIDList);
        }else{
            JOptionPane.showMessageDialog(EnterHEMESample.this, "Please try again when the run is complete.");
        }

    }

    private void fillSampleIon() throws Exception{
        Instrument instrument = (Instrument)comboBoxInstrument.getSelectedItem();
        String runID = textRunID.getText();
        String variantCallerID = comboBoxVariantCallerIDList.getSelectedItem().toString();
        RunFolder runFolder = SSHConnection.getRunFolderIon(instrument, runID); 

        try{
            Integer.parseInt(runID);
        }catch(Exception e){
            throw new Exception("Run ID must be an integer.");
        }

        ArrayList<String> sampleIDList = SSHConnection.getSampleListIon(instrument, runFolder, variantCallerID);
        fillSample(sampleIDList,runFolder,textNote,false);
    }

    private void fillSample(ArrayList<String> samples, RunFolder runFolder,JTextArea textNote,boolean isTMBNormal){
        
        textNote.setText("");
        StringBuilder builder = new StringBuilder();
        textRunFolder.setText(runFolder.runFolderName);
        for(String sample : samples){
            if (!isTMBNormal){
                builder.append(sample);
            } else if(isTMBNormal && sample.endsWith("-N") ){
                builder.append(sample);
            }
            builder.append("\n");
        }
        textNote.setText(builder.toString());
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

    private void clearAndDisableSamplePanel(){

        updateSamplePanel(false, "heme");
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
