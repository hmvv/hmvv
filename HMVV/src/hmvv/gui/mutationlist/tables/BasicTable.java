package hmvv.gui.mutationlist.tables;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;

import hmvv.gui.mutationlist.AnnotationFrame;
import hmvv.gui.mutationlist.MutationTraceFrame;
import hmvv.gui.mutationlist.tablemodels.BasicTableModel;
import hmvv.io.DatabaseCommands;
import hmvv.io.InternetCommands;
import hmvv.io.SSHConnection;
import hmvv.model.Annotation;
import hmvv.model.Coordinate;
import hmvv.model.Mutation;

public class BasicTable extends CommonTable{
	private static final long serialVersionUID = 1L;
	
	public BasicTable(BasicTableModel model){
		super(model);
		constructRenderers();
		constructListeners();
	}

	private void constructRenderers(){
		TableCellRenderer renderer = new TableCellRenderer();
		setDefaultRenderer(Object.class, renderer);
		setDefaultRenderer(Integer.class, renderer);
	}
	
	private void constructListeners(){
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent c) {
				try{
					handleMouseClick(c.getPoint());
				}catch(Exception e){
					e.printStackTrace();
					JOptionPane.showMessageDialog(BasicTable.this, e.getMessage());
				}
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				handleMouseMoved(e.getPoint());
			}			
		});
		
		model.addTableModelListener(new ReportedCheckboxChangeListener());
	}
	
	@Override 
	public boolean isCellEditable(int row, int column) {
		return column == 0;
	}

	private void handleMouseMoved(Point p) {
		if((columnAtPoint (p) == 1) || (columnAtPoint (p) == 3) || (columnAtPoint (p) == 4) 
				|| (columnAtPoint (p) == 5) || (columnAtPoint (p) == 6) || (columnAtPoint (p) == 12) || (columnAtPoint (p) == 13)){
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}else{
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	private void handleMouseClick(Point pClick) throws Exception{
		if(columnAtPoint (pClick) == 1){
			searchGoogleForGene();
		}else if(columnAtPoint (pClick) == 3){
			searchGoogleForProteinChange();
			searchGoogleForDNAChange();
		}else if(columnAtPoint (pClick) == 4){
			searchGoogleForDNAChange();
			searchGoogleForProteinChange();
		}else if(columnAtPoint (pClick) == 5){
			searchSNP();
		}else if(columnAtPoint (pClick) == 6){
			searchCosmic();
		}else if(columnAtPoint (pClick) == 12){
			handFindSimilarSamples();
		}else if(columnAtPoint (pClick) == 13){
			handleAnnotationClick();
		}
	}

	private class TableCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if ((column == 1) || (column == 3) || (column == 4) || (column == 5) || (column == 6) || (column == 12) || (column == 13) ) {
				c.setForeground(Color.BLUE);
			}else{
				c.setForeground(Color.BLACK);
			}
			return c;
		}
	}

	private void searchGoogleForGene(){
		int viewRow = getSelectedRow();
		int modelRow = convertRowIndexToModel(viewRow);
		String gene = (getModel().getValueAt(modelRow, 1)).toString();
		InternetCommands.searchGoogle(gene);
	}

	private void searchGoogleForDNAChange(){
		Mutation mutation = getSelectedMutation();
		String change = mutation.getValue("HGVSc").toString();
		searchGoogleForMutation(mutation, change);
	}

	private void searchGoogleForProteinChange(){
		Mutation mutation = getSelectedMutation();
		String change = mutation.getValue("HGVSp").toString();
		searchGoogleForMutation(mutation, change);
		
		String abbreviatedChange = abbreviationtoLetter(change);
		searchGoogleForMutation(mutation, abbreviatedChange);
	}
	
	private void searchGoogleForMutation(Mutation mutation, String change){
		String gene = mutation.getValue("gene").toString();
		String changeOnly = change.replaceAll(".*:", "");
		String changeFinal = changeOnly.replaceAll(">", "%3E");
		String search = gene + "+" + changeFinal;
		InternetCommands.searchGoogle(search);
	}

	private void searchSNP(){
		Mutation mutation = getSelectedMutation();
		String dbSNP = mutation.getValue("dbSNPID").toString();
		if(!dbSNP.equals("")){
			InternetCommands.searchSNP(dbSNP);
		}
	}

	private void searchCosmic(){
		Mutation mutation = getSelectedMutation();
		String cosmic = mutation.getValue("cosmicID").toString();
		if(!cosmic.equals("")){
			InternetCommands.searchCosmic(cosmic);
		}
	}

	private void handFindSimilarSamples() throws Exception{
		//search for previous samples with this mutation
		Mutation mutation = getSelectedMutation();

		ArrayList<Mutation> mutations = DatabaseCommands.getMatchingMutations(mutation);
		MutationTraceFrame mutationTrace = new MutationTraceFrame(this, mutations, "Mutation Trace");
		mutationTrace.setVisible(true);
	}

	private void handleAnnotationClick() throws Exception{
		Mutation mutation = getSelectedMutation();
		String chr = mutation.getValue("chr").toString();
		String pos = mutation.getValue("pos").toString();
		String ref = mutation.getValue("ref").toString();
		String alt = mutation.getValue("alt").toString();
		Coordinate coordinate = new Coordinate(chr, pos, ref, alt);
		
		Annotation annotation = DatabaseCommands.getAnnotation(coordinate);

		Boolean annotationAlreadyOpen = false;
		if(annotation.getEditStatus().equals(Annotation.STATUS.open)){
			annotationAlreadyOpen = true;
			//TODO consider allowing user to override the lock in situations where the previous user didn't properly release the lock
			JOptionPane.showMessageDialog(BasicTable.this, "You or someone else is working on this mutation, open in read only mode");
		}
		
		boolean readOnly = annotationAlreadyOpen || !SSHConnection.isSuperUser();
		AnnotationFrame editAnnotation = new AnnotationFrame(readOnly, annotation, this);
		editAnnotation.setVisible(true);
	}

	private class ReportedCheckboxChangeListener implements TableModelListener{
		@Override
		public void tableChanged(TableModelEvent e) {
			try {
				int row = e.getFirstRow();
				Mutation mutation = model.getMutation(row);
				
				Boolean reported = (Boolean)mutation.getValue("reported");
				String sampleID = mutation.getValue("sampleID").toString();
				String chr = mutation.getValue("chr").toString();
				String pos = mutation.getValue("pos").toString();
				String ref = mutation.getValue("ref").toString();
				String alt = mutation.getValue("alt").toString();
				Coordinate coordinate = new Coordinate(chr, pos, ref, alt);
				DatabaseCommands.updateReportedStatus(reported, sampleID, coordinate);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(BasicTable.this, e1);
			}
		}
	}

	public void notifyAnnotationUpdated(Annotation annotation) {
		int viewRow = getSelectedRow();
		int modelRow = convertRowIndexToModel(viewRow);
		BasicTableModel model = (BasicTableModel)getModel();
		if(annotation.isAnnotationSet()){
			model.updateAnnotationText("Annotation", modelRow);
		}else{
			model.updateAnnotationText("Enter", modelRow);
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
}
