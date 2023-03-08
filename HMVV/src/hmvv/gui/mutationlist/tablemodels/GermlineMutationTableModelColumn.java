package hmvv.gui.mutationlist.tablemodels;

import hmvv.model.Annotation;
import hmvv.model.MutationGermline;
import hmvv.model.VariantPredictionClass;

public class GermlineMutationTableModelColumn extends HMVVTableModelColumn{
	/*
	 * Column definitions to include description, title, Class, and getValue function
	 */

	public static final GermlineMutationTableModelColumn chrColumn = new GermlineMutationTableModelColumn("The chromosome location of this mutation",
			"chr",
			String.class,
			(MutationGermline mutation) -> mutation.getChr());

	public static final GermlineMutationTableModelColumn posColumn = new GermlineMutationTableModelColumn("Mutation position on the chromosome",
			"pos",
			String.class,
			(MutationGermline mutation) -> mutation.getPos());

	public static final GermlineMutationTableModelColumn refColumn = new GermlineMutationTableModelColumn("Reference base call",
			"ref",
			String.class,
			(MutationGermline mutation) -> mutation.getRef());

	public static final GermlineMutationTableModelColumn altColumn = new GermlineMutationTableModelColumn("Mutation base call",
			"alt",
			String.class,
			(MutationGermline mutation) -> mutation.getAlt());

    // custom

	public static final GermlineMutationTableModelColumn reportedColumn = new GermlineMutationTableModelColumn("Check to indicate this gene was included in the final report.",
			"reported",
			Boolean.class,
			(MutationGermline mutation) -> mutation.isReported());

	public static final GermlineMutationTableModelColumn otherReportedColumn = new GermlineMutationTableModelColumn("List of sample ID's for this patient that had this variant (R = reported)",
			"prev",
			String.class,
			(MutationGermline mutation) -> mutation.getOtherMutationsString());

	public static final GermlineMutationTableModelColumn occurrenceColumn = new GermlineMutationTableModelColumn("Number of previous occurrences of the detected variant in our database (excluding control samples).",
			"occurrence",
			Integer.class,
			(MutationGermline mutation) -> mutation.getOccurrence());

	public static final GermlineMutationTableModelColumn annotationColumn = new GermlineMutationTableModelColumn("The text entered by the pathologist to generate the clinical laboratory report.",
			"annotation",
			String.class,
			(MutationGermline mutation) -> getAnnotationDisplayText(mutation));

	public static String getAnnotationDisplayText(MutationGermline mutation) {
		Annotation latestAnnotation = mutation.getLatestAnnotation();
		if(latestAnnotation == null) {
			return "Enter";
		}
		return latestAnnotation.getDisplayText();
	}

	public static final GermlineMutationTableModelColumn somaticColumn = new GermlineMutationTableModelColumn("The somatic designation as entered by the pathologist in the annotation report.",
			"somatic",
			String.class,
			(MutationGermline mutation) -> getSomaticDisplayText(mutation));

	public static String getSomaticDisplayText(MutationGermline mutation) {
		Annotation latestAnnotation = mutation.getLatestAnnotation();
		if(latestAnnotation == null) {
			return "";
		}
		return latestAnnotation.getSomaticDisplayText();
	}

	public static final GermlineMutationTableModelColumn classificationColumn = new GermlineMutationTableModelColumn("The classification designation as entered by the pathologist in the annotation report.",
			"classification",
			String.class,
			(MutationGermline mutation) -> getClassificationDisplayText(mutation));

	public static String getClassificationDisplayText(MutationGermline mutation) {
		Annotation latestAnnotation = mutation.getLatestAnnotation();
		if(latestAnnotation == null) {
			return "";
		}
		return latestAnnotation.getClassificationDisplayText();
	}

	public static final GermlineMutationTableModelColumn gotoIGVColumn = new GermlineMutationTableModelColumn("Link to load the variant coordinate into IGV.",
			"IGV",
			String.class,
			(MutationGermline mutation) -> mutation.getChr() + ":" + mutation.getPos());

	public static final GermlineMutationTableModelColumn igvLoadColumn = new GermlineMutationTableModelColumn("Check to indicate this mutation is included for downloading BAM file.",
			"Load IGV",
			Boolean.class,
			(MutationGermline mutation) -> mutation.isSelected());

	//VEP

	public static final GermlineMutationTableModelColumn geneColumn = new GermlineMutationTableModelColumn("The name of the gene this mutation is located on.",
			"gene",
			String.class,
			(MutationGermline mutation) -> mutation.getGene());

