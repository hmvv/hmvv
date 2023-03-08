package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.GermlineProteinDomainTableModel;
import hmvv.io.InternetCommands;
import javax.swing.JDialog;
import hmvv.model.MutationGermline;

public class GermlineProteinDomainTable extends CommonTableGermline{
	private static final long serialVersionUID = 1L;

	public GermlineProteinDomainTable(JDialog parent, GermlineProteinDomainTableModel model){
		super(parent, model);
	}
	
	@Override
	protected HMVVTableColumn[] constructCustomColumns(){
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 1,10,14,16,17);
	}
	
	@Override
	protected void handleMousePressed(int column) throws Exception{
		if(column == 1){
			searchGoogleForGene();
		}else if(column == 10){
			searchNCBIProtein();
		}else if(column == 14){
			searchUniprotProteinVariant();
		}else if(column == 16){
			searchExpasyVariant();
		}else if(column == 17){
			searchUniprotProtein();
		}
	}

	private void searchNCBIProtein() throws Exception{
		MutationGermline mutation = getSelectedMutation();
		String expasy_id = mutation.getProtein_id();
		if(!expasy_id.equals("") && !expasy_id.equals("null")){
			InternetCommands.searchNCBIProtein(expasy_id.replaceAll(" ","_"));
		}
	}

	private void searchExpasyVariant() throws Exception{
		MutationGermline mutation = getSelectedMutation();
		String expasy_id = mutation.getExpasy_id();
		if(!expasy_id.equals("") && !expasy_id.equals("null")){
			InternetCommands.searchExpasyVariant(expasy_id);
		}
	}
	private void searchUniprotProteinVariant() throws Exception{
		MutationGermline mutation = getSelectedMutation();
		String uniprot_id = mutation.getUniprot_variant();
		if(!uniprot_id.equals("") && !uniprot_id.equals("null")){
			InternetCommands.searchUniprotProtein(uniprot_id);
		}
	}

	private void searchUniprotProtein() throws Exception{
		MutationGermline mutation = getSelectedMutation();
		String uniprot_id = mutation.getUniprot_id();
		if(!uniprot_id.equals("") && !uniprot_id.equals("null")){
			InternetCommands.searchUniprotProtein(uniprot_id);
		}
	}
}
