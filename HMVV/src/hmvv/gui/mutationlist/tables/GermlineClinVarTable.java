package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.GermlineCardiacAtlasTableModel;
import hmvv.gui.mutationlist.tablemodels.GermlineClinVarTableModel;
import hmvv.main.HMVVFrame;

public class GermlineClinVarTable extends CommonTableGermline{
	private static final long serialVersionUID = 1L;

	public GermlineClinVarTable(HMVVFrame parent, GermlineClinVarTableModel model){
		super(parent, model);
	}

	@Override
	protected HMVVTableColumn[] constructCustomColumns(){
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 3);
	}

	@Override
	protected void handleMousePressed(int column) throws Exception{
		if(column == 4){
//			searchCosmic();
		}
	}
}