	public static final GermlineMutationTableModelColumn exonsColumn = new GermlineMutationTableModelColumn("The exon location of the detected variant.",
			"exons",
			String.class,
			(MutationGermline mutation) -> mutation.getExons());

	public static final GermlineMutationTableModelColumn typeColumn = new GermlineMutationTableModelColumn("Variant type, including snv, deletion, insertion, indel.",
			"type",
			String.class,
			(MutationGermline mutation) -> mutation.getType());

	public static final GermlineMutationTableModelColumn variantClassificationColumn = new GermlineMutationTableModelColumn("Classifcation as predicted by Variant Effect Predictor.",
			"snpeff prediction",
			VariantPredictionClass.class,
			(MutationGermline mutation) -> mutation.getVariantPredictionClass());

	public static final GermlineMutationTableModelColumn altFreqColumn = new GermlineMutationTableModelColumn(
			"Thermo Fisher: Allele frequency based on Flow Evaluator observation counts. Illumina: The percentage of reads supporting the alternate allele.",
			"alt Freq",
			Double.class,
			(MutationGermline mutation) -> mutation.getAltFreq());

	public static final GermlineMutationTableModelColumn readDPColumn = new GermlineMutationTableModelColumn(
			"Thermo Fisher: Flow Evaluator read depth at the locus to a position and used in variant calling. Illumina: Number of base calls aligned to a position and used in variant calling.",
			"read DP",
			Integer.class,
			(MutationGermline mutation) -> mutation.getReadDP());

	public static final GermlineMutationTableModelColumn altReadDPColumn = new GermlineMutationTableModelColumn(
			"Thermo Fisher: Flow Evaluator Alternate allele observations. Illumina: The number of alternate calls.",
			"altread DP",
			Integer.class,
			(MutationGermline mutation) -> mutation.getAltReadDP());

	public static final GermlineMutationTableModelColumn ConsequenceColumn = new GermlineMutationTableModelColumn("Consquence of mutation as predicted by Variant Effect Predictor(VEP)",
			"snpeff consequence",
			String.class,
			(MutationGermline mutation) -> mutation.getConsequence());


	public static final GermlineMutationTableModelColumn HGVScColumn = new GermlineMutationTableModelColumn("Human Genome Variation Society Coding DNA nomenclature.",
			"HGVSc",
			String.class,
			(MutationGermline mutation) -> mutation.getHGVSc());

	public static final GermlineMutationTableModelColumn HGVSpColumn = new GermlineMutationTableModelColumn("Human Genome Variation Society Protein nomenclature.",
			"HGVSp",
			String.class,
			(MutationGermline mutation) -> mutation.getHGVSp());

    //transcript
	public static final GermlineMutationTableModelColumn TRANSCRIPT_STRAND = new GermlineMutationTableModelColumn("Strand direction of the selected transcript.",
			"strand",
			String.class,
			(MutationGermline mutation) -> mutation.getTranscript_strand());
	public static final GermlineMutationTableModelColumn ALT_TRANSCRIPT_START = new GermlineMutationTableModelColumn("Start genomic location of the selected transcript.",
			"start",
			String.class,
			(MutationGermline mutation) -> mutation.getAlt_transcript_start());

	public static final GermlineMutationTableModelColumn ALT_TRANSCRIPT_END = new GermlineMutationTableModelColumn("End genomic location of the selected transcript.",
			"end",
			String.class,
			(MutationGermline mutation) -> mutation.getAlt_transcript_end());

	public static final GermlineMutationTableModelColumn ALT_TRANSCRIPT_POSITION = new GermlineMutationTableModelColumn("Position of variant in the selected transcript",
			"position",
			String.class,
			(MutationGermline mutation) -> mutation.getAlt_transcript_position());




	//clinvar

	public static final GermlineMutationTableModelColumn clinvarID = new GermlineMutationTableModelColumn("ClinVar Variation ID",
			"ID",
			String.class,
			(MutationGermline mutation) -> mutation.getClinvarID());

	public static final GermlineMutationTableModelColumn clinicalDisease = new GermlineMutationTableModelColumn("ClinVar's preferred disease name for the concept specified by disease identifiers in CLNDISDB",
			"disease",
			String.class,
			(MutationGermline mutation) -> mutation.getClinicaldisease());

	public static final GermlineMutationTableModelColumn clinicalSignificance = new GermlineMutationTableModelColumn("Clinical significance for this single variant",
			"significance",
			String.class,
			(MutationGermline mutation) -> mutation.getClinicalsignificance());

