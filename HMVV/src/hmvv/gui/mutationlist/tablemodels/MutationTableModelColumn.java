package hmvv.gui.mutationlist.tablemodels;

import hmvv.model.Mutation;

public class MutationTableModelColumn extends HMVVTableModelColumn{
	/*
	 * Column definitions to include description, title, Class, and getValue function
	 */
	public static final MutationTableModelColumn reportedColumn = new MutationTableModelColumn("Check to indicate this gene was included in the final report.",
			"reported",
			Boolean.class,
			(Mutation mutation) -> mutation.isReported());
	
	public static final MutationTableModelColumn geneColumn = new MutationTableModelColumn("The name of the gene this mutation is located on.",
			"gene",
			String.class,
			(Mutation mutation) -> mutation.getGene());
	
	public static final MutationTableModelColumn exonsColumn = new MutationTableModelColumn("The exon location of the detected variant.",
			"exons",
			String.class,
			(Mutation mutation) -> mutation.getExons());
	
	public static final MutationTableModelColumn HGVScColumn = new MutationTableModelColumn("Human Genome Variation Society Coding DNA nomenclature.",
			"HGVSc",
			String.class,
			(Mutation mutation) -> mutation.getHGVSc());
	
	public static final MutationTableModelColumn HGVSpColumn = new MutationTableModelColumn("Human Genome Variation Society Protein nomenclature.",
			"HGVSp",
			String.class,
			(Mutation mutation) -> mutation.getHGVSp());
	
	public static final MutationTableModelColumn dbSNPIDColumn = new MutationTableModelColumn("Hyperlink to the variant in the dbSNP database.",
			"dbSNPID",
			String.class,
			(Mutation mutation) -> mutation.getDbSNPID());
	
	
	public static final MutationTableModelColumn cosmicIDColumn = new MutationTableModelColumn("Hyperlink to the variant in the COSMIC database.",
			"cosmicID",
			String.class,
			(Mutation mutation) -> mutation.cosmicIDsToString(","));
	
	public static final MutationTableModelColumn typeColumn = new MutationTableModelColumn("Variant type, including snv, deletion, insertion, indel.",
			"type",
			String.class,
			(Mutation mutation) -> mutation.getType());
	
	public static final MutationTableModelColumn genotypeColumn = new MutationTableModelColumn("Impact as predicted by Variant Effect Predictor.",
			"genotype",
			String.class,
			(Mutation mutation) -> mutation.getGenotype());
	
	public static final MutationTableModelColumn altFreqColumn = new MutationTableModelColumn(
			"Thermo Fisher: Allele frequency based on Flow Evaluator observation counts. Illumina: The percentage of reads supporting the alternate allele.",
			"altFreq",
			Double.class,
			(Mutation mutation) -> mutation.getAltFreq());
	
	public static final MutationTableModelColumn readDPColumn = new MutationTableModelColumn(
			"Thermo Fisher: Flow Evaluator read depth at the locus to a position and used in variant calling. Illumina: Number of base calls aligned to a position and used in variant calling.",
			"readDP",
			Integer.class,
			(Mutation mutation) -> mutation.getReadDP());
	
	public static final MutationTableModelColumn altReadDPColumn = new MutationTableModelColumn(
			"Thermo Fisher: Flow Evaluator Alternate allele observations. Illumina: The number of alternate calls.",
			"altReadDP",
			Integer.class,
			(Mutation mutation) -> mutation.getAltReadDP());
	
	public static final MutationTableModelColumn occurrenceColumn = new MutationTableModelColumn("Number of previous occurrences of the detected variant in our database.",
			"occurrence",
			Integer.class,
			(Mutation mutation) -> mutation.getOccurrence());
	
	public static final MutationTableModelColumn annotationColumn = new MutationTableModelColumn("The text entered by the pathologist to generate the clinical laboratory report.",
			"annotation",
			String.class,
			(Mutation mutation) -> mutation.getAnnotation());
	
	public static final MutationTableModelColumn originColumn = new MutationTableModelColumn("",//TODO Find this
			"origin",
			String.class,
			(Mutation mutation) -> mutation.getOrigin());
	
	public static final MutationTableModelColumn clinicalAlleleColumn = new MutationTableModelColumn("",//TODO Find this
			"clinicalAllele",
			String.class,
			(Mutation mutation) -> mutation.getClinicalAllele());
	
	public static final MutationTableModelColumn clinicalSigColumn = new MutationTableModelColumn("",//TODO Find this
			"clinicalSig",
			String.class,
			(Mutation mutation) -> mutation.getClinicalSig());
	
	public static final MutationTableModelColumn clinicalAccColumn = new MutationTableModelColumn("",//TODO Find this
			"clinicalAcc",
			String.class,
			(Mutation mutation) -> mutation.getClinicalAcc());
	
