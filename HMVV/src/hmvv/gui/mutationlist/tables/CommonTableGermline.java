package hmvv.gui.mutationlist.tables;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

import hmvv.gui.BooleanRenderer;
import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.AnnotationFrame;
import hmvv.gui.mutationlist.tablemodels.GermlineCommonTableModel;
import hmvv.io.DatabaseCommands;
import hmvv.io.InternetCommands;
import hmvv.main.Configurations;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.model.*;

public abstract class CommonTableGermline extends JTable{
	private static final long serialVersionUID = 1L;

	protected JDialog parent;
	protected GermlineCommonTableModel model;

	private HMVVTableColumn[] customColumns;

	public CommonTableGermline(JDialog parent, GermlineCommonTableModel model){
		super();
		this.parent = parent;
		this.model = model;
		setModel(model);

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		customColumns = constructCustomColumns();
		formatTable();
		constructListeners();
	}

	//Implement table header tool tips.
	protected JTableHeader createDefaultTableHeader() {
		return new JTableHeader(columnModel) {
			private static final long serialVersionUID = 1L;

			public String getToolTipText(MouseEvent e) {
				int index = table.columnAtPoint(e.getPoint());
				int realIndex = table.convertColumnIndexToModel(index);
				if(realIndex >= 0) {
					return model.getColumnDescription(realIndex);
				}else {
					return "";
				}
			}
		};
	}

	/**
	 * Can be overwritten by subclasses to create different behaviors
	 * @return
	 */
	protected abstract HMVVTableColumn[] constructCustomColumns();

