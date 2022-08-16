package hmvv.gui.adminFrames;

import hmvv.gui.GUICommonTools;
import hmvv.gui.sampleList.SampleListFrame;
import hmvv.io.DatabaseCommands;
import hmvv.io.LIS.LISConnection;
import hmvv.io.SSHConnection;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.main.HMVVFrame;
import hmvv.model.Patient;
import hmvv.model.Sample;
import hmvv.model.TMBSample;

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

    private JComboBox<String> comboBoxInstrument;
    private JComboBox<String> comboBoxAssay;
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
        comboBoxInstrument = new JComboBox<String>();
        try{
            for(String instrument : DatabaseCommands.getAllInstruments()){
                // remove instruments during sample entry, these cannot be deleted in the database
                // as we want all the samples displayed on the samplelist
                if(instrument.equals("pgm") || instrument.equals("miseq") || instrument.equals("nextseq550")){continue;}
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
        comboBoxAssay = new JComboBox<String>();
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
                            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                            btnEnterSample.setText("Processing...");
                            btnEnterSample.setEnabled(false);
                            try {
                                //enterData();
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

    private void findAssays() throws Exception{
        String instrument = comboBoxInstrument.getSelectedItem().toString();
        comboBoxAssay.removeAllItems();
        for(String assay : DatabaseCommands.getAssaysForInstrument(instrument)){
            comboBoxAssay.addItem(assay);
        }
    }

    private void findRun(String runID, JComboBox<String> comboboxSample, boolean isTMBNormal) throws Exception{

        String instrument = comboBoxInstrument.getSelectedItem().toString();
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


        if(instrument.equals("miseq") || instrument.equals("nextseq") || instrument.equals("novaseq") ){
            findRunIllumina(instrument, runID, comboboxSample, isTMBNormal);
        }else if(instrument.equals("pgm") || instrument.equals("proton")){
            findRunIon(instrument, runID);
        }else {
            throw new Exception(String.format("Unsupported instrument (%s)", instrument));
        }


        updateMainPanel(false);
        comboboxSample.setEnabled(true);
        updateSamplePanel(true,comboBoxAssay.getSelectedItem().toString());

    }

    private void findRunIllumina(String instrument, String runID, JComboBox<String> combobox,boolean isTMBNormal) throws Exception {
        String runFolderName = SSHConnection.getRunFolderIllumina(instrument, runID); 
        ArrayList<String> sampeIDList = SSHConnection.getSampleListIllumina(instrument, runFolderName);
        fillSample(sampeIDList,runFolderName,textNote,isTMBNormal);
    }

    private void findRunIon(String instrument, String runID) throws Exception {
        if (SSHConnection.checkProtonCopyComplete(instrument,runID)) {
            ArrayList<String> coverageIDList = SSHConnection.getCandidateCoverageIDs(instrument, runID);
            ArrayList<String> variantCallerIDList = SSHConnection.getCandidateVariantCallerIDs(instrument, runID);
            fillComboBoxes(coverageIDList, variantCallerIDList);
        }else{
            JOptionPane.showMessageDialog(EnterHEMESample.this, "Please try again when the run is complete.");
        }

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
        String RunFolderName = SSHConnection.getRunFolderIllumina(instrument, runID); 
        fillSample(sampleIDList,RunFolderName,textNote,false);
    }

    private void fillSample(ArrayList<String> samples, String runFolderName,JTextArea textNote,boolean isTMBNormal){
        

        textRunFolder.setText(runFolderName);
        int j = 1;
        for(int i =0; i < samples.size(); i++){
            String currentSample =samples.get(i);
            if (!isTMBNormal){
                textNote.insert(currentSample + "\r\n",i);
            } else if(isTMBNormal && currentSample.endsWith("-N") ){
                textNote.insert(currentSample + "\r\n",i);
            }
            j = j + currentSample.length();
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
