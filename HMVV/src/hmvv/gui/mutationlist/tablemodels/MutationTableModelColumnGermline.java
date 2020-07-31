package hmvv.gui.mutationlist.tablemodels;

import hmvv.model.Annotation;
import hmvv.model.GermlineMutation;
import hmvv.model.VariantPredictionClass;

public class MutationTableModelColumnGermline extends HMVVTableModelColumn{
	/*
	 * Column definitions to include description, title, Class, and getValue function
	 */

	public static final MutationTableModelColumnGermline chrColumn = new MutationTableModelColumnGermline("The chromosome location of this mutation",
			"chr",
			String.class,
			(GermlineMutation mutation) -> mutation.getChr());

	public static final MutationTableModelColumnGermline posColumn = new MutationTableModelColumnGermline("Mutation position on the chromosome",
			"pos",
			String.class,
			(GermlineMutation mutation) -> mutation.getPos());

	public static final MutationTableModelColumnGermline refColumn = new MutationTableModelColumnGermline("Reference base call",
			"ref",
			String.class,
			(GermlineMutation mutation) -> mutation.getRef());

	public static final MutationTableModelColumnGermline altColumn = new MutationTableModelColumnGermline("Mutation base call",
			"alt",
			String.class,
			(GermlineMutation mutation) -> mutation.getAlt());

    // custom

	public static final MutationTableModelColumnGermline reportedColumn = new MutationTableModelColumnGermline("Check to indicate this gene was included in the final report.",
			"reported",
			Boolean.class,
			(GermlineMutation mutation) -> mutation.isReported());

	public static final MutationTableModelColumnGermline otherReportedColumn = new MutationTableModelColumnGermline("List of sample ID's for this patient that had this variant (R = reported)",
			"prev",
			String.class,
			(GermlineMutation mutation) -> mutation.getOtherMutationsString());

	public static final MutationTableModelColumnGermline occurrenceColumn = new MutationTableModelColumnGermline("Number of previous occurrences of the detected variant in our database (excluding control samples).",
			"occurrence",
			Integer.class,
			(GermlineMutation mutation) -> mutation.getOccurrence());

	public static final MutationTableModelColumnGermline annotationColumn = new MutationTableModelColumnGermline("The text entered by the pathologist to generate the clinical laboratory report.",
			"annotation",
			String.class,
			(GermlineMutation mutation) -> getAnnotationDisplayText(mutation));

	public static String getAnnotationDisplayText(GermlineMutation mutation) {
		Annotation latestAnnotation = mutation.getLatestAnnotation();
		if(latestAnnotation == null) {
			return "Enter";
		}
		return latestAnnotation.getDisplayText();
	}

	public static final MutationTableModelColumnGermline somaticColumn = new MutationTableModelColumnGermline("The somatic designation as entered by the pathologist in the annotation report.",
			"somatic",
			String.class,
			(GermlineMutation mutation) -> getSomaticDisplayText(mutation));

	public static String getSomaticDisplayText(GermlineMutation mutation) {
		Annotation latestAnnotation = mutation.getLatestAnnotation();
		if(latestAnnotation == null) {
			return "";
		}
		return latestAnnotation.getSomaticDisplayText();
	}

	public static final MutationTableModelColumnGermline classificationColumn = new MutationTableModelColumnGermline("The classification designation as entered by the pathologist in the annotation report.",
			"classification",
			String.class,
			(GermlineMutation mutation) -> getClassificationDisplayText(mutation));

	public static String getClassificationDisplayText(GermlineMutation mutation) {
		Annotation latestAnnotation = mutation.getLatestAnnotation();
		if(latestAnnotation == null) {
			return "";
		}
		return latestAnnotation.getClassificationDisplayText();
	}

	public static final MutationTableModelColumnGermline gotoIGVColumn = new MutationTableModelColumnGermline("Link to load the variant coordinate into IGV.",
			"IGV",
			String.class,
			(GermlineMutation mutation) -> mutation.getChr() + ":" + mutation.getPos());

	public static final MutationTableModelColumnGermline igvLoadColumn = new MutationTableModelColumnGermline("Check to indicate this mutation is included for downloading BAM file.",
			"Load IGV",
			Boolean.class,
			(GermlineMutation mutation) -> mutation.isSelected());

	//VEP

	public static final MutationTableModelColumnGermline geneColumn = new MutationTableModelColumnGermline("The name of the gene this mutation is located on.",
			"gene",
			String.class,
			(GermlineMutation mutation) -> mutation.getGene());

	public static final MutationTableModelColumnGermline exonsColumn = new MutationTableModelColumnGermline("The exon location of the detected variant.",
			"exons",
			String.class,
			(GermlineMutation mutation) -> mutation.getExons());

