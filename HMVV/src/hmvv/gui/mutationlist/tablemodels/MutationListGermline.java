package hmvv.gui.mutationlist.tablemodels;

import hmvv.gui.mutationlist.MutationListFilters;
import hmvv.gui.mutationlist.MutationListFiltersGermline;
import hmvv.model.GermlineMutation;
import hmvv.model.Mutation;

import java.util.ArrayList;

public class MutationListGermline {
	private ArrayList<GermlineMutation> mutations;
	private ArrayList<GermlineMutation> filteredMutations;
	private ArrayList<MutationListListener> listeners;

	public MutationListGermline(ArrayList<GermlineMutation> mutations){
		this.mutations = mutations;
		this.filteredMutations = new ArrayList<GermlineMutation>();
		this.listeners = new ArrayList<MutationListListener>();
	}
	
	public final void addFilteredMutation(GermlineMutation mutation) {
		filteredMutations.add(mutation);
	}
	
	public void updateReportedStatus(boolean reported, int index){
		Mutation mutation = getMutation(index);
		mutation.setReported(reported);
		notifyReportedStatusChanged(index);
	}

	public void sortModel(int[] newOrder){
		ArrayList<GermlineMutation> newSortedOrder = new ArrayList<GermlineMutation>(mutations);
		
		//get new order
		for(int i = 0; i < mutations.size(); i++){
			GermlineMutation mutation = mutations.get(i);
			newSortedOrder.set(newOrder[i], mutation);
		}
		
		//set model with new order
		for(int i = 0; i < mutations.size(); i++){
			GermlineMutation mutation = newSortedOrder.get(i);
			mutations.set(i, mutation);
		}
		notifyStructureChanged();
	}
	
	public void filterMutations(MutationListFiltersGermline mutationListFilters){
		mutationListFilters.filterMutations(mutations, filteredMutations);
		notifyDataChanged();
	}
	
	public final Mutation getMutation(int index){
		return mutations.get(index);
	}
	
	public int getMutationCount() {
		return mutations.size();
	}

	public int getSelectedMutationCount() {
		int count = 0;
		for (int i = 0; i < mutations.size(); i++) {
			Mutation mutation = mutations.get(i);
			if (mutation.isSelected()) {
				count++;
			}
		}
		return count;
	}

	public ArrayList<Mutation> getSelectedMutations() {

		ArrayList<Mutation> selectedMutations = new ArrayList<Mutation>();

		for (int i = 0; i < mutations.size(); i++){
			Mutation m = mutations.get(i);
			if (m.isSelected()) {
				selectedMutations.add(m);
			}
		}
		return selectedMutations;
	}
	
	public int getFilteredMutationCount() {
		return filteredMutations.size();
	}
	
	public Mutation getFilteredMutation(int index) {
		return filteredMutations.get(index);
	}
	
	public void addListener(CommonTableModelGermline listener){
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
}
