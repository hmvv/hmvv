package hmvv.gui.mutationlist.tablemodels;

import hmvv.model.MutationSomatic;
import hmvv.model.VariantPredictionClass;

public class MutationTableModelColumn extends HMVVTableModelColumn{
	/*
	 * Column definitions to include description, title, Class, and getValue function
	 */

	public static final MutationTableModelColumn chrColumn = new MutationTableModelColumn("The chromosome location of this mutation",
			"chr",
			String.class,
			(MutationSomatic mutation) -> mutation.getChr());

	public static final MutationTableModelColumn posColumn = new MutationTableModelColumn("Mutation position on the chromosome",
			"pos",
			String.class,
			(MutationSomatic mutation) -> mutation.getPos());

	public static final MutationTableModelColumn refColumn = new MutationTableModelColumn("Reference base call",
			"ref",
			String.class,
			(MutationSomatic mutation) -> mutation.getRef());

	public static final MutationTableModelColumn altColumn = new MutationTableModelColumn("Mutation base call",
			"alt",
			String.class,
			(MutationSomatic mutation) -> mutation.getAlt());

    // custom

	public static final MutationTableModelColumn reportedColumn = new MutationTableModelColumn("Check to indicate this gene was included in the final report.",
			"reported",
			Boolean.class,
			(MutationSomatic mutation) -> mutation.isReported());

	public static final MutationTableModelColumn otherReportedColumn = new MutationTableModelColumn("List of sample ID's for this patient that had this variant (R = reported)",
			"prev",
			String.class,
			(MutationSomatic mutation) -> mutation.getOtherMutationsString());
	
	public static final MutationTableModelColumn occurrenceColumn = new MutationTableModelColumn("Number of previous occurrences of the detected variant in our database (excluding control samples).",
			"occurrence",
			Integer.class,
			(MutationSomatic mutation) -> mutation.getOccurrence());

	public static final MutationTableModelColumn variantRepeatCountColumn = new MutationTableModelColumn("Number of occurrences of the detected variant in other samples of the same run",
			"repeats",
			Integer.class,
			(MutationSomatic mutation) -> mutation.getvariantRepeatCount());

	public static final MutationTableModelColumn annotationColumn = new MutationTableModelColumn("The text entered by the pathologist to generate the clinical laboratory report.",
			"annotation",
			String.class,
			(MutationSomatic mutation) -> mutation.getAnnotationDisplayText());


	public static final MutationTableModelColumn somaticColumn = new MutationTableModelColumn("The somatic designation as entered by the pathologist in the annotation report.",
			"somatic",
			String.class,
			(MutationSomatic mutation) -> mutation.getOriginAnnotationAssignment());

	public static final MutationTableModelColumn gotoIGVColumn = new MutationTableModelColumn("Link to load the variant coordinate into IGV.",
			"IGV",
			String.class,
			(MutationSomatic mutation) -> mutation.getChr() + ":" + mutation.getPos());

	public static final MutationTableModelColumn igvLoadColumn = new MutationTableModelColumn("Check to indicate this mutation is included for downloading BAM file.",
			"Load IGV",
			Boolean.class,
			(MutationSomatic mutation) -> mutation.isSelected());

	//VEP

	public static final MutationTableModelColumn geneColumn = new MutationTableModelColumn("The name of the gene this mutation is located on.",
			"gene",
			String.class,
			(MutationSomatic mutation) -> mutation.getGene());
	
	public static final MutationTableModelColumn exonsColumn = new MutationTableModelColumn("The exon location of the detected variant.",
			"exons",
			String.class,
			(MutationSomatic mutation) -> mutation.getExons());

	public static final MutationTableModelColumn typeColumn = new MutationTableModelColumn("Variant type, including snv, deletion, insertion, indel.",
			"type",
			String.class,
			(MutationSomatic mutation) -> mutation.getType());

