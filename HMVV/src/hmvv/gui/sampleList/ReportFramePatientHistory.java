package hmvv.gui.sampleList;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.MutationListFrame;
import hmvv.model.PatientHistory;

public class ReportFramePatientHistory extends ReportFrame{
	private static final long serialVersionUID = 1L;
	
	private Table table;
	private ArrayList<PatientHistory> patientHistory;
	
	public ReportFramePatientHistory(MutationListFrame parent, ArrayList<PatientHistory> patientHistory) {
		super(parent, "Patient History");
		this.patientHistory = patientHistory;
		table = new Table();
		table.setAutoCreateRowSorter(true);
		table.resizeColumnWidths();
		
		constructFrame();
	}	
	
	@Override
	public Component getReport() {
		return new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	@Override
	public String buildTextExtract() {
		StringBuilder sb = new StringBuilder();
		for(PatientHistory historyItem : patientHistory) {
			sb.append("-----------------------" + historyItem.orderNumber + "--------------------------------");
			sb.append("\n");
			
			sb.append("ReportType: " + historyItem.reportType);
			sb.append("\n");
			
			sb.append("ReportNumber: " + historyItem.reportNumber);
			sb.append("\n");
			
			sb.append("ReportRevisionNumber: " + historyItem.reportRevisionNumber);
			sb.append("\n");
						
			sb.append("SignoutDate: " + historyItem.reportSignoutDate);
			sb.append("\n");
			
			sb.append("\n");
			sb.append(historyItem.interpretation);
			sb.append("\n");
			sb.append("\n");
		}
		return sb.toString();
	}
	
	private class Table extends JTable{
		private static final long serialVersionUID = 1L;
		TableModel tableModel;

		public Table() {
			tableModel = new TableModel();
			setModel(tableModel);
			setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			getColumnModel().getColumn(5).setCellRenderer(new WordWrapCellRenderer());

		}

		public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
			Component c = super.prepareRenderer(renderer, row, column);
			if(row == getSelectedRow()) {
				return c;
			}
			if ((row+1) % 2 == 0) {
				c.setBackground(GUICommonTools.LIGHT_GRAY);
			}else {
				c.setBackground(GUICommonTools.WHITE_COLOR);
			}
			return c;
		}

		private void resizeColumnWidths() {    
			int buffer = 12;

			for (int column = 0; column < getColumnCount(); column++) {
				TableColumn tableColumn = table.getColumnModel().getColumn(column);

				TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
				Component headerComp = headerRenderer.getTableCellRendererComponent(table, tableColumn.getHeaderValue(), false, false, 0, 0);

				int minWidth = headerComp.getPreferredSize().width + buffer;
		    	int maxWidth = 150;
		    	
		        int width = minWidth;
		        for (int row = 0; row < getRowCount(); row++) {
		            TableCellRenderer renderer = getCellRenderer(row, column);
		            Component comp = prepareRenderer(renderer, row, column);
		            width = Math.max(comp.getPreferredSize().width + buffer , width);
		        }
		        width = Math.min(maxWidth, width);
		        columnModel.getColumn(column).setPreferredWidth(width);
		    }
		    columnModel.getColumn(5).setPreferredWidth(635);
		}
	}
	
	class WordWrapCellRenderer extends JTextArea implements TableCellRenderer {
		private static final long serialVersionUID = 1L;

		WordWrapCellRenderer() {
	        setLineWrap(true);
	        setWrapStyleWord(true);
	    }

	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    	
	    	
	    	if ((row+1) % 2 == 0) {
				setBackground(GUICommonTools.LIGHT_GRAY);
			}else {
				setBackground(GUICommonTools.WHITE_COLOR);
			}
	    	
	    	if(value != null) {
				setText(value.toString());
			}
			
	        setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
	        if (table.getRowHeight(row) != getPreferredSize().height) {
	            table.setRowHeight(row, getPreferredSize().height);
	        }
	        return this;
	    }
	}
	
	private class TableModel extends AbstractTableModel{
		private static final long serialVersionUID = 1L;

		@Override
		public int getColumnCount() {
			return 6;
		}

		@Override
		public int getRowCount() {
			return patientHistory.size();
		}

		public String getColumnName(int column) {
			switch(column) {
				case 0: return "OrderNumber";
				case 1: return "ReportType";
				case 2: return "ReportNumber";
				case 3: return "RevisionNumber";
				case 4: return "SignoutDate";
				case 5: return "Interpretation";
				default: return "";
			}
	    }
		
		public Class<?> getColumnClass(int column) {
			switch(column) {
				case 0: return String.class;
				case 1: return String.class;
				case 2: return String.class;
				case 3: return String.class;
				case 4: return Timestamp.class;
				case 5: return String.class;
				default: return String.class;
			}
	    }
		
		@Override
		public Object getValueAt(int row, int column) {
			switch(column) {
				case 0: return patientHistory.get(row).orderNumber;
				case 1: return patientHistory.get(row).reportType;
				case 2: return patientHistory.get(row).reportNumber;
				case 3: return patientHistory.get(row).reportRevisionNumber;
				case 4: return patientHistory.get(row).reportSignoutDate;
				case 5: return patientHistory.get(row).interpretation;
				default: return "";
			}
		}
	}
}
