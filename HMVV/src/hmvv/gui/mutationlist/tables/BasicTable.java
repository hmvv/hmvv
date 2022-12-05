package hmvv.gui.mutationlist.tables;

import javax.swing.*;
import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.BasicTableModel;
import hmvv.io.IGVConnection;
import hmvv.main.HMVVFrame;
import hmvv.model.MutationSomatic;
import hmvv.gui.mutationlist.MutationListFrame;


public class BasicTable extends CommonTable{
	private static final long serialVersionUID = 1L;
	
	public BasicTable(HMVVFrame parent, BasicTableModel model){
		super(parent, model);
	}
	
	@Override
	protected HMVVTableColumn[] constructCustomColumns(){
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 2, 3, 4, 7, 8, 12, 14);
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
			//parent.setVisible(false);
			
		}
	}

	private void handleIGVClick() throws Exception{
		MutationSomatic mutation = getSelectedMutation();
		String result = IGVConnection.loadCoordinateIntoIGV(this, mutation.getCoordinate());
		if(result.length() > 0) {
			JOptionPane.showMessageDialog(this, result);
		}
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
