package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.MutationListFrame;
import hmvv.gui.mutationlist.tablemodels.GnomadTableModel;
import hmvv.io.InternetCommands;
import hmvv.model.Mutation;

public class GnomadTable extends CommonTable{
	private static final long serialVersionUID = 1L;

	public GnomadTable (MutationListFrame parent, GnomadTableModel model){
		super(parent, model);
	}

	@Override
	protected HMVVTableColumn[] constructCustomColumns(){
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(),  7);
	}

	@Override
	protected void handleMouseClick(int column) throws Exception{
		if(column == 7){
			searchGnomad();
		}
	}

	private void searchGnomad(){
		Mutation mutation = getSelectedMutation();
		String gnomad_id = mutation.getGnomadID();
		if(!gnomad_id.equals("") && !gnomad_id.equals("null")){
			InternetCommands.searchGnomad(gnomad_id);
		}
	}
}
