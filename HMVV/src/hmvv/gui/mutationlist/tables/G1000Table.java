package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.MutationListFrame;
import hmvv.gui.mutationlist.tablemodels.G1000TableModel;

public class G1000Table extends CommonTable{
	private static final long serialVersionUID = 1L;

	public G1000Table(MutationListFrame parent, G1000TableModel model){
		super(parent, model);
	}
	
	@Override
	protected HMVVTableColumn[] constructCustomColumns(){
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount());
	}
	
	@Override
	protected void handleMousePressed(int column) throws Exception{

	}
}
