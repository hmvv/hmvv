package hmvv.gui.mutationlist.tables;

import java.util.ArrayList;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.MutationListFrame;
import hmvv.gui.mutationlist.MutationTraceFrame;
import hmvv.gui.mutationlist.tablemodels.BasicTableModel;
import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.io.DatabaseCommands;
import hmvv.model.Mutation;
public class BasicTable extends CommonTable{
	private static final long serialVersionUID = 1L;
	
	public BasicTable(MutationListFrame parent, BasicTableModel model){
		super(parent, model);
	}
	
	@Override
	protected HMVVTableColumn[] constructCustomColumns(){
		return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 1, 3, 4, 5, 6, 12, 13);
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
}