	public static final MutationTableModelColumnGermline typeColumn = new MutationTableModelColumnGermline("Variant type, including snv, deletion, insertion, indel.",
			"type",
			String.class,
			(GermlineMutation mutation) -> mutation.getType());

	public static final MutationTableModelColumnGermline variantClassificationColumn = new MutationTableModelColumnGermline("Classifcation as predicted by Variant Effect Predictor.",
			"snpEFF-Prediction",
			VariantPredictionClass.class,
			(GermlineMutation mutation) -> mutation.getVariantPredictionClass());

	public static final MutationTableModelColumnGermline altFreqColumn = new MutationTableModelColumnGermline(
			"Thermo Fisher: Allele frequency based on Flow Evaluator observation counts. Illumina: The percentage of reads supporting the alternate allele.",
			"altFreq",
			Double.class,
			(GermlineMutation mutation) -> mutation.getAltFreq());

	public static final MutationTableModelColumnGermline readDPColumn = new MutationTableModelColumnGermline(
			"Thermo Fisher: Flow Evaluator read depth at the locus to a position and used in variant calling. Illumina: Number of base calls aligned to a position and used in variant calling.",
			"readDP",
			Integer.class,
			(GermlineMutation mutation) -> mutation.getReadDP());

	public static final MutationTableModelColumnGermline altReadDPColumn = new MutationTableModelColumnGermline(
			"Thermo Fisher: Flow Evaluator Alternate allele observations. Illumina: The number of alternate calls.",
			"altReadDP",
			Integer.class,
			(GermlineMutation mutation) -> mutation.getAltReadDP());

	public static final MutationTableModelColumnGermline ConsequenceColumn = new MutationTableModelColumnGermline("Consquence of mutation as predicted by Variant Effect Predictor(VEP)",
			"snpEFF-Consequence",
			String.class,
			(GermlineMutation mutation) -> mutation.getConsequence());


	public static final MutationTableModelColumnGermline HGVScColumn = new MutationTableModelColumnGermline("Human Genome Variation Society Coding DNA nomenclature.",
			"HGVSc",
			String.class,
			(GermlineMutation mutation) -> mutation.getHGVSc());

	public static final MutationTableModelColumnGermline HGVSpColumn = new MutationTableModelColumnGermline("Human Genome Variation Society Protein nomenclature.",
			"HGVSp",
			String.class,
			(GermlineMutation mutation) -> mutation.getHGVSp());

    //transcript
	public static final MutationTableModelColumnGermline TRANSCRIPT_STRAND = new MutationTableModelColumnGermline("Strand direction of the selected transcript.",
			"Strand",
			String.class,
			(GermlineMutation mutation) -> mutation.getTranscript_strand());
	public static final MutationTableModelColumnGermline ALT_TRANSCRIPT_START = new MutationTableModelColumnGermline("Start genomic location of the selected transcript.",
			"Start",
			String.class,
			(GermlineMutation mutation) -> mutation.getAlt_transcript_start());

	public static final MutationTableModelColumnGermline ALT_TRANSCRIPT_END = new MutationTableModelColumnGermline("End genomic location of the selected transcript.",
			"End",
			String.class,
			(GermlineMutation mutation) -> mutation.getAlt_transcript_end());

	public static final MutationTableModelColumnGermline ALT_TRANSCRIPT_POSITION = new MutationTableModelColumnGermline("Position of variant in the selected transcript",
			"Position",
			String.class,
			(GermlineMutation mutation) -> mutation.getAlt_transcript_position());




	//clinvar

	public static final MutationTableModelColumnGermline clinvarID = new MutationTableModelColumnGermline("ClinVar Variation ID",
			"clinVarID",
			String.class,
			(GermlineMutation mutation) -> mutation.getClinvarID());

	public static final MutationTableModelColumnGermline clinicalDisease = new MutationTableModelColumnGermline("ClinVar's preferred disease name for the concept specified by disease identifiers in CLNDISDB",
			"Disease",
			String.class,
			(GermlineMutation mutation) -> mutation.getClinicaldisease());

	public static final MutationTableModelColumnGermline clinicalSignificance = new MutationTableModelColumnGermline("Clinical significance for this single variant",
			"Significance",
			String.class,
			(GermlineMutation mutation) -> mutation.getClinicalsignificance());

	public static final MutationTableModelColumnGermline clinicalConsequence = new MutationTableModelColumnGermline("",//TODO Find this
			"Consequence",
			String.class,
			(GermlineMutation mutation) -> mutation.getClinicalconsequence());

	public static final MutationTableModelColumnGermline originColumn = new MutationTableModelColumnGermline("Allele origin",
			"Origin",
			String.class,
			(GermlineMutation mutation) -> mutation.getClinicalorigin());


