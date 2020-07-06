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
	
	static ArrayList<Mutation> getBaseMutationsBySample(Sample sample) throws Exception{
		ArrayList<Mutation> mutations = getMutationDataByID(sample, false);
		addReportedMutationsByMRN(sample, mutations);		
		return mutations;
	}

	static ArrayList<Mutation> getExtraMutationsBySample(Sample sample) throws Exception{
		ArrayList<Mutation> mutations = getMutationDataByID(sample, true);
		addReportedMutationsByMRN(sample, mutations);		
		return mutations;
	}

	private static ArrayList<Mutation> getMutationDataByID(Sample sample, boolean getFilteredData) throws Exception{
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
		ArrayList<Mutation> mutations = makeModel(rs);
		preparedStatement.close();
		return mutations;
	}


	public static ArrayList<GermlineMutation> getGermlineMutationDataByID(Sample sample) throws Exception{
		String query = "select t2.sampleID, t2.reported, t2.gene, t2.exon, t2.chr, t2.pos, t2.ref, t2.alt,"
				+ " t2.impact,t2.type, t2.altFreq, t2.readDepth, t2.altReadDepth, "
				+ " t2.consequence,t2.HGVSc, t2.HGVSp, t2.ALT_TRANSCRIPT_START, t2.ALT_TRANSCRIPT_END,t2.ALT_VARIANT_POSITION,"

				+ " t1.lastName, t1.firstName, t1.orderNumber, t6.assayName, "

				+ " t5.clinvarID, t5.cln_disease, t5.cln_significance, t5.cln_consequence,t5.cln_origin, "

				+ " t8.AF "

				+ " from sampleVariantsGermline as t2"
				+ " join samples as t1 on t2.sampleID = t1.sampleID "
				+ " join assays as t6 on t1.assayID = t6.assayID"

				+ " left join db_clinvar_42019 as t5"
				+ " on t2.chr = t5.chr and t2.pos = t5.pos and t2.ref = t5.ref and t2.alt = t5.alt"

				+ " left join db_gnomad_r211 as t8 on t2.chr = t8.chr and t2.pos = t8.pos and t2.ref = t8.ref and t2.alt = t8.alt "

				+ " where t2.sampleID = ? ";

		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, ""+sample.sampleID);
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<GermlineMutation> mutations = makeGermlineModel(rs);
		preparedStatement.close();
		return mutations;
	}



	/**
	 * Get all reported mutations from the MRN on the provided sample
	 * @param sample
	 * @return
	 * @throws Exception
	 */
	private static void addReportedMutationsByMRN(Sample sample, ArrayList<Mutation> mutations) throws Exception {
		ArrayList<Mutation> reportedMutations = new ArrayList<Mutation>();
		for(Sample otherSample : sample.getLinkedPatientSamples()) {
			reportedMutations.addAll(getMutationDataByID(otherSample, false));			
		}
		
		for(Mutation mutation : mutations) {
			for(Mutation other : reportedMutations) {
				if(mutation.equals(other)) {
					mutation.addOtherMutation(other);
				}
			}
		}
	}
	
	/**
	 * Acquires the cosmicID from the database. If it isn't found, an empty array is returned
	 */
	static ArrayList<String> getCosmicIDs(Mutation mutation) throws Exception{
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

	static void updateOncokbInfo(Mutation mutation) throws Exception{
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

	static void updatePmkbInfo(Mutation mutation) throws Exception{
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

	static void updateCivicInfo(Mutation mutation) throws Exception{
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

	/**
	 * Acquires the number of occurrences of this mutation from the database.
	 */
	static int getOccurrenceCount(Mutation mutation) throws Exception{

        Coordinate coordinate = mutation.getCoordinate();
		String assay = mutation.getAssay();
		String query = "select count(*) as occurrence "

				+ " from sampleVariants"
				+ " join samples on samples.sampleID = sampleVariants.sampleID "
				+ " join assays on assays.assayID = samples.assayID "

				+ " where sampleVariants.impact != 'No Call' and sampleVariants.chr = ? and sampleVariants.pos = ? and sampleVariants.ref = ? and sampleVariants.alt = ? and assays.assayName = ?"
				+ " and sampleVariants.altFreq >= " + Configurations.ALLELE_FREQ_FILTER
				+ " and samples.lastName not like 'Horizon%' ";//Filter control samples from occurrence count
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
	
	private static ArrayList<Mutation> makeModel(ResultSet rs) throws Exception{
		ArrayList<Mutation> mutations = new ArrayList<Mutation>();

		while(rs.next()){
			Mutation mutation = new Mutation();

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
			ArrayList<Annotation> annotationHistory = getVariantAnnotationHistory(mutation);
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

	private static ArrayList<GermlineMutation> makeGermlineModel(ResultSet rs) throws Exception{
		ArrayList<GermlineMutation> mutations = new ArrayList<GermlineMutation>();

		while(rs.next()){
			GermlineMutation mutation = new GermlineMutation();

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


			//temp holder fields - filled later separately
			mutation.setOccurrence(getIntegerOrNull(rs, "occurrence"));


			//annotation history
			ArrayList<Annotation> annotationHistory = getVariantAnnotationHistory(mutation);
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

	private static String getStringOrBlank(ResultSet rs, String columnLabel){
		try{
			if( rs.getString(columnLabel) == null) {
				return "";
			}
			return rs.getString(columnLabel);
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
	
	private static ArrayList<Annotation> getVariantAnnotationHistory(Mutation mutation) throws Exception{
		Coordinate coordinate = mutation.getCoordinate();
		ArrayList<Annotation> annotations = new ArrayList<Annotation>() ;
		PreparedStatement selectStatement = databaseConnection.prepareStatement("select annotationID, classification, curation, somatic, enteredBy, enterDate from variantAnnotation where chr = ? and pos = ? and ref = ? and alt = ? order by annotationID asc");
		selectStatement.setString(1, coordinate.getChr());
		selectStatement.setString(2, coordinate.getPos());
		selectStatement.setString(3, coordinate.getRef());
		selectStatement.setString(4, coordinate.getAlt());
		ResultSet rs = selectStatement.executeQuery();
		while(rs.next()){
			annotations.add(new Annotation(rs.getInt("annotationID"), mutation, rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getTimestamp(6)));
		}
		selectStatement.close();
		return annotations;
	}
}
