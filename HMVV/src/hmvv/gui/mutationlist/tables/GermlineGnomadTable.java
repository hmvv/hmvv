package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.GermlineGnomadTableModel;
import hmvv.io.InternetCommands;
import hmvv.main.HMVVFrame;
import hmvv.model.MutationGermline;

public class GermlineGnomadTable extends CommonTableGermline{
	private static final long serialVersionUID = 1L;

	public GermlineGnomadTable(HMVVFrame parent, GermlineGnomadTableModel model){
		super(parent, model);
	}

	@Override
	protected HMVVTableColumn[] constructCustomColumns(){
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 1,7);
	}

	@Override
	protected void handleMousePressed(int column) throws Exception{
		if (column == 1){
			searchGoogleForGene();
		} else if(column == 7){
			searchGnomad();
		}
	}

	private void searchGnomad() throws Exception{
		MutationGermline mutation = getSelectedMutation();
		String gnomad_id = mutation.getGnomad_id();
		if(!gnomad_id.equals("") && !gnomad_id.equals("null")){
			InternetCommands.searchGnomad(gnomad_id);
		}
	}
}
