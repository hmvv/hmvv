package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.MutationListFrame;
import hmvv.gui.mutationlist.tablemodels.CoordinatesTableModel;

public class CoordinatesTable extends CommonTable{
	private static final long serialVersionUID = 1L;

	public CoordinatesTable(MutationListFrame parent, CoordinatesTableModel model){
		super(parent, model);
	}
	
	@Override
	protected HMVVTableColumn[] constructCustomColumns(){
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 1, 3, 4);
	}
	
	@Override
	protected void handleMouseClick(int column) throws Exception{
		if(column == 1){
			searchGoogleForGene();
		}else if(column == 3){
			searchGoogleForProteinChange();
			searchGoogleForDNAChange();
		}else if(column == 4){
			searchGoogleForDNAChange();
			searchGoogleForProteinChange();
		}
	}
}