	public static final GermlineMutationTableModelColumn clinicalConsequence = new GermlineMutationTableModelColumn("",//TODO Find this
			"consequence",
			String.class,
			(MutationGermline mutation) -> mutation.getClinicalconsequence());

	public static final GermlineMutationTableModelColumn originColumn = new GermlineMutationTableModelColumn("Allele origin",
			"origin",
			String.class,
			(MutationGermline mutation) -> mutation.getClinicalorigin());


	public static final GermlineMutationTableModelColumn lastNameColumn = new GermlineMutationTableModelColumn("The patient's last name.",
			"last name",
			String.class,
			(MutationGermline mutation) -> mutation.getLastName());

	public static final GermlineMutationTableModelColumn firstNameColumn = new GermlineMutationTableModelColumn("The patient's first name.",
			"first name",
			String.class,
			(MutationGermline mutation) -> mutation.getFirstName());

	public static final GermlineMutationTableModelColumn orderNumberColumn = new GermlineMutationTableModelColumn("The lab order number.",
			"order number",
			String.class,
			(MutationGermline mutation) -> mutation.getOrderNumber());

	public static final GermlineMutationTableModelColumn assayColumn = new GermlineMutationTableModelColumn("The assay used",
			"assay",
			String.class,
			(MutationGermline mutation) -> mutation.getAssay());

	public static final GermlineMutationTableModelColumn IDColumn = new GermlineMutationTableModelColumn("The unique ID for the sample",
			"sample ID",
			Integer.class,
			(MutationGermline mutation) -> mutation.getSampleID());


	// gnomad
	public static final GermlineMutationTableModelColumn gnomadIDColumn = new GermlineMutationTableModelColumn("The global allele frequency",
			"ID",
			String.class,
			(MutationGermline mutation) -> mutation.getGnomad_id());

	public static final GermlineMutationTableModelColumn gnomadAltFreqColumn = new GermlineMutationTableModelColumn("The global allele frequency",
			"global",
			Double.class,
			(MutationGermline mutation) -> mutation.getGnomad_allfreq());

	public static final GermlineMutationTableModelColumn gnomadAltFreq_afrColumn = new GermlineMutationTableModelColumn("The AFR allele frequency",
			"african american",
			Double.class,
			(MutationGermline mutation) -> mutation.getGnomad_allfreq_afr());

	public static final GermlineMutationTableModelColumn gnomadAltFreq_amrColumn = new GermlineMutationTableModelColumn("The AMR allele frequency",
			"american admixed/latino",
			Double.class,
			(MutationGermline mutation) -> mutation.getGnomad_allfreq_amr());

	public static final GermlineMutationTableModelColumn gnomadAltFreq_asjColumn = new GermlineMutationTableModelColumn("The ASJ allele frequency",
			"ashkenazi jewish",
			Double.class,
			(MutationGermline mutation) -> mutation.getGnomad_allfreq_asj());

	public static final GermlineMutationTableModelColumn gnomadAltFreq_easColumn = new GermlineMutationTableModelColumn("The EAS allele frequency",
			"east asian",
			Double.class,
			(MutationGermline mutation) -> mutation.getGnomad_allfreq_eas());

	public static final GermlineMutationTableModelColumn gnomadAltFreq_finColumn = new GermlineMutationTableModelColumn("The FIN allele frequency",
			"finnish",
			Double.class,
			(MutationGermline mutation) -> mutation.getGnomad_allfreq_fin());

	public static final GermlineMutationTableModelColumn gnomadAltFreq_nfeColumn = new GermlineMutationTableModelColumn("The NFE allele frequency",
			"non-finnish european",
			Double.class,
			(MutationGermline mutation) -> mutation.getGnomad_allfreq_nfe());

	public static final GermlineMutationTableModelColumn gnomadAltFreq_sasColumn = new GermlineMutationTableModelColumn("The SAS allele frequency",
			"south asian",
			Double.class,
			(MutationGermline mutation) -> mutation.getGnomad_allfreq_sas());

	public static final GermlineMutationTableModelColumn gnomadAltFreq_othColumn = new GermlineMutationTableModelColumn("The OTHERS allele frequency",
			"other",
			Double.class,
			(MutationGermline mutation) -> mutation.getGnomad_allfreq_oth());


	public static final GermlineMutationTableModelColumn gnomadAltFreq_maleColumn = new GermlineMutationTableModelColumn("The MALE allele frequency",
			"male",
			Double.class,
			(MutationGermline mutation) -> mutation.getGnomad_allfreq_male());


