package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

import hmvv.io.DatabaseCommands;
import hmvv.model.Mutation;

public class MutationList {
	private ArrayList<Mutation> mutations;
	private ArrayList<Mutation> filteredMutations;
	private ArrayList<MutationListListener> listeners;
	private ArrayList<Mutation> mutationsInNormalPair;
	
	public MutationList(ArrayList<Mutation> mutations){
		this.mutations = mutations;
		this.filteredMutations = new ArrayList<Mutation>();
		this.listeners = new ArrayList<MutationListListener>();
	}
	
	public void addListener(CommonTableModel listener){
		listeners.add(listener);
	}
	
	public final Mutation getMutation(int index){
		return mutations.get(index);
	}
	
	public void updateAnnotationText(String text, int index){
		Mutation mutation = getMutation(index);
		mutation.setAnnotation(text);
		notifyRowUpdated(index);
	}
	
	public void updateReportedStatus(boolean reported, int index){
		Mutation mutation = getMutation(index);
		mutation.setReported(reported);
		notifyReportedStatusChanged(index);
	}
	
	public void sortModel(int[] newOrder){
		ArrayList<Mutation> newSortedOrder = new ArrayList<Mutation>(mutations);
		
		//get new order
		for(int i = 0; i < mutations.size(); i++){
			Mutation mutation = mutations.get(i);
			newSortedOrder.set(newOrder[i], mutation);
		}
		
		//set model with new order
		for(int i = 0; i < mutations.size(); i++){
			Mutation mutation = newSortedOrder.get(i);
			mutations.set(i, mutation);
		}
		notifyStructureChanged();
	}
	
	private boolean includeMutationCosmicFilter(boolean includeCosmicOnly, Mutation mutation){
		if(!includeCosmicOnly){
			return true;
		}
		if(mutation.getCosmicID() == null){
			return false;
		}
		return !mutation.getCosmicID().equals("");
	}
	
	private boolean includeReportedFilter(boolean includeReportedOnly, Mutation mutation){
		if(!includeReportedOnly){
			return true;
		}
		return mutation.isReported();
	}
	
	private boolean includeVariantFilter(int frequencyFrom, int frequencyTo, Mutation mutation){
		double variantFrequency = mutation.getAltFreq();
		if(frequencyFrom > variantFrequency){
			return false;
		}
		
		if(frequencyTo < variantFrequency){
			return false;
		}
		return true;
	}
	
	private boolean includeOccurrenceFilter(int minOccurence, Mutation mutation){
		if(mutation.getOccurrence() != null){
			int occurrence = mutation.getOccurrence();
			if(minOccurence > occurrence){
				return false;
			}
		}
		return true;//default to true
	}
	
	private boolean includeReadDepthFilter(int minReadDepth, Mutation mutation){
		if(mutation.getReadDP() != null){
			int readDepth = mutation.getReadDP();
			if(minReadDepth > readDepth){
				return false;
			}
		}
		return true;//default to true;
	}
	
	private boolean includePopulationFrequencyFilter(int maxPopulationFrequency, Mutation mutation){
		if(mutation.getAltGlobalFreq() != null){
			double populationFrequency = mutation.getAltGlobalFreq();
			if(maxPopulationFrequency < populationFrequency){
				return false;
			}
		}
		return true;//default to true;
	}
	
	private boolean includeMutation(boolean includeCosmicOnly, boolean includeReportedOnly, int frequencyFrom, int frequencyTo, int minOccurence, int minReadDepth, int maxPopulationFrequency, Mutation mutation){
		if(!includeMutationCosmicFilter(includeCosmicOnly, mutation)){
			return false;
		}
		if(!includeReportedFilter(includeReportedOnly, mutation)){
			return false;
		}
		if(!includeVariantFilter(frequencyFrom, frequencyTo, mutation)){
			return false;
		}
		if(!includeOccurrenceFilter(minOccurence, mutation)){
			return false;
		}
		if(!includeReadDepthFilter(minReadDepth, mutation)){
			return false;
		}
		if(!includePopulationFrequencyFilter(maxPopulationFrequency, mutation)){
			return false;
		}
		
		return true;
	}
	
	public void filterMutations(boolean includeCosmicOnly, boolean includeReportedOnly, boolean filterNormalPair, int normalPairSampleID, int frequencyFrom, int frequencyTo, int minOccurence, int minReadDepth, int maxPopulationFrequency) throws Exception{
		ArrayList<Mutation> allMutations = new ArrayList<Mutation>(mutations.size() + filteredMutations.size());
		allMutations.addAll(mutations);
		allMutations.addAll(filteredMutations);

		if(mutationsInNormalPair == null && filterNormalPair){
			mutationsInNormalPair = DatabaseCommands.getPairedNormalMutations(normalPairSampleID);
			if(mutationsInNormalPair == null){
				mutationsInNormalPair = new ArrayList<Mutation>();//empty array list because no normal was identified
			}
		}

		ArrayList<Mutation> newFilteredMutations = new ArrayList<Mutation>();
		
		for(int i = 0; i < allMutations.size(); i++){
			Mutation mutation = allMutations.get(i);

			if(!includeMutation(includeCosmicOnly, includeReportedOnly, frequencyFrom, frequencyTo, minOccurence, minReadDepth, maxPopulationFrequency, mutation)){				
				newFilteredMutations.add(mutation);
				continue;
			}
			
			if(filterNormalPair){
				for(Mutation m : mutationsInNormalPair){
					if(m.getCoordinate().equals(mutation.getCoordinate())){
						newFilteredMutations.add(mutation);
						break;
					}
				}
			}
		}
		allMutations.removeAll(newFilteredMutations);
		
		mutations.clear();
		mutations.addAll(allMutations);
		filteredMutations.clear();
		filteredMutations.addAll(newFilteredMutations);
		notifyDataChanged();
	}
	
	public int getMutationCount() {
		return mutations.size();
	}
	
	private void notifyReportedStatusChanged(int index){
		for(MutationListListener listener : listeners){
			listener.mutationReportedStatusChanged(index);
		}
	}
	
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
