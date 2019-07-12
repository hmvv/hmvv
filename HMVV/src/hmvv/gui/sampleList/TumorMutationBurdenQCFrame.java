package hmvv.gui.sampleList;

import hmvv.gui.GUICommonTools;
import hmvv.io.SSHConnection;
import hmvv.model.Sample;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class TumorMutationBurdenQCFrame extends JDialog {

    private static final long serialVersionUID = 1L;

    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane tableScrollPane;

    private Sample sample;

    public TumorMutationBurdenQCFrame(SampleListFrame parent, Sample sample) throws Exception{
        super(parent, "Sample AssayQC");
        this.sample = sample;

        Rectangle bounds = GUICommonTools.getBounds(parent);
        setSize((int)(bounds.width*.5), (int)(bounds.height*.5));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);

        createComponents();
        layoutComponents();
        setLocationRelativeTo(parent);

        String title = "TMB Quality Control - " + sample.getLastName() + "," + sample.getFirstName() +
                " (runID = " + sample.runID + ", sampleID = " + sample.sampleID + ")";
        setTitle(title);

    }

    private void createComponents(){

        tableModel = new DefaultTableModel(new String[]{"Metrics", "Tumor", "Normal"}, 0);
        table = new JTable(tableModel);

        tableScrollPane = new JScrollPane();
        tableScrollPane.setViewportView(table);
        table.getTableHeader().setFont(GUICommonTools.TAHOMA_BOLD_14);
        table.setFont(GUICommonTools.TAHOMA_BOLD_12);
    }

    private void layoutComponents(){

        Container pane = getContentPane();
        JPanel mainPanel = new JPanel();
        mainPanel.add(tableScrollPane);
        pane.add(mainPanel,BorderLayout.PAGE_START);

    }

    public boolean setValues() throws Exception {

        try {
            ArrayList<String> exomeQC = SSHConnection.readTMBSeqStatsFile(sample);

            for (int i = 0; i < exomeQC.size(); i++) {
                String[] rowdata = exomeQC.get(i).split(",");
                tableModel.addRow(new Object[]{rowdata[0], rowdata[1], rowdata[2]});
            }

            return true;

        }catch(Exception e) {
            JOptionPane.showMessageDialog(null, "TMB Assay - Pipeline not completed.");
            return false;
        }
    }
}
