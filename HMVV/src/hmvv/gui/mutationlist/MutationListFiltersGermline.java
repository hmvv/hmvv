package hmvv.gui.mutationlist;

import hmvv.gui.GUICommonTools;
import hmvv.main.Configurations;
import hmvv.model.GermlineMutation;
import hmvv.model.Mutation;
import hmvv.model.VariantPredictionClass;

import javax.swing.*;
import java.util.ArrayList;

public class MutationListFiltersGermline {
	
	private ArrayList<GermlineFilter> filters;
	
	public MutationListFiltersGermline() {
		this.filters = new ArrayList<GermlineFilter>();
	}
	
	private void addFilter(GermlineFilter filter) {
		filters.add(filter);
	}
	
	public void filterMutations(ArrayList<GermlineMutation> mutations, ArrayList<GermlineMutation> filteredMutations){
		ArrayList<GermlineMutation> allMutations = new ArrayList<GermlineMutation>(mutations.size() + filteredMutations.size());
		allMutations.addAll(mutations);
		allMutations.addAll(filteredMutations);

		ArrayList<GermlineMutation> newFilteredMutations = new ArrayList<GermlineMutation>();
		
		for(int i = 0; i < allMutations.size(); i++){
			GermlineMutation mutation = allMutations.get(i);

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

	private boolean includeMutation(GermlineMutation mutation){
		for(GermlineFilter f : filters) {
			if(f.exclude(mutation)) {
				return false;
			}
		}
		return true;
	}

	public void addMaxGnomadFrequencyFilter(JTextField maxPopulationFrequencyGnomadTextField) {
		addFilter(new MaxGnomadFrequencyGermlineFilter(maxPopulationFrequencyGnomadTextField));
	}
	
	public void addMaxOccurrenceFilter(JTextField occurenceFromTextField) {
		addFilter(new MaxOccurrenceGermlineFilter(occurenceFromTextField));
	}
	
	public void addMinReadDepthFilter(JTextField minReadDepthTextField) {
		addFilter(new MinReadDepthGermlineFilter(minReadDepthTextField));
	}
	
	public void addReportedOnlyFilter(JCheckBox reportedOnlyCheckbox) {
		addFilter(new ReportedOnlyGermlineFilter(reportedOnlyCheckbox));
	}
	
	public void addVariantAlleleFrequencyFilter(JTextField textFreqFrom, JTextField textVarFreqTo) {
		addFilter(new VariantAlleleFrequencyGermlineFilter(textFreqFrom, textVarFreqTo));
	}
	
	public void addVariantPredicationClassFilter(JComboBox<VariantPredictionClass> predictionFilterComboBox) {
		addFilter(new VariantPredicationClassGermlineFilter(predictionFilterComboBox));
	}

	public void addTranscriptFlagGermlineFilter(JCheckBox transcriptFlagCheckBox) {
		addFilter(new TranscriptFlagGermlineFilter(transcriptFlagCheckBox));
	}

	public void addSynonymousFlagGermlineFilter(JCheckBox synonymousFlagCheckBox) {
		addFilter(new SynonymousFlagGermlineFilter(synonymousFlagCheckBox));
	}
}

interface GermlineFilter {
	public boolean exclude(GermlineMutation mutation);
}

class MaxGnomadFrequencyGermlineFilter implements GermlineFilter{

	private JTextField maxGAFTextField;
	
	public MaxGnomadFrequencyGermlineFilter(JTextField maxGAFTextField) {
		this.maxGAFTextField = maxGAFTextField;
	}
	
	@Override
	public boolean exclude(GermlineMutation mutation) {
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

class MaxOccurrenceGermlineFilter implements GermlineFilter{

	private JTextField maxOccurrenceTextField;
	
	public MaxOccurrenceGermlineFilter(JTextField maxOccurrenceTextField) {
		this.maxOccurrenceTextField = maxOccurrenceTextField;
	}
	
	@Override
	public boolean exclude(GermlineMutation mutation) {
		int maxOccurence = GUICommonTools.getNumber(maxOccurrenceTextField, Configurations.MAX_OCCURENCE_FILTER);
		if(mutation.getOccurrence() != null){
			int occurrence = mutation.getOccurrence();
			if(maxOccurence < occurrence){
				return true;
			}
		}
		return false;
	}
}

class MinReadDepthGermlineFilter implements GermlineFilter{

	private JTextField minReadDepthTextField;
	
	public MinReadDepthGermlineFilter(JTextField minReadDepthTextField) {
		this.minReadDepthTextField = minReadDepthTextField;
	}
	
	@Override
	public boolean exclude(GermlineMutation mutation) {
		int minReadDepth = GUICommonTools.getNumber(minReadDepthTextField, Configurations.GERMLINE_READ_DEPTH_FILTER);
		if(mutation.getReadDP() != null){
			int readDepth = mutation.getReadDP();
			if(minReadDepth > readDepth){
				return true;
			}
		}
		return false;
	}
}

class ReportedOnlyGermlineFilter implements GermlineFilter{
	
	private JCheckBox reportedOnlyCheckBox;
	
	public ReportedOnlyGermlineFilter(JCheckBox reportedOnlyCheckBox) {
		this.reportedOnlyCheckBox = reportedOnlyCheckBox;
	}
	
	@Override
	public boolean exclude(GermlineMutation mutation) {
		boolean includeReportedOnly = reportedOnlyCheckBox.isSelected();
		if(includeReportedOnly && !mutation.isReported()) {
			return true;
		}
		return false;
	}
}

class VariantAlleleFrequencyGermlineFilter implements GermlineFilter{
	
	private JTextField frequencyFromTextField;
	private JTextField frequencyToTextField;	

	public VariantAlleleFrequencyGermlineFilter(JTextField frequencyFromTextField, JTextField frequencyToTextField) {
		this.frequencyFromTextField = frequencyFromTextField;
		this.frequencyToTextField = frequencyToTextField;
	}
	
	@Override
	public boolean exclude(GermlineMutation mutation) {
		int frequencyFrom =  GUICommonTools.getNumber(frequencyFromTextField, Configurations.GERMLINE_ALLELE_FREQ_FILTER);//TODO Base this on Configurations.getAlleleFrequencyFilter
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

class VariantPredicationClassGermlineFilter implements GermlineFilter{

	private JComboBox<VariantPredictionClass> predictionFilterComboBox;
	
	public VariantPredicationClassGermlineFilter(JComboBox<VariantPredictionClass> predictionFilterComboBox) {
		this.predictionFilterComboBox = predictionFilterComboBox;
	}
	
	@Override
	public boolean exclude(GermlineMutation mutation) {
		VariantPredictionClass minPredictionClass = (VariantPredictionClass)predictionFilterComboBox.getSelectedItem();
		if(mutation.getVariantPredictionClass() != null){
			if(mutation.getVariantPredictionClass().importance < minPredictionClass.importance){
				return true;
			}
		}
		return false;
	}

}

class TranscriptFlagGermlineFilter implements GermlineFilter{

	private JCheckBox transcriptFlagCheckBox;

	public TranscriptFlagGermlineFilter(JCheckBox transcriptFlagCheckBox) {
		this.transcriptFlagCheckBox = transcriptFlagCheckBox;
	}

	@Override
	public boolean exclude(GermlineMutation mutation) {
		boolean includeTranscriptFlagOnly = transcriptFlagCheckBox.isSelected();
		if(includeTranscriptFlagOnly && mutation.getAlt_transcript_position().equals("MIDDLE")) {
			return true;
		}
		return false;
	}
}

class SynonymousFlagGermlineFilter implements GermlineFilter{

	private JCheckBox synonymousFlagCheckBox;

	public SynonymousFlagGermlineFilter(JCheckBox synonymousFlagCheckBox) {
		this.synonymousFlagCheckBox = synonymousFlagCheckBox;
	}

	@Override
	public boolean exclude(GermlineMutation mutation) {
		boolean includeSynonymousFlagOnly = synonymousFlagCheckBox.isSelected();
		if(includeSynonymousFlagOnly && mutation.getConsequence().equals("synonymous_variant")) {
			return true;
		}
		return false;
	}
}