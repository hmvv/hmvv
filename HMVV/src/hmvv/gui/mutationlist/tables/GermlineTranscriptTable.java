package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.GermlineTranscriptTableModel;
import hmvv.main.HMVVFrame;

public class GermlineTranscriptTable extends CommonTableGermline{
	private static final long serialVersionUID = 1L;

	public GermlineTranscriptTable(HMVVFrame parent, GermlineTranscriptTableModel model){
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
			searchGoogleForDNAChange();
		}
	}
}
