package hmvv.gui.sampleList;

import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.table.*;

import hmvv.gui.GUICommonTools;
import hmvv.io.DatabaseCommands;
import hmvv.main.HMVVFrame;
import hmvv.model.Amplicon;
import hmvv.model.Sample;

import java.awt.*;
import java.util.ArrayList;

public class ViewAmpliconFrame extends JDialog {

	private static final long serialVersionUID = 1L;
	private JLabel patientNameLabel;
	private JLabel ampliconsReportLabel;
	private JLabel totalAmpliconsCountLabel;
	private JLabel failedAmpliconsLabel;
	private JLabel totalAmpliconsLabel;
    private JLabel qcMeasureDescriptionLabel;

	private Sample sample;

	//Table
	private JTable table;
	private ViewAmpliconFrameTableModel tableModel;
	private JScrollPane tableScrollPane;
    private TableRowSorter<ViewAmpliconFrameTableModel> sorter;


	public ViewAmpliconFrame(HMVVFrame parent, Sample sample) throws Exception{
        super(parent, "Sample Amplicons");
		this.sample = sample;

        tableModel = new ViewAmpliconFrameTableModel();

        Rectangle bounds = GUICommonTools.getBounds(parent);
        setSize((int)(bounds.width*.5), (int)(bounds.height*.90));

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        createComponents();
        layoutComponents();
        setLocationRelativeTo(parent);
        buildModelFromDatabase();

        String title = "Sample Amplicons List - " + sample.getLastName() + "," + sample.getFirstName() +
                " (runID = " + sample.runID + ", sampleID = " + sample.sampleID + ")";
        setTitle(title);
	}

	private void createComponents(){

	    table = new JTable(tableModel) {
            private static final long serialVersionUID = 1L;
        };

	    table.setAutoCreateRowSorter(true);
	    sorter = new TableRowSorter<ViewAmpliconFrameTableModel>(tableModel);
        table.setRowSorter(sorter);

        ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();

        tableScrollPane = new JScrollPane();
        tableScrollPane.setViewportView(table);

		failedAmpliconsLabel = new JLabel("Amplicons below cutoff");
		failedAmpliconsLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);

		totalAmpliconsLabel = new JLabel("Total amplicons");
		totalAmpliconsLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);

		patientNameLabel = new JLabel("sample");
		patientNameLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);

		ampliconsReportLabel = new JLabel("below");
		ampliconsReportLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);

		totalAmpliconsCountLabel = new JLabel("total");
		totalAmpliconsCountLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);

        qcMeasureDescriptionLabel = new JLabel("measure");
		qcMeasureDescriptionLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
    }

    private void layoutComponents(){
        GroupLayout gl_contentPane = new GroupLayout(getContentPane());
        gl_contentPane.setHorizontalGroup(gl_contentPane.createSequentialGroup()
                        .addGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(patientNameLabel, GroupLayout.PREFERRED_SIZE, 396, GroupLayout.PREFERRED_SIZE)
						    .addGroup(gl_contentPane.createSequentialGroup()
							    .addComponent(failedAmpliconsLabel, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE)
							    .addGap(18)
							    .addComponent(ampliconsReportLabel))
						    .addGroup(gl_contentPane.createSequentialGroup()
							    .addComponent(totalAmpliconsLabel, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE)
							    .addGap(18)
							    .addComponent(totalAmpliconsCountLabel, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)))
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
						.addComponent(totalAmpliconsLabel, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
						.addComponent(totalAmpliconsCountLabel, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(patientNameLabel, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
                    .addGap(25))
                        .addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 969, Short.MAX_VALUE))
                );
        getContentPane().setLayout(gl_contentPane);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        table.getColumnModel().getColumn(2).setCellRenderer( centerRenderer );

        table.removeColumn(table.getColumnModel().getColumn(0));

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
    private void buildModelFromDatabase() throws Exception{

        ArrayList<Amplicon> amplicons = DatabaseCommands.getAmplicons(sample);
        tableModel.setAmplicons(amplicons);

        String qcMeasureDescription = "";
        if(amplicons.size() > 0){
            qcMeasureDescription = amplicons.get(0).getQCMeasureDescription();
        }
        qcMeasureDescriptionLabel.setText(qcMeasureDescription);
        //TODO add this to the layout

		patientNameLabel.setText(String.format("%s,%s: %s", sample.getLastName(), sample.getFirstName(), sample.getOrderNumber()));
        if(amplicons.size() == 0){
            ampliconsReportLabel.setText("No amplicon data found.");
        }else{
            int failed = 0;
            for(Amplicon amplicon : amplicons){
                if(amplicon.isFailedAmplicon()){
                    failed++;
                }
            }
            ampliconsReportLabel.setText(String.format("%s  [ %.2f %%] ", failed,  failed * 100.0 / amplicons.size()) );
        }
        totalAmpliconsCountLabel.setText(""+amplicons.size() );
	}
}
