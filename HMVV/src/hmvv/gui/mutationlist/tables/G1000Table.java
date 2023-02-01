package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.G1000TableModel;
import javax.swing.JDialog;

public class G1000Table extends CommonTable{
	private static final long serialVersionUID = 1L;

	public G1000Table(JDialog parent, G1000TableModel model){
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
