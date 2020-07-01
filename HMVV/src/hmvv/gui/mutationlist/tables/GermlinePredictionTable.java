package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.CosmicTableModel;
import hmvv.gui.mutationlist.tablemodels.GermlinePredictionTableModel;
import hmvv.main.HMVVFrame;

public class GermlinePredictionTable extends CommonTable{
	private static final long serialVersionUID = 1L;

	public GermlinePredictionTable(HMVVFrame parent, GermlinePredictionTableModel model){
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
