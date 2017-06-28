package hmvv.gui.mutationlist.tables;

import hmvv.gui.CustomColumn;
import hmvv.gui.mutationlist.MutationListFrame;
import hmvv.gui.mutationlist.tablemodels.CommonTableModel;

public class MutationTraceTable extends CommonTable{
	private static final long serialVersionUID = 1L;

	public MutationTraceTable(MutationListFrame parent, CommonTableModel model) {
		super(parent, model);
	}

	@Override 
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	@Override
	protected CustomColumn[] constructCustomColumns() {
		return CustomColumn.getCustomColumnArray(model.getColumnCount(), 1, 3, 4);
	}

	@Override
	protected void handleMouseClick(int column) throws Exception {
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
