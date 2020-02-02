package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.CosmicTableModel;
import hmvv.main.HMVVFrame;

public class CosmicTable extends CommonTable{
	private static final long serialVersionUID = 1L;

	public CosmicTable(HMVVFrame parent, CosmicTableModel model){
		super(parent, model);
	}
	
	@Override
	protected HMVVTableColumn[] constructCustomColumns(){
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 7);
	}
	
	@Override
	protected void handleMousePressed(int column) throws Exception{
		if(column == 7){
			searchCosmic();
		}
	}
}
