package hmvv.gui.mutationlist;

import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import hmvv.gui.GUICommonTools;
import hmvv.main.Configurations;
import hmvv.model.Mutation;
import hmvv.model.VariantPredictionClass;

public class MutationListFilters {
	
	private ArrayList<Filter> filters;
	
	public MutationListFilters() {
		this.filters = new ArrayList<Filter>();
	}
	
	private void addFilter(Filter filter) {
		filters.add(filter);
	}
	
	public void filterMutations(ArrayList<Mutation> mutations, ArrayList<Mutation> filteredMutations){
		ArrayList<Mutation> allMutations = new ArrayList<Mutation>(mutations.size() + filteredMutations.size());
		allMutations.addAll(mutations);
		allMutations.addAll(filteredMutations);

		ArrayList<Mutation> newFilteredMutations = new ArrayList<Mutation>();
		
		for(int i = 0; i < allMutations.size(); i++){
			Mutation mutation = allMutations.get(i);

			if(!includeMutation(mutation)){				
				newFilteredMutations.add(mutation);
				continue;
			}
		}
		allMutations.removeAll(newFilteredMutations);
		
		mutations.clear();
		mutations.addAll(allMutations);
		filteredMutations.clear();
		filteredMutations.addAll(newFilteredMutations);
	}
	
	
	private boolean includeMutation(Mutation mutation){
		for(Filter f : filters) {
			if(f.exclude(mutation)) {
				return false;
			}
		}
		return true;
	}
	
	public void addCosmicIDFilter(JCheckBox cosmicIDCheckBox) {
		addFilter(new CosmicIDFilter(cosmicIDCheckBox));
	}
	
	public void addMaxG1000FrequencyFilter(JTextField maxPopulationFrequencyG1000TextField) {
		addFilter(new MaxG1000FrequencyFilter(maxPopulationFrequencyG1000TextField));
	}
	
	public void addMaxGnomadFrequencyFilter(JTextField maxPopulationFrequencyGnomadTextField) {
		addFilter(new MaxGnomadFrequencyFilter(maxPopulationFrequencyGnomadTextField));
	}
	
	public void addMaxOccurrenceFilter(JTextField occurenceFromTextField) {
		addFilter(new MaxOccurrenceFilter(occurenceFromTextField));
	}
	
	public void addMinReadDepthFilter(JTextField minReadDepthTextField) {
		addFilter(new MinReadDepthFilter(minReadDepthTextField));
	}
	
	public void addReportedOnlyFilter(JCheckBox reportedOnlyCheckbox) {
		addFilter(new ReportedOnlyFilter(reportedOnlyCheckbox));
	}
	
	public void addVariantAlleleFrequencyFilter(JTextField textFreqFrom, JTextField textVarFreqTo) {
		addFilter(new VariantAlleleFrequencyFilter(textFreqFrom, textVarFreqTo));
	}
	
	public void addVariantPredicationClassFilter(JComboBox<VariantPredictionClass> predictionFilterComboBox) {
		addFilter(new VariantPredicationClassFilter(predictionFilterComboBox));
	}
}

interface Filter {
	public boolean exclude(Mutation mutation);
}

class CosmicIDFilter implements Filter{
	
	private JCheckBox cosmicIDCheckBox;
	
	public CosmicIDFilter(JCheckBox cosmicIDCheckBox) {
		this.cosmicIDCheckBox = cosmicIDCheckBox;
	}
	
	@Override
	public boolean exclude(Mutation mutation) {
		boolean includeCosmicOnly = cosmicIDCheckBox.isSelected();
		
		if(!includeCosmicOnly){
			return false;
		}
		
		if(mutation.getCosmicID() == null){
			return true;
		}
		
		return (mutation.getCosmicID().size() == 0);
	}
}

class MaxG1000FrequencyFilter implements Filter{

	private JTextField maxGAFTextField;
	
	public MaxG1000FrequencyFilter(JTextField maxGAFTextField) {
		this.maxGAFTextField = maxGAFTextField;
	}
	
	@Override
	public boolean exclude(Mutation mutation) {
		int maxPopulationFrequency = GUICommonTools.getNumber(maxGAFTextField, Configurations.MAX_GLOBAL_ALLELE_FREQ_FILTER);
		if(mutation.getAltGlobalFreq() != null){
			double populationFrequency = mutation.getAltGlobalFreq();
			if(maxPopulationFrequency < populationFrequency){
				return true;
			}
		}
		return false;
	}
}