	public static final MutationTableModelColumn pubmedColumn = new MutationTableModelColumn("Link to relevant pubmed articles.",
			"pubmed",
			String.class,
			(Mutation mutation) -> mutation.getPubmed());
	
	public static final MutationTableModelColumn chrColumn = new MutationTableModelColumn("The chromosome location of this mutation",
			"chr",
			String.class,
			(Mutation mutation) -> mutation.getChr());
	
	public static final MutationTableModelColumn posColumn = new MutationTableModelColumn("Mutation position on the chromosome",
			"pos",
			String.class,
			(Mutation mutation) -> mutation.getPos());
	
	public static final MutationTableModelColumn refColumn = new MutationTableModelColumn("Reference base call",
			"ref",
			String.class,
			(Mutation mutation) -> mutation.getRef());
	
	public static final MutationTableModelColumn altColumn = new MutationTableModelColumn("Mutation base call",
			"alt",
			String.class,
			(Mutation mutation) -> mutation.getAlt());
	
	public static final MutationTableModelColumn ConsequenceColumn = new MutationTableModelColumn("Consquence of mutation as predicted by ________",//TODO Fill in this blank
			"Consequence",
			String.class,
			(Mutation mutation) -> mutation.getConsequence());
	
	public static final MutationTableModelColumn SiftColumn = new MutationTableModelColumn("Consequence of mutation as predicted by Sift",
			"Sift",
			String.class,
			(Mutation mutation) -> mutation.getSift());
	
	public static final MutationTableModelColumn PolyPhenColumn = new MutationTableModelColumn("Consequence of mutation as predicted by PolyPhen",
			"PolyPhen",
			String.class,
			(Mutation mutation) -> mutation.getPolyPhen());
	
	public static final MutationTableModelColumn altCountColumn = new MutationTableModelColumn("",//TODO Find this
			"altCount",
			Integer.class,
			(Mutation mutation) -> mutation.getAltCount());
	
	public static final MutationTableModelColumn totalCountColumn = new MutationTableModelColumn("",//TODO Find this
			"totalCount",
			Integer.class,
			(Mutation mutation) -> mutation.getTotalCount());
	
	public static final MutationTableModelColumn altGlobalFreqColumn = new MutationTableModelColumn("1000 Genomes Project: Global Freqency",
			"altGlobalFreq",
			Double.class,
			(Mutation mutation) -> mutation.getAltGlobalFreq());
	
	public static final MutationTableModelColumn americanFreqColumn = new MutationTableModelColumn("1000 Genomes Project: American Freqency",
			"americanFreq",
			Double.class,
			(Mutation mutation) -> mutation.getAmericanFreq());
	
	public static final MutationTableModelColumn asianFreqColumn = new MutationTableModelColumn("1000 Genomes Project: Asian Freqency",
			"asianFreq",
			Double.class,
			(Mutation mutation) -> mutation.getAsianFreq());
	
	public static final MutationTableModelColumn afrFreqColumn = new MutationTableModelColumn("1000 Genomes Project: African Freqency",
			"afrFreq",
			Double.class,
			(Mutation mutation) -> mutation.getAfricanFreq());
	
	public static final MutationTableModelColumn eurFreqColumn = new MutationTableModelColumn("1000 Genomes Project: European Freqency",
			"eurFreq",
			Double.class,
			(Mutation mutation) -> mutation.getEurFreq());
	
	public static final MutationTableModelColumn lastNameColumn = new MutationTableModelColumn("The patient's last name.",
			"lastName",
			String.class,
			(Mutation mutation) -> mutation.getLastName());
	
	public static final MutationTableModelColumn firstNameColumn = new MutationTableModelColumn("The patient's first name.",
			"firstName",
			String.class,
			(Mutation mutation) -> mutation.getFirstName());
	
	public static final MutationTableModelColumn orderNumberColumn = new MutationTableModelColumn("The lab order number.",
			"orderNumber",
			String.class,
			(Mutation mutation) -> mutation.getOrderNumber());
	
	public static final MutationTableModelColumn assayColumn = new MutationTableModelColumn("The assay used",
			"assay",
			String.class,
			(Mutation mutation) -> mutation.getAssay());
	
	public static final MutationTableModelColumn IDColumn = new MutationTableModelColumn("The unique ID for the sample",
			"ID",
			Integer.class,
			(Mutation mutation) -> mutation.getSampleID());
	
	public static final MutationTableModelColumn tumorSourceColumn = new MutationTableModelColumn("The source organ/tissue of the tumor",
			"Tumor Source",
			Integer.class,
			(Mutation mutation) -> mutation.getTumorSource());
	
	public static final MutationTableModelColumn tumorPercentColumn = new MutationTableModelColumn("The percent of tumor in the sample",
			"Tumor Percent",
			String.class,
			(Mutation mutation) -> mutation.getTumorPercent());
	
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