	public static final MutationTableModelColumnGermline lastNameColumn = new MutationTableModelColumnGermline("The patient's last name.",
			"lastName",
			String.class,
			(GermlineMutation mutation) -> mutation.getLastName());

	public static final MutationTableModelColumnGermline firstNameColumn = new MutationTableModelColumnGermline("The patient's first name.",
			"firstName",
			String.class,
			(GermlineMutation mutation) -> mutation.getFirstName());

	public static final MutationTableModelColumnGermline orderNumberColumn = new MutationTableModelColumnGermline("The lab order number.",
			"orderNumber",
			String.class,
			(GermlineMutation mutation) -> mutation.getOrderNumber());

	public static final MutationTableModelColumnGermline assayColumn = new MutationTableModelColumnGermline("The assay used",
			"assay",
			String.class,
			(GermlineMutation mutation) -> mutation.getAssay());

	public static final MutationTableModelColumnGermline IDColumn = new MutationTableModelColumnGermline("The unique ID for the sample",
			"sampleID",
			Integer.class,
			(GermlineMutation mutation) -> mutation.getSampleID());


	// gnomad
	public static final MutationTableModelColumnGermline gnomadIDColumn = new MutationTableModelColumnGermline("The global allele frequency",
			"GnomadID",
			String.class,
			(GermlineMutation mutation) -> mutation.getGnomad_id());

	public static final MutationTableModelColumnGermline gnomadAltFreqColumn = new MutationTableModelColumnGermline("The global allele frequency",
			"Global",
			Double.class,
			(GermlineMutation mutation) -> mutation.getGnomad_allfreq());

	public static final MutationTableModelColumnGermline gnomadAltFreq_afrColumn = new MutationTableModelColumnGermline("The AFR allele frequency",
			"African American",
			Double.class,
			(GermlineMutation mutation) -> mutation.getGnomad_allfreq_afr());

	public static final MutationTableModelColumnGermline gnomadAltFreq_amrColumn = new MutationTableModelColumnGermline("The AMR allele frequency",
			"American Admixed/Latino",
			Double.class,
			(GermlineMutation mutation) -> mutation.getGnomad_allfreq_amr());

	public static final MutationTableModelColumnGermline gnomadAltFreq_asjColumn = new MutationTableModelColumnGermline("The ASJ allele frequency",
			"Ashkenazi Jewish",
			Double.class,
			(GermlineMutation mutation) -> mutation.getGnomad_allfreq_asj());

	public static final MutationTableModelColumnGermline gnomadAltFreq_easColumn = new MutationTableModelColumnGermline("The EAS allele frequency",
			"East Asian",
			Double.class,
			(GermlineMutation mutation) -> mutation.getGnomad_allfreq_eas());

	public static final MutationTableModelColumnGermline gnomadAltFreq_finColumn = new MutationTableModelColumnGermline("The FIN allele frequency",
			"Finnish",
			Double.class,
			(GermlineMutation mutation) -> mutation.getGnomad_allfreq_fin());

	public static final MutationTableModelColumnGermline gnomadAltFreq_nfeColumn = new MutationTableModelColumnGermline("The NFE allele frequency",
			"Non-Finnish European",
			Double.class,
			(GermlineMutation mutation) -> mutation.getGnomad_allfreq_nfe());

	public static final MutationTableModelColumnGermline gnomadAltFreq_sasColumn = new MutationTableModelColumnGermline("The SAS allele frequency",
			"South Asian",
			Double.class,
			(GermlineMutation mutation) -> mutation.getGnomad_allfreq_sas());

	public static final MutationTableModelColumnGermline gnomadAltFreq_othColumn = new MutationTableModelColumnGermline("The OTHERS allele frequency",
			"Other",
			Double.class,
			(GermlineMutation mutation) -> mutation.getGnomad_allfreq_oth());


	public static final MutationTableModelColumnGermline gnomadAltFreq_maleColumn = new MutationTableModelColumnGermline("The MALE allele frequency",
			"Male",
			Double.class,
			(GermlineMutation mutation) -> mutation.getGnomad_allfreq_male());


	public static final MutationTableModelColumnGermline gnomadAltFreq_femaleColumn = new MutationTableModelColumnGermline("The FEMALE allele frequency",
			"Female",
			Double.class,
			(GermlineMutation mutation) -> mutation.getGnomad_allfreq_female());

	public static final MutationTableModelColumnGermline cardiacAtlasIDColumn = new MutationTableModelColumnGermline("The global allele frequency",
			"CardiacAtlasID",
			String.class,
			(GermlineMutation mutation) -> mutation.getCardiacAtlasId());


	public static final MutationTableModelColumnGermline cardiac_cdsVariant_Column = new MutationTableModelColumnGermline("The global allele frequency",
			"CDS-Variant",
			String.class,
			(GermlineMutation mutation) -> mutation.getCds_variant());

