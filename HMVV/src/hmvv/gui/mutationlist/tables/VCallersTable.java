package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.VCallersTableModel;
import hmvv.main.HMVVFrame;

public class VCallersTable extends CommonTable{
	private static final long serialVersionUID = 1L;

	public VCallersTable(HMVVFrame parent, VCallersTableModel model){
		super(parent, model);
	}
	
	@Override
	protected HMVVTableColumn[] constructCustomColumns(){
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount());
	}

	@Override
	protected void handleMousePressed(int column) throws Exception {
		
	}
	
}
