package hmvv.gui.mutationlist.tables;

import javax.swing.*;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.MutationListFrame;
import hmvv.gui.mutationlist.tablemodels.BasicTableModel;
import hmvv.io.IGVConnection;
import hmvv.model.Mutation;
public class BasicTable extends CommonTable{
	private static final long serialVersionUID = 1L;
	
	public BasicTable(MutationListFrame parent, BasicTableModel model){
		super(parent, model);
	}
	
	@Override
	protected HMVVTableColumn[] constructCustomColumns(){
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 1, 3, 6, 7, 11, 13);
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
		if(column == 1){
			new Thread(new Runnable() {
				public void run() {
					try {
						handleIGVClick();
					}catch(Exception e) {
						JOptionPane.showMessageDialog(BasicTable.this, e.getMessage());
					}
				}
			}).start();			
		}else if (column == 2) {
			handleLoadIGVCheckBoxClick();
		}else if (column == 3) {
			searchGoogleForGene();
		}else if(column == 6){
            searchGoogleForDNAChange();
        }else if(column == 7){
             searchGoogleForProteinChange();
        }else if (column == 11) {
			searchCosmic();
		}else if(column == 13){
			handleAnnotationClick();
		}
	}

	private void handleIGVClick() throws Exception{
		Mutation mutation = getSelectedMutation();
		String result = IGVConnection.loadCoordinateIntoIGV(this, mutation.getCoordinate());
		if(result.length() > 0) {
			JOptionPane.showMessageDialog(this, result);
		}
	}

	private void handleLoadIGVCheckBoxClick(){
		Mutation mutation = getSelectedMutation();
		if (mutation.isSelected()){
			mutation.setSelected(false);
		}else{
			mutation.setSelected(true);
		}
	}
}
