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
import hmvv.model.AmpliconCount;
import hmvv.model.Sample;

import java.awt.*;
import java.util.ArrayList;

public class ViewAmpliconFrame extends JDialog {

	private static final long serialVersionUID = 1L;
	private JLabel lblSample;
	private JLabel lblNumber;
	private JLabel lblTotal;
	private JLabel lblAmpliconsBelowCutoff;
	private JLabel lblTotalAmplicons;

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

		lblAmpliconsBelowCutoff = new JLabel("Amplicons below cutoff");
		lblAmpliconsBelowCutoff.setFont(GUICommonTools.TAHOMA_BOLD_14);

		lblTotalAmplicons = new JLabel("Total amplicons");
		lblTotalAmplicons.setFont(GUICommonTools.TAHOMA_BOLD_14);

		lblSample = new JLabel("sample");
		lblSample.setFont(GUICommonTools.TAHOMA_BOLD_14);

		lblNumber = new JLabel("below");
		lblNumber.setFont(GUICommonTools.TAHOMA_BOLD_14);

		lblTotal = new JLabel("total");
		lblTotal.setFont(GUICommonTools.TAHOMA_BOLD_14);
    }

    private void layoutComponents(){
        GroupLayout gl_contentPane = new GroupLayout(getContentPane());
        gl_contentPane.setHorizontalGroup(gl_contentPane.createSequentialGroup()
                        .addGroup(gl_contentPane.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(lblSample, GroupLayout.PREFERRED_SIZE, 396, GroupLayout.PREFERRED_SIZE)
						    .addGroup(gl_contentPane.createSequentialGroup()
							    .addComponent(lblAmpliconsBelowCutoff, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE)
							    .addGap(18)
							    .addComponent(lblNumber))
						    .addGroup(gl_contentPane.createSequentialGroup()
							    .addComponent(lblTotalAmplicons, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE)
							    .addGap(18)
							    .addComponent(lblTotal, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)))
                        .addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 969, Short.MAX_VALUE)
        );

        gl_contentPane.setVerticalGroup(gl_contentPane.createSequentialGroup()
                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(6)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAmpliconsBelowCutoff, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNumber))
					.addGap(4)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTotalAmplicons, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblTotal, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(lblSample, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
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

        ArrayList<Amplicon> amplicons = DatabaseCommands.getFailedAmplicon(sample.sampleID);
        for(Amplicon a : amplicons) {
            tableModel.addAmplicon(a);
        }

		lblSample.setText(String.format("%s,%s: %s", sample.getLastName(), sample.getFirstName(), sample.getOrderNumber()));
		AmpliconCount ampliconCount = DatabaseCommands.getAmpliconCount(sample.sampleID);
		lblNumber.setText(String.format("%s  [ %.2f %%] ", ampliconCount.failedAmplicon, (Float.parseFloat(ampliconCount.failedAmplicon)/Float.parseFloat(ampliconCount.totalAmplicon))*100));
		lblTotal.setText(ampliconCount.totalAmplicon);
	}
}
