package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.GermlinePredictionTableModel;
import hmvv.gui.mutationlist.tablemodels.GermlineProteinDomainTableModel;
import hmvv.io.InternetCommands;
import hmvv.main.HMVVFrame;
import hmvv.model.GermlineMutation;

public class GermlineProteinDomainTable extends CommonTableGermline{
	private static final long serialVersionUID = 1L;

	public GermlineProteinDomainTable(HMVVFrame parent, GermlineProteinDomainTableModel model){
		super(parent, model);
	}
	
	@Override
	protected HMVVTableColumn[] constructCustomColumns(){
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 1,6,13,15,16);
	}
	
	@Override
	protected void handleMousePressed(int column) throws Exception{
		if(column == 1){
			searchGoogleForGene();
		}else if(column == 13){
			searchUniprotProteinVariant();
		}else if(column == 15){
			searchExpasyVariant();
		}else if(column == 16){
			searchUniprotProtein();
		}
	}


	private void searchExpasyVariant() throws Exception{
		GermlineMutation mutation = getSelectedMutation();
		String expasy_id = mutation.getExpasy_id();
		if(!expasy_id.equals("") && !expasy_id.equals("null")){
			InternetCommands.searchExpasyVariant(expasy_id);
		}
	}
	private void searchUniprotProteinVariant() throws Exception{
		GermlineMutation mutation = getSelectedMutation();
		String uniprot_id = mutation.getUniprot_variant();
		if(!uniprot_id.equals("") && !uniprot_id.equals("null")){
			InternetCommands.searchUniprotProtein(uniprot_id);
		}
	}

	private void searchUniprotProtein() throws Exception{
		GermlineMutation mutation = getSelectedMutation();
		String uniprot_id = mutation.getUniprot_id();
		if(!uniprot_id.equals("") && !uniprot_id.equals("null")){
			InternetCommands.searchUniprotProtein(uniprot_id);
		}
	}
}
