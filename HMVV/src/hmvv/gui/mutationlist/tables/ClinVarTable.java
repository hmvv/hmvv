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
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount() );
	}
	
	@Override
	protected void handleMouseClick(int column) throws Exception{

	}
	
	private void searchClinvar(){
		Mutation mutation = getSelectedMutation();
		String clinvar = mutation.getClinicalAcc();
		if(!clinvar.equals("") && !clinvar.equals("null")){
			InternetCommands.searchClinvar(clinvar);
		}
	}
	
	private void searchPubmed(){
		Mutation mutation = getSelectedMutation();
		String pubmed = mutation.getPubmed();
		if(!pubmed.equals("") && !pubmed.equals("null")){
			InternetCommands.searchPubmed(pubmed);
		}
	}
}
