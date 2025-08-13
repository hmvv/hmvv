package hmvv.gui.sampleList;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.*;

import hmvv.gui.GUICommonTools;
import hmvv.io.DatabaseCommands;
import hmvv.model.GeneTargetQC;
import hmvv.model.Sample;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ViewGeneTargetFrame extends JDialog {

	private static final long serialVersionUID = 1L;

    private Window parent;

	private JLabel patientNameLabel;
	private JLabel ampliconsReportLabel;
	private JLabel totalGenesCountLabel;
	private JLabel failedAmpliconsLabel;
	private JLabel totalGenesLabel;
    private JLabel qcMeasureDescriptionLabel;

	private Sample sample;

	//Table
	private JTable table;
	private ViewGeneTargetFrameTableModel tableModel;
	private JScrollPane tableScrollPane;
    private TableRowSorter<ViewGeneTargetFrameTableModel> sorter;


	public ViewGeneTargetFrame(Window parent, Sample sample) throws Exception{
        super(parent, "Sample Gene Targets", ModalityType.APPLICATION_MODAL);
        this.parent = parent;
		this.sample = sample;

        tableModel = new ViewGeneTargetFrameTableModel(sample);

        Rectangle bounds = GUICommonTools.getBounds(parent);
        setSize((int)(bounds.width*.90), (int)(bounds.height*.90));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        createComponents();
        layoutComponents();
        activateComponents();
        setLocationRelativeTo(this.parent);
        buildModelFromDatabase();
        

        String title = "Sample Gene Targets List - " + sample.getLastName() + "," + sample.getFirstName() +
                " (runID = " + sample.runID + ", sampleID = " + sample.sampleID + ")";
        setTitle(title);

	}

	private void createComponents(){

	    table = new JTable(tableModel) {
            private static final long serialVersionUID = 1L;
        };

	    table.setAutoCreateRowSorter(true);
	    sorter = new TableRowSorter<ViewGeneTargetFrameTableModel>(tableModel);
        table.setRowSorter(sorter);

        ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();

        tableScrollPane = new JScrollPane();
        tableScrollPane.setViewportView(table);

		failedAmpliconsLabel = new JLabel("");
		failedAmpliconsLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);

		totalGenesLabel = new JLabel("Number of Genes:");
		totalGenesLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);

		patientNameLabel = new JLabel(String.format(" %s,%s: %s", sample.getLastName(), sample.getFirstName(), sample.getOrderNumber()));
		patientNameLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);

		ampliconsReportLabel = new JLabel("");
		ampliconsReportLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);

		totalGenesCountLabel = new JLabel("");
		totalGenesCountLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);

        qcMeasureDescriptionLabel = new JLabel("QC Measure: % of Targets Passing Threshold");
		qcMeasureDescriptionLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
    }

    private void layoutComponents(){
        GroupLayout gl_contentPane = new GroupLayout(getContentPane());
        gl_contentPane.setHorizontalGroup(gl_contentPane.createSequentialGroup()
                        .addGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(patientNameLabel, GroupLayout.PREFERRED_SIZE, 396, GroupLayout.PREFERRED_SIZE)
                            .addComponent(qcMeasureDescriptionLabel, GroupLayout.PREFERRED_SIZE, 396, GroupLayout.PREFERRED_SIZE)
                            //.addComponent(ampliconDepthButton, GroupLayout.PREFERRED_SIZE, 396, GroupLayout.PREFERRED_SIZE)
						    .addGroup(gl_contentPane.createSequentialGroup()
							    .addComponent(failedAmpliconsLabel, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE)
							    .addGap(18)
							    .addComponent(ampliconsReportLabel))
						    .addGroup(gl_contentPane.createSequentialGroup()
							    .addComponent(totalGenesLabel, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE)
							    .addGap(18)
							    .addComponent(totalGenesCountLabel, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)))
                        .addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 969, Short.MAX_VALUE)
        );

        gl_contentPane.setVerticalGroup(gl_contentPane.createSequentialGroup()
                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(6)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(failedAmpliconsLabel, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
						.addComponent(ampliconsReportLabel))
					.addGap(4)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(totalGenesLabel, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
						.addComponent(totalGenesCountLabel, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(patientNameLabel, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
                    .addGap(18)
                    .addComponent(qcMeasureDescriptionLabel, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
                    .addGap(18)
                    //.addComponent(ampliconDepthButton, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
                    .addGap(25))
                    .addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 969, Short.MAX_VALUE))
                );
        getContentPane().setLayout(gl_contentPane);

        table.getColumnModel().getColumn(1).setCellRenderer(new CustomRenderer());
        table.getColumnModel().getColumn(2).setCellRenderer(new CustomRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new CustomRenderer());

        resizeColumnWidths();
    }

    private void resizeColumnWidths() {
        TableColumnModel columnModel = table.getColumnModel();

        for (int column = 0; column < table.getColumnCount(); column++) {
            TableColumn tableColumn = columnModel.getColumn(column);

            TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(table, tableColumn.getHeaderValue(), false, false, 0, 0);

            int minWidth = headerComp.getPreferredSize().width;
            int maxWidth = 150;

            int width = minWidth;
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 25 , width);
            }
            width = Math.min(maxWidth, width);
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

    private void activateComponents(){
    }

    private void buildModelFromDatabase() throws Exception{
        ArrayList<GeneTargetQC> geneTargets = DatabaseCommands.getGeneTargetQCData(sample);
        totalGenesCountLabel.setText(""+geneTargets.size() );
        tableModel.setGeneTargetQCs(geneTargets);
	}

    

    class CustomRenderer extends DefaultTableCellRenderer {
        private final DecimalFormat df = new DecimalFormat("0.0");

        CustomRenderer(){
            setHorizontalAlignment( JLabel.CENTER );
        }

        @Override
        public Component getTableCellRendererComponent(JTable itable, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel oLabel = (JLabel) super.getTableCellRendererComponent(itable, value, isSelected, hasFocus, row, column);
            if (value instanceof Double) {
                oLabel.setText(df.format(value) + "%");
            }
            return oLabel;
        }
    }
}