	public static final GermlineMutationTableModelColumn gnomadAltFreq_femaleColumn = new GermlineMutationTableModelColumn("The FEMALE allele frequency",
			"female",
			Double.class,
			(MutationGermline mutation) -> mutation.getGnomad_allfreq_female());

	public static final GermlineMutationTableModelColumn cardiacAtlasIDColumn = new GermlineMutationTableModelColumn("The global allele frequency",
			"ID",
			String.class,
			(MutationGermline mutation) -> mutation.getCardiacAtlasId());


	public static final GermlineMutationTableModelColumn cardiac_cdsVariant_Column = new GermlineMutationTableModelColumn("The global allele frequency",
			"cds variant",
			String.class,
			(MutationGermline mutation) -> mutation.getCds_variant());

	public static final GermlineMutationTableModelColumn cardiac_proteinVariant_Column = new GermlineMutationTableModelColumn("The global allele frequency",
			"protein variant",
			String.class,
			(MutationGermline mutation) -> mutation.getProtein_variant());

	public static final GermlineMutationTableModelColumn cardiac_variantType_Column = new GermlineMutationTableModelColumn("The global allele frequency",
			"variant type",
			String.class,
			(MutationGermline mutation) -> mutation.getVariant_type());


	//protein domain

	public static final GermlineMutationTableModelColumn protein_id_column = new GermlineMutationTableModelColumn("The protein domain from nextprot database",
			"ID",
			String.class,
			(MutationGermline mutation) -> mutation.getProtein_id());


	public static final GermlineMutationTableModelColumn protein_type_column = new GermlineMutationTableModelColumn("The protein domain from nextprot database",
			"type",
			String.class,
			(MutationGermline mutation) -> mutation.getProtein_type());


	public static final GermlineMutationTableModelColumn protein_feature_column = new GermlineMutationTableModelColumn("The protein domain from nextprot database",
			"feature",
			String.class,
			(MutationGermline mutation) -> mutation.getProtein_feature());

	public static final GermlineMutationTableModelColumn protein_note_column = new GermlineMutationTableModelColumn("The protein domain from nextprot database",
			"note",
			String.class,
			(MutationGermline mutation) -> mutation.getProtein_note());

	public static final GermlineMutationTableModelColumn protein_start_column = new GermlineMutationTableModelColumn("The protein domain from nextprot database",
			"start",
			Double.class,
			(MutationGermline mutation) -> mutation.getProtein_start());

	public static final GermlineMutationTableModelColumn protein_end_column = new GermlineMutationTableModelColumn("The protein domain from nextprot database",
			"end",
			Double.class,
			(MutationGermline mutation) -> mutation.getProtein_end());

	public static final GermlineMutationTableModelColumn nextprot_column = new GermlineMutationTableModelColumn("The protein domain from nextprot database",
			"nextprot",
			String.class,
			(MutationGermline mutation) -> mutation.getNextprot());


	public static final GermlineMutationTableModelColumn uniprot_column = new GermlineMutationTableModelColumn("The protein domain from uniprot database",
			"uniprot",
			String.class,
			(MutationGermline mutation) -> mutation.getUniprot_id());

	public static final GermlineMutationTableModelColumn pfam_column = new GermlineMutationTableModelColumn("The protein domain from pfam database",
			"pfam",
			String.class,
			(MutationGermline mutation) -> mutation.getPfam());

	public static final GermlineMutationTableModelColumn scoop_column = new GermlineMutationTableModelColumn("The protein domain from scoop database",
			"scoop",
			String.class,
			(MutationGermline mutation) -> mutation.getScoop());
	public static final GermlineMutationTableModelColumn uniprot_variant_column = new GermlineMutationTableModelColumn("The protein domain from nextprot variant database",
			"uniprot variant",
			String.class,
			(MutationGermline mutation) -> mutation.getUniprot_variant());
	public static final GermlineMutationTableModelColumn expasy_column = new GermlineMutationTableModelColumn("The protein domain from expasy database",
			"expasy",
			String.class,
			(MutationGermline mutation) -> mutation.getExpasy_id());

	//variant prediction
	public static final GermlineMutationTableModelColumn revel_column = new GermlineMutationTableModelColumn("The variant effect prediction from revel database",
			"revel",
			String.class,
			(MutationGermline mutation) -> mutation.getRevel());

	public static final GermlineMutationTableModelColumn cadd_phred_column = new GermlineMutationTableModelColumn("The variant effect prediction from cadd database",
			"cadd phred",
			String.class,
			(MutationGermline mutation) -> mutation.getCadd_phred());

