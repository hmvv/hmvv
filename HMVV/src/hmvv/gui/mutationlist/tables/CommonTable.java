package hmvv.gui.mutationlist.tables;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import hmvv.gui.BooleanRenderer;
import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.AnnotationFrame;
import hmvv.gui.mutationlist.MutationListFrame;
import hmvv.gui.mutationlist.tablemodels.CommonTableModel;
import hmvv.io.DatabaseCommands;
import hmvv.io.InternetCommands;
import hmvv.io.SSHConnection;
import hmvv.model.Annotation;
import hmvv.model.Coordinate;
import hmvv.model.GeneAnnotation;
import hmvv.model.Mutation;

public abstract class CommonTable extends JTable{
	private static final long serialVersionUID = 1L;
	
	protected MutationListFrame parent;
	protected CommonTableModel model;
	
	private HMVVTableColumn[] customColumns;
	
	public CommonTable(MutationListFrame parent, CommonTableModel model){
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
			public void mouseClicked(MouseEvent c) {
				try{
					int column = columnAtPoint(c.getPoint());
					int row = rowAtPoint(c.getPoint());
					if(getValueAt(row, column) == null){
						return;
					}
					handleMouseClick(column);
				}catch(Exception e){
					JOptionPane.showMessageDialog(CommonTable.this, e.getMessage());
				}
			}
		});
		model.addTableModelListener(new ReportedCheckboxChangeListener());
	}
	
	protected abstract void handleMouseClick(int column) throws Exception;
	
	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
		Component c = super.prepareRenderer(renderer, row, column);
		c.setForeground(customColumns[column].color);
		return c;
	}
	
	private void constructRenderers(){
		setDefaultRenderer(Boolean.class, new BooleanRenderer());
	}
	
	protected final Mutation getSelectedMutation(){
		int viewRow = getSelectedRow();
		int modelRow = convertRowIndexToModel(viewRow);
		Mutation mutation = model.getMutation(modelRow);
		return mutation;
	}
	
	public final CommonTableModel getTableModel(){
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
	
	protected void searchGoogleForGene(){
		int viewRow = getSelectedRow();
		int modelRow = convertRowIndexToModel(viewRow);
		String gene = (getModel().getValueAt(modelRow, 1)).toString();
		InternetCommands.searchGoogle(gene);
	}

	protected void searchGoogleForDNAChange(){
		Mutation mutation = getSelectedMutation();
		String change = mutation.getHGVSc();
		searchGoogleForMutation(mutation, change);
	}

	protected void searchGoogleForProteinChange(){
		Mutation mutation = getSelectedMutation();
		String change = mutation.getHGVSp();
		searchGoogleForMutation(mutation, change);
		
		String abbreviatedChange = abbreviationtoLetter(change);
		searchGoogleForMutation(mutation, abbreviatedChange);
	}
	
	protected void searchGoogleForMutation(Mutation mutation, String change){
		String gene = mutation.getGene();
		String changeOnly = change.replaceAll(".*:", "");
		String changeFinal = changeOnly.replaceAll(">", "%3E");
		String search = gene + "+" + changeFinal;
		InternetCommands.searchGoogle(search);
	}
	
	protected void searchSNP(){
		Mutation mutation = getSelectedMutation();
		String dbSNP = mutation.getDbSNPID();
		if(!dbSNP.equals("")){
			InternetCommands.searchSNP(dbSNP);
		}
	}

	protected void searchCosmic(){
		Mutation mutation = getSelectedMutation();
		ArrayList<String> cosmic = mutation.getCosmicID();
		if(cosmic.size() > 0){
			InternetCommands.searchCosmic(cosmic);			
		}
	}
	
	private String abbreviationtoLetter(String mutation){
		return mutation
			.replaceAll("Ala", "A")
			.replaceAll("Cys", "C")
			.replaceAll("Glu", "E")
			.replaceAll("Phe", "F")
			.replaceAll("Gly", "G")
			.replaceAll("His", "H")
			.replaceAll("Ile", "I")
			.replaceAll("Lys", "K")
			.replaceAll("Leu", "L")
			.replaceAll("Met", "M")
			.replaceAll("Asn", "N")
			.replaceAll("Hyp", "O")
			.replaceAll("Pro", "P")
			.replaceAll("Gln", "Q")
			.replaceAll("Arg", "R")
			.replaceAll("Ser", "S")
			.replaceAll("Thr", "T")
			.replaceAll("Glp", "U")
			.replaceAll("Val", "V")
			.replaceAll("Trp", "W")
			.replaceAll("Ter", "X")
			.replaceAll("Tyr", "Y");
	}
	

	protected void handleAnnotationClick() throws Exception{
		Mutation mutation = getSelectedMutation();
		Annotation annotation = mutation.getAnnotationObject();
		
		String gene = mutation.getGene();
		GeneAnnotation geneAnnotation = DatabaseCommands.getGeneAnnotation(gene);
		
		Boolean annotationAlreadyOpen = false;
		if(annotation.getEditStatus().equals(Annotation.STATUS.open) || geneAnnotation.isLocked()){
			int selectionValue = JOptionPane.showConfirmDialog(this, "This annotation is currently locked. Would you like to unlock it?");
			if(selectionValue == JOptionPane.CANCEL_OPTION) {
				return;
			}else if(selectionValue == JOptionPane.YES_OPTION) {
				annotationAlreadyOpen = false;
			}else if(selectionValue == JOptionPane.NO_OPTION) {
				annotationAlreadyOpen = true;
			}
		}
		
		boolean readOnly = annotationAlreadyOpen || !SSHConnection.isSuperUser();
		AnnotationFrame editAnnotation = new AnnotationFrame(readOnly, mutation, geneAnnotation, annotation, this, parent);
		editAnnotation.setVisible(true);
	}
	
	public void notifyAnnotationUpdated(Annotation annotation) {
		int viewRow = getSelectedRow();
		int modelRow = convertRowIndexToModel(viewRow);
		CommonTableModel model = (CommonTableModel)getModel();
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
					Mutation mutation = model.getMutation(row);
					Boolean reported = mutation.isReported();
					Integer sampleID = mutation.getSampleID();
					Coordinate coordinate = mutation.getCoordinate();
					DatabaseCommands.updateReportedStatus(reported, sampleID, coordinate);
				}
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(CommonTable.this, e1);
			}
		}
	}
}
