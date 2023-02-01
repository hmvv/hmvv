package hmvv.gui.mutationlist;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.tablemodels.GermlineHGMDGeneLevelMutationsTableModel;
import hmvv.gui.mutationlist.tablemodels.GermlineHGMDGeneLevelSummaryTableModel;
import hmvv.io.DatabaseCommands;
import hmvv.io.InternetCommands;
import hmvv.main.Configurations;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.model.MutationGermline;
import hmvv.model.MutationGermlineHGMD;
import hmvv.model.MutationGermlineHGMDGeneLevel;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;


public class MutationGermlineHGMDGeneFrame extends JDialog {
	private static final long serialVersionUID = 1L;

	public final JDialog parent;
	private MutationGermline mutation;

	private GermlineHGMDGeneLevelSummaryTableModel summaryTableModel;
	private JTable summaryTable;
	private JScrollPane summaryScrollPane;

	private GermlineHGMDGeneLevelMutationsTableModel mutationTableModel;
	private JTable mutationTable;
	private JScrollPane mutationScrollPane;
	private TableRowSorter<GermlineHGMDGeneLevelMutationsTableModel> mutationsSorter;

	public MutationGermlineHGMDGeneFrame(JDialog parent, MutationGermline mutation) throws Exception {
		super(parent, "Title Set Later", Dialog.ModalityType.APPLICATION_MODAL);
		String title = "Annotation Draft";
		setTitle(title);

		this.parent = parent;
		this.mutation = mutation;

		constructComponents();
		layoutComponents();

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*.90), (int)(bounds.height*.75));
		setMinimumSize(new Dimension(500, getHeight()/2));

		setLocationRelativeTo(parent);
		setAlwaysOnTop(false);

	}

	
	private void constructComponents() throws Exception {

		ArrayList<MutationGermlineHGMDGeneLevel> genelevel_mutations_summary = DatabaseCommands.getMutationSummaryForGene(mutation.getGene());
		summaryTableModel = new GermlineHGMDGeneLevelSummaryTableModel(genelevel_mutations_summary);
		summaryTable = new JTable(summaryTableModel);
		summaryTableRenderer();
		summaryTableConstructListeners();

		ArrayList<MutationGermlineHGMD> mutations = DatabaseCommands.getAllMutationsByGene(mutation.getGene());
		mutationTableModel = new GermlineHGMDGeneLevelMutationsTableModel(mutations);
		mutationTable = new JTable(mutationTableModel);
		mutationTableRenderer();
		mutationTableConstructListeners();
		mutationTable.setAutoCreateRowSorter(true);
		mutationsSorter = new TableRowSorter<>(mutationTableModel);
		mutationTable.setRowSorter(mutationsSorter);

        ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        mutationsSorter.setSortKeys(sortKeys);
        mutationsSorter.sort();


	}

	private void summaryTableRenderer(){

		summaryTable.setDefaultRenderer(Object.class, new TableCellRenderer(){
			private DefaultTableCellRenderer DEFAULT_RENDERER =  new DefaultTableCellRenderer();

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = DEFAULT_RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				DEFAULT_RENDERER.setHorizontalAlignment(JLabel.CENTER);

				if(column==0){
					c.setForeground(Color.BLUE);
				} else {
					c.setForeground(Color.BLACK);
				}

				if (isSelected){
					table.setSelectionBackground(Configurations.TABLE_SELECTION_COLOR);
				}
				return c;
			}

		});

		((DefaultTableCellRenderer)summaryTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
		((DefaultTableCellRenderer)summaryTable.getDefaultRenderer(Integer.class)).setHorizontalAlignment(SwingConstants.CENTER);
		resizeColumnWidths(summaryTable);
	}

	private void summaryTableConstructListeners(){
		summaryTable.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int column = summaryTable.columnAtPoint(e.getPoint());
				if(column == 0){
					summaryTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}else{
					summaryTable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}

			}
		});
		summaryTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent c) {
				try{
					int column = summaryTable.columnAtPoint(c.getPoint());
					int row = summaryTable.rowAtPoint(c.getPoint());
					if(column == 0){
						handleMousePressedSummaryTable((String)summaryTable.getValueAt(row,column));
					}

				}catch(Exception e){
					HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, e);
				}
			}
		});

	}

	private void mutationTableRenderer(){
		mutationTable.setDefaultRenderer(Object.class, new TableCellRenderer(){
			private DefaultTableCellRenderer DEFAULT_RENDERER_mutation =  new DefaultTableCellRenderer();

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = DEFAULT_RENDERER_mutation.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				DEFAULT_RENDERER_mutation.setHorizontalAlignment(JLabel.CENTER);

				if(column == 2 || column == 7){
					c.setForeground(Color.BLUE);
				} else {
					c.setForeground(Color.BLACK);
				}

				if (isSelected){
					table.setSelectionBackground(Configurations.TABLE_SELECTION_COLOR);
				}
				return c;
			}

		});

		((DefaultTableCellRenderer)mutationTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
		((DefaultTableCellRenderer)mutationTable.getDefaultRenderer(Integer.class)).setHorizontalAlignment(SwingConstants.CENTER);
		resizeColumnWidths(mutationTable);
	}

	private void mutationTableConstructListeners(){
		mutationTable.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int column = mutationTable.columnAtPoint(e.getPoint());
				if(column == 2 || column == 7){
					mutationTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}else{
					mutationTable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}

			}
		});
		mutationTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent c) {
				try{
					int column = mutationTable.columnAtPoint(c.getPoint());
					int row = mutationTable.rowAtPoint(c.getPoint());
					if(column == 2){
						handleMousePressedMutationTableHGMDID((String)mutationTable.getValueAt(row,column));
					} else if(column == 7){
						int viewRow = mutationTable.getSelectedRow();
						int modelRow = mutationTable.convertRowIndexToModel(viewRow);
						handleMousePressedMutationTableHGMDCitation(mutationTableModel.getMutationAt(modelRow).getPmid());
					}

				}catch(Exception e){
					HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, e);
				}
			}
		});

	}
	private void layoutComponents() throws Exception {

		JPanel mainPanel = new JPanel();

		summaryScrollPane = new JScrollPane(summaryTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		summaryScrollPane.setViewportView(summaryTable);
		summaryScrollPane.setPreferredSize(new Dimension(300,203));
		summaryScrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		JPanel leftPanel = new JPanel();
		leftPanel.setPreferredSize(new Dimension(310,210));
		leftPanel.add(summaryScrollPane);
		mainPanel.add(leftPanel);

		mutationScrollPane = new JScrollPane(mutationTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mutationScrollPane.setViewportView(mutationTable);
		mutationScrollPane.setPreferredSize(new Dimension(1500,390));
		mutationScrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		JPanel rightPanel = new JPanel();
		rightPanel.setPreferredSize(new Dimension(1510,400));
		rightPanel.add(mutationScrollPane);
		mainPanel.add(rightPanel);
		add(mainPanel);

	}

	protected void handleMousePressedSummaryTable(String table) throws Exception{
 	    refilterTable(table);
	}

	protected  void handleMousePressedMutationTableHGMDID(String hgmd_id) throws Exception {
		if(!hgmd_id.equals("") && !hgmd_id.equals("null")){
			InternetCommands.searchGoogleHGMD(mutation.getGene(),hgmd_id);
		}
	}


	protected  void handleMousePressedMutationTableHGMDCitation(String pmid) throws Exception {
				if (!pmid.equals("") && !pmid.equals("null")) {
					InternetCommands.searchPubmed(pmid);
				}
	}

	private void resizeColumnWidths(JTable table) {
		final TableColumnModel columnModel = table.getColumnModel();
		for (int column = 0; column < table.getColumnCount(); column++) {
			int width = 15; // Min width
			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer renderer = table.getCellRenderer(row, column);
				Component comp = table.prepareRenderer(renderer, row, column);
				width = Math.max(comp.getPreferredSize().width + 1, width);
			}
			if (width > 300)
				width = 300;
			columnModel.getColumn(column).setPreferredWidth(width);
		}
	}

	public void refilterTable(String tablename){

		RowFilter<GermlineHGMDGeneLevelMutationsTableModel, Integer> rowFilter = new RowFilter<GermlineHGMDGeneLevelMutationsTableModel, Integer>(){

			private boolean doesMutationTableMatch(Entry<? extends GermlineHGMDGeneLevelMutationsTableModel, ? extends Integer> entry){

                if (tablename.equals("All")){
                    return true;
                }else {
                    int row = entry.getIdentifier();
                    MutationGermlineHGMD sample = mutationTableModel.getMutationAt(row);
                    return sample.getMutation_type().equals(tablename.toLowerCase());
                }
			}

			@Override
			public boolean include(Entry<? extends GermlineHGMDGeneLevelMutationsTableModel, ? extends Integer> entry) {
				return doesMutationTableMatch(entry);
			}
		};
		mutationsSorter.setRowFilter(rowFilter);
	}
}