	public static final MutationTableModelColumn variantClassificationColumn = new MutationTableModelColumn("Classifcation as predicted by Variant Effect Predictor.",
			"vep-Prediction",
			VariantPredictionClass.class,
			(MutationSomatic mutation) -> mutation.getVariantPredictionClass());

	public static final MutationTableModelColumn altFreqColumn = new MutationTableModelColumn(
			"Thermo Fisher: Allele frequency based on Flow Evaluator observation counts. Illumina: The percentage of reads supporting the alternate allele.",
			"altFreq",
			Double.class,
			(MutationSomatic mutation) -> mutation.getAltFreq());

	public static final MutationTableModelColumn readDPColumn = new MutationTableModelColumn(
			"Thermo Fisher: Flow Evaluator read depth at the locus to a position and used in variant calling. Illumina: Number of base calls aligned to a position and used in variant calling.",
			"readDP",
			Integer.class,
			(MutationSomatic mutation) -> mutation.getReadDP());

	public static final MutationTableModelColumn altReadDPColumn = new MutationTableModelColumn(
			"Thermo Fisher: Flow Evaluator Alternate allele observations. Illumina: The number of alternate calls.",
			"altReadDP",
			Integer.class,
			(MutationSomatic mutation) -> mutation.getAltReadDP());

	public static final MutationTableModelColumn ConsequenceColumn = new MutationTableModelColumn("Consquence of mutation as predicted by Variant Effect Predictor(VEP)",
			"vep-Consequence",
			String.class,
			(MutationSomatic mutation) -> mutation.getConsequence());

	public static final MutationTableModelColumn SiftColumn = new MutationTableModelColumn("Consequence of mutation as predicted by Sift",
			"vep-Sift",
			String.class,
			(MutationSomatic mutation) -> mutation.getSift());

	public static final MutationTableModelColumn PolyPhenColumn = new MutationTableModelColumn("Consequence of mutation as predicted by PolyPhen",
			"vep-PolyPhen",
			String.class,
			(MutationSomatic mutation) -> mutation.getPolyPhen());

	public static final MutationTableModelColumn HGVScColumn = new MutationTableModelColumn("Human Genome Variation Society Coding DNA nomenclature.",
			"HGVSc",
			String.class,
			(MutationSomatic mutation) -> mutation.getHGVSc());
	
	public static final MutationTableModelColumn HGVSpColumn = new MutationTableModelColumn("Human Genome Variation Society Protein nomenclature.",
			"HGVSp",
			String.class,
			(MutationSomatic mutation) -> mutation.getHGVSp());
	
	public static final MutationTableModelColumn dbSNPIDColumn = new MutationTableModelColumn("Hyperlink to the variant in the dbSNP database.",
			"dbSNPID",
			String.class,
			(MutationSomatic mutation) -> mutation.getDbSNPID());

	public static final MutationTableModelColumn pubmedColumn = new MutationTableModelColumn("Link to relevant pubmed articles.",
			"pubmed",
			String.class,
			(MutationSomatic mutation) -> mutation.getPubmed());

	//cosmic

	public static final MutationTableModelColumn cosmicIDColumn = new MutationTableModelColumn("Hyperlink to the variant in the COSMIC database.",
			"cosmicID",
			String.class,
			(MutationSomatic mutation) -> mutation.cosmicIDsToString());

    //clinvar

	public static final MutationTableModelColumn clinvarID = new MutationTableModelColumn("ClinVar Variation ID",
			"clinVarID",
			String.class,
			(MutationSomatic mutation) -> mutation.getClinvarID());
	
	public static final MutationTableModelColumn clinicalDisease = new MutationTableModelColumn("ClinVar's preferred disease name for the concept specified by disease identifiers in CLNDISDB",
			"clinvar-Disease",
			String.class,
			(MutationSomatic mutation) -> mutation.getClinicaldisease());
	
	public static final MutationTableModelColumn clinicalSignificance = new MutationTableModelColumn("Clinical significance for this single variant",
			"clinvar-Significance",
			String.class,
			(MutationSomatic mutation) -> mutation.getClinicalsignificance());
	
