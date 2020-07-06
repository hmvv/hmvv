package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.GermlineCardiacAtlasTableModel;
import hmvv.gui.mutationlist.tablemodels.GermlineTranscriptTableModel;
import hmvv.main.HMVVFrame;

public class GermlineCardiacAtlasTable extends CommonTableGermline{
	private static final long serialVersionUID = 1L;

	public GermlineCardiacAtlasTable(HMVVFrame parent, GermlineCardiacAtlasTableModel model){
		super(parent, model);
	}

	@Override
	protected HMVVTableColumn[] constructCustomColumns(){
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 3);
	}

	@Override
	protected void handleMousePressed(int column) throws Exception{
		if(column == 4){
			searchCosmic();
		}
	}
}
