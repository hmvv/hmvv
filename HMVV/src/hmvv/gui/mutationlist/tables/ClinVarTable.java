package hmvv.gui.mutationlist.tables;

import hmvv.gui.CustomColumn;
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
	protected CustomColumn[] constructCustomColumns(){
		return CustomColumn.getCustomColumnArray(model.getColumnCount(), 1, 3, 4, 8, 9);
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
		}else if(column == 8){
			searchClinvar();
		}else if(column == 9){
			searchPubmed();
		}
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
