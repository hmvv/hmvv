package hmvv.gui.sampleList;

import hmvv.gui.GUICommonTools;
import hmvv.io.DatabaseCommands;
import hmvv.main.HMVVFrame;
import hmvv.model.TMBSample;
import hmvv.model.TMBSampleQC;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

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

        TMBSampleQC TMBSampleQC = DatabaseCommands.getTMBSampleQC(sample);
        if (TMBSampleQC == null){
            tableModel.addRow(new Object[]{GUICommonTools.PIPELINE_INCOMPLETE_STATUS});
        }else{
            tableModel.addRow(new Object[]{"Sample ID", TMBSampleQC.sampleID});
            tableModel.addRow(new Object[]{"TMB Pair", TMBSampleQC.TMBPair});
            tableModel.addRow(new Object[]{"TMB Total Variants", TMBSampleQC.TMBTotalVariants});
            tableModel.addRow(new Object[]{"TMB Score", TMBSampleQC.TMBScore});
            tableModel.addRow(new Object[]{"TMB Group", TMBSampleQC.TMBGroup});
            tableModel.addRow(new Object[]{"VarScan & Strelka", TMBSampleQC.varscan_strelka});
            tableModel.addRow(new Object[]{"VarScan & Mutect", TMBSampleQC.varscan_mutect});
            tableModel.addRow(new Object[]{"Mutect & Strelka", TMBSampleQC.mutect_strelka});
            tableModel.addRow(new Object[]{"VarScan & Strelka Mutect", TMBSampleQC.varscan_strelka_mutect});
            tableModel.addRow(new Object[]{"Tumor Total Reads", TMBSampleQC.Tumor_Total_Reads});
            tableModel.addRow(new Object[]{"Normal Total Reads", TMBSampleQC.Normal_Total_Reads});
            tableModel.addRow(new Object[]{"Tumor Q20", TMBSampleQC.Tumor_Q20});
            tableModel.addRow(new Object[]{"Normal Q20", TMBSampleQC.Normal_Q20});
            tableModel.addRow(new Object[]{"Tumor Total Reads AQC", TMBSampleQC.Tumor_Total_Reads_AQC});
            tableModel.addRow(new Object[]{"Normal Total Reads AQC", TMBSampleQC.Normal_Total_Reads_AQC});
            tableModel.addRow(new Object[]{"Tumor Duplicate", TMBSampleQC.Tumor_Duplicate});
            tableModel.addRow(new Object[]{"Normal Duplicate", TMBSampleQC.Normal_Duplicate});
            tableModel.addRow(new Object[]{"Tumor Total Reads ADup", TMBSampleQC.Tumor_Total_Reads_ADup});
            tableModel.addRow(new Object[]{"Normal Total Reads ADup", TMBSampleQC.Normal_Total_Reads_ADup});
            tableModel.addRow(new Object[]{"Tumor Coverage", TMBSampleQC.Tumor_Coverage});
            tableModel.addRow(new Object[]{"Normal Coverage", TMBSampleQC.Normal_Coverage});
            tableModel.addRow(new Object[]{"Tumor Target Coverage", TMBSampleQC.Tumor_Target_Coverage});
            tableModel.addRow(new Object[]{"Normal Target Coverage", TMBSampleQC.Normal_Target_Coverage});
            tableModel.addRow(new Object[]{"Tumor Coverage (10X)", TMBSampleQC.Tumor_Coverage_10X});
            tableModel.addRow(new Object[]{"Normal Coverage (10X)", TMBSampleQC.Normal_Coverage_10X});
            tableModel.addRow(new Object[]{"Tumor Coverage (20X)", TMBSampleQC.Tumor_Coverage_20X});
            tableModel.addRow(new Object[]{"Normal Coverage (20X)", TMBSampleQC.Normal_Coverage_20X});
            tableModel.addRow(new Object[]{"Tumor Coverage (50X)", TMBSampleQC.Tumor_Coverage_50X});
            tableModel.addRow(new Object[]{"Normal Coverage (50X)", TMBSampleQC.Normal_Coverage_50X});
            tableModel.addRow(new Object[]{"Tumor Coverage (100X)", TMBSampleQC.Tumor_Coverage_100X});
            tableModel.addRow(new Object[]{"Normal Coverage (100X)", TMBSampleQC.Normal_Coverage_100X});
            tableModel.addRow(new Object[]{"Tumor Breadth Coverage", TMBSampleQC.Tumor_Breadth_Coverage});
            tableModel.addRow(new Object[]{"TiTv Ratio", TMBSampleQC.TiTv_Ratio});
        }

    }
}

