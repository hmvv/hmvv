package hmvv.gui.mutationlist.tables;

import javax.swing.*;
import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.repeatMutationsPopup;
import hmvv.gui.mutationlist.tablemodels.BasicTableModel;
import hmvv.io.IGVConnection;
import hmvv.model.MutationSomatic;


public class BasicTable extends CommonTable{
	private static final long serialVersionUID = 1L;
	
	public BasicTable(JDialog parent, BasicTableModel model){
		super(parent, model);
	}
	
	@Override
	protected HMVVTableColumn[] constructCustomColumns(){
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 2, 3, 4, 7, 8, 12, 14, 16);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if (column == 0){
			return true;
		} else if (column == 2){
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void handleMousePressed(int column) throws Exception{
		if(column == 2){
			new Thread(new Runnable() {
				public void run() {
					try {
						handleIGVClick();
					}catch(Exception e) {
						JOptionPane.showMessageDialog(BasicTable.this, e.getMessage());
					}
				}
			}).start();			
		}else if (column == 3) {
			handleLoadIGVCheckBoxClick();
		}else if (column == 4) {
			searchGoogleForGene();
		}else if(column == 7){
            searchGoogleForDNAChange();
        }else if(column == 8){
             searchGoogleForProteinChange();
        }else if (column == 12) {
			searchCosmic();
		}else if(column == 14){
			handleAnnotationClick();
		}else if(column == 16){
			handleRepeatsClick();
		}
	}

	private void handleIGVClick() throws Exception{
		MutationSomatic mutation = getSelectedMutation();
		String result = IGVConnection.loadCoordinateIntoIGV(this, mutation.getCoordinate());
		if((result.length() > 0) && (!result.equals("OK"))) {
			JOptionPane.showMessageDialog(this, result);
		}
	}

	private void handleRepeatsClick() throws Exception{
		MutationSomatic mutation = getSelectedMutation();
		repeatMutationsPopup.handleRepeatMutationsClick(parent, mutation);
		
		


	}

	private void handleLoadIGVCheckBoxClick(){
		MutationSomatic mutation = getSelectedMutation();
		if (mutation.isSelected()){
			mutation.setSelected(false);
		}else{
			mutation.setSelected(true);
		}
	}
}