	public static final MutationTableModelColumn clinicalConsequence = new MutationTableModelColumn("",//TODO Find this
			"clinvar-Consequence",
			String.class,
			(MutationSomatic mutation) -> mutation.getClinicalconsequence());

	public static final MutationTableModelColumn originColumn = new MutationTableModelColumn("Allele origin",
			"clinvar-Origin",
			String.class,
			(MutationSomatic mutation) -> mutation.getClinicalorigin());
	
	// g1000
	public static final MutationTableModelColumn altCountColumn = new MutationTableModelColumn("",//TODO Find this
			"g1000-AltCount",
			Integer.class,
			(MutationSomatic mutation) -> mutation.getAltCount());
	
	public static final MutationTableModelColumn totalCountColumn = new MutationTableModelColumn("",//TODO Find this
			"g1000-TotalCount",
			Integer.class,
			(MutationSomatic mutation) -> mutation.getTotalCount());
	
	public static final MutationTableModelColumn altGlobalFreqColumn = new MutationTableModelColumn("1000 Genomes Project: Global Freqency",
			//"g1000-AltFreq",
			"g1000",//This will shrink the column width
			Double.class,
			(MutationSomatic mutation) -> mutation.getAltGlobalFreq());
	
	public static final MutationTableModelColumn americanFreqColumn = new MutationTableModelColumn("1000 Genomes Project: American Freqency",
			"g1000-AmericanFreq",
			Double.class,
			(MutationSomatic mutation) -> mutation.getAmericanFreq());
	
	public static final MutationTableModelColumn eastAsianFreqColumn = new MutationTableModelColumn("1000 Genomes Project:  East Asian Freqency",
			"g1000-EastAsianFreq",
			Double.class,
			(MutationSomatic mutation) -> mutation.getEastAsianFreq());

	public static final MutationTableModelColumn southAsianFreqColumn = new MutationTableModelColumn("1000 Genomes Project: South Asian Freqency",
			"g1000-SouthAsianFreq",
			Double.class,
			(MutationSomatic mutation) -> mutation.getSouthAsianFreq());
	
	public static final MutationTableModelColumn afrFreqColumn = new MutationTableModelColumn("1000 Genomes Project: African Freqency",
			"g1000-AfrFreq",
			Double.class,
			(MutationSomatic mutation) -> mutation.getAfricanFreq());
	
	public static final MutationTableModelColumn eurFreqColumn = new MutationTableModelColumn("1000 Genomes Project: European Freqency",
			"g1000-EurFreq",
			Double.class,
			(MutationSomatic mutation) -> mutation.getEurFreq());

	public static final MutationTableModelColumn lastNameColumn = new MutationTableModelColumn("The patient's last name.",
			"lastName",
			String.class,
			(MutationSomatic mutation) -> mutation.getLastName());
	
	public static final MutationTableModelColumn firstNameColumn = new MutationTableModelColumn("The patient's first name.",
			"firstName",
			String.class,
			(MutationSomatic mutation) -> mutation.getFirstName());
	
	public static final MutationTableModelColumn orderNumberColumn = new MutationTableModelColumn("The lab order number.",
			"orderNumber",
			String.class,
			(MutationSomatic mutation) -> mutation.getOrderNumber());
	
	public static final MutationTableModelColumn assayColumn = new MutationTableModelColumn("The assay used",
			"assay",
			String.class,
			(MutationSomatic mutation) -> mutation.getAssay());
	
	public static final MutationTableModelColumn IDColumn = new MutationTableModelColumn("The unique ID for the sample",
			"sampleID",
			Integer.class,
			(MutationSomatic mutation) -> mutation.getSampleID());
	
	public static final MutationTableModelColumn tumorSourceColumn = new MutationTableModelColumn("The source organ/tissue of the tumor",
			"Tumor Source",
			Integer.class,
			(MutationSomatic mutation) -> mutation.getTumorSource());
	
