package hmvv.gui.adminFrames;

import hmvv.gui.GUICommonTools;
import hmvv.io.DatabaseCommands;
import hmvv.io.SSHConnection;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.main.HMVVFrame;
import hmvv.model.Assay;
import hmvv.model.Instrument;
import hmvv.model.RunFolder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EnterArcherSample extends JDialog {

    private static final long serialVersionUID = 1L;
    
    private JTextField runIDTextField;
    private JTextField runFolderTextField;
    private JTextField assayTextField;

    private JComboBox<Instrument> instrumentComboBox;

    private JButton findRunButton;
    private JButton enterSampleButton;

    private Thread findRunThread;
    private Thread enterSampleThread;
    private JButton btnClear;
    
    private Assay ArcherAssay = Assay.getAssay("archer");


    private RunFolder runFolder = null;

    public EnterArcherSample(HMVVFrame parent) {
        super(parent, "Enter ARCHER Run - Research only", ModalityType.APPLICATION_MODAL);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        createComponents();
        layoutComponents();
        activateComponents();

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }
    
    private void createComponents(){
        instrumentComboBox = new JComboBox<Instrument>();
        try{
            for(Instrument instrument : DatabaseCommands.getInstrumentsForAssay(ArcherAssay)){
                instrumentComboBox.addItem(instrument);
        }
        }catch(Exception e){
            HMVVDefectReportFrame.showHMVVDefectReportFrame(EnterArcherSample.this, e, "Error getting Instruments from database");
            dispose();
            return;
        }

        assayTextField = new JTextField("Archer"); 
        assayTextField.setEditable(false);

        runIDTextField = new JTextField();
        findRunButton = new JButton("Find Run");
        
        runFolderTextField = new JTextField();
        runFolderTextField.setEditable(false);

        enterSampleButton = new JButton("Enter Archer Run");
        enterSampleButton.setFont(GUICommonTools.TAHOMA_BOLD_13);
        btnClear = new JButton("Clear");
        btnClear.setFont(GUICommonTools.TAHOMA_BOLD_13);
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

        //leftPanel.add(new RowPanel("Assay", assayTextField));
        leftPanel.add(new RowPanel("Instrument", instrumentComboBox));
        leftPanel.add(new RowPanel("RunID", runIDPanel));
        leftPanel.add(new RowPanel("RunFolder",runFolderTextField ));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(100, 50));
        bottomPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        JPanel southPanel = new JPanel();
        GridLayout southGridLayout = new GridLayout(1,0);
        southGridLayout.setHgap(30);
        southPanel.setLayout(southGridLayout);
        southPanel.add(enterSampleButton);
        southPanel.add(btnClear);
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
                            JOptionPane.showMessageDialog(EnterArcherSample.this, "Error finding run: " + e.getMessage());
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
                                JOptionPane.showMessageDialog(EnterArcherSample.this, "Please select a run before entering samples.");
                                return;
                            }
                            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                            enterSampleButton.setText("Processing...");
                            enterSampleButton.setEnabled(false);
                            try {
                                enterData();
                                enterSampleButton.setText("Completed");
                            } catch (Exception e) {
                                HMVVDefectReportFrame.showHMVVDefectReportFrame(EnterArcherSample.this, e, "Error entering sample data");
                                enterSampleButton.setText("Enter Archer Run");
                                enterSampleButton.setEnabled(true);
                            }
                            setCursor(Cursor.getDefaultCursor());
                        }
                    });
                    enterSampleThread.start();
                }else if(e.getSource() == btnClear){
                    runFolderTextField.setText("");
                    runIDTextField.setText("");
                    runIDTextField.setEnabled(true);
                    findRunButton.setEnabled(true);
                    instrumentComboBox.setEnabled(true);
                    enterSampleButton.setEnabled(true);
                    enterSampleButton.setText("Enter Archer Run");
                    
                }
            }
        };

        enterSampleButton.addActionListener(actionListener);
        btnClear.addActionListener(actionListener);

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
            DatabaseCommands.insertbclconvertIntoDatabase(instrument, runFolder);
            setEnabled(true);
            }catch(Exception e){
                HMVVDefectReportFrame.showHMVVDefectReportFrame(EnterArcherSample.this, e);
            }
        //}finally {
        //    setEnabled(true);
        //}
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
        updateMainPanel(false);
    }

    private void updateMainPanel(boolean mode){
        instrumentComboBox.setEnabled(mode);
        assayTextField.setEnabled(mode);
        runIDTextField.setEnabled(mode);
        if(mode){
            runIDTextField.setText("");
            runFolderTextField.setText("");
        }
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
