package hmvv.gui.mutationlist.tables;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import hmvv.gui.mutationlist.BooleanRenderer;
import hmvv.gui.mutationlist.tablemodels.CommonTableModel;
import hmvv.model.Mutation;

public abstract class CommonTable extends JTable{
	private static final long serialVersionUID = 1L;
	
	protected CommonTableModel model;
	
	public CommonTable(CommonTableModel model){
		super();
		setModel(model);
		this.model = model;
		
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		boolean canEditFirstColumn = isCellEditable(0, 0);
		setDefaultRenderer(Boolean.class, new BooleanRenderer(canEditFirstColumn));
		
		formatTable();
	}
	
	protected final Mutation getSelectedMutation(){
		int viewRow = getSelectedRow();
		int modelRow = convertRowIndexToModel(viewRow);
		Mutation mutation = model.getMutation(modelRow);
		return mutation;
	}
	
	@Override 
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	private void formatTable(){
		setAutoCreateRowSorter(true);
		getColumnModel().getColumn(0).setPreferredWidth(50);
		getColumnModel().getColumn(1).setPreferredWidth(100);
		getColumnModel().getColumn(3).setMinWidth(200);
		getColumnModel().getColumn(4).setMinWidth(200);
		getColumnModel().getColumn(6).setPreferredWidth(200);
	}
}
