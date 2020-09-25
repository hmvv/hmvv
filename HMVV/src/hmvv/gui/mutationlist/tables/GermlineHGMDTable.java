package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.MutationGermlineHGMDGeneFrame;
import hmvv.gui.mutationlist.tablemodels.GermlineHGMDTableModel;
import hmvv.io.InternetCommands;
import hmvv.main.HMVVFrame;
import hmvv.model.MutationGermline;

public class GermlineHGMDTable extends CommonTableGermline{
	private static final long serialVersionUID = 1L;

	public GermlineHGMDTable(HMVVFrame parent, GermlineHGMDTableModel model){
		super(parent, model);
	}
	
	@Override
	protected HMVVTableColumn[] constructCustomColumns(){
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 6,11,12,13);
	}

	@Override
	protected void handleMousePressed(int column) throws Exception{
		if (column == 13){
			loadHGMDGeneInformation();
		}else if (column == 6){
			searchHGMDID();
		}else if (column == 11){
			searchHGMDCitation();
		}else if (column == 12){
			searchHGMDExtraCitation();
		}
	}

	private void searchHGMDID() throws Exception{
		MutationGermline mutation = getSelectedMutation();
		String hgmd_id = mutation.getMutationGermlineHGMD().getId();
		if(!hgmd_id.equals("") && !hgmd_id.equals("null")){
			InternetCommands.searchGoogleHGMD(mutation.getGene(),hgmd_id);
		}
	}

	private void searchHGMDCitation() throws Exception{
		MutationGermline mutation = getSelectedMutation();
		String pmid = mutation.getMutationGermlineHGMD().getPmid();
		if(!pmid.equals("") && !pmid.equals("null")){
			InternetCommands.searchPubmed(pmid);
		}
	}

	private void searchHGMDExtraCitation() throws Exception{
		MutationGermline mutation = getSelectedMutation();
		String pmids = mutation.getMutationGermlineHGMD().getExtra_pmids().replace(",","&");
		if(!pmids.equals("") && !pmids.equals("null")){
			InternetCommands.searchPubmed(pmids);
		}
	}
	private void loadHGMDGeneInformation() throws Exception{
		MutationGermlineHGMDGeneFrame hgmd_gene_frame = new MutationGermlineHGMDGeneFrame(parent, getSelectedMutation());
		hgmd_gene_frame.setVisible(true);
	}
}
