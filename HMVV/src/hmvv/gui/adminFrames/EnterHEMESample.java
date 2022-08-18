package hmvv.gui.adminFrames;

import java.util.Collections;
import hmvv.gui.GUICommonTools;
import hmvv.gui.sampleList.SampleListFrame;
import hmvv.io.DatabaseCommands;
import hmvv.io.SSHConnection;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.main.HMVVFrame;
import hmvv.model.Assay;
import hmvv.model.Instrument;
import hmvv.model.RunFolder;
import hmvv.model.Sample;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Calendar;

public class EnterHEMESample extends JDialog {

    private static final long serialVersionUID = 1L;
    
    private JTextField runIDTextField;
    private JTextArea sampleListTextArea;
    private JTextField runFolderTextField;
    private JTextField assayTextField;

    private JComboBox<Instrument> instrumentComboBox;

    private JButton findRunButton;
    private JButton enterSampleButton;
    private JButton clearButton;

    private SampleListFrame sampleListFrame;

    private Thread findRunThread;
    private Thread enterSampleThread;
    
    private Assay hemeAssay = Assay.getAssay("heme");
    private RunFolder runFolder = null;
    private ArrayList<String> samples = new ArrayList<String>();

    public EnterHEMESample(HMVVFrame parent, SampleListFrame sampleListFrame) {
        super(parent, "Enter HEME Sample");
        this.sampleListFrame = sampleListFrame;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        createComponents();
        layoutComponents();
        activateComponents();

        pack();
        setModalityType(ModalityType.APPLICATION_MODAL);
        setAlwaysOnTop(true);
        setResizable(false);
        setLocationRelativeTo(parent);
    }
    
    private void createComponents(){
        instrumentComboBox = new JComboBox<Instrument>();
        try{
            for(Instrument instrument : DatabaseCommands.getInstrumentsForAssay(hemeAssay)){
                instrumentComboBox.addItem(instrument);
            }
        }catch(Exception e){
            HMVVDefectReportFrame.showHMVVDefectReportFrame(EnterHEMESample.this, e, "Error getting Instruments from database");
            dispose();
            return;
        }

        assayTextField = new JTextField(hemeAssay.assayName); 
        assayTextField.setEditable(false);

        runIDTextField = new JTextField();
        findRunButton = new JButton("Find Run");
        
        runFolderTextField = new JTextField();
        runFolderTextField.setEditable(false);

        sampleListTextArea = new JTextArea();
        sampleListTextArea.setLineWrap(true);
        sampleListTextArea.setWrapStyleWord(true);
        sampleListTextArea.setColumns(5);

        enterSampleButton = new JButton("Enter HEME Samples");
        enterSampleButton.setFont(GUICommonTools.TAHOMA_BOLD_13);
        clearButton = new JButton("Clear");
        clearButton.setFont(GUICommonTools.TAHOMA_BOLD_13);
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
        runIDPanel.add(runIDTextField);
        runIDPanel.add(findRunButton);

        leftPanel.add(new RowPanel("Assay", assayTextField));
        leftPanel.add(new RowPanel("Instrument", instrumentComboBox));
        leftPanel.add(new RowPanel("RunID", runIDPanel));
        leftPanel.add(new RowPanel("RunFolder",runFolderTextField ));
        leftPanel.add(new RowPanel("SampleName", sampleListTextArea));
        sampleListTextArea.setEnabled(false);

        JLabel sampleListLabel = new JLabel();
        sampleListLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
        Dimension textAreaDimension = new Dimension(270, 150);
        JScrollPane textNoteScroll = new JScrollPane(sampleListTextArea);
        textNoteScroll.setPreferredSize(textAreaDimension);
        leftPanel.add(sampleListLabel);
        leftPanel.add(textNoteScroll);
        leftPanel.add(Box.createVerticalStrut(10));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(100, 50));
        bottomPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        JPanel southPanel = new JPanel();
        GridLayout southGridLayout = new GridLayout(1,0);
        southGridLayout.setHgap(30);
        southPanel.setLayout(southGridLayout);
        southPanel.add(enterSampleButton);
        southPanel.add(clearButton);
        bottomPanel.add(southPanel);

