package hmvv.gui.mutationlist.tablemodels;

import hmvv.model.MutationGermline;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public abstract class GermlineCommonTableModel extends AbstractTableModel implements MutationListListener{
	private static final long serialVersionUID = 1L;

	protected MutationList mutationList;
	protected ArrayList<GermlineMutationTableModelColumn> columns;

	public GermlineCommonTableModel(MutationList mutationList){
		this.mutationList = mutationList;
		mutationList.addListenerGermline(this);
		columns = constructColumns();
	}
	
	protected abstract ArrayList<GermlineMutationTableModelColumn> constructColumns();
	
	public final MutationGermline getMutation(int row){
		return (MutationGermline)mutationList.getMutation(row);
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
		MutationGermline mutation = (MutationGermline)mutationList.getMutation(row);
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
