package hmvv.gui.mutationlist.tablemodels;

import hmvv.model.Annotation;
import hmvv.model.Mutation;
import hmvv.model.VariantPredictionClass;

public class MutationTableModelColumnGermline extends HMVVTableModelColumn{
	/*
	 * Column definitions to include description, title, Class, and getValue function
	 */

	public static final MutationTableModelColumnGermline chrColumn = new MutationTableModelColumnGermline("The chromosome location of this mutation",
			"chr",
			String.class,
			(Mutation mutation) -> mutation.getChr());

	public static final MutationTableModelColumnGermline posColumn = new MutationTableModelColumnGermline("Mutation position on the chromosome",
			"pos",
			String.class,
			(Mutation mutation) -> mutation.getPos());

	public static final MutationTableModelColumnGermline refColumn = new MutationTableModelColumnGermline("Reference base call",
			"ref",
			String.class,
			(Mutation mutation) -> mutation.getRef());

	public static final MutationTableModelColumnGermline altColumn = new MutationTableModelColumnGermline("Mutation base call",
			"alt",
			String.class,
			(Mutation mutation) -> mutation.getAlt());

    // custom

	public static final MutationTableModelColumnGermline reportedColumn = new MutationTableModelColumnGermline("Check to indicate this gene was included in the final report.",
			"reported",
			Boolean.class,
			(Mutation mutation) -> mutation.isReported());

	public static final MutationTableModelColumnGermline otherReportedColumn = new MutationTableModelColumnGermline("List of sample ID's for this patient that had this variant (R = reported)",
			"prev",
			String.class,
			(Mutation mutation) -> mutation.getOtherMutationsString());

	public static final MutationTableModelColumnGermline occurrenceColumn = new MutationTableModelColumnGermline("Number of previous occurrences of the detected variant in our database (excluding control samples).",
			"occurrence",
			Integer.class,
			(Mutation mutation) -> mutation.getOccurrence());

	public static final MutationTableModelColumnGermline annotationColumn = new MutationTableModelColumnGermline("The text entered by the pathologist to generate the clinical laboratory report.",
			"annotation",
			String.class,
			(Mutation mutation) -> getAnnotationDisplayText(mutation));

	public static String getAnnotationDisplayText(Mutation mutation) {
		Annotation latestAnnotation = mutation.getLatestAnnotation();
		if(latestAnnotation == null) {
			return "Enter";
		}
		return latestAnnotation.getDisplayText();
	}

	public static final MutationTableModelColumnGermline somaticColumn = new MutationTableModelColumnGermline("The somatic designation as entered by the pathologist in the annotation report.",
			"somatic",
			String.class,
			(Mutation mutation) -> getSomaticDisplayText(mutation));

	public static String getSomaticDisplayText(Mutation mutation) {
		Annotation latestAnnotation = mutation.getLatestAnnotation();
		if(latestAnnotation == null) {
			return "";
		}
		return latestAnnotation.getSomaticDisplayText();
	}

	public static final MutationTableModelColumnGermline classificationColumn = new MutationTableModelColumnGermline("The classification designation as entered by the pathologist in the annotation report.",
			"classification",
			String.class,
			(Mutation mutation) -> getClassificationDisplayText(mutation));

	public static String getClassificationDisplayText(Mutation mutation) {
		Annotation latestAnnotation = mutation.getLatestAnnotation();
		if(latestAnnotation == null) {
			return "";
		}
		return latestAnnotation.getClassificationDisplayText();
	}

	public static final MutationTableModelColumnGermline gotoIGVColumn = new MutationTableModelColumnGermline("Link to load the variant coordinate into IGV.",
			"IGV",
			String.class,
			(Mutation mutation) -> mutation.getChr() + ":" + mutation.getPos());

	public static final MutationTableModelColumnGermline igvLoadColumn = new MutationTableModelColumnGermline("Check to indicate this mutation is included for downloading BAM file.",
			"Load IGV",
			Boolean.class,
			(Mutation mutation) -> mutation.isSelected());

	//VEP

	public static final MutationTableModelColumnGermline geneColumn = new MutationTableModelColumnGermline("The name of the gene this mutation is located on.",
			"gene",
			String.class,
			(Mutation mutation) -> mutation.getGene());

	public static final MutationTableModelColumnGermline exonsColumn = new MutationTableModelColumnGermline("The exon location of the detected variant.",
			"exons",
			String.class,
			(Mutation mutation) -> mutation.getExons());

	public static final MutationTableModelColumnGermline typeColumn = new MutationTableModelColumnGermline("Variant type, including snv, deletion, insertion, indel.",
			"type",
			String.class,
			(Mutation mutation) -> mutation.getType());

	public static final MutationTableModelColumnGermline variantClassificationColumn = new MutationTableModelColumnGermline("Classifcation as predicted by Variant Effect Predictor.",
			"vep-Prediction",
			VariantPredictionClass.class,
			(Mutation mutation) -> mutation.getVariantPredictionClass());

