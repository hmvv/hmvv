package hmvv.gui.mutationlist.tables;

import hmvv.gui.CustomColumn;
import hmvv.gui.mutationlist.tablemodels.G1000TableModel;

public class G1000Table extends CommonTable{
	private static final long serialVersionUID = 1L;

	public G1000Table(G1000TableModel model){
		super(model);
	}
	
	@Override
	protected CustomColumn[] constructCustomColumns(){
		return CustomColumn.getCustomColumnArray(model.getColumnCount(), 1, 3, 4);
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
