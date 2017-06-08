package hmvv.gui.mutationlist.tables;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import hmvv.gui.mutationlist.tablemodels.ClinVarTableModel;
import hmvv.io.InternetCommands;
import hmvv.model.Mutation;

public class ClinVarTable extends CommonTable{
	private static final long serialVersionUID = 1L;
	
	public ClinVarTable(ClinVarTableModel model){
		super(model);
		setDefaultRenderer(Object.class, new TableCellRenderer());
		constructMouseListener();
	}

	private void constructMouseListener() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent c) {
				Point pClick = c.getPoint();
				if(columnAtPoint (pClick) == 8){
					searchClinvar();
				}else if(columnAtPoint (pClick) == 9){
					searchPubmed();
				}
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				Point p = e.getPoint();
				if((columnAtPoint (p) == 8) || (columnAtPoint (p) == 9)){
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}else{
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		});
	}
	
	private void searchClinvar(){
		Mutation mutation = getSelectedMutation();
		String clinvar = mutation.getValue("clinicalAcc").toString();
		if(!clinvar.equals("") && !clinvar.equals("null")){
			InternetCommands.searchClinvar(clinvar);
		}
	}
	
	private void searchPubmed(){
		Mutation mutation = getSelectedMutation();
		String pubmed = mutation.getValue("pubmed").toString();
		if(!pubmed.equals("") && !pubmed.equals("null")){
			InternetCommands.searchPubmed(pubmed);
		}
	}

	private class TableCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,row, column);
			if ((column == 8) || (column == 9) ) {
				c.setForeground(Color.blue);
			}else{
				c.setForeground(Color.black);
			}
			return c;
		}
	}
}