class MaxGnomadFrequencyFilter implements Filter{

	private JTextField maxGAFTextField;
	
	public MaxGnomadFrequencyFilter(JTextField maxGAFTextField) {
		this.maxGAFTextField = maxGAFTextField;
	}
	
	@Override
	public boolean exclude(Mutation mutation) {
		int maxPopulationFrequency = GUICommonTools.getNumber(maxGAFTextField, Configurations.MAX_GLOBAL_ALLELE_FREQ_FILTER);
		if(mutation.getGnomad_allfreq() != null){
			double populationFrequency = mutation.getGnomad_allfreq();
			if(maxPopulationFrequency < populationFrequency){
				return true;
			}
		}
		return false;
	}
}

class MaxOccurrenceFilter implements Filter{

	private JTextField maxOccurrenceTextField;
	
	public MaxOccurrenceFilter(JTextField maxOccurrenceTextField) {
		this.maxOccurrenceTextField = maxOccurrenceTextField;
	}
	
	@Override
	public boolean exclude(Mutation mutation) {
		int maxOccurence = GUICommonTools.getNumber(maxOccurrenceTextField, Configurations.MIN_OCCURENCE_FILTER);
		if(mutation.getOccurrence() != null){
			int occurrence = mutation.getOccurrence();
			if(maxOccurence < occurrence){
				return true;
			}
		}
		return false;
	}
}

class MinReadDepthFilter implements Filter{

	private JTextField minReadDepthTextField;
	
	public MinReadDepthFilter(JTextField minReadDepthTextField) {
		this.minReadDepthTextField = minReadDepthTextField;
	}
	
	@Override
	public boolean exclude(Mutation mutation) {
		int minReadDepth = GUICommonTools.getNumber(minReadDepthTextField, Configurations.READ_DEPTH_FILTER);
		if(mutation.getReadDP() != null){
			int readDepth = mutation.getReadDP();
			if(minReadDepth > readDepth){
				return true;
			}
		}
		return false;
	}
}

class ReportedOnlyFilter implements Filter{
	
	private JCheckBox reportedOnlyCheckBox;
	
	public ReportedOnlyFilter(JCheckBox reportedOnlyCheckBox) {
		this.reportedOnlyCheckBox = reportedOnlyCheckBox;
	}
	
	@Override
	public boolean exclude(Mutation mutation) {
		boolean includeReportedOnly = reportedOnlyCheckBox.isSelected();
		if(includeReportedOnly && !mutation.isReported()) {
			return true;
		}
		return false;
	}
}

class VariantAlleleFrequencyFilter implements Filter{
	
	private JTextField frequencyFromTextField;
	private JTextField frequencyToTextField;	

	public VariantAlleleFrequencyFilter(JTextField frequencyFromTextField, JTextField frequencyToTextField) {
		this.frequencyFromTextField = frequencyFromTextField;
		this.frequencyToTextField = frequencyToTextField;
	}
	
	@Override
	public boolean exclude(Mutation mutation) {
		int frequencyFrom =  GUICommonTools.getNumber(frequencyFromTextField, Configurations.ALLELE_FREQ_FILTER);//TODO Base this on Configurations.getAlleleFrequencyFilter
		int frequencyTo = GUICommonTools.getNumber(frequencyToTextField, Configurations.MAX_ALLELE_FREQ_FILTER);
		
		double variantFrequency = mutation.getAltFreq();
		if(frequencyFrom > variantFrequency){
			return true;
		}
		
		if(frequencyTo < variantFrequency){
			return true;
		}
		return false;
	}
	
}

class VariantPredicationClassFilter implements Filter{

	private JComboBox<VariantPredictionClass> predictionFilterComboBox;
	
	public VariantPredicationClassFilter(JComboBox<VariantPredictionClass> predictionFilterComboBox) {
		this.predictionFilterComboBox = predictionFilterComboBox;
	}
	
	@Override
	public boolean exclude(Mutation mutation) {
		VariantPredictionClass minPredictionClass = (VariantPredictionClass)predictionFilterComboBox.getSelectedItem();
		if(mutation.getVariantPredictionClass() != null){
			if(mutation.getVariantPredictionClass().importance < minPredictionClass.importance){
				return true;
			}
		}
		return false;
	}

}