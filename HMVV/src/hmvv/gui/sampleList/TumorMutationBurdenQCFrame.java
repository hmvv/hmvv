package hmvv.gui.sampleList;

import hmvv.gui.GUICommonTools;
import hmvv.io.SSHConnection;
import hmvv.main.HMVVFrame;
import hmvv.model.TMBSample;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class TumorMutationBurdenQCFrame extends JDialog {

    private static final long serialVersionUID = 1L;

    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane tableScrollPane;
    private TMBSample sample;
    private Rectangle bounds;

    public TumorMutationBurdenQCFrame(HMVVFrame parent, TMBSample sample) throws Exception{
        super(parent, "Title Set Later", ModalityType.APPLICATION_MODAL);
        String title = "TMB Quality Control - " + sample.getLastName() + "," + sample.getFirstName() +
                " (runID = " + sample.runID + ", sampleID = " + sample.sampleID + ")";
        setTitle(title);
        this.sample = sample;

        this.bounds = GUICommonTools.getBounds(parent);
        setSize((int)(bounds.width*.6), (int)(bounds.height*.6));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        createComponents();
        layoutComponents();
        setComponentValues();
        setLocationRelativeTo(parent);        
    }

    private void createComponents(){
        tableModel = new DefaultTableModel(new String[]{"Metrics", "Stats"}, 0);
        table = new JTable(tableModel);

        ((DefaultTableCellRenderer)table.getDefaultRenderer(String.class)).setHorizontalAlignment(SwingConstants.CENTER);
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        tableScrollPane = new JScrollPane();
        tableScrollPane.setViewportView(table);
        table.getTableHeader().setFont(GUICommonTools.TAHOMA_BOLD_14);
        tableScrollPane.setPreferredSize(new Dimension((int)(bounds.width*.5), (int)(bounds.height*.5)));
        table.setFont(GUICommonTools.TAHOMA_BOLD_12);
    }

    private void layoutComponents(){
        JPanel mainPanel = new JPanel();
        mainPanel.add(tableScrollPane);
        add(mainPanel, BorderLayout.PAGE_START);
    }

    private void setComponentValues() throws Exception {

    	ArrayList<String> exomeQC = SSHConnection.readTMBSeqStatsFile(sample);

    	if (exomeQC.size()>1){

    	    for (int i = 0; i < exomeQC.size(); i++) {
                String[] rowdata = exomeQC.get(i).split(",");
                tableModel.addRow(new Object[]{rowdata[0], rowdata[1]});
            }
    	}else{

    	    tableModel.addRow(new Object[]{GUICommonTools.PIPELINE_INCOMPLETE_STATUS});
    }

    }
}

