package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.ClinVarTableModel;
import hmvv.io.InternetCommands;
import javax.swing.JDialog;
import hmvv.model.MutationSomatic;

public class ClinVarTable extends CommonTable{
	private static final long serialVersionUID = 1L;
	
	public ClinVarTable(JDialog parent, ClinVarTableModel model){
		super(parent, model);
	}
	
	@Override
	protected HMVVTableColumn[] constructCustomColumns(){
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 7);
	}
	
	@Override
	protected void handleMousePressed(int column) throws Exception{
		if(column == 7) {
            searchClinVar();
		}
	}

    private void searchClinVar() throws Exception{
        MutationSomatic mutation = getSelectedMutation();
        String id = mutation.getClinvarID();
        if(!id.equals("") && !id.equals("null")){
            InternetCommands.searchClinvar(id);
        }
    }


}