	public static final MutationTableModelColumnGermline cardiac_proteinVariant_Column = new MutationTableModelColumnGermline("The global allele frequency",
			"Protein-Variant",
			String.class,
			(GermlineMutation mutation) -> mutation.getProtein_variant());

	public static final MutationTableModelColumnGermline cardiac_variantType_Column = new MutationTableModelColumnGermline("The global allele frequency",
			"variant-type",
			String.class,
			(GermlineMutation mutation) -> mutation.getVariant_type());


	//protein domain

	public static final MutationTableModelColumnGermline protein_id_column = new MutationTableModelColumnGermline("The protein domain from nextprot database",
			"protein_id",
			String.class,
			(GermlineMutation mutation) -> mutation.getProtein_id());


	public static final MutationTableModelColumnGermline protein_type_column = new MutationTableModelColumnGermline("The protein domain from nextprot database",
			"protein_type",
			String.class,
			(GermlineMutation mutation) -> mutation.getProtein_type());


	public static final MutationTableModelColumnGermline protein_feature_column = new MutationTableModelColumnGermline("The protein domain from nextprot database",
			"protein_feature",
			String.class,
			(GermlineMutation mutation) -> mutation.getProtein_feature());

	public static final MutationTableModelColumnGermline protein_note_column = new MutationTableModelColumnGermline("The protein domain from nextprot database",
			"protein_note",
			String.class,
			(GermlineMutation mutation) -> mutation.getProtein_note());

	public static final MutationTableModelColumnGermline protein_start_column = new MutationTableModelColumnGermline("The protein domain from nextprot database",
			"protein_start",
			Double.class,
			(GermlineMutation mutation) -> mutation.getProtein_start());

	public static final MutationTableModelColumnGermline protein_end_column = new MutationTableModelColumnGermline("The protein domain from nextprot database",
			"protein_end",
			Double.class,
			(GermlineMutation mutation) -> mutation.getProtein_end());

	public static final MutationTableModelColumnGermline nextprot_column = new MutationTableModelColumnGermline("The protein domain from nextprot database",
			"nextprot",
			String.class,
			(GermlineMutation mutation) -> mutation.getNextprot());


	public static final MutationTableModelColumnGermline uniprot_column = new MutationTableModelColumnGermline("The protein domain from uniprot database",
			"uniprot",
			String.class,
			(GermlineMutation mutation) -> mutation.getUniprot_id());

	public static final MutationTableModelColumnGermline pfam_column = new MutationTableModelColumnGermline("The protein domain from pfam database",
			"pfam",
			String.class,
			(GermlineMutation mutation) -> mutation.getPfam());

	public static final MutationTableModelColumnGermline scoop_column = new MutationTableModelColumnGermline("The protein domain from scoop database",
			"scoop",
			String.class,
			(GermlineMutation mutation) -> mutation.getScoop());
	public static final MutationTableModelColumnGermline uniprot_variant_column = new MutationTableModelColumnGermline("The protein domain from nextprot variant database",
			"uniprot_variant",
			String.class,
			(GermlineMutation mutation) -> mutation.getUniprot_variant());
	public static final MutationTableModelColumnGermline expasy_column = new MutationTableModelColumnGermline("The protein domain from expasy database",
			"expasy",
			String.class,
			(GermlineMutation mutation) -> mutation.getExpasy_id());

	//variant prediction
	public static final MutationTableModelColumnGermline revel_column = new MutationTableModelColumnGermline("The variant effect prediction from revel database",
			"revel",
			String.class,
			(GermlineMutation mutation) -> mutation.getRevel());

	public static final MutationTableModelColumnGermline cadd_phred_column = new MutationTableModelColumnGermline("The variant effect prediction from cadd database",
			"cadd_phred",
			String.class,
			(GermlineMutation mutation) -> mutation.getCadd_phred());

	public static final MutationTableModelColumnGermline canonical_column = new MutationTableModelColumnGermline("The variant effect prediction from canonical database",
			"canonical",
			String.class,
			(GermlineMutation mutation) -> mutation.getCanonical());
	public static final MutationTableModelColumnGermline sift_column = new MutationTableModelColumnGermline("The variant effect prediction from sift database",
			"sift",
			String.class,
			(GermlineMutation mutation) -> mutation.getSift());

	public static final MutationTableModelColumnGermline polyphen_column = new MutationTableModelColumnGermline("The variant effect prediction from polyphen database",
			"polyphen",
			String.class,
			(GermlineMutation mutation) -> mutation.getPolyphen());


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
	public Object getValue(GermlineMutation mutation){
		return operation.getValue(mutation);
	}
	
	/**
	 * Lambda expression interface
	 *
	 */
	public interface MutationGetValueAtOperation{
		Object getValue(GermlineMutation mutation);
	}
}
