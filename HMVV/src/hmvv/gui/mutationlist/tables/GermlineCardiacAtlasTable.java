package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.GermlineCardiacAtlasTableModel;
import hmvv.gui.mutationlist.tablemodels.GermlineTranscriptTableModel;
import hmvv.io.InternetCommands;
import hmvv.main.HMVVFrame;
import hmvv.model.GermlineMutation;
import hmvv.model.Mutation;

public class GermlineCardiacAtlasTable extends CommonTableGermline{
	private static final long serialVersionUID = 1L;

	public GermlineCardiacAtlasTable(HMVVFrame parent, GermlineCardiacAtlasTableModel model){
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
		GermlineMutation mutation = getSelectedMutation();
		String cardiacAtlas_id = mutation.getCardiacAtlasId();
		if(!cardiacAtlas_id.equals("") && !cardiacAtlas_id.equals("null")){
			InternetCommands.searchCardiacAtlas(cardiacAtlas_id);
		}
	}
}
