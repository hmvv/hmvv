package hmvv.io;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import hmvv.main.Configurations;
import hmvv.model.Amplicon;
import hmvv.model.AmpliconCount;
import hmvv.model.Annotation;
import hmvv.model.Coordinate;
import hmvv.model.GeneAnnotation;
import hmvv.model.Mutation;
import hmvv.model.Pipeline;
import hmvv.model.PipelineStatus;
import hmvv.model.Sample;

public class DatabaseCommands {

	private static Connection databaseConnection = null;

	public static Connection getDatabaseConnection(){
		return databaseConnection;
	}

	public static void connect(String user) throws Exception{
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
		String sampleID = sample.sampleID;
		String coverageID = sample.coverageID;//no coverageID on nextseq
		String variantCallerID = sample.callerID;//no variant caller on nextseq
		String runDate = sample.runDate;
		String note = sample.getNote();
		String enteredBy = sample.enteredBy;

		//check if sample is already present in data
		String checkSample = String.format("select * from Samples where instrument = '%s' and runID = '%s' and sampleID = '%s'", instrument, runID, sampleID);
		PreparedStatement pstCheckSample = databaseConnection.prepareStatement(checkSample);
		ResultSet rsCheckSample = pstCheckSample.executeQuery();
		Integer sampleCount = 0;
		while(rsCheckSample.next()){
			sampleCount += 1;
			break;
		}
		if(sampleCount != 0){
			throw new Exception("Error: Supplied sample exists in database; data not entered");
		}


		String enterSample = String.format("insert into Samples "
				+ "(assay, instrument, lastName, firstName, orderNumber, pathNumber, tumorPercent, runID, sampleID, coverageID, callerID, runDate, note, enteredBy, tumorSource) "
				+ "values ('%s','%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
				assay, instrument, lastName, firstName, orderNumber, pathologyNumber, tumorPercent, runID, sampleID, coverageID, variantCallerID, runDate, note, enteredBy, tumorSource);
		PreparedStatement pstEnterSample = databaseConnection.prepareStatement(enterSample);
		pstEnterSample.executeUpdate();

		//get ID
		String findID = String.format("select ID from Samples where instrument = '%s' and runID = '%s' and sampleID = '%s'", instrument, runID, sampleID);

		PreparedStatement pstFindID = databaseConnection.prepareStatement(findID);
		ResultSet rsFindID = pstFindID.executeQuery();
		Integer count = 0;
		String ID = "";
		while(rsFindID.next()){
			ID = rsFindID.getString(1);
			try{
				sample.setID(Integer.parseInt(ID));
			}catch(Exception e){
				throw new Exception("ID Assigned by database is not an integer: " + ID);
			}
			count += 1;
		}
		if(count == 0){
			throw new Exception("Error2: Problem locating the entered sample; data not entered");
		}

		queueSampleForPipelineAnalysis(sample);
	}

	private static void queueSampleForPipelineAnalysis(Sample sample) throws Exception{
		
		String coverageID = "na";
		if(sample.coverageID.contains(".")) {
			coverageID = sample.coverageID.split("\\.")[1]; //get number only			
		}
		
		String variantCallerID = "na";
		if(sample.callerID.contains(".")) {
			variantCallerID = sample.callerID.split("\\.")[1]; //get number only			
		}
		
		String environment = Configurations.getEnvironment();
		
		String queueSample = String.format("INSERT INTO sampleAnalysisQueue " 
				+ "(ID, runID, sampleID, coverageID, vcallerID, assayID,instrumentID,environmentID, timeSubmitted)"
				+	" VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', now() )",
				sample.getID(), sample.runID, sample.sampleID, coverageID, variantCallerID, sample.assay, sample.instrument, environment);

		PreparedStatement pstQueueSample = databaseConnection.prepareStatement(queueSample);
		pstQueueSample.executeUpdate();
		
	}

	/* ************************************************************************
	 * Assay Queries
	 *************************************************************************/
	public static ArrayList<String> getAllAssays() throws Exception{
		ArrayList<String> assays = new ArrayList<String>();
		String getAssay = "select distinct assay from assays";
		PreparedStatement pst = databaseConnection.prepareStatement(getAssay);
		ResultSet rs = pst.executeQuery();
		while(rs.next()){
			assays.add(rs.getString(1));
		}
		pst.close();
		return assays;
	}