        add(leftPanel, BorderLayout.LINE_START);
        add(bottomPanel, BorderLayout.PAGE_END);
    }

    private void activateComponents(){
        findRunButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                findRunThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            findRun();
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
                if(e.getSource() == enterSampleButton) {
                    enterSampleThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(runFolderTextField.getText().equals("")){
                                JOptionPane.showMessageDialog(EnterHEMESample.this, "Please select a run before entering samples.");
                                return;
                            }
                            if(sampleListTextArea.getText().equals("")){
                                JOptionPane.showMessageDialog(EnterHEMESample.this, "No samples found. Please select a valid run before entering samples.");
                                return;
                            }

                            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                            enterSampleButton.setText("Processing...");
                            enterSampleButton.setEnabled(false);
                            try {
                                enterData();
                                enterSampleButton.setText("Completed");
                            } catch (Exception e) {
                                HMVVDefectReportFrame.showHMVVDefectReportFrame(EnterHEMESample.this, e, "Error entering sample data");
                                enterSampleButton.setText("Enter HEME Samples");
                                enterSampleButton.setEnabled(true);
                            }
                            setCursor(Cursor.getDefaultCursor());
                        }
                    });
                    enterSampleThread.start();
                }else if (e.getSource() == clearButton){
                    updateMainPanel(true);
                    sampleListTextArea.setText(null);
                    runFolderTextField.setText(null);
                }
            }
        };

        enterSampleButton.addActionListener(actionListener);
        clearButton.addActionListener(actionListener);
        runIDTextField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent arg0) {
                if(arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                    findRunButton.doClick();
                }
            }

            @Override
            public void keyReleased(KeyEvent arg0) {}

            @Override
            public void keyTyped(KeyEvent arg0) {}
        });
    }

    private void enterData() throws Exception{
        try {
            setEnabled(false);
            Instrument instrument = (Instrument) instrumentComboBox.getSelectedItem();
            
            if(samples.size() != 0){
                for(String currentSample : samples){
                    try{
                        Sample sampleObject = sampleListFrame.getSampleTabelModel().getSample(instrument, runFolder, "", "", currentSample);
                        if(sampleObject == null){
                            Sample sample = constructSampleFromTextFields(currentSample);
                            DatabaseCommands.insertDataIntoDatabase(sample);
                            sampleListFrame.addSample(sample);
                        }
                    }catch(Exception e){
                        HMVVDefectReportFrame.showHMVVDefectReportFrame(EnterHEMESample.this, e);
                    }
                }
            }
        }finally {
            setEnabled(true);
        }
    }

    private Sample constructSampleFromTextFields(String currentSample) throws Exception{
        int sampleID = -1;//This will be computed by the database when the sample is inserted
        Instrument instrument = (Instrument) instrumentComboBox.getSelectedItem();
        String runID = runIDTextField.getText();
        String sampleName = currentSample;
        String runDate = GUICommonTools.extendedDateFormat1.format(Calendar.getInstance().getTime());
        String enteredBy = SSHConnection.getUserName();
        return new Sample(sampleID, hemeAssay, instrument, runFolder, "", "", "", "", "", "", "", runID, sampleName, "", "", runDate, "", "", "", enteredBy);
    }

    private void findRun() throws Exception{
        String runID = runIDTextField.getText();
        Instrument instrument = (Instrument)instrumentComboBox.getSelectedItem();
        try{
            Integer.parseInt(runID);
        }catch(Exception e){
            throw new Exception("Run ID must be an integer.");
        }

        runFolder = SSHConnection.getRunFolderIllumina(instrument, runID); 
        runFolderTextField.setText(runFolder.runFolderName);

        samples = SSHConnection.getSampleListIllumina(instrument, runFolder);
        fillSample(samples, instrument, runFolder, sampleListTextArea);
        updateMainPanel(false);
    }

    private void fillSample(ArrayList<String> samples, Instrument instrument, RunFolder runFolder, JTextArea textNotel){
        sampleListTextArea.setText("");
        StringBuilder builder = new StringBuilder();
        for(String sampleName : samples){
            Sample sampleObject = sampleListFrame.getSampleTabelModel().getSample(instrument, runFolder, "", "", sampleName);
            if(sampleObject == null){
                builder.append(sampleName);
            }else{
                builder.append("---" + sampleName);
            }
            builder.append("\n");
        }
        sampleListTextArea.setText(builder.toString());
    }

    private void updateMainPanel(boolean mode){
        instrumentComboBox.setEnabled(mode);
        assayTextField.setEnabled(mode);
        runIDTextField.setEnabled(mode);
        if(mode){runIDTextField.setText("");}
        findRunButton.setEnabled(mode);
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