	public static final MutationTableModelColumnGermline altFreqColumn = new MutationTableModelColumnGermline(
			"Thermo Fisher: Allele frequency based on Flow Evaluator observation counts. Illumina: The percentage of reads supporting the alternate allele.",
			"altFreq",
			Double.class,
			(Mutation mutation) -> mutation.getAltFreq());

	public static final MutationTableModelColumnGermline readDPColumn = new MutationTableModelColumnGermline(
			"Thermo Fisher: Flow Evaluator read depth at the locus to a position and used in variant calling. Illumina: Number of base calls aligned to a position and used in variant calling.",
			"readDP",
			Integer.class,
			(Mutation mutation) -> mutation.getReadDP());

	public static final MutationTableModelColumnGermline altReadDPColumn = new MutationTableModelColumnGermline(
			"Thermo Fisher: Flow Evaluator Alternate allele observations. Illumina: The number of alternate calls.",
			"altReadDP",
			Integer.class,
			(Mutation mutation) -> mutation.getAltReadDP());

	public static final MutationTableModelColumnGermline ConsequenceColumn = new MutationTableModelColumnGermline("Consquence of mutation as predicted by Variant Effect Predictor(VEP)",
			"vep-Consequence",
			String.class,
			(Mutation mutation) -> mutation.getConsequence());


	public static final MutationTableModelColumnGermline HGVScColumn = new MutationTableModelColumnGermline("Human Genome Variation Society Coding DNA nomenclature.",
			"HGVSc",
			String.class,
			(Mutation mutation) -> mutation.getHGVSc());

	public static final MutationTableModelColumnGermline HGVSpColumn = new MutationTableModelColumnGermline("Human Genome Variation Society Protein nomenclature.",
			"HGVSp",
			String.class,
			(Mutation mutation) -> mutation.getHGVSp());


    //clinvar

	public static final MutationTableModelColumnGermline clinvarID = new MutationTableModelColumnGermline("ClinVar Variation ID",
			"clinVarID",
			String.class,
			(Mutation mutation) -> mutation.getClinvarID());

	public static final MutationTableModelColumnGermline clinicalDisease = new MutationTableModelColumnGermline("ClinVar's preferred disease name for the concept specified by disease identifiers in CLNDISDB",
			"clinvar-Disease",
			String.class,
			(Mutation mutation) -> mutation.getClinicaldisease());

	public static final MutationTableModelColumnGermline clinicalSignificance = new MutationTableModelColumnGermline("Clinical significance for this single variant",
			"clinvar-Significance",
			String.class,
			(Mutation mutation) -> mutation.getClinicalsignificance());

	public static final MutationTableModelColumnGermline clinicalConsequence = new MutationTableModelColumnGermline("",//TODO Find this
			"clinvar-Consequence",
			String.class,
			(Mutation mutation) -> mutation.getClinicalconsequence());

	public static final MutationTableModelColumnGermline originColumn = new MutationTableModelColumnGermline("Allele origin",
			"clinvar-Origin",
			String.class,
			(Mutation mutation) -> mutation.getClinicalorigin());


	public static final MutationTableModelColumnGermline lastNameColumn = new MutationTableModelColumnGermline("The patient's last name.",
			"lastName",
			String.class,
			(Mutation mutation) -> mutation.getLastName());

	public static final MutationTableModelColumnGermline firstNameColumn = new MutationTableModelColumnGermline("The patient's first name.",
			"firstName",
			String.class,
			(Mutation mutation) -> mutation.getFirstName());

	public static final MutationTableModelColumnGermline orderNumberColumn = new MutationTableModelColumnGermline("The lab order number.",
			"orderNumber",
			String.class,
			(Mutation mutation) -> mutation.getOrderNumber());

	public static final MutationTableModelColumnGermline assayColumn = new MutationTableModelColumnGermline("The assay used",
			"assay",
			String.class,
			(Mutation mutation) -> mutation.getAssay());

	public static final MutationTableModelColumnGermline IDColumn = new MutationTableModelColumnGermline("The unique ID for the sample",
			"sampleID",
			Integer.class,
			(Mutation mutation) -> mutation.getSampleID());

	// gnomad

	public static final MutationTableModelColumnGermline gnomadID = new MutationTableModelColumnGermline("Identifier for gnomad db",
			"gnomadID",
			String.class,
			(Mutation mutation) -> mutation.getGnomadID());

	public static final MutationTableModelColumnGermline gnomadAltFreqColumn = new MutationTableModelColumnGermline("The global allele frequency",
			//"gnomad-AltFreq",
			"gnomad",//This will shrink the column width
			Double.class,
			(Mutation mutation) -> mutation.getGnomad_allfreq());


	/**
	 * The Lambda interface object
	 */
	private final MutationGetValueAtOperation operation;

	public MutationTableModelColumnGermline(String description, String title, Class<?> columnClass, MutationGetValueAtOperation operation) {
		super(description, title, columnClass);
		this.operation = operation;
	}
	
	/**
	 * Lambda expression function
	 */
	public Object getValue(Mutation mutation){
		return operation.getValue(mutation);
	}
	
	/**
	 * Lambda expression interface
	 *
	 */
	public interface MutationGetValueAtOperation{
		Object getValue(Mutation mutation);
	}
}
