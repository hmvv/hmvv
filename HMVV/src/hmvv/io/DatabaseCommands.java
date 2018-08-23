package hmvv.io;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

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
import hmvv.model.VariantPredictionClass;

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
		String sampleName = sample.sampleName;
		String coverageID = sample.coverageID;//no coverageID on nextseq
		String variantCallerID = sample.callerID;//no variant caller on nextseq
		String runDate = sample.runDate;
		String note = sample.getNote();
		String enteredBy = sample.enteredBy;

		//check if sample is already present in data
		String checkSample = String.format("select samples.runID, samples.sampleName, assays.assayName, instruments.instrumentName from samples " +
				"join instruments on instruments.instrumentID = samples.instrumentID " +
				"join assays on assays.assayID = samples.assayID " +
				"where instruments.instrumentName = '%s' and assays.assayName = '%s' and samples.runID = '%s' and samples.sampleName = '%s' ", instrument, assay, runID, sampleName);
		PreparedStatement pstCheckSample = databaseConnection.prepareStatement(checkSample);
		ResultSet rsCheckSample = pstCheckSample.executeQuery();
		Integer sampleCount = 0;
		while(rsCheckSample.next()){
			sampleCount += 1;
			break;
		}
		if(sampleCount != 0) {
			throw new Exception("Error: Supplied sample exists in database; data not entered");
		}

		//need coverage/callerID placeholder for command-line
        if (coverageID==""){coverageID="na";}
        if (variantCallerID==""){variantCallerID="na";}

		String enterSample = String.format("insert into samples "
				+ "(assayID, instrumentID, runID, sampleName, coverageID, callerID, lastName, firstName, orderNumber, pathNumber, tumorSource ,tumorPercent,  runDate, note, enteredBy) "
				+ "values ( (select assayID from assays where assayName='"+assay+"'), (select instrumentID from instruments where instrumentName='"+instrument+"'), '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s' )",
				runID, sampleName, coverageID, variantCallerID, lastName, firstName, orderNumber, pathologyNumber, tumorSource, tumorPercent,  runDate, note,enteredBy);
		PreparedStatement pstEnterSample = databaseConnection.prepareStatement(enterSample);
		pstEnterSample.executeUpdate();

		//get ID
		String findID = String.format("select samples.sampleID from samples " +
				"join instruments on instruments.instrumentID = samples.instrumentID " +
				"join assays on assays.assayID = samples.assayID " +
				"where instruments.instrumentName = '%s' and assays.assayName = '%s' and samples.runID = '%s' and samples.sampleName = '%s'", instrument, assay, runID, sampleName);

		PreparedStatement pstFindID = databaseConnection.prepareStatement(findID);
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

		queueSampleForRunningPipeline(sample);
	}

	private static void queueSampleForRunningPipeline(Sample sample) throws Exception{
		String queueSample = String.format("INSERT INTO pipelineQueue "
				+ "(sampleID,timeSubmitted) VALUES ('%s', now() )",sample.getSampleID());

		PreparedStatement pstQueueSample = databaseConnection.prepareStatement(queueSample);
		pstQueueSample.executeUpdate();
	}

	/* ************************************************************************
	 * Assay Queries
	 *************************************************************************/
	public static ArrayList<String> getAllAssays() throws Exception{
		ArrayList<String> assays = new ArrayList<String>();
		String getAssay = "select distinct assayName from assays";
		PreparedStatement pst = databaseConnection.prepareStatement(getAssay);
		ResultSet rs = pst.executeQuery();
		while(rs.next()){
			assays.add(rs.getString(1));
		}
		pst.close();
		return assays;
	}
 // TODO create this table in DB
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
			return getUnfilteredMutationDataByID(normalID);
		}
	}

	public static ArrayList<String> getInstrumentsForAssay(String assay) throws Exception{
		ArrayList<String> instruments = new ArrayList<String>();
		PreparedStatement preparedStatement = databaseConnection.prepareStatement("select instruments.instrumentName from instruments " +
                "join assayInstrument on assayInstrument.instrumentID = instruments.instrumentID " +
                "join assays on assays.assayID = assayInstrument.assayID " +
                "where assays.assayName = ?");
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
	public static ArrayList<Mutation> getUnfilteredMutationDataByID(int ID) throws Exception{
		return getMutationDataByID(ID, false);
	}
	
	public static ArrayList<Mutation> getFilteredMutationDataByID(int ID) throws Exception{
		return getMutationDataByID(ID, true);
	}
	
	private static ArrayList<Mutation> getMutationDataByID(int ID, boolean getFilteredData) throws Exception{
		String query = "select t2.reported, t2.gene, t2.exon, t2.HGVSc, t2.HGVSp, t2.dbSNPID,"
				+ " t2.type, t2.impact, t2.altFreq, t2.readDepth, t2.altReadDepth, "
				+ " t2.chr, t2.pos, t2.ref, t2.alt, t2.consequence, t2.Sift, t2.PolyPhen,"
				+ " t4.altCount, t4.totalCount, t4.altGlobalFreq, t4.americanFreq, t4.asianFreq, t4.afrFreq, t4.eurFreq,"
				+ " t5.origin, t5.clinicalAllele, t5.clinicalSig, t5.clinicalAcc, t2.pubmed,"
				+ " t1.lastName, t1.firstName, t1.orderNumber, t6.assayName, t1.tumorSource, t1.tumorPercent, t2.sampleID "
				+ " from sampleVariants as t2"
				+ " left join db_g1000 as t4"
				+ " on t2.chr = t4.chr and t2.pos = t4.pos and t2.ref = t4.ref and t2.alt = t4.alt"
				+ " left join db_clinvar as t5"
				+ " on t2.chr = t5.chr and t2.pos = t5.pos and t2.ref = t5.ref and t2.alt = t5.alt"
				+ " left join samples as t1"
				+ " on t2.sampleID = t1.sampleID "
				+ " left join assays as t6"
				+ " on t1.assayID = t6.assayID"
				+ " where t2.sampleID = ? ";
		String where = "((t2.genotype = 'HIGH' or t2.genotype = 'MODERATE') and t2.altFreq >= 10 and t2.readDP >= 100)";
		if(getFilteredData) {
			where = " !" + where;
		}
		query = query + " and " + where;
		
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
		rs.close();
		return cosmicIDs;
	}
	
	public static String getCosmicInfo(String cosmicID) throws Exception{
		String query = "select info from cosmic_grch37v86 where cosmicID = ?";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, cosmicID);
		ResultSet rs = preparedStatement.executeQuery();
		String info = null;
		if(rs.next()){
			info = rs.getString("info");
		}
		rs.close();
		return info;
	}

	/**
	 * Acquires the number of occurrences of this mutation from the database.
	 */
	public static int getOccurrenceCount(Mutation mutation) throws Exception{
		Coordinate coordinate = mutation.getCoordinate();
		String assay = mutation.getAssay();
		String query = "select count(*) as occurrence from sampleVariants"
				+ " join samples on samples.sampleID = sampleVariants.sampleID "
				+ " join assays on assays.assayID = samples.assayID "
				+ " where sampleVariants.impact != 'No Call' and sampleVariants.chr = ? and sampleVariants.pos = ? and sampleVariants.ref = ? and sampleVariants.alt = ? and assays.assayName = ?";
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
				+ " from sampleVariants as t2"
				+ " left join samples as t1"
				+ " on t2.sampleID = t1.sampleID"
				+ " left join assays as t3"
				+ " on t3.assayID = t1.assayID"
				+ " where t2.impact != ? and t3.assayName = ? and t2.chr = ? and t2.pos = ? and t2.ref = ? and t2.alt = ?";
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
			mutation.setExons(getStringOrBlank(rs, "exon"));
			mutation.setHGVSc(getStringOrBlank(rs, "HGVSc"));
			mutation.setHGVSp(getStringOrBlank(rs, "HGVSp"));			

			//basic
			mutation.setDbSNPID(getStringOrBlank(rs, "dbSNPID"));
			mutation.setType(getStringOrBlank(rs, "type"));
			VariantPredictionClass variantPredictionClass = VariantPredictionClass.createPredictionClass(getStringOrBlank(rs, "impact"));
			mutation.setVariantPredictionClass(variantPredictionClass);
			mutation.setAltFreq(getDoubleOrNull(rs, "altFreq"));
			mutation.setReadDP(getIntegerOrNull(rs, "readDepth"));
			mutation.setAltReadDP(getIntegerOrNull(rs, "altReadDepth"));
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
			mutation.setConsequence(getStringOrBlank(rs, "consequence"));
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
			mutation.setAssay(getStringOrBlank(rs, "assayName"));
			mutation.setSampleID(getIntegerOrNull(rs, "sampleID"));
			mutation.setTumorSource(getStringOrBlank(rs, "tumorSource"));
			mutation.setTumorPercent(getStringOrBlank(rs, "tumorPercent"));

			ArrayList<Annotation> annotationHistory = getVariantAnnotationHistory(mutation.getCoordinate());
			mutation.setAnnotationHistory(annotationHistory);

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
				"s.runDate, s.note, s.enteredBy from samples as s " +
				"join instruments  as i on i.instrumentID = s.instrumentID " +
				"join assays as a on a.assayID = s.assayID ";
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
				row.getString("note"),
				row.getString("enteredBy")
				);
		return sample;
	}

	public static Sample getSample(int sampleID) throws Exception{
		PreparedStatement updateStatement = databaseConnection.prepareStatement("select s.sampleID, a.assayName as assay, i.instrumentName as instrument, s.lastName, s.firstName, s.orderNumber, " +
				"s.pathNumber, s.tumorSource, s.tumorPercent, s.runID, s.sampleName, s.coverageID, s.callerID, " +
				"s.runDate, s.note, s.enteredBy from samples as s " +
				"join instruments  as i on i.instrumentID = s.instrumentID " +
				"join assays as a on a.assayID = s.assayID " +
				"where s.sampleID = ? ");
		updateStatement.setString(1, sampleID+"");
		ResultSet getSampleResult = updateStatement.executeQuery();
		if(getSampleResult.next()){
			return new Sample(
                    sampleID,
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

	public static void updateSampleNote(int sampleID, String newNote) throws Exception{
		PreparedStatement updateStatement = databaseConnection.prepareStatement("update samples set note = ? where sampleID = ?");
		updateStatement.setString(1, newNote);
		updateStatement.setString(2, ""+sampleID);
		updateStatement.executeUpdate();
		updateStatement.close();
	}

	public static void updateSample(Sample sample) throws Exception{
		PreparedStatement updateStatement = databaseConnection.prepareStatement("update samples set lastName = ?, firstName = ?, orderNumber = ?, pathNumber = ?, "
				+ "tumorSource = ?, tumorPercent = ?, note = ? where sampleID = ?");
		updateStatement.setString(1, sample.getLastName());
		updateStatement.setString(2, sample.getFirstName());
		updateStatement.setString(3, sample.getOrderNumber());
		updateStatement.setString(4, sample.getPathNumber());
		updateStatement.setString(5, sample.getTumorSource());
		updateStatement.setString(6, sample.getTumorPercent());
		updateStatement.setString(7, sample.getNote());
		updateStatement.setString(8, sample.sampleID+"");
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
		PreparedStatement updateStatement = databaseConnection.prepareStatement("select ampliconName, readDepth from sampleAmplicons where sampleID = ? and readDepth<100");
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
	public static ArrayList<GeneAnnotation> getGeneAnnotationHistory(String gene) throws Exception{
		ArrayList<GeneAnnotation>  geneannotations = new ArrayList<GeneAnnotation>() ;
		PreparedStatement selectStatement = databaseConnection.prepareStatement("select geneAnnotationID, gene, curation, enteredBy, enterDate from geneAnnotation where gene = ? order by geneAnnotationID asc");
		selectStatement.setString(1, gene);
		ResultSet rs = selectStatement.executeQuery();
		while(rs.next()){
			geneannotations.add(new GeneAnnotation(rs.getInt("geneAnnotationID") , rs.getString("gene") , rs.getString("curation") , rs.getString("enteredBy") , rs.getTimestamp("enterDate")));
		}
		return geneannotations;
	}
	
	public static ArrayList<Annotation> getVariantAnnotationHistory(Coordinate coordinate) throws Exception{
		ArrayList<Annotation> annotations = new ArrayList<Annotation>() ;
		PreparedStatement selectStatement = databaseConnection.prepareStatement("select annotationID, classification, curation, somatic, enteredBy, enterDate from variantAnnotation where chr = ? and pos = ? and ref = ? and alt = ? order by annotationID asc");
		selectStatement.setString(1, coordinate.getChr());
		selectStatement.setString(2, coordinate.getPos());
		selectStatement.setString(3, coordinate.getRef());
		selectStatement.setString(4, coordinate.getAlt());
		ResultSet rs = selectStatement.executeQuery();
		while(rs.next()){
			annotations.add(new Annotation(rs.getInt("annotationID"),coordinate, rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getTimestamp(6)));
		}
		return annotations;
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
		Coordinate coordinate = annotation.coordinate;
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

	/* ************************************************************************
	 * Monitor Pipelines Queries
	 *************************************************************************/
	private static Pipeline getPipeline(ResultSet row) throws SQLException{
		Pipeline pipeline = new Pipeline(
				row.getInt("queueID"),
				row.getInt("sampleID"),
				row.getString("runID"),
				row.getString("sampleName"),
				row.getString("assayName"),
				row.getString("instrumentName"),
				row.getString("plstatus"),
				row.getString("timeUpdated")
				);
		return pipeline;
	}

	public static ArrayList<Pipeline> getAllPipelines() throws Exception{
		String query = "select t3.queueID, t1.sampleID, t1.runID, t1.sampleName, t4.assayName, t5.instrumentName, t2.plStatus, t2.timeUpdated " +
				" from pipelineQueue as t3 " +
				" join samples as t1 on t1.sampleID = t3.sampleID " +
				" join assays as t4 on t4.assayID = t1.assayID " +
		        " join instruments as t5 on t5.instrumentID = t1.instrumentID " +
				" left join ( " +
				" select pipelineStatusID, queueID, plStatus, timeUpdated " +
				" from pipelineStatus  " +
				" where pipelineStatusID in " +
				" (select  max(pipelineStatusID) " +
				" from pipelineStatus " +
				" group by queueID) ) as t2 " +
				" on t3.queueID = t2.queueID " +
				" order by t3.queueID" ;

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
			Timestamp timeUpdated = rs.getTimestamp("timeUpdated");
			
			PipelineStatus pipelineStatus = new PipelineStatus(pipelineStatusID, queueID, plStatusString, timeUpdated);
			rows.add(pipelineStatus);
		}
		preparedStatement.close();

		return rows;
	}
	
	//TODO Design this appropriately
	public static ArrayList<Amplicon> getAmpliconQCData() throws Exception{
		String query = "select sampleAmplicons.sampleID, sampleAmplicons.ampliconName, sampleAmplicons.readDepth, samples.lastName from sampleAmplicons"
				+ " join samples on sampleAmplicons.sampleID = samples.sampleID"
				+ " join assays on assays.assayID = samples.assayID"
				+ " where samples.lastName like 'Horizon%' ";
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
	
	public static float getPipelineTimeEstimate(Pipeline pipeline) throws Exception {
		
		int averageRunTime = 0;
		int fileSize = SSHConnection.getFileSize(pipeline) / 1000 ; // get size in KB
		
		PreparedStatement preparedStatement = databaseConnection.prepareStatement("select AVG(pipelineLogs.runTimeSecs) as averagetime from pipelineLogs " +
				" join pipelineQueue on pipelineQueue.queueID = pipelineLogs.queueID " +
				" join samples on samples.sampleID = pipelineQueue.sampleID " +
                " join assays on assays.assayId = samples.assayID " +
                " join instruments on instruments.instrumentID = instruments.instrumentID " +
				" where instruments.instrumentName=? and assays.assayName=? and pipelineLogs.fileSizeKB between ? and ? ;");
		preparedStatement.setString(1, pipeline.getInstrumentName());
		preparedStatement.setString(2, pipeline.getAssayName());
		preparedStatement.setInt(3, fileSize - 100);
		preparedStatement.setInt(4, fileSize + 100);
		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){
			averageRunTime = rs.getInt("averagetime");
		}
		preparedStatement.close();
		//System.out.println(pipeline.getInstrumentID() + ' ' + pipeline.getAssayID() + ' ' + pipeline.getsampleName() +" filesize " + fileSize + " averageRunTime " + averageRunTime);
		return (float)averageRunTime;
	}
}