	private void constructListeners(){
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int column = columnAtPoint(e.getPoint());
				int row = rowAtPoint(e.getPoint());
				if(getValueAt(row, column) == null){
					setCursor(HMVVTableColumn.defaultColumn.cursor);
				}
				setCursor(customColumns[column].cursor);
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent c) {
				try{
					int column = columnAtPoint(c.getPoint());
					int row = rowAtPoint(c.getPoint());
					if(getValueAt(row, column) == null){
						return;
					}
					handleMousePressed(column);
				}catch(Exception e){
					HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, e);
				}
			}
		});
		model.addTableModelListener(new ReportedCheckboxChangeListener());
	}

	protected abstract void handleMousePressed(int column) throws Exception;

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
		Component c = super.prepareRenderer(renderer, row, column);

		int modelRow = getRowSorter().convertRowIndexToModel(row);
		int columnStatusPosition = 0;
		Boolean statusColumnValue =  (Boolean)model.getValueAt(modelRow, columnStatusPosition);

		if( (column ==0) && (statusColumnValue) ) {
			c.setBackground(Configurations.TABLE_REPORTED_COLOR);
		}
		else {
			c.setBackground(Color.white);
		}

		if(row == this.getSelectedRow()) {
			setSelectionBackground(Configurations.TABLE_SELECTION_COLOR);
			if (isCellSelected(row, column)){
				if( (column ==0) && (statusColumnValue) ) {
					c.setBackground(Configurations.TABLE_REPORTED_COLOR);
				}
				else {
					c.setBackground(Configurations.TABLE_SELECTION_COLOR);
				}
				c.setForeground(Configurations.TABLE_SELECTION_FONT_COLOR);
			}
			return c;
		}
		c.setForeground(customColumns[column].color);
		return c;
	}

	private void constructRenderers(){
		setDefaultRenderer(Boolean.class, new BooleanRenderer());
		((DefaultTableCellRenderer)getDefaultRenderer(Integer.class)).setHorizontalAlignment(SwingConstants.CENTER);
		((DefaultTableCellRenderer)getDefaultRenderer(Double.class)).setHorizontalAlignment(SwingConstants.CENTER);
		((DefaultTableCellRenderer)getDefaultRenderer(String.class)).setHorizontalAlignment(SwingConstants.CENTER);
	}

	protected final MutationGermline getSelectedMutation(){
		int viewRow = getSelectedRow();
		int modelRow = convertRowIndexToModel(viewRow);
		MutationGermline mutation = model.getMutation(modelRow);
		return mutation;
	}

	public final GermlineCommonTableModel getTableModel(){
		return model;
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return column == 0;
	}

	private void formatTable(){
		setAutoCreateRowSorter(true);
		constructRenderers();
		resizeColumnWidths();
	}

	public void resizeColumnWidths() {
		TableColumnModel columnModel = getColumnModel();
		int buffer = 12;
		if(parent.getWidth() < 1200) {
			buffer = 0;
		}

		for (int column = 0; column < getColumnCount(); column++) {
			TableColumn tableColumn = columnModel.getColumn(column);

			TableCellRenderer headerRenderer = getTableHeader().getDefaultRenderer();
			Component headerComp = headerRenderer.getTableCellRendererComponent(this, tableColumn.getHeaderValue(), false, false, 0, 0);

			int minWidth = headerComp.getPreferredSize().width + buffer;
			int maxWidth = 225;

			int width = minWidth;
			for (int row = 0; row < getRowCount(); row++) {
				TableCellRenderer renderer = getCellRenderer(row, column);
				Component comp = prepareRenderer(renderer, row, column);
				width = Math.max(comp.getPreferredSize().width + buffer , width);
			}
			width = Math.min(maxWidth, width);
			columnModel.getColumn(column).setPreferredWidth(width);
		}
	}

	protected void searchGoogleForGene() throws Exception{
		MutationGermline mutation = getSelectedMutation();
		InternetCommands.searchGene(mutation.getGene());
	}

	protected void searchGoogleForDNAChange() throws Exception{
		MutationGermline mutation = getSelectedMutation();
		String change = mutation.getHGVSc();
		searchGoogleForMutation(mutation, change);
	}

	protected void searchGoogleForProteinChange() throws Exception{
		MutationGermline mutation = getSelectedMutation();
		String change = mutation.getHGVSp();
		searchGoogleForMutation(mutation, change);

		String abbreviatedChange = Configurations.abbreviationtoLetter(change);
		searchGoogleForMutation(mutation, abbreviatedChange);
	}

	protected void searchGoogleForMutation(MutationGermline mutation, String change) throws Exception{
		String gene = mutation.getGene();
		String changeOnly = change.replaceAll(".*:", "");
		String changeFinal = changeOnly.replaceAll(">", "%3E");
		String search = gene + "+" + changeFinal;
		InternetCommands.searchGoogle(search);
	}

	protected void searchClinvarID() throws Exception{
		MutationGermline mutation = getSelectedMutation();
		InternetCommands.searchClinvarID(mutation.getClinvarID());
	}


	protected void handleAnnotationClick() throws Exception{
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		MutationGermline mutation = getSelectedMutation();
		String gene = mutation.getGene();

		ArrayList<GeneAnnotation> geneAnnotationHistory = DatabaseCommands.getGeneAnnotationHistory(gene,Configurations.MUTATION_TYPE.GERMLINE);

		AnnotationFrame editAnnotation = new AnnotationFrame(parent, mutation, geneAnnotationHistory);
		editAnnotation.setVisible(true);
		this.setCursor(Cursor.getDefaultCursor());
	}

	public void notifyAnnotationUpdated(Annotation annotation) {
		int viewRow = getSelectedRow();
		int modelRow = convertRowIndexToModel(viewRow);
		GermlineCommonTableModel model = (GermlineCommonTableModel) getModel();
		model.mutationUpdated(modelRow);
	}

	/**
	 * Here we are assuming all tables have the reported field on column 0
	 */
	private class ReportedCheckboxChangeListener implements TableModelListener{
		@Override
		public void tableChanged(TableModelEvent e) {
			try {
				if(e.getColumn() == 0){
					int row = e.getFirstRow();
					MutationGermline mutation = model.getMutation(row);
					Boolean reported = mutation.isReported();
					Integer sampleID = mutation.getSampleID();
					Coordinate coordinate = mutation.getCoordinate();
					DatabaseCommands.updateReportedStatus(reported, sampleID, coordinate);
				}
			} catch (Exception e1) {
				HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, e1);
			}
		}
	}
}
