package hmvv.gui.mutationlist.tablemodels;

import javax.swing.table.AbstractTableModel;
import hmvv.model.Mutation;

public abstract class CommonTableModel extends AbstractTableModel implements MutationListListener{
	private static final long serialVersionUID = 1L;

	protected MutationList mutationList;

	public CommonTableModel(MutationList mutationList){
		this.mutationList = mutationList;
		mutationList.addListener(this);
	}
	
	public final Mutation getMutation(int row){
		return mutationList.getMutation(row);
	}
	
	public void updateAnnotationText(String text, int row){
		mutationList.updateAnnotationText(text, row);
	}
	
	public void updateReportedStatus(boolean reported, int row){
		mutationList.updateReportedStatus(reported, row);
	}
	
	/**
	 * Here we are assuming all tables have the reported field on column 0
	 */
	@Override
	public void setValueAt(Object object, int row, int column){
		if(column == 0){
			updateReportedStatus((Boolean)object, row);
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
		return mutationList.getMutationCount();
	}

	protected abstract String[] getColumnNames();
	
	@Override
	public abstract Class<?> getColumnClass(int column);
	
	@Override
	public abstract Object getValueAt(int row, int column);
	
	@Override
	public void mutationUpdated(int index){
		fireTableRowsUpdated(index, index);
	}
	
	@Override
	public void mutationListStructureChanged(){
		fireTableStructureChanged();
	}
	
	@Override
	public void mutationDataChanged(){
		fireTableDataChanged();
	}
}
