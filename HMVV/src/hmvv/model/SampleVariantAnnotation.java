package hmvv.model;

import java.util.Date;

import hmvv.main.Configurations.MUTATION_SOMATIC_HISTORY;
import hmvv.main.Configurations.MUTATION_TIER;

public class SampleVariantAnnotation extends CommonAnnotation{
	
	public final MutationCommon mutation;
	public final MUTATION_TIER mutation_tier;
	public final MUTATION_SOMATIC_HISTORY mutation_somatic_history;
	public final String curation;
	public final boolean possibleGermline;
	
	public SampleVariantAnnotation(Integer annotationID, MutationCommon mutation, MUTATION_TIER mutation_tier, MUTATION_SOMATIC_HISTORY mutation_somatic_history, boolean possibleGermline, String curation, String enteredBy, Date enterDate) {
		super(annotationID, enteredBy, enterDate);
		this.mutation = mutation;
		this.mutation_tier = mutation_tier;
		this.mutation_somatic_history = mutation_somatic_history;
		this.curation = curation;
		this.possibleGermline = possibleGermline;
	}
	
	public SampleVariantAnnotation(MutationCommon mutation, MUTATION_TIER mutation_tier, MUTATION_SOMATIC_HISTORY mutation_somatic_history, boolean possibleGermline, String curation, String enteredBy, Date enterDate) {
		this(-1, mutation, mutation_tier, mutation_somatic_history, possibleGermline, curation, enteredBy, enterDate);
	}

	public boolean equals(Object o) {
		if(o instanceof SampleVariantAnnotation) {
			SampleVariantAnnotation other = (SampleVariantAnnotation) o;
			if(!other.mutation.getSampleVariantID().equals(mutation.getSampleVariantID())) {
				return false;
			}
			if(!other.mutation_tier.equals(mutation_tier)) {
				return false;
			}
			if(!other.mutation_somatic_history.equals(mutation_somatic_history)) {
				return false;
			}
			if(!other.possibleGermline == possibleGermline) {
				return false;
			}
			if(!other.curation.equals(curation)) {
				return false;
			}
			return true;
		}
		return false;
	}
}
