package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import hmvv.model.Mutation;

public abstract class CommonTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	protected ArrayList<Mutation> mutations;

	public CommonTableModel(ArrayList<Mutation> mutations){
		this.mutations = mutations;
	}

	public abstract String[] getColumnNames();
	
	public final Mutation getMutation(int row){
		return mutations.get(row);
	}
	
	public final void updateMutationData(int row, String key, Object value){
		mutations.get(row).addData(key, value);
		String[] columnNames = getColumnNames();
		for(int column = 0; column < columnNames.length; column++){
			if(columnNames[column].equals(key)){
				fireTableCellUpdated(row, column);
			}
		}		
	}
	
	@Override 
	public final boolean isCellEditable(int row, int column) {
		return false;
	}
	
	@Override
	public final String getColumnName(int column) {
		return getColumnNames()[column];
	}

	@Override
	public final int getColumnCount() {
		return getColumnNames().length;
	}

	@Override
	public final int getRowCount() {
		return mutations.size();
	}

	@Override
	public final Object getValueAt(int row, int column) {
		return mutations.get(row).getValue(getColumnName(column));
	}

}