	public static final GermlineMutationTableModelColumn canonical_column = new GermlineMutationTableModelColumn("The variant effect prediction from canonical database",
			"canonical",
			String.class,
			(MutationGermline mutation) -> mutation.getCanonical());
	public static final GermlineMutationTableModelColumn sift_column = new GermlineMutationTableModelColumn("The variant effect prediction from sift database",
			"sift",
			String.class,
			(MutationGermline mutation) -> mutation.getSift());

	public static final GermlineMutationTableModelColumn polyphen_column = new GermlineMutationTableModelColumn("The variant effect prediction from polyphen database",
			"polyphen",
			String.class,
			(MutationGermline mutation) -> mutation.getPolyphen());


	public static final GermlineMutationTableModelColumn phastCons100_column = new GermlineMutationTableModelColumn("The variant effect prediction from PhastCons100 database",
			"phastcons100",
			String.class,
			(MutationGermline mutation) -> mutation.getPhastCons100());


	public static final GermlineMutationTableModelColumn phastCons20_column = new GermlineMutationTableModelColumn("The variant effect prediction from PhastCons20 database",
			"phastcons20",
			String.class,
			(MutationGermline mutation) -> mutation.getPhastCons20());


	public static final GermlineMutationTableModelColumn phylop100_column = new GermlineMutationTableModelColumn("The variant effect prediction from Phylop100 database",
			"phylop100",
			String.class,
			(MutationGermline mutation) -> mutation.getPhyloP100());

	public static final GermlineMutationTableModelColumn phylop20_column = new GermlineMutationTableModelColumn("The variant effect prediction from Phylop20 database",
			"phylop20",
			String.class,
			(MutationGermline mutation) -> mutation.getPhyloP20());

	public static final GermlineMutationTableModelColumn GERP_RS_column = new GermlineMutationTableModelColumn("The variant effect prediction from GERP_RS database",
			"GERP RS",
			String.class,
			(MutationGermline mutation) -> mutation.getGERP_RS());

	public static final GermlineMutationTableModelColumn HGMDID_column = new GermlineMutationTableModelColumn("The ID from the HGMD database",
			"ID",
			String.class,
			(MutationGermline mutation) -> mutation.getMutationGermlineHGMD().getId());

	public static final GermlineMutationTableModelColumn HGMDVariant_column = new GermlineMutationTableModelColumn("The variant from the HGMD database",
			"variant",
			String.class,
			(MutationGermline mutation) -> mutation.getMutationGermlineHGMD().getVariant());

	public static final GermlineMutationTableModelColumn HGMDAAChange_column = new GermlineMutationTableModelColumn("The AA change from the HGMD database",
			"AA change",
			String.class,
			(MutationGermline mutation) -> mutation.getMutationGermlineHGMD().getAAchange());

	public static final GermlineMutationTableModelColumn HGMDDisease_column = new GermlineMutationTableModelColumn("The disease from the HGMD database",
			"disease",
			String.class,
			(MutationGermline mutation) -> mutation.getMutationGermlineHGMD().getDisease());

	public static final GermlineMutationTableModelColumn HGMDCategory_column = new GermlineMutationTableModelColumn("The category from the HGMD database",
			"category",
			String.class,
			(MutationGermline mutation) -> mutation.getMutationGermlineHGMD().getCategory());

	public static final GermlineMutationTableModelColumn HGMDCitation_column = new GermlineMutationTableModelColumn("The citation from the HGMD database",
			"citation",
			String.class,
			(MutationGermline mutation) -> mutation.getMutationGermlineHGMD().getPmid_info());

	public static final GermlineMutationTableModelColumn HGMDExtraCitation_column = new GermlineMutationTableModelColumn("The extra citations from the HGMD database",
			"extra refs",
			String.class,
			(MutationGermline mutation) -> mutation.getMutationGermlineHGMD().getExtra_pmids());

	/**
	 * The Lambda interface object
	 */
	private final MutationGetValueAtOperation operation;

	public GermlineMutationTableModelColumn(String description, String title, Class<?> columnClass, MutationGetValueAtOperation operation) {
		super(description, title, columnClass);
		this.operation = operation;
	}
	
	/**
	 * Lambda expression function
	 */
	public Object getValue(MutationGermline mutation){
		return operation.getValue(mutation);
	}
	
	/**
	 * Lambda expression interface
	 *
	 */
	public interface MutationGetValueAtOperation{
		Object getValue(MutationGermline mutation);
	}
}
