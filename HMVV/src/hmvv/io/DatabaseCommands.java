package hmvv.io;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.TreeMap;

import hmvv.main.Configurations;
import hmvv.model.*;

public class DatabaseCommands {

	private static Connection databaseConnection = null;

	public static void connect() throws Exception{
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://" + SSHConnection.getForwardingHost() +":" + SSHConnection.getForwardingPort() + "/";
		String[] credentials = null;

		if(!SSHConnection.isSuperUser()){
			credentials = Configurations.READ_ONLY_CREDENTIALS;
		}else{
			credentials = Configurations.READ_WRITE_CREDENTIALS;
		}

		try{
			Class.forName(driver);
			databaseConnection = DriverManager.getConnection(url+Configurations.DATABASE_NAME+"?noAccessToProcedureBodies=true", credentials[0], credentials[1]);
		}catch (Exception e){
			throw new Exception("mysql connection error: " + e.getMessage());
		}
	}

	/* ************************************************************************
	 * Insert Data Command
	 *************************************************************************/
	public static void insertDataIntoDatabase(Sample sample) throws Exception{
		String assay = sample.assay;
		String instrument = sample.instrument;
		String lastName = sample.getLastName();
		String firstName = sample.getFirstName();
		String orderNumber = sample.getOrderNumber();
		String pathologyNumber = sample.getPathNumber();
		String tumorSource = sample.getTumorSource();
		String tumorPercent = sample.getTumorPercent();
		String runID = sample.runID;
		String sampleName = sample.sampleName;
		String coverageID = sample.coverageID;//no coverageID on nextseq
		String variantCallerID = sample.callerID;//no variant caller on nextseq
		String runDate = sample.runDate;
		String patientHistory = sample.getPatientHistory();
		String diagnosis = sample.getDiagnosis();
		String note = sample.getNote();
		String enteredBy = sample.enteredBy;

		//check if sample is already present in data
		String checkSample = "select samples.runID, samples.sampleName, assays.assayName, instruments.instrumentName from samples " +
				"join instruments on instruments.instrumentID = samples.instrumentID " +
				"join assays on assays.assayID = samples.assayID " +
				"where instruments.instrumentName = ? and assays.assayName = ? and samples.runID = ? and samples.sampleName = ? ";
		PreparedStatement pstCheckSample = databaseConnection.prepareStatement(checkSample);
		pstCheckSample.setString(1, instrument);
		pstCheckSample.setString(2, assay);
		pstCheckSample.setString(3, runID);
		pstCheckSample.setString(4, sampleName);
		ResultSet rsCheckSample = pstCheckSample.executeQuery();
		Integer sampleCount = 0;
		while(rsCheckSample.next()){
			sampleCount += 1;
			break;
		}
		pstCheckSample.close();


		if(sampleCount != 0) {
			throw new Exception("Error: Supplied sample exists in database; data not entered");
		}

		String enterSample = "insert into samples "
				+ "(assayID, instrumentID, runID, sampleName, coverageID, callerID, lastName, firstName, orderNumber, pathNumber, tumorSource ,tumorPercent,  runDate, note, enteredBy, patientHistory, bmDiagnosis) "
				+ "values ("
				+ "	(select assayID from assays where assayName = ?),"
				+ "	(select instrumentID from instruments where instrumentName = ? ),"
				+ "	?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
		PreparedStatement pstEnterSample = databaseConnection.prepareStatement(enterSample);
		pstEnterSample.setString(1, assay);
		pstEnterSample.setString(2, instrument);
		pstEnterSample.setString(3, runID);
		pstEnterSample.setString(4, sampleName);
		pstEnterSample.setString(5, coverageID);
		pstEnterSample.setString(6, variantCallerID);
		pstEnterSample.setString(7, lastName);
		pstEnterSample.setString(8, firstName);
		pstEnterSample.setString(9, orderNumber);
		pstEnterSample.setString(10, pathologyNumber);
		pstEnterSample.setString(11, tumorSource);
		pstEnterSample.setString(12, tumorPercent);
		pstEnterSample.setString(13, runDate);
		pstEnterSample.setString(14, note);
		pstEnterSample.setString(15, enteredBy);
		pstEnterSample.setString(16, patientHistory);
		pstEnterSample.setString(17, diagnosis);
		
		pstEnterSample.executeUpdate();
		pstEnterSample.close();

		//get ID
		String findID = "select samples.sampleID from samples " +
				"join instruments on instruments.instrumentID = samples.instrumentID " +
				"join assays on assays.assayID = samples.assayID " +
				"where instruments.instrumentName = ? and assays.assayName = ? and samples.runID = ? and samples.sampleName = ?";
		PreparedStatement pstFindID = databaseConnection.prepareStatement(findID);
		pstFindID.setString(1, instrument);
		pstFindID.setString(2, assay);
		pstFindID.setString(3, runID);
		pstFindID.setString(4, sampleName);
		
		ResultSet rsFindID = pstFindID.executeQuery();
		Integer count = 0;
		String sampleID = "";
		while(rsFindID.next()){
			sampleID = rsFindID.getString(1);
			try{
				sample.setSampleID(Integer.parseInt(sampleID));
			}catch(Exception e){
				throw new Exception("sampleID Assigned by database is not an integer: " + sampleID);
			}
			count += 1;
		}
		if(count == 0){
			throw new Exception("Error2: Problem locating the entered sample; data not entered");
		}
		pstFindID.close();

		if ( assay.equals("tmb")){

			insertTumorNormalPair(sample);
		}

		queueSampleForRunningPipeline(sample);
	}