	public static final MutationTableModelColumn tumorPercentColumn = new MutationTableModelColumn("The percent of tumor in the sample",
			"Tumor Percent",
			String.class,
			(MutationSomatic mutation) -> mutation.getTumorPercent());

	// gnomad

	public static final MutationTableModelColumn gnomadID = new MutationTableModelColumn("Identifier for gnomad db",
			"gnomadID",
			String.class,
			(MutationSomatic mutation) -> mutation.getGnomadID());

	public static final MutationTableModelColumn gnomadAltFreqColumn = new MutationTableModelColumn("The global allele frequency",
			//"gnomad-AltFreq",
			"gnomad",//This will shrink the column width
			Double.class,
			(MutationSomatic mutation) -> mutation.getGnomad_allfreq());

	//oncokb

	public static final MutationTableModelColumn oncokbID = new MutationTableModelColumn("Identifier for Oncokb db",
			"oncokbID",
			String.class,
			(MutationSomatic mutation) -> mutation.getOncokbID());

	public static final MutationTableModelColumn oncogenicityColumn = new MutationTableModelColumn("The oncogenicity",
			"oncokb-Oncogenicity",
			String.class,
			(MutationSomatic mutation) -> mutation.getOncogenicity());

	public static final MutationTableModelColumn oncoMutationEffectColumn = new MutationTableModelColumn("The mutation effect",
			"oncokb-MutationEffect",
			String.class,
			(MutationSomatic mutation) -> mutation.getOnco_MutationEffect());

	//civic

	public static final MutationTableModelColumn civicID = new MutationTableModelColumn("Identifier for Civic db",
			"civicID",
			String.class,
			(MutationSomatic mutation) -> mutation.getCivicID());

	public static final MutationTableModelColumn civicOriginColumn = new MutationTableModelColumn("The variant origin",
			"civic-VariantOrigin",
			String.class,
			(MutationSomatic mutation) -> mutation.getCivic_variant_origin());

	//pmkb

	public static final MutationTableModelColumn pmkbID = new MutationTableModelColumn("Identifier for pmkb db",
			"pmkbID",
			String.class,
			(MutationSomatic mutation) -> mutation.getPmkbID());
	public static final MutationTableModelColumn pmkbTumorTypeColumn = new MutationTableModelColumn("Identifier for pmkb db",
			"pmkb-TumorType",
			String.class,
			(MutationSomatic mutation) -> mutation.getPmkb_tumor_type());
	public static final MutationTableModelColumn pmkbTissueTypeColumn = new MutationTableModelColumn("Identifier for pmkb db",
			"pmkb-TissueType",
			String.class,
			(MutationSomatic mutation) -> mutation.getPmkb_tissue_type());


	public static final MutationTableModelColumn VarScanVAF = new MutationTableModelColumn("Variant Allele Frequency reported by VarScan",
			"VarScanVAF",
			Double.class,
			(MutationSomatic mutation) -> mutation.getVarScanVAF());

	public static final MutationTableModelColumn Mutect2VAF = new MutationTableModelColumn("Variant Allele Frequency reported by Mutect2",
			"Mutect2VAF",
			Double.class,
			(MutationSomatic mutation) -> mutation.getMutect2VAF());

	public static final MutationTableModelColumn freebayesVAF = new MutationTableModelColumn("Variant Allele Frequency reported by FreeBayes",
			"freebayesVAF",
			Double.class,
			(MutationSomatic mutation) -> mutation.getfreebayesVAF());
//

	/**
	 * The Lambda interface object
	 */
	private final MutationGetValueAtOperation operation;
	
	public MutationTableModelColumn(String description, String title, Class<?> columnClass, MutationGetValueAtOperation operation) {
		super(description, title, columnClass);
		this.operation = operation;
	}
	
	/**
	 * Lambda expression function
	 */
	public Object getValue(MutationSomatic mutation){
		return operation.getValue(mutation);
	}
	
	/**
	 * Lambda expression interface
	 *
	 */
	public interface MutationGetValueAtOperation{
		Object getValue(MutationSomatic mutation);
	}
}
