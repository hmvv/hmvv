package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.CommonTableModel;
import javax.swing.JDialog;

public class MutationTraceTable extends CommonTable{
	private static final long serialVersionUID = 1L;

	public MutationTraceTable(JDialog parent, CommonTableModel model) {
		super(parent, model);
	}

	@Override 
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	@Override
	protected HMVVTableColumn[] constructCustomColumns() {
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 1, 3, 4);
	}

	@Override
	protected void handleMousePressed(int column) throws Exception {
		if(column == 1){
			searchGoogleForGene();
		}else if(column == 3){
			searchGoogleForProteinChange();
			searchGoogleForDNAChange();
		}else if(column == 4){
			searchGoogleForDNAChange();
			searchGoogleForProteinChange();
		}
	}
}