	private static void queueSampleForRunningPipeline(Sample sample) throws Exception{
		String queueSample = 
				"INSERT INTO pipelineQueue "
				+ "(sampleID,timeSubmitted) VALUES (?, now() )";
		PreparedStatement pstQueueSample = databaseConnection.prepareStatement(queueSample);
		pstQueueSample.setInt(1, sample.getSampleID());
		
		pstQueueSample.executeUpdate();
		pstQueueSample.close();

	}

	private static void insertTumorNormalPair(Sample sampleTMB)throws Exception{

		String enterSampleNormalPair = "insert into sampleNormalPair "
				+ "(sampleID,normalPairRunID,normalSampleName,enterDate) "
				+ "values ( ?,?,?,now())";
		PreparedStatement pstEnterSampleNormalPair = databaseConnection.prepareStatement(enterSampleNormalPair);
		pstEnterSampleNormalPair.setInt(1, sampleTMB.sampleID);
		pstEnterSampleNormalPair.setString(2, sampleTMB.getNormalRunID());
		pstEnterSampleNormalPair.setString(3, sampleTMB.getNormalSampleName());

		pstEnterSampleNormalPair.executeUpdate();
		pstEnterSampleNormalPair.close();


	}

	/* ************************************************************************
	 * Assay Queries
	 *************************************************************************/
	public static ArrayList<String> getAllInstruments() throws Exception{
		ArrayList<String> instruments = new ArrayList<String>();
		String getAssay = "select distinct instrumentName from instruments order by instrumentName";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(getAssay);
		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){
			instruments.add(rs.getString(1));
		}
		preparedStatement.close();
		return instruments;
	}
	
	public static ArrayList<String> getAllAssays() throws Exception{
		ArrayList<String> assays = new ArrayList<String>();
		String getAssay = "select distinct assayName from assays order by assayName";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(getAssay);
		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){
			assays.add(rs.getString(1));
		}
		preparedStatement.close();
		return assays;
	}

	public static ArrayList<String> getAssaysForInstrument(String instrument) throws Exception{
		ArrayList<String> assays = new ArrayList<String>();
		PreparedStatement preparedStatement = databaseConnection.prepareStatement("select assays.assayName from assays " +
				"join assayInstrument on assayInstrument.assayID = assays.assayID " +
				"join instruments on instruments.instrumentID = assayInstrument.instrumentID " +
				"where instruments.instrumentName = ?");
		preparedStatement.setString(1, instrument);
		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){
			assays.add(rs.getString(1));
		}
		preparedStatement.close();
		return assays;
	}

	public static void createAssay(String instrument, String assay) throws Exception{
		PreparedStatement preparedStatement = databaseConnection.prepareStatement("insert into assays values (?, ?)");
		preparedStatement.setString(1, assay);
		preparedStatement.setString(2, instrument);
		preparedStatement.executeUpdate();
		preparedStatement.close();
	}

	/* ************************************************************************
	 * Mutation Queries
	 *************************************************************************/
	public static ArrayList<Mutation> getBaseMutationsBySample(Sample sample) throws Exception{
		return getMutationDataByID(sample, false);
	}

	public static ArrayList<Mutation> getExtraMutationsBySample(Sample sample) throws Exception{
		return getMutationDataByID(sample, true);
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

	/**
	 * Acquires the cosmicID from the database. If it isn't found, an empty array is returned
	 */
	public static ArrayList<String> getCosmicIDs(Mutation mutation) throws Exception{
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

	public static String getCosmicInfo(String cosmicID) throws Exception{
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

	public static void updateOncokbInfo(Mutation mutation) throws Exception{

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

	public static void updatePmkbInfo(Mutation mutation) throws Exception{

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

	public static void updateCivicInfo(Mutation mutation) throws Exception{
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
	public static int getOccurrenceCount(Mutation mutation) throws Exception{
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

	//	/**
	//	 * Updates the reported flag in the mutation data table
	//	 *
	//	 * @param setToReported
	//	 * @param sampleID This is primary key for mutation data table
	//	 * @param chr
	//	 * @param pos
	//	 * @param ref
	//	 * @param alt
	//	 * @throws SQLException
	//	 */
	public static void updateReportedStatus(boolean setToReported, Integer sampleID, Coordinate coordinate) throws SQLException{
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

	/* ************************************************************************
	 * Sample Queries
	 *************************************************************************/
	public static ArrayList<Sample> getAllSamples() throws Exception{
		String query = "select s.sampleID, a.assayName as assay, i.instrumentName as instrument, s.lastName, s.firstName, s.orderNumber, " +
				"s.pathNumber, s.tumorSource, s.tumorPercent, s.runID, s.sampleName, s.coverageID, s.callerID, " +
				"s.runDate, s.patientHistory, s.bmDiagnosis, s.note, s.enteredBy from samples as s " +
				"join instruments  as i on i.instrumentID = s.instrumentID " +
				"join assays as a on a.assayID = s.assayID ";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<Sample> samples = new ArrayList<Sample>();
		while(rs.next()){
			Sample s = getSample(rs);

			if (s.assay.equals("tmb")){

				updateTMBInfo(s);
			}
			samples.add(s);
		}
		preparedStatement.close();
		return samples;
	}

	private static Sample getSample(ResultSet row) throws SQLException{
		Sample sample = new Sample(
				Integer.parseInt(row.getString("sampleID")),
				row.getString("assay"),
				row.getString("instrument"),
				row.getString("lastName"),
				row.getString("firstName"),
				row.getString("orderNumber"),
				row.getString("pathNumber"),
				row.getString("tumorSource"),
				row.getString("tumorPercent"),
				row.getString("runID"),
				row.getString("sampleName"),
				row.getString("coverageID"),
				row.getString("callerID"),
				row.getString("runDate"),
				row.getString("patientHistory"),
				row.getString("bmDiagnosis"),
				row.getString("note"),
				row.getString("enteredBy")
				);
		return sample;
	}

	private static void updateTMBInfo(Sample sample) throws SQLException{

		PreparedStatement preparedStatement = databaseConnection.prepareStatement("select normalPairRunID,normalSampleName from sampleNormalPair where sampleID = ?");
		preparedStatement.setInt(1, sample.sampleID);
		ResultSet rs = preparedStatement.executeQuery();
		if(rs.next()){
			sample.setNormalRunID(rs.getString("normalPairRunID"));
			sample.setNormalSampleName(rs.getString("normalSampleName"));
		}
		preparedStatement.close();

	}
	public static void updateSampleNote(int sampleID, String newNote) throws Exception{
		PreparedStatement updateStatement = databaseConnection.prepareStatement("update samples set note = ? where sampleID = ?");
		updateStatement.setString(1, newNote);
		updateStatement.setString(2, ""+sampleID);
		updateStatement.executeUpdate();
		updateStatement.close();
	}

	public static void updateSample(Sample sample) throws Exception{
		PreparedStatement updateStatement = databaseConnection.prepareStatement("update samples set lastName = ?, firstName = ?, orderNumber = ?, pathNumber = ?, "
				+ "tumorSource = ?, tumorPercent = ?, patientHistory = ?, bmDiagnosis = ?, note = ? where sampleID = ?");
		updateStatement.setString(1, sample.getLastName());
		updateStatement.setString(2, sample.getFirstName());
		updateStatement.setString(3, sample.getOrderNumber());
		updateStatement.setString(4, sample.getPathNumber());
		updateStatement.setString(5, sample.getTumorSource());
		updateStatement.setString(6, sample.getTumorPercent());
		updateStatement.setString(7, sample.getPatientHistory());
		updateStatement.setString(8, sample.getDiagnosis());
		updateStatement.setString(9, sample.getNote());
		updateStatement.setString(10, sample.sampleID+"");
		updateStatement.executeUpdate();
		updateStatement.close();
	}

	public static void deleteSample(int sampleID) throws SQLException{
		CallableStatement callableStatement = databaseConnection.prepareCall("{call deleteSample(?)}");
		callableStatement.setString(1, ""+sampleID);
		callableStatement.executeUpdate();
		callableStatement.close();
	}

	/* ************************************************************************
	 * Amplicon Queries
	 *************************************************************************/
	public static AmpliconCount getAmpliconCount(int sampleID) throws Exception{
		PreparedStatement updateStatement = databaseConnection.prepareStatement("CALL calcAmpliconCount(?);");
		updateStatement.setString(1, ""+sampleID);
		ResultSet getSampleResult = updateStatement.executeQuery();
		if(getSampleResult.next()){
			AmpliconCount ampliconCount = new AmpliconCount(sampleID, getSampleResult.getString(1), getSampleResult.getString(2));
			if(getSampleResult.next()){
				throw new Exception("Error: more than one amplicon count result located for the sample");
			}
			updateStatement.close();
			getSampleResult.close();
			return ampliconCount;
		}
		updateStatement.close();
		throw new Exception("No amplicon data found in the database");
	}

	public static ArrayList<Amplicon> getFailedAmplicon(int sampleID) throws Exception{
		ArrayList<Amplicon> amplicons = new ArrayList<Amplicon>();
		PreparedStatement updateStatement = databaseConnection.prepareStatement("select ampliconName, gene, readDepth from sampleAmplicons where sampleID = ? and readDepth<100");
		updateStatement.setString(1, ""+sampleID);
		ResultSet getSampleResult = updateStatement.executeQuery();
		while(getSampleResult.next()){
			Amplicon amplicon = new Amplicon(sampleID, getSampleResult.getString("gene"), getSampleResult.getString("ampliconName"), getSampleResult.getInt("readDepth"));
			amplicons.add(amplicon);
		}
		updateStatement.close();
		return amplicons;
	}

	/* ************************************************************************
	 * Annotation Queries
	 *************************************************************************/
	public static ArrayList<GeneAnnotation> getGeneAnnotationHistory(String gene) throws Exception{
		ArrayList<GeneAnnotation>  geneannotations = new ArrayList<GeneAnnotation>() ;
		PreparedStatement selectStatement = databaseConnection.prepareStatement("select geneAnnotationID, gene, curation, enteredBy, enterDate from geneAnnotation where gene = ? order by geneAnnotationID asc");
		selectStatement.setString(1, gene);
		ResultSet rs = selectStatement.executeQuery();
		while(rs.next()){
			geneannotations.add(new GeneAnnotation(rs.getInt("geneAnnotationID") , rs.getString("gene") , rs.getString("curation") , rs.getString("enteredBy") , rs.getTimestamp("enterDate")));
		}
		selectStatement.close();
		return geneannotations;
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

	public static String getVariantAnnotationDraft(Coordinate coordinate) throws Exception{
		String draft="";
		PreparedStatement selectStatement = databaseConnection.prepareStatement("select draft from variantAnnotationDraft where chr = ? and pos = ? and ref = ? and alt = ?");
		selectStatement.setString(1, coordinate.getChr());
		selectStatement.setString(2, coordinate.getPos());
		selectStatement.setString(3, coordinate.getRef());
		selectStatement.setString(4, coordinate.getAlt());
		ResultSet rs = selectStatement.executeQuery();
		if(rs.next()){
			draft=rs.getString(1);
		}
		selectStatement.close();
		return draft;
	}

	public static void addGeneAnnotationCuration(GeneAnnotation geneAnnotation) throws Exception{
		String gene = geneAnnotation.gene;
		String curation = geneAnnotation.curation;
		String enteredBy = geneAnnotation.enteredBy;
		PreparedStatement pstEnterGeneAnnotation = databaseConnection.prepareStatement("insert into geneAnnotation (gene, curation, enteredBy, enterDate) values (?, ?, ?, ?)");
		pstEnterGeneAnnotation.setString(1, gene);
		pstEnterGeneAnnotation.setString(2, curation);
		pstEnterGeneAnnotation.setString(3, enteredBy);
		pstEnterGeneAnnotation.setTimestamp(4, new java.sql.Timestamp(geneAnnotation.enterDate.getTime()));
		pstEnterGeneAnnotation.executeUpdate();
		pstEnterGeneAnnotation.close();
	}

	public static void addVariantAnnotationCuration(Annotation annotation) throws Exception{
		Coordinate coordinate = annotation.mutation.getCoordinate();
		String chr = coordinate.getChr();
		String pos = coordinate.getPos();
		String ref = coordinate.getRef();
		String alt = coordinate.getAlt();
		String classification = annotation.classification;
		String curation = annotation.curation;
		String somatic = annotation.somatic;
		String enteredBy = annotation.enteredBy;

		PreparedStatement pstEnterAnnotation = databaseConnection.prepareStatement("insert into variantAnnotation ( chr, pos, ref, alt, classification, curation, somatic, enteredBy, enterDate) "
				+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
		pstEnterAnnotation.setString(1, chr);
		pstEnterAnnotation.setString(2, pos);
		pstEnterAnnotation.setString(3, ref);
		pstEnterAnnotation.setString(4, alt);
		pstEnterAnnotation.setString(5, classification);
		pstEnterAnnotation.setString(6, curation);
		pstEnterAnnotation.setString(7, somatic);
		pstEnterAnnotation.setString(8, enteredBy);
		pstEnterAnnotation.setTimestamp(9, new java.sql.Timestamp(annotation.enterDate.getTime()));
		pstEnterAnnotation.executeUpdate();
		pstEnterAnnotation.close();
	}

	public static void addVariantAnnotationDraft(Coordinate coordinate, String draft) throws Exception{
		String chr = coordinate.getChr();
		String pos = coordinate.getPos();
		String ref = coordinate.getRef();
		String alt = coordinate.getAlt();

		PreparedStatement selectStatement = databaseConnection.prepareStatement("select draft from variantAnnotationDraft where chr = ? and pos = ? and ref = ? and alt = ?");
		selectStatement.setString(1, chr);
		selectStatement.setString(2, pos);
		selectStatement.setString(3, ref);
		selectStatement.setString(4, alt);
		ResultSet rsCheckSample = selectStatement.executeQuery();

		if(rsCheckSample.next()){
			PreparedStatement pstEnterAnnotation = databaseConnection.prepareStatement("update variantAnnotationDraft set draft=? where chr = ? and pos = ? and ref = ? and alt = ?");
			pstEnterAnnotation.setString(1, draft);
			pstEnterAnnotation.setString(2, chr);
			pstEnterAnnotation.setString(3, pos);
			pstEnterAnnotation.setString(4, ref);
			pstEnterAnnotation.setString(5, alt);
			pstEnterAnnotation.executeUpdate();
			pstEnterAnnotation.close(); }
		else {
			PreparedStatement pstEnterAnnotation = databaseConnection.prepareStatement("insert into variantAnnotationDraft ( chr, pos, ref, alt, draft) "
					+ "values (?, ?, ?, ?, ?)");
			pstEnterAnnotation.setString(1, chr);
			pstEnterAnnotation.setString(2, pos);
			pstEnterAnnotation.setString(3, ref);
			pstEnterAnnotation.setString(4, alt);
			pstEnterAnnotation.setString(5, draft);
			pstEnterAnnotation.executeUpdate();
			pstEnterAnnotation.close();
		}
	}

	/* ************************************************************************
	 * Monitor Pipelines Queries
	 *************************************************************************/
	private static Pipeline getPipeline(ResultSet row) throws Exception{
		Pipeline pipeline = new Pipeline(
				row.getInt("queueID"),
				row.getInt("sampleID"),
				row.getString("runID"),
				row.getString("sampleName"),
				row.getString("assayName"),
				row.getString("instrumentName"),
				row.getString("plstatus"),
				row.getTimestamp("timeUpdated")
				);
		return pipeline;
	}

	public static ArrayList<Pipeline> getAllPipelines() throws Exception{
		String query = "select" + 
				" queueTable.queueID, queueTable.sampleID, queueTable.runID, queueTable.sampleName, queueTable.assayName, queueTable.instrumentName," + 
				" statusTable.plStatus, statusTable.timeUpdated" + 
				" from" + 
				" (" + 
					" select pipelineQueue.queueID, samples.sampleID, samples.runID, samples.sampleName, assays.assayName, instruments.instrumentName" + 
					" from pipelineQueue" + 
					" join samples on samples.sampleID = pipelineQueue.sampleID" + 
					" join assays on assays.assayID = samples.assayID" + 
					" join instruments on instruments.instrumentID = samples.instrumentID" + 
					" where timeSubmitted >= now() - interval 10 day" + 
					" ) as queueTable" + 
				
				" left join " + 
				
				" (" + 
				" select pipelineStatus.pipelineStatusID, pipelineStatus.queueID, pipelineStatus.plStatus, pipelineStatus.timeUpdated" + 
				"   from" + 
				"    (" + 
				"     select max(pipelineStatusID) as pipelineStatusID" + 
				"     from pipelineStatus" + 
				"     where timeUpdated >= now() - interval 10 day" + 
				"     group by queueID" + 
				"    ) as maxPipelineStatusID" + 
				"   join pipelineStatus on pipelineStatus.pipelineStatusID = maxPipelineStatusID.pipelineStatusID " + 
				"   " + 
				" ) as statusTable" + 
				 
				" on queueTable.queueID = statusTable.queueID" + 
				" order by queueTable.queueID desc;";
		
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<Pipeline> pipelines = new ArrayList<Pipeline>();

		while(rs.next()){
			Pipeline p = getPipeline(rs);
			pipelines.add(p);
		}
		preparedStatement.close();

		return pipelines;
	}

	public static ArrayList<PipelineStatus> getPipelineDetail(int queueID) throws Exception{
		PreparedStatement preparedStatement = databaseConnection.prepareStatement("select pipelineStatusID, plStatus, timeUpdated from pipelineStatus where queueID = ? order by timeupdated asc");
		preparedStatement.setInt(1, queueID);
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<PipelineStatus> rows = new ArrayList<PipelineStatus>();

		while(rs.next()){
			int pipelineStatusID = rs.getInt("pipelineStatusID");
			String plStatusString = rs.getString("plStatus");
			Timestamp timeUpdated = rs.getTimestamp("timeUpdated");

			PipelineStatus pipelineStatus = new PipelineStatus(pipelineStatusID, queueID, plStatusString, timeUpdated);
			rows.add(pipelineStatus);
		}
		preparedStatement.close();

		return rows;
	}

	public static float getPipelineTimeEstimate(Pipeline pipeline) throws Exception {
		int averageRunTime = 0;
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(
				" select AVG(runtime) as averageMinutes from " +
				"  ( " +
				"  select samples.runID, instruments.instrumentName, assays.assayName, ps1.queueID, pipelineStatus.timeUpdated as startTime, ps1.timeUpdated as completedTime, TIMESTAMPDIFF(MINUTE, pipelineStatus.timeUpdated, ps1.timeUpdated) as runtime " +
				 
				"  from pipelineStatus " +
				"  join pipelineStatus ps1 on pipelineStatus.queueID = ps1.queueID " +
				"  join pipelineQueue on ps1.queueID = pipelineQueue.queueID " +
				"  join samples on samples.sampleID = pipelineQueue.sampleID " +
				"  join assays on assays.assayID = samples.assayID " +
				"  join instruments on instruments.instrumentID = samples.instrumentID " +

				"  where pipelineStatus.plStatus = \"started\" " +
				"  and ps1.plStatus = \"pipelineCompleted\" " +
				"  and instruments.instrumentName = ? and assays.assayName = ?) temp "
		);
		
		preparedStatement.setString(1, pipeline.getInstrumentName());
		preparedStatement.setString(2, pipeline.getAssayName());

		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){
			averageRunTime = rs.getInt("averageMinutes");
		}
		preparedStatement.close();
		return (float)averageRunTime;
	}

	/* ************************************************************************
	 * Query QC Plot
	 *************************************************************************/

	/**
	 *
	 * @param assay
	 * @return list of amplicons ordered by gene, ampliconName, then readDepth
	 * @throws Exception
	 */
	public static TreeMap<String, GeneQCDataElementTrend> getAmpliconQCData(String assay) throws Exception{
		String query = "select sampleAmplicons.sampleID, sampleAmplicons.gene, sampleAmplicons.ampliconName, sampleAmplicons.readDepth from sampleAmplicons"
				+ " join samples on sampleAmplicons.sampleID = samples.sampleID"
				+ " join assays on assays.assayID = samples.assayID"
				+ " where samples.lastName like 'Horizon%' ";

		String geneFilter;
		if(assay.equals("heme")) {
			geneFilter = " and (sampleAmplicons.gene = 'BRAF' or sampleAmplicons.gene = 'KIT' or sampleAmplicons.gene = 'KRAS') and assayName = 'heme'";
		}else if(assay.equals("gene50")) {
			geneFilter = " and (sampleAmplicons.gene like '%EGFR%' or sampleAmplicons.gene like '%KRAS%' or sampleAmplicons.gene like '%NRAS%') and assayName = 'gene50'";
		}else if(assay.equals("neuro")) {
			//TODO the sampleAmplicons table currently does not properly store gene name
			//geneFilter = " and (sampleAmplicons.gene = 'EGFR' or sampleAmplicons.gene = 'IDH1' or sampleAmplicons.gene = 'KRAS' or sampleAmplicons.gene = 'NRAS') and assayName = 'neuro'";
			throw new Exception("Unsupported Assay: " + assay);
		}else {
			throw new Exception("Unsupported Assay: " + assay);
		}
		query += geneFilter;

		query += " order by sampleID, gene, ampliconName asc";

		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		ResultSet rs = preparedStatement.executeQuery();

		TreeMap<String, GeneQCDataElementTrend> geneAmpliconTrends = new TreeMap<String, GeneQCDataElementTrend>();

		while(rs.next()){
			String gene = rs.getString("gene");
			if(assay.equals("gene50")) {
				String[] splitGene = gene.split("_");
				if(splitGene.length == 3) {//the expected value
					gene = splitGene[1];
				}
			}
			QCDataElement amplicon = new QCDataElement(rs.getInt("sampleID"), gene, rs.getString("ampliconName"), rs.getInt("readDepth"));
			GeneQCDataElementTrend geneAmpliconTrend = geneAmpliconTrends.get(amplicon.gene);
			if(geneAmpliconTrend == null) {
				geneAmpliconTrend = new GeneQCDataElementTrend(amplicon.gene);
				geneAmpliconTrends.put(amplicon.gene, geneAmpliconTrend);
			}

			geneAmpliconTrend.addDataElement(amplicon);
		}
		preparedStatement.close();

		return geneAmpliconTrends;
	}
	
	/**
	 *
	 * @param assay
	 * @return list of amplicons ordered by gene, ampliconName, then readDepth
	 * @throws Exception
	 */
	public static TreeMap<String, GeneQCDataElementTrend> getSampleQCData(String assay) throws Exception{
		String query = "select sampleVariants.sampleID, sampleVariants.gene, sampleVariants.HGVSc, sampleVariants.HGVSp, sampleVariants.altFreq, cosmicID"
				+ " from samples"
				+ " join assays on assays.assayID = samples.assayID"
				+ " join sampleVariants on sampleVariants.sampleID = samples.sampleID"
				+ " join db_cosmic_grch37v86 on sampleVariants.chr = db_cosmic_grch37v86.chr and sampleVariants.pos = db_cosmic_grch37v86.pos and sampleVariants.ref = db_cosmic_grch37v86.ref and sampleVariants.alt = db_cosmic_grch37v86.alt "
				+ " where samples.lastName like 'Horizon%' "
				+ " and HGVSp IS NOT NULL";//have to do this because old data has null values

		String geneFilter;
		if(assay.equals("heme")) {
			geneFilter =
					//COSM1140132 and COSM532 have the same coordinates and track together
					//COSM1135366 and COSM521 have the same coordinates and track together
					"   and ( cosmicID = 'COSM476' or cosmicID = 'COSM1314' or cosmicID = 'COSM521' or cosmicID = 'COSM532')"
					+ " and assays.assayName = 'heme' ";
		}else if(assay.equals("gene50")) {
			geneFilter =
					"   and ( cosmicID = 'COSM6252' or cosmicID = 'COSM532' or cosmicID = 'COSM580')"
							+ " and assays.assayName = 'gene50' ";
		}else if(assay.equals("neuro")) {
			geneFilter =
					"   and ( cosmicID = 'COSM6252' or cosmicID = 'COSM97131' or cosmicID = 'COSM532' or cosmicID = 'COSM580')"
							+ " and assays.assayName = 'neuro' ";
		}else {
			throw new Exception("Unsupported Assay: " + assay);
		}

		query += geneFilter;
		query += " order by sampleID, gene";

		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		ResultSet rs = preparedStatement.executeQuery();

		TreeMap<String, GeneQCDataElementTrend> geneVariantTrends = new TreeMap<String, GeneQCDataElementTrend>();

		while(rs.next()){
			String variant = rs.getString("HGVSp");
			variant = variant.replaceAll(".*:", "");
			variant = Configurations.abbreviationtoLetter(variant);
			variant += "(" + rs.getString("cosmicID") + ")";

			QCDataElement variantDataElement = new QCDataElement(rs.getInt("sampleID"), rs.getString("gene"), variant, Math.round(rs.getFloat("altFreq")));
			GeneQCDataElementTrend geneVariantTrend = geneVariantTrends.get(variantDataElement.gene);
			if(geneVariantTrend == null) {
				geneVariantTrend = new GeneQCDataElementTrend(variantDataElement.gene);
				geneVariantTrends.put(variantDataElement.gene, geneVariantTrend);
			}

			geneVariantTrend.addDataElement(variantDataElement);
		}
		preparedStatement.close();

		return geneVariantTrends;
	}

    /* ************************************************************************
     * Query Tumor Mutation Burden Data
     *************************************************************************/

    public static ExomeTumorMutationBurden getSampleTumorMutationBurden(Sample sample)throws Exception{

        PreparedStatement preparedStatement = databaseConnection.prepareStatement("select TMBPair,TMBTotalVariants,TMBScore,TMBGroup from sampleTumorMutationBurden where sampleID = ? ");
        preparedStatement.setInt(1, sample.sampleID);
        ResultSet rs = preparedStatement.executeQuery();
        ExomeTumorMutationBurden exomeTumorMutationBurden = new ExomeTumorMutationBurden();
        exomeTumorMutationBurden.setSampleID(sample.sampleID);
        while(rs.next()){
            exomeTumorMutationBurden.setTMBPair(rs.getString("TMBPair"));
            Integer totalvariants = rs.getInt("TMBTotalVariants");
            exomeTumorMutationBurden.setTMBTotalVariants(totalvariants.toString());
            Float tmbscore = rs.getFloat("TMBScore");
            exomeTumorMutationBurden.setTMBScore(tmbscore.toString());
            exomeTumorMutationBurden.setTMBGroup(rs.getString("TMBGroup"));
        }
        preparedStatement.close();

    return exomeTumorMutationBurden;
    }


}
