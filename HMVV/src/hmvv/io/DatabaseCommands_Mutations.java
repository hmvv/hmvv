package hmvv.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import hmvv.main.Configurations;
import hmvv.model.*;

public class DatabaseCommands_Mutations {

	private static Connection databaseConnection = null;
	static void setConnection(Connection databaseConnection) {
		DatabaseCommands_Mutations.databaseConnection = databaseConnection;
	}
	
	static ArrayList<MutationSomatic> getBaseMutationsBySample(Sample sample) throws Exception{
		ArrayList<MutationSomatic> mutations = getMutationDataByID(sample, false);
		addReportedMutationsByMRN(sample, mutations);		
		return mutations;
	}

	static ArrayList<MutationSomatic> getExtraMutationsBySample(Sample sample) throws Exception{
		ArrayList<MutationSomatic> mutations = getMutationDataByID(sample, true);
		addReportedMutationsByMRN(sample, mutations);		
		return mutations;
	}

	private static ArrayList<MutationSomatic> getMutationDataByID(Sample sample, boolean getFilteredData) throws Exception{
		String query = "select t2.sampleID, t2.reported, t2.gene, t2.exon, t2.chr, t2.pos, t2.ref, t2.alt,"
				+ " t2.impact,t2.type, t2.altFreq, t2.readDepth, t2.altReadDepth, "
				+ " t2.consequence, t2.Sift, t2.PolyPhen,t2.HGVSc, t2.HGVSp, t2.dbSNPID,t2.pubmed,"
				+ " t1.lastName, t1.firstName, t1.orderNumber, t6.assayName, t1.tumorSource, t1.tumorPercent,"
				+ " t4.altCount, t4.totalCount, t4.altGlobalFreq, t4.americanFreq, t4.eastAsianFreq,t4.southAsianFreq, t4.afrFreq, t4.eurFreq,"
				+ " t5.clinvarID, t5.cln_disease, t5.cln_significance, t5.cln_consequence,t5.cln_origin, "
				+ " t8.AF "
				+ " from sampleVariants as t2"
				+ " join samples as t1 on t2.sampleID = t1.sampleID "
				+ " join assays as t6 on t1.assayID = t6.assayID"
				+ " left join db_g1000_phase3v1 as t4"
				+ " on t2.chr = t4.chr and t2.pos = t4.pos and t2.ref = t4.ref and t2.alt = t4.alt"
				+ " left join db_clinvar_42019 as t5"
				+ " on t2.chr = t5.chr and t2.pos = t5.pos and t2.ref = t5.ref and t2.alt = t5.alt"
				+ " left join db_gnomad_r211 as t8 on t2.chr = t8.chr and t2.pos = t8.pos and t2.ref = t8.ref and t2.alt = t8.alt "
				+ " where t2.sampleID = ? ";
		//				+ " and t2.exon != '' ";//Filter the introns
		String where = " ( (t2.impact = 'HIGH' or t2.impact = 'MODERATE') and t2.altFreq >= " + Configurations.getAlleleFrequencyFilter(sample) + " and t2.readDepth >= " + Configurations.READ_DEPTH_FILTER + ")";
		if(getFilteredData) {
			where = " !" + where;
		}
		query = query + " and " + where ;
		
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, ""+sample.sampleID);
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<MutationSomatic> mutations = makeModel(rs);
		preparedStatement.close();
		return mutations;
	}


	public static ArrayList<MutationGermline> getGermlineMutationDataByID(Sample sample, boolean getFilteredData) throws Exception{
		String query = "select t2.sampleID, t2.reported, t2.gene, t2.exon, t2.chr, t2.pos, t2.ref, t2.alt,"
				+ " t2.impact,t2.type, t2.altFreq, t2.readDepth, t2.altReadDepth, "
				+ " t2.consequence,t2.HGVSc, t2.HGVSp, t2.STRAND, t2.ALT_TRANSCRIPT_START, t2.ALT_TRANSCRIPT_END,t2.ALT_VARIANT_POSITION,"
				+ " t2.protein_id, t2.protein_type, t2.protein_feature, t2.protein_note, t2.protein_start, t2.protein_end ,"
				+ " t2.nextprot,t2.uniprot_id, t2.pfam, t2.scoop, t2.uniprot_variant,t2.expasy_id,"
				+ " t2.revel,t2.cadd_phred, t2.canonical, t2.sift,t2.polyphen,"
				+ " t2.phastCons100, t2.phyloP100, t2.phastCons20, t2.phyloP20, t2.GERP_RS,"
		        + "  t2.hgmd_ID, t2.hgmd_Variant, t2.hgmd_AAchange, t2.hgmd_Disease, t2.hgmd_Category, t2.hgmd_PMID,t2.hgmd_Citation,t2.hgmd_ExtraCitations,"
				+ " t1.lastName, t1.firstName, t1.orderNumber, t6.assayName, "
				+ " t5.clinvarID, t5.cln_disease, t5.cln_significance, t5.cln_consequence,t5.cln_origin, "
				+ " t8.AF, t8.AF_afr, t8.AF_amr, t8.AF_asj, t8.AF_eas, t8.AF_fin, t8.AF_nfe, t8.AF_sas, t8.AF_oth, t8.AF_male, t8.AF_female "
				+ " from sampleVariantsGermline as t2"
				+ " join samples as t1 on t2.sampleID = t1.sampleID "
				+ " join assays as t6 on t1.assayID = t6.assayID"
				+ " left join db_clinvar_42019 as t5"
				+ " on t2.chr = t5.chr and t2.pos = t5.pos and t2.ref = t5.ref and t2.alt = t5.alt"
				+ " left join db_gnomad_r211_lf as t8 on t2.chr = t8.chr and t2.pos = t8.pos and t2.ref = t8.ref and t2.alt = t8.alt "
				+ " where t2.sampleID = ? ";
		String where = " ( t2.altFreq >= " + Configurations.GERMLINE_ALLELE_FREQ_FILTER +
				" and t2.readDepth >= " + Configurations.GERMLINE_READ_DEPTH_FILTER +
				" and t8.AF <= " + Configurations.GERMLINE_GNOMAD_MAX_GLOBAL_ALLELE_FREQ_FILTER +  ")";
		if(getFilteredData) {
			where = " !" + where;
		}
		query = query + " and " + where ;
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, ""+sample.sampleID);
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<MutationGermline> mutations = makeGermlineModel(rs);
		preparedStatement.close();
		return mutations;
	}
	/**
	 * Get all reported mutations from the MRN on the provided sample
	 * @param sample
	 * @return
	 * @throws Exception
	 */
	private static void addReportedMutationsByMRN(Sample sample, ArrayList<MutationSomatic> mutations) throws Exception {
		ArrayList<MutationSomatic> reportedMutations = new ArrayList<MutationSomatic>();
		for(Sample otherSample : sample.getLinkedPatientSamples()) {
			reportedMutations.addAll(getMutationDataByID(otherSample, false));
		}

		for(MutationSomatic mutation : mutations) {
			for(MutationSomatic other : reportedMutations) {
				if(mutation.equals(other)) {
					mutation.addOtherMutation(other);
				}
			}
		}
	}

	/**
	 * Acquires the cosmicID from the database. If it isn't found, an empty array is returned
	 */
	static ArrayList<String> getCosmicIDs(MutationSomatic mutation) throws Exception{
		Coordinate coordinate = mutation.getCoordinate();
		String query = "select cosmicID from db_cosmic_grch37v86 where chr = ? and pos = ? and ref = ? and alt = ?";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, coordinate.getChr());
		preparedStatement.setString(2, coordinate.getPos());
		preparedStatement.setString(3, coordinate.getRef());
		preparedStatement.setString(4, coordinate.getAlt());
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<String> cosmicIDs = new ArrayList<String>();
		while(rs.next()){
			String result = rs.getString(1);
			cosmicIDs.add(result);
		}
		preparedStatement.close();
		return cosmicIDs;
	}

	static String getCosmicInfo(String cosmicID) throws Exception{
		String query = "select info from db_cosmic_grch37v86 where cosmicID = ?";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, cosmicID);
		ResultSet rs = preparedStatement.executeQuery();
		String info = null;
		if(rs.next()){
			info = rs.getString("info");
		}
		preparedStatement.close();
		return info;
	}

	static void updateOncokbInfo(MutationSomatic mutation) throws Exception{
		String ENST_id = mutation.getHGVSc().split("\\.")[0];
		String ENSP = "";
		String[] getHGVSpArray = mutation.getHGVSp().split("\\.");
		if(getHGVSpArray.length > 1) {
			ENSP = getHGVSpArray[2];
		}else {
			//TODO What to do here?
			return;
		}

		String query = "select Protein_Change,Protein_Change_LF,Oncogenicity, Mutation_Effect from db_oncokb where Isoform = ? and  Gene = ? and Protein_Change_LF = ? limit 1";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, ENST_id);
		preparedStatement.setString(2, mutation.getGene());
		preparedStatement.setString(3, ENSP);
		ResultSet rs = preparedStatement.executeQuery();
		if(rs.next()){
			mutation.setOnco_Protein_Change(getStringOrBlank(rs, "Protein_Change"));
			mutation.setOnco_Protein_Change_LF(getStringOrBlank(rs, "Protein_Change_LF"));
			mutation.setOncogenicity(getStringOrBlank(rs, "Oncogenicity"));
			mutation.setOnco_MutationEffect(getStringOrBlank(rs, "Mutation_Effect"));
		}
		preparedStatement.close();
	}

	static void updatePmkbInfo(MutationSomatic mutation) throws Exception{
		String ENSP = "";
		String[] getHGVSpArray = mutation.getHGVSp().split("\\.");
		if(getHGVSpArray.length > 1) {
			ENSP = Configurations.abbreviationtoLetter(getHGVSpArray[2]);
		}else {
			//TODO What to do here?
			return;
		}
		String query = "select tumor_type,tissue_type from db_pmkb_42019 where gene = ? and variant = ? limit 1";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, mutation.getGene());
		preparedStatement.setString(2, ENSP);
		ResultSet rs = preparedStatement.executeQuery();
		if(rs.next()){
			mutation.setPmkb_tumor_type(getStringOrBlank(rs, "tumor_type"));
			mutation.setPmkb_tissue_type(getStringOrBlank(rs, "tissue_type"));
		}
		preparedStatement.close();
	}

	static void updateCivicInfo(MutationSomatic mutation) throws Exception{
		String ENSP = "";
		String[] getHGVSpArray = mutation.getHGVSp().split("\\.");
		if(getHGVSpArray.length > 1) {
			ENSP = getHGVSpArray[2];
		}else {
			//TODO What to do here?
			return;
		}

		String query = "select variant_origin,variant_civic_url from db_civic_42019 where gene = ? and variant_LF = ? limit 1 ";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, mutation.getGene());
		preparedStatement.setString(2, ENSP);
		ResultSet rs = preparedStatement.executeQuery();
		if(rs.next()){
			mutation.setCivic_variant_origin(getStringOrBlank(rs, "variant_origin"));
			mutation.setCivic_variant_url(getStringOrBlank(rs, "variant_civic_url"));
		}
		preparedStatement.close();
	}


	static void updateGermlineCardiacAtlasInfo(MutationGermline mutation) throws Exception{
		String[] getHGVScArray = mutation.getHGVSc().split("\\:");
		String cds_variant = getHGVScArray[1];

		String query = "select gene, cds_variant,protein_variant,variant_type from db_cardiac_72020 where gene = ? and cds_variant = ? limit 1";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, mutation.getGene());
		preparedStatement.setString(2, cds_variant);
		ResultSet rs = preparedStatement.executeQuery();
		if(rs.next()){
			mutation.setCardiacAtlasId(getStringOrBlank(rs, "gene"));
			mutation.setCds_variant(getStringOrBlank(rs, "cds_variant"));
			mutation.setProtein_variant(getStringOrBlank(rs, "protein_variant"));
			mutation.setVariant_type(getStringOrBlank(rs, "variant_type"));

		}
		preparedStatement.close();
	}

	/**
	 * Acquires the number of occurrences of this mutation from the database.
	 */
	static int getOccurrenceCount(MutationCommon mutation) throws Exception{

        Coordinate coordinate = mutation.getCoordinate();
		String assay = mutation.getAssay();

		String query = "select count as occurrence "
					+ " from occurrenceCount"
					+ " where chr = ? and pos = ? and ref = ? and alt = ? and assay = ?";

		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, coordinate.getChr());
		preparedStatement.setString(2, coordinate.getPos());
		preparedStatement.setString(3, coordinate.getRef());
		preparedStatement.setString(4, coordinate.getAlt());
		preparedStatement.setString(5, assay);

		ResultSet rs = preparedStatement.executeQuery();
		if(rs.next()){
			String result = rs.getString(1);
			preparedStatement.close();
			//not checking for errors because query is count(*), so this should work
			return Integer.parseInt(result);
		}else{
			preparedStatement.close();
			return 0;
		}
	}

	static void updateReportedStatus(boolean setToReported, Integer sampleID, Coordinate coordinate) throws SQLException{
		String reported = (setToReported) ? "1" : "0";
		PreparedStatement updateStatement = databaseConnection.prepareStatement(
				"update sampleVariants set reported = ? where sampleID = ? and chr = ? and pos = ? and ref = ? and alt = ?");
		updateStatement.setString(1, reported);
		updateStatement.setString(2, sampleID.toString());
		updateStatement.setString(3, coordinate.getChr());
		updateStatement.setString(4, coordinate.getPos());
		updateStatement.setString(5, coordinate.getRef());
		updateStatement.setString(6, coordinate.getAlt());
		updateStatement.executeUpdate();
		updateStatement.close();
	}

	private static ArrayList<MutationSomatic> makeModel(ResultSet rs) throws Exception{
		ArrayList<MutationSomatic> mutations = new ArrayList<MutationSomatic>();

		while(rs.next()){
			MutationSomatic mutation = new MutationSomatic();

			//common
			boolean reported = Integer.parseInt(rs.getString("reported")) != 0;
			mutation.setReported(reported);
			mutation.setGene(getStringOrBlank(rs, "gene"));
			mutation.setExons(getStringOrBlank(rs, "exon"));
			mutation.setChr(getStringOrBlank(rs, "chr"));
			mutation.setPos(getStringOrBlank(rs, "pos"));
			mutation.setRef(getStringOrBlank(rs, "ref"));
			mutation.setAlt(getStringOrBlank(rs, "alt"));
			VariantPredictionClass variantPredictionClass = VariantPredictionClass.createPredictionClass(getStringOrBlank(rs, "impact"));
			mutation.setVariantPredictionClass(variantPredictionClass);
			mutation.setType(getStringOrBlank(rs, "type"));
			mutation.setAltFreq(getDoubleOrNull(rs, "altFreq"));
			mutation.setReadDP(getIntegerOrNull(rs, "readDepth"));
			mutation.setAltReadDP(getIntegerOrNull(rs, "altReadDepth"));
			mutation.setConsequence(getStringOrBlank(rs, "consequence"));
			mutation.setSift(getStringOrBlank(rs, "Sift"));
			mutation.setPolyPhen(getStringOrBlank(rs, "PolyPhen"));
			mutation.setHGVSc(getStringOrBlank(rs, "HGVSc"));
			mutation.setHGVSp(getStringOrBlank(rs, "HGVSp"));
			mutation.setDbSNPID(getStringOrBlank(rs, "dbSNPID"));
			mutation.setPubmed(getStringOrBlank(rs, "pubmed"));

			//Sample
			mutation.setLastName(getStringOrBlank(rs, "lastName"));
			mutation.setFirstName(getStringOrBlank(rs, "firstName"));
			mutation.setOrderNumber(getStringOrBlank(rs, "orderNumber"));
			mutation.setAssay(getStringOrBlank(rs, "assayName"));
			mutation.setSampleID(getIntegerOrNull(rs, "sampleID"));
			mutation.setTumorSource(getStringOrBlank(rs, "tumorSource"));
			mutation.setTumorPercent(getStringOrBlank(rs, "tumorPercent"));

			//G1000
			mutation.setAltCount(getIntegerOrNull(rs, "altCount"));
			mutation.setTotalCount(getIntegerOrNull(rs, "totalCount"));
			mutation.setAltGlobalFreq(getDoubleOrNull(rs, "altGlobalFreq"));
			mutation.setAmericanFreq(getDoubleOrNull(rs, "americanFreq"));
			mutation.setEastAsianFreq(getDoubleOrNull(rs, "eastAsianFreq"));
			mutation.setSouthAsianFreq(getDoubleOrNull(rs, "southAsianFreq"));
			mutation.setAfricanFreq(getDoubleOrNull(rs, "afrFreq"));
			mutation.setEurFreq(getDoubleOrNull(rs, "eurFreq"));

			//ClinVar
			mutation.setClinvarID(getStringOrBlank(rs, "clinvarID"));
			mutation.setClinicaldisease(getStringOrBlank(rs, "cln_disease"));
			mutation.setClinicalsignificance(getStringOrBlank(rs, "cln_significance"));
			mutation.setClinicalconsequence(getStringOrBlank(rs, "cln_consequence"));
			mutation.setClinicalorigin(getStringOrBlank(rs, "cln_origin"));


			//temp holder fields - filled later separately
			mutation.setCosmicID(getStringOrBlank(rs, "cosmicID"));
			mutation.setOccurrence(getIntegerOrNull(rs, "occurrence"));


			//annotation history
			ArrayList<Annotation> annotationHistory = getVariantAnnotationHistory(mutation.getCoordinate(),Configurations.MUTATION_TYPE.SOMATIC);
			mutation.setAnnotationHistory(annotationHistory);

			//gnomad
			Double gnomadAllFreq = getDoubleOrNull(rs, "AF");
			if(gnomadAllFreq != null) {
				mutation.setGnomad_allfreq(gnomadAllFreq);
			}

			mutations.add(mutation);
		}
		return mutations;
	}

	private static ArrayList<MutationGermline> makeGermlineModel(ResultSet rs) throws Exception{
		ArrayList<MutationGermline> mutations = new ArrayList<MutationGermline>();

		while(rs.next()){
			MutationGermline mutation = new MutationGermline();

			//common
			boolean reported = Integer.parseInt(rs.getString("reported")) != 0;
			mutation.setReported(reported);
			mutation.setGene(getStringOrBlank(rs, "gene"));
			mutation.setExons(getStringOrBlank(rs, "exon"));
			mutation.setChr(getStringOrBlank(rs, "chr"));
			mutation.setPos(getStringOrBlank(rs, "pos"));
			mutation.setRef(getStringOrBlank(rs, "ref"));
			mutation.setAlt(getStringOrBlank(rs, "alt"));
			VariantPredictionClass variantPredictionClass = VariantPredictionClass.createPredictionClass(getStringOrBlank(rs, "impact"));
			mutation.setVariantPredictionClass(variantPredictionClass);
			mutation.setType(getStringOrBlank(rs, "type"));
			mutation.setAltFreq(getDoubleOrNull(rs, "altFreq"));
			mutation.setReadDP(getIntegerOrNull(rs, "readDepth"));
			mutation.setAltReadDP(getIntegerOrNull(rs, "altReadDepth"));
			mutation.setConsequence(getStringOrBlank(rs, "consequence"));
			mutation.setHGVSc(getStringOrBlank(rs, "HGVSc"));
			mutation.setHGVSp(getStringOrBlank(rs, "HGVSp"));

			//transcript
			mutation.setTranscript_strand(getStringOrBlank(rs,"STRAND"));
			mutation.setAlt_transcript_start(getStringOrBlank(rs,"ALT_TRANSCRIPT_START"));
			mutation.setAlt_transcript_end(getStringOrBlank(rs,"ALT_TRANSCRIPT_END"));
			mutation.setAlt_transcript_position(getStringOrBlank(rs,"ALT_VARIANT_POSITION"));

			//protein
			mutation.setProtein_id(getStringOrBlank(rs,"protein_id"));
			mutation.setProtein_type(getStringOrBlank(rs,"protein_type"));
			mutation.setProtein_feature(getStringOrBlank(rs,"protein_feature"));
			mutation.setProtein_note(getStringOrBlank(rs,"protein_note"));
			mutation.setProtein_start(getDoubleOrNull(rs,"protein_start"));
			mutation.setProtein_end(getDoubleOrNull(rs,"protein_end"));
			mutation.setNextprot(getStringOrBlank(rs,"nextprot"));
			mutation.setUniprot_id(getStringOrBlank(rs,"uniprot_id"));
			mutation.setPfam(getStringOrBlank(rs,"pfam"));
			mutation.setScoop(getStringOrBlank(rs,"scoop"));
			mutation.setUniprot_variant(getStringOrBlank(rs,"uniprot_variant"));
			mutation.setExpasy_id(getStringOrBlank(rs,"expasy_id"));

			//prediction
			mutation.setRevel(getStringOrBlank(rs,"revel"));
			mutation.setCadd_phred(getStringOrBlank(rs,"cadd_phred"));
			mutation.setCanonical(getStringOrBlank(rs,"canonical"));
			mutation.setSift(getStringOrBlank(rs,"sift"));
			mutation.setPolyphen(getStringOrBlank(rs,"polyphen"));

			//conservation
			mutation.setPhastCons100(getStringOrBlank(rs,"phastCons100"));
			mutation.setPhyloP100(getStringOrBlank(rs,"phyloP100"));
			mutation.setPhastCons20(getStringOrBlank(rs,"phastCons20"));
			mutation.setPhyloP20(getStringOrBlank(rs,"phyloP20"));
			mutation.setGERP_RS(getStringOrBlank(rs,"GERP_RS"));

			//Sample
			mutation.setLastName(getStringOrBlank(rs, "lastName"));
			mutation.setFirstName(getStringOrBlank(rs, "firstName"));
			mutation.setOrderNumber(getStringOrBlank(rs, "orderNumber"));
			mutation.setAssay(getStringOrBlank(rs, "assayName"));
			mutation.setSampleID(getIntegerOrNull(rs, "sampleID"));


			//ClinVar
			mutation.setClinvarID(getStringOrBlank(rs, "clinvarID"));
			mutation.setClinicaldisease(getStringOrBlank(rs, "cln_disease"));
			mutation.setClinicalsignificance(getStringOrBlank(rs, "cln_significance"));
			mutation.setClinicalconsequence(getStringOrBlank(rs, "cln_consequence"));
			mutation.setClinicalorigin(getStringOrBlank(rs, "cln_origin"));

			//HGMD mutation
			MutationGermlineHGMD mutationGermlineHGMD = new MutationGermlineHGMD(getStringOrBlank(rs, "hgmd_ID"));
			mutation.setMutationGermlineHGMD(mutationGermlineHGMD);
			mutationGermlineHGMD.setVariant(getStringOrBlank(rs,"hgmd_Variant"));
			mutationGermlineHGMD.setAAchange(getStringOrBlank(rs,"hgmd_AAchange"));
			mutationGermlineHGMD.setDisease(getStringOrBlank(rs,"hgmd_Disease"));
			mutationGermlineHGMD.setCategory(getStringOrBlank(rs,"hgmd_Category"));
			mutationGermlineHGMD.setPmid(getStringOrBlank(rs,"hgmd_PMID"));
			mutationGermlineHGMD.setPmid_info(getStringOrBlank(rs,"hgmd_Citation"));
			mutationGermlineHGMD.setExtra_pmids(getStringOrBlank(rs,"hgmd_ExtraCitations"));


			//temp holder fields - filled later separately
			mutation.setOccurrence(getIntegerOrNull(rs, "occurrence"));


			//annotation history
			ArrayList<Annotation> annotationHistory = getVariantAnnotationHistory(mutation.getCoordinate(),Configurations.MUTATION_TYPE.GERMLINE);
			mutation.setAnnotationHistory(annotationHistory);

			//gnomad
			Double gnomadAllFreq = getDoubleOrNull(rs, "AF");
			if(gnomadAllFreq != null) {
				mutation.setGnomad_allfreq(gnomadAllFreq);

				mutation.setGnomad_allfreq_afr(getDoubleOrNull(rs, "AF_afr"));
				mutation.setGnomad_allfreq_amr(getDoubleOrNull(rs, "AF_amr"));
				mutation.setGnomad_allfreq_asj(getDoubleOrNull(rs, "AF_asj"));
				mutation.setGnomad_allfreq_eas(getDoubleOrNull(rs, "AF_eas"));
				mutation.setGnomad_allfreq_fin(getDoubleOrNull(rs, "AF_fin"));
				mutation.setGnomad_allfreq_nfe(getDoubleOrNull(rs, "AF_nfe"));
				mutation.setGnomad_allfreq_sas(getDoubleOrNull(rs, "AF_sas"));
				mutation.setGnomad_allfreq_oth(getDoubleOrNull(rs, "AF_oth"));
				mutation.setGnomad_allfreq_male(getDoubleOrNull(rs, "AF_male"));
				mutation.setGnomad_allfreq_female(getDoubleOrNull(rs, "AF_female"));
			}
			mutations.add(mutation);
		}
		return mutations;
	}

	private static String getStringOrBlank(ResultSet rs, String columnLabel){
		try{
			if( rs.getString(columnLabel) == null) {
				return "";
			}
			return rs.getString(columnLabel).replaceAll("_"," ");
		}catch(Exception e){
			return "";
		}
	}

	private static Integer getIntegerOrNull(ResultSet rs, String columnLabel){
		try{
			String value = rs.getString(columnLabel);
			return Integer.parseInt(value);
		}catch(Exception e){
			return null;
		}
	}

	private static Double getDoubleOrNull(ResultSet rs, String columnLabel){
		try{
			String value = rs.getString(columnLabel);
			return Double.parseDouble(value);
		}catch(Exception e){
			return null;
		}
	}
	
	private static ArrayList<Annotation> getVariantAnnotationHistory(Coordinate coordinate, Configurations.MUTATION_TYPE mutation_type) throws Exception{

		ArrayList<Annotation> annotations = new ArrayList<Annotation>() ;

		String tablename = "";
		if (mutation_type == Configurations.MUTATION_TYPE.SOMATIC) {
			tablename = "variantAnnotation";
		} else if (mutation_type == Configurations.MUTATION_TYPE.GERMLINE) {
			tablename = "germlineVariantAnnotation";
		}

		final String query = String.format("select annotationID, classification, curation, somatic, enteredBy, enterDate from %s where chr = ? and pos = ? and ref = ? and alt = ? order by annotationID asc",tablename);
		PreparedStatement selectStatement = databaseConnection.prepareStatement(query);
		selectStatement.setString(1, coordinate.getChr());
		selectStatement.setString(2, coordinate.getPos());
		selectStatement.setString(3, coordinate.getRef());
		selectStatement.setString(4, coordinate.getAlt());
		ResultSet rs = selectStatement.executeQuery();
		while(rs.next()){
			annotations.add(new Annotation(rs.getInt("annotationID"),coordinate, rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getTimestamp(6)));
		}
		selectStatement.close();
		return annotations;
	}
}
