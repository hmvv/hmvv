package hmvv.gui.mutationlist.tables;

import javax.swing.JDialog;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.GermlineCardiacAtlasTableModel;
import hmvv.io.InternetCommands;
import hmvv.model.MutationGermline;

public class GermlineCardiacAtlasTable extends CommonTableGermline{
	private static final long serialVersionUID = 1L;

	public GermlineCardiacAtlasTable(JDialog parent, GermlineCardiacAtlasTableModel model){
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
			searchCardiacAtlas();
		}
	}

	private void searchCardiacAtlas() throws Exception{
		MutationGermline mutation = getSelectedMutation();
		String cardiacAtlas_id = mutation.getCardiacAtlasId();
		if(!cardiacAtlas_id.equals("") && !cardiacAtlas_id.equals("null")){
			InternetCommands.searchCardiacAtlas(cardiacAtlas_id);
		}
	}
}
