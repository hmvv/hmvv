package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.CosmicTableModel;
import hmvv.gui.mutationlist.tablemodels.GermlineTranscriptTableModel;
import hmvv.main.HMVVFrame;

public class GermlineTranscriptTable extends CommonTableGermline{
	private static final long serialVersionUID = 1L;

	public GermlineTranscriptTable(HMVVFrame parent, GermlineTranscriptTableModel model){
		super(parent, model);
	}
	
	@Override
	protected HMVVTableColumn[] constructCustomColumns(){
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 4);
	}
	
	@Override
	protected void handleMousePressed(int column) throws Exception{
		if(column == 4){
			searchCosmic();
		}
	}
}
