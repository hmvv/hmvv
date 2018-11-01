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
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 11, 13, 16);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		if (column == 0){
			return true;
		} else if (column == 17){
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void handleMouseClick(int column) throws Exception{
		if (column == 11) {
			searchCosmic();
		}else if(column == 13){
			handleAnnotationClick();
		}else if(column == 16){
			new Thread(new Runnable() {
				public void run() {
					try {
						handleIGVClick_2();
					}catch(Exception e) {
						JOptionPane.showMessageDialog(BasicTable.this, e.getMessage());
					}
				}
			}).start();			
		}else if (column ==17) {
			handleLoadIGVCheckBoxClick();
		}
	}

//	private void handleIGVClick() throws Exception{
//		Mutation mutation = getSelectedMutation();
//		String result = IGVConnection.loadCoordinateIntoIGV(this, mutation.getCoordinate());
//		if(result.length() > 0) {
//			JOptionPane.showMessageDialog(this, result);
//		}
//	}

	private void handleIGVClick_2() throws Exception{
		Mutation mutation = getSelectedMutation();
		String result = IGVConnection.loadCoordinateIntoIGV_2(this, mutation.getCoordinate());
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
