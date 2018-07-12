package hmvv.gui.mutationlist.tables;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.MutationListFrame;
import hmvv.gui.mutationlist.MutationTraceFrame;
import hmvv.gui.mutationlist.tablemodels.BasicTableModel;
import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.io.DatabaseCommands;
import hmvv.io.IGVConnection;
import hmvv.model.Mutation;
public class BasicTable extends CommonTable{
	private static final long serialVersionUID = 1L;
	
	public BasicTable(MutationListFrame parent, BasicTableModel model){
		super(parent, model);
	}
	
	@Override
	protected HMVVTableColumn[] constructCustomColumns(){
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 1, 3, 4, 5, 6, 12, 13, 16);
	}
	
	@Override
	protected void handleMouseClick(int column) throws Exception{
		if(column == 1){
			searchGoogleForGene();
		}else if(column == 3){
			searchGoogleForProteinChange();
			searchGoogleForDNAChange();
		}else if(column == 4){
			searchGoogleForDNAChange();
			searchGoogleForProteinChange();
		}else if(column == 5){
			searchSNP();
		}else if(column == 6){
			searchCosmic();
		}else if(column == 12){
			findSimilarSamples();
		}else if(column == 13){
			handleAnnotationClick();
		}else if(column == 16){
			new Thread(new Runnable() {
				public void run() {
					try {
						handleIGVClick();
					}catch(Exception e) {
						JOptionPane.showMessageDialog(BasicTable.this, e.getMessage());
					}
				}
			}).start();			
		}
	}
	
	private void findSimilarSamples() throws Exception{
		//search for previous samples with this mutation
		Mutation mutation = getSelectedMutation();

		ArrayList<Mutation> mutations = DatabaseCommands.getMatchingMutations(mutation);
		MutationList matchingMutationList = new MutationList(mutations);
		MutationTraceFrame mutationTrace = new MutationTraceFrame(parent, matchingMutationList, "Mutation Trace");
		mutationTrace.setVisible(true);
	}
	
	private void handleIGVClick() throws Exception{
		Mutation mutation = getSelectedMutation();
		String result = IGVConnection.loadCoordinateIntoIGV(this, mutation.getCoordinate());
		if(result.length() > 0) {
			JOptionPane.showMessageDialog(this, result);
		}
	}
}
