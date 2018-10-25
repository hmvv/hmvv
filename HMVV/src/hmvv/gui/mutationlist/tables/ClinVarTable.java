package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.MutationListFrame;
import hmvv.gui.mutationlist.tablemodels.ClinVarTableModel;
import hmvv.io.InternetCommands;
import hmvv.model.Mutation;

public class ClinVarTable extends CommonTable{
	private static final long serialVersionUID = 1L;
	
	public ClinVarTable(MutationListFrame parent, ClinVarTableModel model){
		super(parent, model);
	}
	
	@Override
	protected HMVVTableColumn[] constructCustomColumns(){
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 7);
	}
	
	@Override
	protected void handleMouseClick(int column) throws Exception{
		if(column == 7) {
            searchClinVar();
		}
	}

    private void searchClinVar(){
        Mutation mutation = getSelectedMutation();
        String id = mutation.getClinvarID();
        if(!id.equals("") && !id.equals("null")){
            InternetCommands.searchClinvar(id);
        }
    }


}
