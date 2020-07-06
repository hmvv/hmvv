package hmvv.gui.mutationlist.tablemodels;

import hmvv.model.Mutation;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public abstract class CommonTableModelGermline extends AbstractTableModel implements MutationListListener{
	private static final long serialVersionUID = 1L;

	protected MutationListGermline mutationList;
	protected ArrayList<MutationTableModelColumnGermline> columns;

	public CommonTableModelGermline(MutationListGermline mutationList){
		this.mutationList = mutationList;
		mutationList.addListener(this);
		columns = constructColumns();
	}
	
	protected abstract ArrayList<MutationTableModelColumnGermline> constructColumns();
	
	public final Mutation getMutation(int row){
		return mutationList.getMutation(row);
	}
	
	/**
	 * Here we are assuming all tables have the reported field on column 0
	 */
	@Override
	public void setValueAt(Object object, int row, int column){
		if(column == 0){
			mutationList.updateReportedStatus((Boolean)object, row);
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	@Override
	public final int getColumnCount() {
		return columns.size();
	}
	
	@Override
	public final int getRowCount() {
		return mutationList.getMutationCount();
	}
	
	@Override
	public final String getColumnName(int column) {
		return columns.get(column).title;
	}

	@Override
	public  final Object getValueAt(int row, int column) {
		Mutation mutation = mutationList.getMutation(row);
		return columns.get(column).getValue(mutation);
	}
	
	public String getColumnDescription(int column){
		return columns.get(column).description;
	}
	
	@Override
	public final Class<?> getColumnClass(int column) {
		return columns.get(column).columnClass;
	}
	
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
	
	/**
	 * Here we are assuming the tables have the reported field on column 0
	 */
	@Override
	public void mutationReportedStatusChanged(int index){
		fireTableCellUpdated(index, 0);
	}
}
