package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

import hmvv.gui.mutationlist.MutationListFilters;
import hmvv.main.Configurations;
import hmvv.model.MutationCommon;

public class MutationList {
	private Configurations.MUTATION_TYPE mutation_type;
	private ArrayList<MutationCommon> mutations;
	private ArrayList<MutationCommon> filteredMutations;
	private ArrayList<MutationListListener> listeners;
	
	public MutationList(ArrayList<MutationCommon> mutations, Configurations.MUTATION_TYPE mutation_type){
		this.mutations = mutations;
		this.mutation_type = mutation_type;
		this.filteredMutations = new ArrayList<MutationCommon>();
		this.listeners = new ArrayList<MutationListListener>();
	}
	
	public final void addFilteredMutation(MutationCommon mutation) {
		filteredMutations.add(mutation);
	}
	
	public void updateReportedStatus(boolean reported, int index){
		MutationCommon mutation = getMutation(index);
		mutation.setReported(reported);
		notifyReportedStatusChanged(index);
	}

	public void sortModel(int[] newOrder){
		ArrayList<MutationCommon> newSortedOrder = new ArrayList<MutationCommon>(mutations);
		
		//get new order
		for(int i = 0; i < mutations.size(); i++){
			MutationCommon mutation = mutations.get(i);
			newSortedOrder.set(newOrder[i], mutation);
		}
		
		//set model with new order
		for(int i = 0; i < mutations.size(); i++){
			MutationCommon mutation = newSortedOrder.get(i);
			mutations.set(i, mutation);
		}
		notifyStructureChanged();
	}
	
	public void filterMutations(MutationListFilters mutationListFilters){
		mutationListFilters.filterMutations(mutations, filteredMutations);
		notifyDataChanged();
	}
	
	public final MutationCommon getMutation(int index){
		return mutations.get(index);
	}
	
	public int getMutationCount() {
		return mutations.size();
	}

	public int getSelectedMutationCount() {
		int count = 0;
		for (int i = 0; i < mutations.size(); i++) {
			MutationCommon mutation = mutations.get(i);
			if (mutation.isSelected()) {
				count++;
			}
		}
		return count;
	}

	public ArrayList<MutationCommon> getSelectedMutations() {

		ArrayList<MutationCommon> selectedMutations = new ArrayList<MutationCommon>();

		for (int i = 0; i < mutations.size(); i++){
			MutationCommon m = mutations.get(i);
			if (m.isSelected()) {
				selectedMutations.add(m);
			}
		}
		return selectedMutations;
	}
	
	public int getFilteredMutationCount() {
		return filteredMutations.size();
	}
	
	public MutationCommon getFilteredMutation(int index) {
		return filteredMutations.get(index);
	}
	
	public void addListener(CommonTableModel listener){
		listeners.add(listener);
	}

	public void addListenerGermline(GermlineCommonTableModel listener){
		listeners.add(listener);
	}
	
	private void notifyReportedStatusChanged(int index){
		for(MutationListListener listener : listeners){
			listener.mutationReportedStatusChanged(index);
		}
	}

	@SuppressWarnings("unused")
	private void notifyRowUpdated(int index){
		for(MutationListListener listener : listeners){
			listener.mutationUpdated(index);
		}
	}
	
	private void notifyDataChanged(){
		for(MutationListListener listener : listeners){
			listener.mutationDataChanged();
		}
	}
	
	private void notifyStructureChanged(){
		for(MutationListListener listener : listeners){
			listener.mutationListStructureChanged();
		}
	}

	public Configurations.MUTATION_TYPE getMutation_type() {
		return mutation_type;
	}

	public void setMutation_type(Configurations.MUTATION_TYPE mutation_type) {
		this.mutation_type = mutation_type;
	}
}