	private static int getPairedNormal(int tumorID) throws Exception{
		PreparedStatement preparedStatement = databaseConnection.prepareStatement("select normalID from tumorNormalPair where tumorID = ?");
		preparedStatement.setInt(1, tumorID);
		ResultSet rs = preparedStatement.executeQuery();
		if(rs.next()){
			return rs.getInt(1);
		}
		return -1;
	}

	public static ArrayList<Mutation> getPairedNormalMutations(int ID) throws Exception{
		int normalID = getPairedNormal(ID);
		if(normalID == -1){
			return null;
		}else{
			return getMutationDataByID(normalID);
		}
	}

	public static ArrayList<String> getInstrumentsForAssay(String assay) throws Exception{
		ArrayList<String> instruments = new ArrayList<String>();
		PreparedStatement preparedStatement = databaseConnection.prepareStatement("select instrument from assays where assay = ?");
		preparedStatement.setString(1, assay);
		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){
			instruments.add(rs.getString(1));
		}
		preparedStatement.close();
		return instruments;
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
	public static ArrayList<Mutation> getMutationDataByID(int ID) throws Exception{
		String query = "select t2.reported, t2.gene, t2.exons, t2.HGVSc, t2.HGVSp, t2.dbSNPID,"
				+ " t2.type, t2.genotype, t2.altFreq, t2.readDP, t2.altReadDP, "
				+ " t2.chr, t2.pos, t2.ref, t2.alt, t2.Consequence, t2.Sift, t2.PolyPhen,"
				+ " t4.altCount, t4.totalCount, t4.altGlobalFreq, t4.americanFreq, t4.asianFreq, t4.afrFreq, t4.eurFreq,"
				+ " t5.origin, t5.clinicalAllele, t5.clinicalSig, t5.clinicalAcc, t2.pubmed,"
				+ " t1.lastName, t1.firstName, t1.orderNumber, t1.assay, t1.tumorSource, t1.tumorPercent, t2.sampleID "
				+ " from data as t2"
				+ " left join g1000 as t4"
				+ " on t2.chr = t4.chr and t2.pos = t4.pos and t2.ref = t4.ref and t2.alt = t4.alt"
				+ " left join clinvar as t5"
				+ " on t2.chr = t5.chr and t2.pos = t5.pos and t2.ref = t5.ref and t2.alt = t5.alt"
				+ " left join Samples as t1"
				+ " on t2.sampleID = t1.ID"
				+ " where t2.sampleID = ?";

		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, ""+ID);
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
		String refMD5 = getMD5(coordinate.getRef());
		String altMD5 = getMD5(coordinate.getAlt());
		//TODO upgrade to latest COSMIC database
		String query = "select cosmicID from cosmic_grch37v82 where chr = ? and pos = ? and refMD5 = ? and altMD5 = ?";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, coordinate.getChr());
		preparedStatement.setString(2, coordinate.getPos());
		preparedStatement.setString(3, refMD5);
		preparedStatement.setString(4, altMD5);
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<String> cosmicIDs = new ArrayList<String>();
		while(rs.next()){
			String result = rs.getString(1);
			cosmicIDs.add(result);
		}
		rs.close();
		return cosmicIDs;
	}

	/**
	 * Acquires the number of occurrences of this mutation from the database.
	 */
	public static int getOccurrenceCount(Mutation mutation) throws Exception{
		Coordinate coordinate = mutation.getCoordinate();
		String assay = mutation.getAssay();
		String query = "select count(*) as occurrence from data"
				+ " where genotype != 'No Call' and chr = ? and pos = ? and ref = ? and alt = ? and assay = ?";
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

	public static ArrayList<Mutation> getMatchingMutations(Mutation mutation) throws Exception{
		Coordinate coordinate = mutation.getCoordinate();

		String query = "select t2.reported, t2.gene, t2.exons, t2.HGVSc, t2.HGVSp,"
				+ " t2.altFreq, t2.readDP, t2.altReadDP, t2.chr, t2.pos, t2.ref, t2.alt, t2.sampleID, "
				+ " t1.lastName, t1.firstName, t1.orderNumber, t1.assay, t1.tumorSource, t1.tumorPercent "
				+ " from data as t2"
				+ " left join Samples as t1"
				+ " on t2.sampleID = t1.ID"
				+ " where t2.genotype != ? and t1.assay = ? and t2.chr = ? and t2.pos = ? and t2.ref = ? and t2.alt = ?";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, "No Call");
		preparedStatement.setString(2, mutation.getAssay());
		preparedStatement.setString(3, coordinate.getChr());
		preparedStatement.setString(4, coordinate.getPos());
		preparedStatement.setString(5, coordinate.getRef());
		preparedStatement.setString(6, coordinate.getAlt());

		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<Mutation> mutations = makeModel(rs);
		preparedStatement.close();
		return mutations;
	}

	public static ArrayList<Mutation> getMutationDataByQuery(String assay, String orderNumber, String lastName, String firstName, String gene, String cosmicID, String cDNA, String codon) throws Exception{
		String query = "select t2.reported, t2.gene, t2.exons, t2.HGVSc, t2.HGVSp, t2.dbSNPID, t3.cosmicID, " +
				"t2.type, t2.genotype, t2.altFreq, t2.readDP, t2.altReadDP, t6.occurrence, " +
				"t2.chr, t2.pos, t2.ref, t2.alt, t2.Consequence, t2.Sift, t2.PolyPhen, " +
				"t4.altCount, t4.totalCount, t4.altGlobalFreq, t4.americanFreq, t4.asianFreq, t4.afrFreq, t4.eurFreq, " +
				"t5.origin, t5.clinicalAllele, t5.clinicalSig, t5.clinicalAcc,t2.pubmed, " +
				"t1.lastName, t1.firstName, t1.orderNumber, t1.assay, t1.tumorSource, t1.tumorPercent, t2.sampleID " +
				"from " + 
				"data as t2 left join " +
				"cosmic_grch37v82 as t3 " +
				"on t2.chr = t3.chr and t2.pos = t3.pos and t2.ref = t3.ref and t2.alt = t3.alt " +
				"left join g1000 as t4 " +
				"on t2.chr = t4.chr and t2.pos = t4.pos and t2.ref = t4.ref and t2.alt = t4.alt " +
				"left join clinvar as t5 " +
				"on t2.chr = t5.chr and t2.pos = t5.pos and t2.ref = t5.ref and t2.alt = t5.alt " +
				"left join Samples as t1 " +
				"on t2.sampleID = t1.ID " +
				"left join " +
				"(select chr, pos, ref, alt, assay, count(*) as occurrence from " + 
				"(select chr, pos, ref, alt, assay, sampleID from data " +
				"where genotype != 'No Call' " +
				"group by chr, pos, ref, alt, assay, sampleID) as t7 " +
				"group by chr,pos,ref,alt, assay " +
				") as t6 " +
				"on t2.chr = t6.chr and t2.pos = t6.pos and t2.ref = t6.ref and t2.alt = t6.alt and t2.assay = t6.assay ";
		String whereClause = "";

		if(!assay.equals("All")){
			whereClause += String.format(" and t2.assay = '%s'", assay);
		}
		if(!orderNumber.equals("")){
			whereClause += String.format(" and t1.orderNumber = '%s'", orderNumber);
		}
		if(!lastName.equals("")){
			whereClause += String.format(" and t1.lastName = '%s'", lastName);
		}
		if(!firstName.equals("")){
			whereClause += String.format(" and t1.firstName = '%s'", firstName);
		}
		if(!gene.equals("")){
			whereClause += String.format(" and t2.gene = '%s'", gene);
		}
		if(!cosmicID.equals("")){
			whereClause += String.format(" and t3.cosmicID like '%%%s%%'", cosmicID);
		}
		if(!cDNA.equals("")){
			whereClause += String.format(" and t2.HGVSc like '%%%s%%'", cDNA);
		}
		if(!codon.equals("")){
			whereClause += String.format(" and t2.HGVSp like '%%%s%%'", codon);
		}
		if(whereClause.equals("")){
			throw new Exception("You need to specify at least one search term");
		}
		else{
			String whereClauseFinal = "where" + whereClause.replaceFirst("and", "");
			query += whereClauseFinal;	
		}

		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		ResultSet resultSet = preparedStatement.executeQuery();
		ArrayList<Mutation> mutations = makeModel(resultSet);
		preparedStatement.close();
		return mutations;
	}

	private static ArrayList<Mutation> makeModel(ResultSet rs) throws Exception{
		ArrayList<Mutation> mutations = new ArrayList<Mutation>();

		while(rs.next()){
			Mutation mutation = new Mutation();

			//common
			boolean reported = Integer.parseInt(rs.getString("reported")) != 0;
			mutation.setReported(reported);
			mutation.setGene(getStringOrBlank(rs, "gene"));
			mutation.setExons(getStringOrBlank(rs, "exons"));
			mutation.setHGVSc(getStringOrBlank(rs, "HGVSc"));
			mutation.setHGVSp(getStringOrBlank(rs, "HGVSp"));			

			//basic
			mutation.setDbSNPID(getStringOrBlank(rs, "dbSNPID"));
			mutation.setType(getStringOrBlank(rs, "type"));
			mutation.setGenotype(getStringOrBlank(rs, "genotype"));
			mutation.setAltFreq(getDoubleOrNull(rs, "altFreq"));
			mutation.setReadDP(getIntegerOrNull(rs, "readDP"));
			mutation.setAltReadDP(getIntegerOrNull(rs, "altReadDP"));
			mutation.setCosmicID(getStringOrBlank(rs, "cosmicID"));
			mutation.setOccurrence(getIntegerOrNull(rs, "occurrence"));

			//ClinVar
			mutation.setOrigin(getStringOrBlank(rs, "origin"));
			mutation.setClinicalAllele(getStringOrBlank(rs, "clinicalAllele"));
			mutation.setClinicalSig(getStringOrBlank(rs, "clinicalSig"));
			mutation.setClinicalAcc(getStringOrBlank(rs, "clinicalAcc"));
			mutation.setPubmed(getStringOrBlank(rs, "pubmed"));

			//Coordinates
			mutation.setChr(getStringOrBlank(rs, "chr"));
			mutation.setPos(getStringOrBlank(rs, "pos"));
			mutation.setRef(getStringOrBlank(rs, "ref"));
			mutation.setAlt(getStringOrBlank(rs, "alt"));
			mutation.setConsequence(getStringOrBlank(rs, "Consequence"));
			mutation.setSift(getStringOrBlank(rs, "Sift"));
			mutation.setPolyPhen(getStringOrBlank(rs, "PolyPhen"));

			//G1000
			mutation.setAltCount(getIntegerOrNull(rs, "altCount"));
			mutation.setTotalCount(getIntegerOrNull(rs, "totalCount"));
			mutation.setAltGlobalFreq(getDoubleOrNull(rs, "altGlobalFreq"));
			mutation.setAmericanFreq(getDoubleOrNull(rs, "americanFreq"));
			mutation.setAsianFreq(getDoubleOrNull(rs, "asianFreq"));
			mutation.setAfricanFreq(getDoubleOrNull(rs, "afrFreq"));
			mutation.setEurFreq(getDoubleOrNull(rs, "eurFreq"));

			//Sample
			mutation.setLastName(getStringOrBlank(rs, "lastName"));
			mutation.setFirstName(getStringOrBlank(rs, "firstName"));
			mutation.setOrderNumber(getStringOrBlank(rs, "orderNumber"));
			mutation.setAssay(getStringOrBlank(rs, "assay"));
			mutation.setSampleID(getIntegerOrNull(rs, "sampleID"));
			mutation.setTumorSource(getStringOrBlank(rs, "tumorSource"));
			mutation.setTumorPercent(getStringOrBlank(rs, "tumorPercent"));

			Annotation annotationObject = getAnnotation(mutation.getCoordinate());
			mutation.setAnnotationObject(annotationObject);

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

	/**
	 * Updates the reported flag in the mutation data table
	 * 
	 * @param setToReported
	 * @param sampleID This is primary key for mutation data table
	 * @param chr
	 * @param pos
	 * @param ref
	 * @param alt
	 * @throws SQLException
	 */
	public static void updateReportedStatus(boolean setToReported, Integer sampleID, Coordinate coordinate) throws SQLException{
		String reported = (setToReported) ? "1" : "0";
		PreparedStatement updateStatement = databaseConnection.prepareStatement(
				"update data set reported = ? where sampleID = ? and chr = ? and pos = ? and ref = ? and alt = ?");
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
		String query = "select * from Samples order by ID desc";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<Sample> samples = new ArrayList<Sample>();
		while(rs.next()){
			Sample s = getSample(rs);
			samples.add(s);
		}
		preparedStatement.close();
		return samples;
	}

	private static Sample getSample(ResultSet row) throws SQLException{
		Sample sample = new Sample(
				Integer.parseInt(row.getString("ID")),
				row.getString("assay"),
				row.getString("instrument"),
				row.getString("lastName"),
				row.getString("firstName"),
				row.getString("orderNumber"),
				row.getString("pathNumber"),
				row.getString("tumorSource"),
				row.getString("tumorPercent"),
				row.getString("runID"),
				row.getString("sampleID"),
				row.getString("coverageID"),
				row.getString("callerID"),
				row.getString("runDate"),
				row.getString("note"),
				row.getString("enteredBy")
				);
		return sample;
	}

	public static Sample getSample(int ID) throws Exception{
		PreparedStatement updateStatement = databaseConnection.prepareStatement("select assay, instrument, lastName, firstName, orderNumber,"
				+ "pathNumber, tumorSource, tumorPercent, runID, sampleID, coverageID, callerID, runDate, note, enteredBy from Samples where ID = ?");
		updateStatement.setString(1, ID+"");
		ResultSet getSampleResult = updateStatement.executeQuery();
		if(getSampleResult.next()){
			return new Sample(
					ID,
					getSampleResult.getString(1),
					getSampleResult.getString(2),
					getSampleResult.getString(3),
					getSampleResult.getString(4),
					getSampleResult.getString(5),
					getSampleResult.getString(6),
					getSampleResult.getString(7),
					getSampleResult.getString(8),
					getSampleResult.getString(9),
					getSampleResult.getString(10),
					getSampleResult.getString(11),
					getSampleResult.getString(12),
					getSampleResult.getString(13),
					getSampleResult.getString(14),
					getSampleResult.getString(15)
					);
		}
		return null;
	}

	public static void updateSampleNote(int ID, String newNote) throws Exception{
		PreparedStatement updateStatement = databaseConnection.prepareStatement("update Samples set note = ? where ID = ?");
		updateStatement.setString(1, newNote);
		updateStatement.setString(2, ""+ID);
		updateStatement.executeUpdate();
		updateStatement.close();
	}

	public static void updateSample(Sample sample) throws Exception{
		PreparedStatement updateStatement = databaseConnection.prepareStatement("update Samples set lastName = ?, firstName = ?, orderNumber = ?, pathNumber = ?, "
				+ "tumorSource = ?, tumorPercent = ?, note = ? where ID = ?");
		updateStatement.setString(1, sample.getLastName());
		updateStatement.setString(2, sample.getFirstName());
		updateStatement.setString(3, sample.getOrderNumber());
		updateStatement.setString(4, sample.getPathNumber());
		updateStatement.setString(5, sample.getTumorSource());
		updateStatement.setString(6, sample.getTumorPercent());
		updateStatement.setString(7, sample.getNote());
		updateStatement.setString(8, sample.ID+"");
		updateStatement.executeUpdate();
		updateStatement.close();
	}

	public static void deleteSample(int ID) throws SQLException{
		CallableStatement callableStatement = databaseConnection.prepareCall("{call deleteSample(?)}");
		callableStatement.setString(1, ""+ID);
		callableStatement.executeUpdate();
		callableStatement.close();
	}

	/* ************************************************************************
	 * Amplicon Queries
	 *************************************************************************/
	public static AmpliconCount getAmpliconCount(int sampleID) throws Exception{
		PreparedStatement updateStatement = databaseConnection.prepareStatement("select totalAmplicon, failedAmplicon from ampliconCount where sampleID = ?");
		updateStatement.setString(1, ""+sampleID);
		ResultSet getSampleResult = updateStatement.executeQuery();
		if(getSampleResult.next()){//TODO Handle more than one entry in the database?
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
		PreparedStatement updateStatement = databaseConnection.prepareStatement("select ampliconName, ampliconCov from amplicon where sampleID = ?");
		updateStatement.setString(1, ""+sampleID);
		ResultSet getSampleResult = updateStatement.executeQuery();
		while(getSampleResult.next()){
			Amplicon amplicon = new Amplicon(sampleID, getSampleResult.getString(1), getSampleResult.getString(2));
			amplicons.add(amplicon);
		}
		updateStatement.close();
		return amplicons;
	}

	/* ************************************************************************
	 * Annotation Queries
	 *************************************************************************/
	public static GeneAnnotation getGeneAnnotation(String gene) throws Exception{
		PreparedStatement selectStatement = databaseConnection.prepareStatement("select curation, locked from GeneAnnotation where gene = ?");
		selectStatement.setString(1, gene);
		ResultSet rs = selectStatement.executeQuery();
		if(rs.next()){
			String curation = rs.getString(1);
			boolean locked = rs.getBoolean(2);
			return new GeneAnnotation(gene, curation, locked);
		}else{
			GeneAnnotation geneAnnotation = new GeneAnnotation(gene, "", false);
			createGeneAnnotation(geneAnnotation);
			return geneAnnotation;
		}
	}

	public static Annotation getAnnotation(Coordinate coordinate) throws Exception{
		PreparedStatement selectStatement = databaseConnection.prepareStatement("select classification, curation, somatic, updateStat, status from annotation where chr = ? and pos = ? and ref = ? and alt = ?");
		selectStatement.setString(1, coordinate.getChr());
		selectStatement.setString(2, coordinate.getPos());
		selectStatement.setString(3, coordinate.getRef());
		selectStatement.setString(4, coordinate.getAlt());
		ResultSet rs = selectStatement.executeQuery();
		Integer n = 0;
		String classification = null;
		String curation = null;
		String somatic = null;
		String updateStatus = null;
		String editStatus = null;
		while(rs.next()){
			n += 1;
			classification = rs.getString(1);
			curation = rs.getString(2);
			somatic = rs.getString(3);
			updateStatus = rs.getString(4);
			editStatus = rs.getString(5);

			if(classification == null) classification = "";
			if(curation == null) curation = "";
			if(somatic == null) somatic = "";
			if(updateStatus == null) updateStatus = "";
			if(editStatus == null) editStatus = "";
		}

		selectStatement.close();
		if(n != 0){
			return new Annotation(coordinate, classification, curation, somatic, updateStatus, Annotation.STATUS.valueOf(editStatus));
		}else{
			Annotation annotation = new Annotation(coordinate, Annotation.STATUS.close);
			createAnnotation(annotation);
			return annotation;
		}
	}

	public static void createGeneAnnotation(GeneAnnotation geneAnnotation) throws Exception{
		PreparedStatement updateStatement = databaseConnection.prepareStatement("insert into GeneAnnotation(gene, curation, locked) values(?, ?, ?)");
		updateStatement.setString(1, geneAnnotation.getGene());
		updateStatement.setString(2, geneAnnotation.getCuration());
		updateStatement.setBoolean(3, geneAnnotation.isLocked());
		updateStatement.executeUpdate();
		updateStatement.close();
	}

	public static void createAnnotation(Annotation annotation) throws Exception{
		Coordinate coordinate = annotation.getCoordinate();
		PreparedStatement updateStatement = databaseConnection.prepareStatement("insert into annotation(chr, pos, ref, alt, status) values(?, ?, ?, ?, ?)");
		updateStatement.setString(1, coordinate.getChr());
		updateStatement.setString(2, coordinate.getPos());
		updateStatement.setString(3, coordinate.getRef());
		updateStatement.setString(4, coordinate.getAlt());
		updateStatement.setString(5, "open");
		updateStatement.executeUpdate();
		updateStatement.close();
	}

	public static void updateAnnotation(Annotation annotation) throws Exception{
		Coordinate coordinate = annotation.getCoordinate();
		PreparedStatement updateStatement = databaseConnection.prepareStatement("update annotation set"
				+ " classification= ? , somatic = ? , curation = ? , updateStat = ?"
				+ " where chr = ? and pos = ? and ref = ? and alt = ?");
		updateStatement.setString(1, annotation.getClassification());
		updateStatement.setString(2, annotation.getSomatic());
		updateStatement.setString(3, annotation.getCuration());
		updateStatement.setString(4, annotation.getUpdateStatus());
		updateStatement.setString(5, coordinate.getChr());
		updateStatement.setString(6, coordinate.getPos());
		updateStatement.setString(7, coordinate.getRef());
		updateStatement.setString(8, coordinate.getAlt());
		updateStatement.executeUpdate();
		updateStatement.close();
	}

	public static void setGeneAnnotationCuration(GeneAnnotation geneAnnotation) throws Exception{
		PreparedStatement updateStatement = databaseConnection.prepareStatement("update GeneAnnotation set"
				+ " curation = ? "
				+ " where gene = ?");
		updateStatement.setString(1, geneAnnotation.getCuration());
		updateStatement.setString(2, geneAnnotation.getGene());
		updateStatement.executeUpdate();
		updateStatement.close();
	}

	public static void setGeneAnnotationLock(GeneAnnotation geneAnnotation) throws Exception{
		PreparedStatement updateStatement = databaseConnection.prepareStatement("update GeneAnnotation set"
				+ " locked = ?"
				+ " where gene = ?");
		updateStatement.setBoolean(1, geneAnnotation.isLocked());
		updateStatement.setString(2, geneAnnotation.getGene());
		updateStatement.executeUpdate();
		updateStatement.close();
	}

	public static void setAnnotationStatus(Annotation.STATUS status, Annotation annotation) throws Exception{
		Coordinate coordinate = annotation.getCoordinate();
		PreparedStatement updateStatement = databaseConnection.prepareStatement("update annotation set status= ? where chr = ? and pos = ? and ref = ? and alt = ?");
		updateStatement.setString(1, status.toString());
		updateStatement.setString(2, coordinate.getChr());
		updateStatement.setString(3, coordinate.getPos());
		updateStatement.setString(4, coordinate.getRef());
		updateStatement.setString(5, coordinate.getAlt());
		updateStatement.executeUpdate();
		updateStatement.close();
	}

	public static void deleteAnnotation(Annotation annotation) throws Exception{
		PreparedStatement updateStatement = databaseConnection.prepareStatement("delete from annotation where chr = ? and pos = ? and ref = ? and alt = ?");
		Coordinate coordinate = annotation.getCoordinate();
		updateStatement.setString(1, coordinate.getChr());
		updateStatement.setString(2, coordinate.getPos());
		updateStatement.setString(3, coordinate.getRef());
		updateStatement.setString(4, coordinate.getAlt());
		updateStatement.executeUpdate();
		updateStatement.close();
	}

	private static String getMD5(String source) throws NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(source.getBytes());
		byte[] digest = md.digest();
		return DatatypeConverter.printHexBinary(digest).toLowerCase();
	}

	/* ************************************************************************
	 * Monitor Pipelines Queries
	 *************************************************************************/
	private static Pipeline getPipeline(ResultSet row) throws SQLException{
		Pipeline pipeline = new Pipeline(
				row.getInt("queueID"),
				row.getString("runID"),
				row.getString("sampleID"),
				row.getString("assayID"),
				row.getString("instrumentID"),
				row.getString("environmentID"),				
				row.getString("plstatus"),
				row.getString("timeUpdated")
				);
		return pipeline;
	}

	public static ArrayList<Pipeline> getAllPipelines() throws Exception{
		String query = "select t3.queueID, t3.runID, t3.sampleID, t3.assayID, t3.instrumentID, t3.environmentID, t2.plStatus, t2.timeUpdated " +
				"from sampleAnalysisQueue as t3 " +
				"left join ( " +
				"select pipelineStatusID, queueID, plStatus, timeUpdated " +
				"from pipelineStatus  " +
				"where pipelineStatusID in " +
				"(select  max(pipelineStatusID) " +
				"from pipelineStatus " + 
				"group by queueID) ) as t2 " +
				"on t3.queueID = t2.queueID " +
				"order by t3.queueID" ;
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
		PreparedStatement preparedStatement = databaseConnection.prepareStatement("select pipelineStatusID, plStatus, timeUpdated from pipelineStatus where queueID = ? ");
		preparedStatement.setInt(1, queueID);
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<PipelineStatus> rows = new ArrayList<PipelineStatus>();
		
		while(rs.next()){
			int pipelineStatusID = rs.getInt("pipelineStatusID");
			String plStatusString = rs.getString("plStatus");
			Date timeUpdated = rs.getTimestamp("timeUpdated");
			
			PipelineStatus pipelineStatus = new PipelineStatus(pipelineStatusID, queueID, plStatusString, timeUpdated);
			rows.add(pipelineStatus);
		}
		preparedStatement.close();

		return rows;
	}
	
	//TODO make this query more appropriate and generic
	public static ArrayList<Amplicon> getAmpliconQCData() throws Exception{
		String query = "select amplicon.sampleID, ampliconName, ampliconCov, ID, Samples.instrument, lastName"
				+ " from amplicon join Samples on amplicon.sampleID = Samples.ID"
				+ " where lastName like 'Horizon%' and ampliconCov != 0 and amplicon.assay != 'neuro' and ampliconName like 'BCOR%'";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<Amplicon> amplicons = new ArrayList<Amplicon>();

		while(rs.next()){
			Amplicon amplicon = new Amplicon(rs.getInt(1), rs.getString(2), rs.getString(3));
			amplicons.add(amplicon);
		}
		preparedStatement.close();

		return amplicons;
	}
}
