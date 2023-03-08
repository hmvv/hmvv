package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.GermlinePredictionTableModel;
import javax.swing.JDialog;

public class GermlinePredictionTable extends CommonTableGermline{
	private static final long serialVersionUID = 1L;

	public GermlinePredictionTable(JDialog parent, GermlinePredictionTableModel model){
		super(parent, model);
	}
	
	@Override
	protected HMVVTableColumn[] constructCustomColumns(){
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 1);
	}
	
	@Override
	protected void handleMousePressed(int column) throws Exception{
		if(column == 1){
			searchGoogleForGene();
		}
	}
}
