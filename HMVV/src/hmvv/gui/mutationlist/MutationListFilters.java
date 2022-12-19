package hmvv.gui.mutationlist;

import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import hmvv.gui.GUICommonTools;
import hmvv.main.Configurations;
import hmvv.model.MutationCommon;
import hmvv.model.MutationGermline;
import hmvv.model.MutationSomatic;
import hmvv.model.Sample;
import hmvv.model.VariantPredictionClass;

public class MutationListFilters {
	
	private ArrayList<Filter> filters;
	
	public MutationListFilters() {
		this.filters = new ArrayList<Filter>();
	}
	
	private void addFilter(Filter filter) {
		filters.add(filter);
	}
	
	public void filterMutations(ArrayList<MutationCommon> mutations, ArrayList<MutationCommon> filteredMutations){
		ArrayList<MutationCommon> allMutations = new ArrayList<MutationCommon>(mutations.size() + filteredMutations.size());
		allMutations.addAll(mutations);
		allMutations.addAll(filteredMutations);

		ArrayList<MutationCommon> newFilteredMutations = new ArrayList<MutationCommon>();
		
		for(int i = 0; i < allMutations.size(); i++){
			MutationCommon mutation = allMutations.get(i);

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

	private boolean includeMutation(MutationCommon mutation){
		for(Filter f : filters) {
			if(f.exclude(mutation)) {
				return false;
			}
		}
		return true;
	}
	

	//common
	public void addMaxOccurrenceFilter(JTextField occurenceFromTextField) {
		addFilter(new MaxOccurrenceFilter(occurenceFromTextField));
	}

	public void addReportedOnlyFilter(JCheckBox reportedOnlyCheckbox) {
		addFilter(new ReportedOnlyFilter(reportedOnlyCheckbox));
	}

	public void addVariantPredicationClassFilter(JComboBox<VariantPredictionClass> predictionFilterComboBox) {
		addFilter(new VariantPredicationClassFilter(predictionFilterComboBox));
	}


	//somatic
	public void addCosmicIDFilter(JCheckBox cosmicIDCheckBox) {
		addFilter(new CosmicIDFilter(cosmicIDCheckBox));
	}
	
	public void addMaxG1000FrequencyFilter(JTextField maxPopulationFrequencyG1000TextField) {
		addFilter(new MaxG1000FrequencyFilter(maxPopulationFrequencyG1000TextField));
	}
	
	public void addMaxGnomadFrequencyFilter(JTextField maxPopulationFrequencyGnomadTextField) {
		addFilter(new MaxGnomadFrequencyFilter(maxPopulationFrequencyGnomadTextField));
	}

	public void addMinReadDepthFilter(JTextField minReadDepthTextField) {
		addFilter(new MinReadDepthFilter(minReadDepthTextField));
	}
	
	public void addVariantAlleleFrequencyFilter(Sample sample, JTextField textFreqFrom, JTextField textVarFreqTo) {
		addFilter(new VariantAlleleFrequencyFilter(sample, textFreqFrom, textVarFreqTo));
	}


	//germline
	public void addMinReadDepthGermlineFilter(JTextField minReadDepthTextField) {
		addFilter(new MinReadDepthGermlineFilter(minReadDepthTextField));
	}

	public void addVariantAlleleFrequencyGermlineFilter(JTextField textFreqFrom, JTextField textVarFreqTo) {
		addFilter(new VariantAlleleFrequencyGermlineFilter(textFreqFrom, textVarFreqTo));
	}

	public void addMaxGnomadFrequencyGermlineFilter (JTextField maxPopulationFrequencyGnomadTextField) {
		addFilter(new MaxGnomadFrequencyGermlineFilter(maxPopulationFrequencyGnomadTextField));
	}

	public void addTranscriptFlagGermlineFilter(JCheckBox transcriptFlagCheckBox) {
		addFilter(new TranscriptFlagGermlineFilter(transcriptFlagCheckBox));
	}

	public void addSynonymousFlagGermlineFilter(JCheckBox synonymousFlagCheckBox) {
		addFilter(new SynonymousFlagGermlineFilter(synonymousFlagCheckBox));
	}
}

interface Filter {
	public boolean exclude(MutationCommon mutation);
}

//common
class VariantPredicationClassFilter implements Filter{

	private JComboBox<VariantPredictionClass> predictionFilterComboBox;

	public VariantPredicationClassFilter(JComboBox<VariantPredictionClass> predictionFilterComboBox) {
		this.predictionFilterComboBox = predictionFilterComboBox;
	}

	@Override
	public boolean exclude(MutationCommon mutation) {
		VariantPredictionClass minPredictionClass = (VariantPredictionClass)predictionFilterComboBox.getSelectedItem();
		if(mutation.getVariantPredictionClass() != null){
			if(mutation.getVariantPredictionClass().importance < minPredictionClass.importance){
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
	public boolean exclude(MutationCommon mutation) {
		boolean includeReportedOnly = reportedOnlyCheckBox.isSelected();
		if(includeReportedOnly && !mutation.isReported()) {
			return true;
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
	public boolean exclude(MutationCommon mutation) {
		int maxOccurence = GUICommonTools.parseIntegerFromTextField(maxOccurrenceTextField, Configurations.MAX_OCCURENCE_FILTER);
		if(mutation.getOccurrence() != null){
			int occurrence = mutation.getOccurrence();
			if(maxOccurence < occurrence){
				return true;
			}
		}
		return false;
	}
}


//somatic

class MinReadDepthFilter implements Filter{

	private JTextField minReadDepthTextField;

	public MinReadDepthFilter(JTextField minReadDepthTextField) {
		this.minReadDepthTextField = minReadDepthTextField;
	}

	@Override
	public boolean exclude(MutationCommon mutation) {
		int minReadDepth = GUICommonTools.parseIntegerFromTextField(minReadDepthTextField, Configurations.READ_DEPTH_FILTER);
		if(mutation.getReadDP() != null){
			int readDepth = mutation.getReadDP();
			if(minReadDepth > readDepth){
				return true;
			}
		}
		return false;
	}
}

class VariantAlleleFrequencyFilter implements Filter{

	private Sample sample;
	private JTextField frequencyFromTextField;
	private JTextField frequencyToTextField;

	public VariantAlleleFrequencyFilter(Sample sample, JTextField frequencyFromTextField, JTextField frequencyToTextField) {
		this.sample = sample;
		this.frequencyFromTextField = frequencyFromTextField;
		this.frequencyToTextField = frequencyToTextField;
	}

	@Override
	public boolean exclude(MutationCommon mutation) {
		double frequencyFrom =  GUICommonTools.parseDoubleFromTextField(frequencyFromTextField, Configurations.getDefaultAlleleFrequencyFilter(sample));
		double frequencyTo = GUICommonTools.parseDoubleFromTextField(frequencyToTextField, Configurations.MAX_ALLELE_FREQ_FILTER);

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

class CosmicIDFilter implements Filter{
	
	private JCheckBox cosmicIDCheckBox;
	
	public CosmicIDFilter(JCheckBox cosmicIDCheckBox) {
		this.cosmicIDCheckBox = cosmicIDCheckBox;
	}
	
	@Override
	public boolean exclude(MutationCommon mutation) {

		MutationSomatic current_mutation = (MutationSomatic)mutation;

		boolean includeCosmicOnly = cosmicIDCheckBox.isSelected();
		
		if(!includeCosmicOnly){
			return false;
		}
		
		return current_mutation.cosmicIDsToString().equals("");
	}
}

class MaxG1000FrequencyFilter implements Filter{

	private JTextField maxGAFTextField;
	
	public MaxG1000FrequencyFilter(JTextField maxGAFTextField) {
		this.maxGAFTextField = maxGAFTextField;
	}
	
	@Override
	public boolean exclude(MutationCommon mutation) {

		MutationSomatic current_mutation = (MutationSomatic)mutation;

		int maxPopulationFrequency = GUICommonTools.parseIntegerFromTextField(maxGAFTextField, Configurations.MAX_GLOBAL_ALLELE_FREQ_FILTER);
		if(current_mutation.getAltGlobalFreq() != null){
			double populationFrequency = ((MutationSomatic)mutation).getAltGlobalFreq();
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
	public boolean exclude(MutationCommon mutation) {

		MutationSomatic current_mutation = (MutationSomatic)mutation;

		int maxPopulationFrequency = GUICommonTools.parseIntegerFromTextField(maxGAFTextField, Configurations.MAX_GLOBAL_ALLELE_FREQ_FILTER);
		if(current_mutation.getGnomad_allfreq() != null){
			double populationFrequency = current_mutation.getGnomad_allfreq();
			if(maxPopulationFrequency < populationFrequency){
				return true;
			}
		}
		return false;
	}
}


//germline

class MinReadDepthGermlineFilter implements Filter{

	private JTextField minReadDepthTextField;

	public MinReadDepthGermlineFilter(JTextField minReadDepthTextField) {
		this.minReadDepthTextField = minReadDepthTextField;
	}

	@Override
	public boolean exclude(MutationCommon mutation) {
		int minReadDepth = GUICommonTools.parseIntegerFromTextField(minReadDepthTextField, Configurations.GERMLINE_READ_DEPTH_FILTER);
		if(mutation.getReadDP() != null){
			int readDepth = mutation.getReadDP();
			if(minReadDepth > readDepth){
				return true;
			}
		}
		return false;
	}
}

class VariantAlleleFrequencyGermlineFilter implements Filter{

	private JTextField frequencyFromTextField;
	private JTextField frequencyToTextField;

	public VariantAlleleFrequencyGermlineFilter(JTextField frequencyFromTextField, JTextField frequencyToTextField) {
		this.frequencyFromTextField = frequencyFromTextField;
		this.frequencyToTextField = frequencyToTextField;
	}

	@Override
	public boolean exclude(MutationCommon mutation) {
		int frequencyFrom =  GUICommonTools.parseIntegerFromTextField(frequencyFromTextField, Configurations.GERMLINE_ALLELE_FREQ_FILTER);//TODO Base this on Configurations.getAlleleFrequencyFilter
		int frequencyTo = GUICommonTools.parseIntegerFromTextField(frequencyToTextField, Configurations.MAX_ALLELE_FREQ_FILTER);

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

class MaxGnomadFrequencyGermlineFilter implements Filter{

	private JTextField maxGAFTextField;

	public MaxGnomadFrequencyGermlineFilter(JTextField maxGAFTextField) {
		this.maxGAFTextField = maxGAFTextField;
	}

	@Override
	public boolean exclude(MutationCommon mutation) {

		MutationGermline current_mutation = (MutationGermline)mutation;

		int maxPopulationFrequency = GUICommonTools.parseIntegerFromTextField(maxGAFTextField, Configurations.MAX_GLOBAL_ALLELE_FREQ_FILTER);
		if(current_mutation.getGnomad_allfreq() != null){
			double populationFrequency = current_mutation.getGnomad_allfreq();
			if(maxPopulationFrequency < populationFrequency){
				return true;
			}
		}
		return false;
	}
}

class TranscriptFlagGermlineFilter implements Filter{

	private JCheckBox transcriptFlagCheckBox;

	public TranscriptFlagGermlineFilter(JCheckBox transcriptFlagCheckBox) {
		this.transcriptFlagCheckBox = transcriptFlagCheckBox;
	}

	@Override
	public boolean exclude(MutationCommon mutation) {

		MutationGermline current_mutation = (MutationGermline)mutation;

		boolean includeTranscriptFlagOnly = transcriptFlagCheckBox.isSelected();
		if(includeTranscriptFlagOnly && current_mutation.getAlt_transcript_position().equals("MIDDLE")) {
			return true;
		}
		return false;
	}
}

class SynonymousFlagGermlineFilter implements Filter{

	private JCheckBox synonymousFlagCheckBox;

	public SynonymousFlagGermlineFilter(JCheckBox synonymousFlagCheckBox) {
		this.synonymousFlagCheckBox = synonymousFlagCheckBox;
	}

	@Override
	public boolean exclude(MutationCommon mutation) {

		MutationGermline current_mutation = (MutationGermline)mutation;

		boolean includeSynonymousFlagOnly = synonymousFlagCheckBox.isSelected();
		if(includeSynonymousFlagOnly && current_mutation.getConsequence().equals("synonymous_variant")) {
			return true;
		}
		return false;
	}
}