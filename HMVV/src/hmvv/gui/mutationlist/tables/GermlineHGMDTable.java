package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.MutationGermlineHGMDFrame;
import hmvv.gui.mutationlist.tablemodels.GermlineHGMDTableModel;
import hmvv.gui.mutationlist.tablemodels.GermlinePredictionTableModel;
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
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 1,7);
	}

	@Override
	protected void handleMousePressed(int column) throws Exception{
		if (column == 1){
			searchGoogleForGene();
		}else if (column == 7){
			loadHGMDWindow();
		}
	}

	private void loadHGMDWindow() throws Exception{
		MutationGermline mutation = getSelectedMutation();
		MutationGermlineHGMDFrame hgmdFrame = new MutationGermlineHGMDFrame(parent,mutation);
		hgmdFrame.setVisible(true);
	}
}
