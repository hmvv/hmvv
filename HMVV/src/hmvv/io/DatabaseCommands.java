package hmvv.io;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import hmvv.main.Configurations;
import hmvv.model.Amplicon;
import hmvv.model.AmpliconCount;
import hmvv.model.Annotation;
import hmvv.model.Coordinate;
import hmvv.model.Mutation;
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
	//TODO Consider using a transaction statement
	public static void insertDataIntoDatabase(String dataFile, String ampliconFile, Integer totalAmpliconCount, Integer failedAmpliconCount, Sample sample) throws Exception{
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

		String checkSample = String.format("select * from ngs.Samples where instrument = '%s' and runID = '%s' and sampleID = '%s'", instrument, runID, sampleID);

		//enter sample
		PreparedStatement pstCheckSample = databaseConnection.prepareStatement(checkSample);
		ResultSet rsCheckSample = pstCheckSample.executeQuery();
		Integer sampleCount = 0;
		while(rsCheckSample.next()){
			sampleCount += 1;
		}
		if(sampleCount != 0){
			throw new Exception("Error1: Supplied sample exists in database; data not entered");
		}
		
		String enterSample = String.format("insert into ngs.Samples "
				+ "(assay, instrument, lastName, firstName, orderNumber, pathNumber, tumorPercent, runID, sampleID, coverageID, callerID, runDate, note, enteredBy, tumorSource) "
				+ "values ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
				assay, instrument, lastName, firstName, orderNumber, pathologyNumber, tumorPercent, runID, sampleID, coverageID, variantCallerID, runDate, note, enteredBy, tumorSource);
		PreparedStatement pstEnterSample = databaseConnection.prepareStatement(enterSample);
		pstEnterSample.executeUpdate();

		//get ID
		String findID = String.format("select ID from ngs.Samples where instrument = '%s' and runID = '%s' and sampleID = '%s'", instrument, runID, sampleID);

		PreparedStatement pstFindID = databaseConnection.prepareStatement(findID);
		ResultSet rsFindID = pstFindID.executeQuery();
		Integer count = 0;
		String ID = "";
		while(rsFindID.next()){
			ID = rsFindID.getString(1);
			count += 1;
		}
		if(count == 0){
			throw new Exception("Error2: Problem locating the entered sample; data not entered");
		}

		//enter amplicon counts
		String enterAmpliconCount = String.format("insert into ngs.ampliconCount values (%s, %d, %d)", ID, totalAmpliconCount, failedAmpliconCount);
		PreparedStatement pstEnterAmpliconCount = databaseConnection.prepareStatement(enterAmpliconCount);
		pstEnterAmpliconCount.executeUpdate();
		
		//enter variant data
		String enterData = String.format("load data infile '%s' into table ngs.data ignore 1 lines "
				+ "(gene, exons, chr, pos, ref, alt, genotype, type, quality, altFreq, readDP, altReadDP, Consequence, sift, PolyPhen, HGVSc, HGVSp, dbSNPID, pubmed)"
				+ " set sampleID = '%s', assay = '%s'", dataFile, ID, assay);
		PreparedStatement pstEnterData = databaseConnection.prepareStatement(enterData);
		pstEnterData.executeUpdate();
		
		if((sample.instrument.equals("pgm")) || (sample.instrument.equals("proton"))){
			//enter failed amplicon data
			String enterAmplicon = String.format("load data infile '%s' into table ngs.amplicon "
					+ "(ampliconName, ampliconCov) set sampleID = '%s', assay = '%s'", ampliconFile, ID, assay);
			PreparedStatement pstEnterAmplicon = databaseConnection.prepareStatement(enterAmplicon);
			pstEnterAmplicon.executeUpdate();
		}else{
			//enter failed amplicon data
			String enterAmplicon = String.format("load data infile '%s' into table ngs.amplicon ignore 1 lines "//Skip first line, which contains the sample headers
					+ "(ampliconName, ampliconCov) set sampleID = '%s', assay = '%s'", ampliconFile, ID, assay);
			PreparedStatement pstEnterAmplicon = databaseConnection.prepareStatement(enterAmplicon);
			pstEnterAmplicon.executeUpdate();
		}
	}
	
	/* ************************************************************************
	 * Assay Queries
	 *************************************************************************/
	public static ArrayList<String> getAllAssays() throws Exception{
		ArrayList<String> assays = new ArrayList<String>();
		String getAssay = "select distinct assay from ngs.assays";
		PreparedStatement pst = databaseConnection.prepareStatement(getAssay);
		ResultSet rs = pst.executeQuery();
		while(rs.next()){
			assays.add(rs.getString(1));
		}
		pst.close();
		return assays;
	}
	
	public static ArrayList<String> getInstrumentsForAssay(String assay) throws Exception{
		ArrayList<String> instruments = new ArrayList<String>();
		PreparedStatement preparedStatement = databaseConnection.prepareStatement("select instrument from ngs.assays where assay = ?");
		preparedStatement.setString(1, assay);
		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){
			instruments.add(rs.getString(1));
		}
		preparedStatement.close();
		return instruments;
	}
	
	public static void createAssay(String instrument, String assay) throws Exception{
		PreparedStatement preparedStatement = databaseConnection.prepareStatement("insert into ngs.assays values (?, ?)");
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
				+ " t5.origin, t5.clinicalAllele, t5.clinicalSig, t5.clinicalAcc,t2.pubmed,"
				+ " t1.lastName, t1.firstName, t1.orderNumber, t1.assay, t2.sampleID, t7.updateStat"
				+ " from ngs.data as t2"
				+ " left join ngs.g1000 as t4"
				+ " on t2.chr = t4.chr and t2.pos = t4.pos and t2.ref = t4.ref and t2.alt = t4.alt"
				+ " left join ngs.clinvar as t5"
				+ " on t2.chr = t5.chr and t2.pos = t5.pos and t2.ref = t5.ref and t2.alt = t5.alt"
				+ " left join ngs.Samples as t1"
				+ " on t2.sampleID = t1.ID"
				+ " left join"
				+ " ngs.annotation as t7"
				+ " on t2.chr = t7.chr and t2.pos = t7.pos and t2.ref = t7.ref and t2.alt = t7.alt"
				+ " where t2.sampleID = ?";
		
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, ""+ID);
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<Mutation> mutations = makeModel(rs);
		preparedStatement.close();
		return mutations;
	}
	
	/**
	 * Acquires the cosmicID from the database. If it isn't found, null is returned
	 */
	public static String getCosmicID(Mutation mutation) throws Exception{
		Coordinate coordinate = mutation.getCoordinate();
		String query = "select cosmicID from cosmic where chr = ? and pos = ? and ref = ? and alt = ?";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, coordinate.getChr());
		preparedStatement.setString(2, coordinate.getPos());
		preparedStatement.setString(3, coordinate.getRef());
		preparedStatement.setString(4, coordinate.getAlt());
		ResultSet rs = preparedStatement.executeQuery();
		//TODO do we need to handle multiple rows? Can a chr/pos/ref/alt match multiple cosmicIDs, especially different versions of the cosmic database?
		if(rs.next()){
			String result = rs.getString(1);
			preparedStatement.close();
			return result;
		}else{
			rs.close();
			return null;
		}
	}
	
	/**
	 * Acquires the number of occurrences of this mutation from the database.
	 */
	public static int getOccurrenceCount(Mutation mutation) throws Exception{
		Coordinate coordinate = mutation.getCoordinate();
		String assay = mutation.getValue("assay").toString();
		String query = "select count(*) as occurrence from ngs.data"
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
		String chr = mutation.getValue("chr").toString();
		String pos = mutation.getValue("pos").toString();
		String ref = mutation.getValue("ref").toString();
		String alt = mutation.getValue("alt").toString();
		Integer posInt = Integer.parseInt(pos);
		String assay = mutation.getValue("assay").toString();
		
		CallableStatement callableStatement = databaseConnection.prepareCall("{call getDataByCoordinate(?,?,?,?,?)}");
		callableStatement.setString(1, chr);
		callableStatement.setInt(2, posInt);
		callableStatement.setString(3, ref);
		callableStatement.setString(4, alt);
		callableStatement.setString(5, assay);
		callableStatement.executeQuery();
		ResultSet rs = callableStatement.executeQuery();
		ArrayList<Mutation> mutations = makeModel(rs);
		callableStatement.close();
		return mutations;
	}
	
	public static ArrayList<Mutation> getMutationDataByQuery(String assay, String orderNumber, String lastName, String firstName, String gene, String cosmicID, String cDNA, String codon) throws Exception{
		String query = "select t2.reported, t2.gene, t2.exons, t2.HGVSc, t2.HGVSp, t2.dbSNPID, t3.cosmicID, " +
				"t2.type, t2.genotype, t2.altFreq, t2.readDP, t2.altReadDP, t6.occurance, " +
				"t2.chr, t2.pos, t2.ref, t2.alt, t2.Consequence, t2.Sift, t2.PolyPhen, " +
				"t4.altCount, t4.totalCount, t4.altGlobalFreq, t4.americanFreq, t4.asianFreq, t4.afrFreq, t4.eurFreq, " +
				"t5.origin, t5.clinicalAllele, t5.clinicalSig, t5.clinicalAcc,t2.pubmed, " +
				"t1.lastName, t1.firstName, t1.orderNumber, t1.assay, t2.sampleID, t7.updateStat as annotation " +
				"from " + 
				"ngs.data as t2 left join " +
				"ngs.cosmic as t3 " +
				"on t2.chr = t3.chr and t2.pos = t3.pos and t2.ref = t3.ref and t2.alt = t3.alt " +
				"left join ngs.g1000 as t4 " +
				"on t2.chr = t4.chr and t2.pos = t4.pos and t2.ref = t4.ref and t2.alt = t4.alt " +
				"left join ngs.clinvar as t5 " +
				"on t2.chr = t5.chr and t2.pos = t5.pos and t2.ref = t5.ref and t2.alt = t5.alt " +
				"left join ngs.Samples as t1 " +
				"on t2.sampleID = t1.ID " +
				"left join " +
				"(select chr, pos, ref, alt, assay, count(*) as occurance from " + 
				"(select chr, pos, ref, alt, assay, sampleID from ngs.data " +
				"where genotype != 'No Call' " +
				"group by chr, pos, ref, alt, assay, sampleID) as t7 " +
				"group by chr,pos,ref,alt, assay " +
				") as t6 " +
				"on t2.chr = t6.chr and t2.pos = t6.pos and t2.ref = t6.ref and t2.alt = t6.alt and t2.assay = t6.assay " + 
				"left join ngs.annotation as t7 " + 
				"on t2.chr = t7.chr and t2.pos = t7.pos and t2.ref = t7.ref and t2.alt = t7.alt ";
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

		ResultSetMetaData rsmt = rs.getMetaData();
		int colCount = rsmt.getColumnCount();
		String[] columnNames = new String[colCount];
		
		for(int i = 0; i < colCount; i++){
			columnNames[i] = rsmt.getColumnName(i+1);
			//TODO don't hack this these column names
			if (columnNames[i].equals("updateStat")){
				columnNames[i] = "annotation";
			}
			if (columnNames[i].equals("occurance")){
				columnNames[i] = "occurrence";
			}
		}
		
		while(rs.next()){
			Mutation mutation = new Mutation();
			for(int i = 0; i < colCount; i++){
				String value = rs.getString(i+1);
				if(columnNames[i].equals("reported")){
					//Sets "reported" to true if the value in the database is not 0
					mutation.addData(columnNames[i], Integer.parseInt(value) != 0);
				}else if (columnNames[i].equals("annotation")){
					//TODO don't hack this one column's value
					Object parseValue = parseValue(value);
					if(parseValue == null){
						mutation.addData(columnNames[i], "Enter");
					}else{
						mutation.addData(columnNames[i], "Annotation");
					}
				}else{
					mutation.addData(columnNames[i], parseValue(value));
				}
			}
			mutations.add(mutation);
		}
		return mutations;
	}
	
	private static Object parseValue(Object value){
		if(value == null){
			return null;
		}
		
		String stringVal = value.toString();
		
		if(stringVal.equals("true")){
			return true;
		}
		if(stringVal.equals("false")){
			return false;
		}
		if(stringVal.toLowerCase().equals("null")){
			return null;
		}
		
		try{
			return Integer.parseInt(stringVal);
		}catch(Exception e){}
		
		try{
			return Double.parseDouble(stringVal);
		}catch(Exception e){}
		
		return stringVal;
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
	public static void updateReportedStatus(boolean setToReported, String sampleID, Coordinate coordinate) throws SQLException{
		String reported = (setToReported) ? "1" : "0";
		PreparedStatement updateStatement = databaseConnection.prepareStatement(
				"update ngs.data set reported = ? where sampleID = ? and chr = ? and pos = ? and ref = ? and alt = ?");
		updateStatement.setString(1, reported);
		updateStatement.setString(2, sampleID);
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
	public static ArrayList<Sample> getSamplesByAssay(String assay) throws Exception{
		CallableStatement callableStatement = databaseConnection.prepareCall("{call getSample(?)}");
		callableStatement.setString(1, assay);
		ResultSet sampleResultSet = callableStatement.executeQuery();
		ArrayList<Sample> samples = new ArrayList<Sample>();
		while(sampleResultSet.next()){
			Sample s = getSample(sampleResultSet);
			samples.add(s);
		}
		callableStatement.close();
		return samples;
	}
	
	private static Sample getSample(ResultSet row) throws SQLException{
		return new Sample(
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
		PreparedStatement updateStatement = databaseConnection.prepareStatement("update ngs.Samples set note = ? where ID = ?");
		updateStatement.setString(1, newNote);
		updateStatement.setString(2, ""+ID);
		updateStatement.executeUpdate();
		updateStatement.close();
	}
	
	public static void updateSample(Sample sample) throws Exception{
		PreparedStatement updateStatement = databaseConnection.prepareStatement("update ngs.Samples set lastName = ?, firstName = ?, orderNumber = ?, pathNumber = ?, "
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
		return null;
	}
	
	public static ArrayList<Amplicon> getFailedAmplicon(int sampleID) throws Exception{
		ArrayList<Amplicon> amplicons = new ArrayList<Amplicon>();
		PreparedStatement updateStatement = databaseConnection.prepareStatement("select assay, ampliconName, ampliconCov from amplicon where sampleID = ?");
		updateStatement.setString(1, ""+sampleID);
		ResultSet getSampleResult = updateStatement.executeQuery();
		while(getSampleResult.next()){
			Amplicon amplicon = new Amplicon(sampleID, getSampleResult.getString(1), getSampleResult.getString(2), getSampleResult.getString(3));
			amplicons.add(amplicon);
		}
		updateStatement.close();
		return amplicons;
	}
	
	/* ************************************************************************
	 * Annotation Queries
	 *************************************************************************/
	public static Annotation getAnnotation(Coordinate coordinate) throws Exception{
		PreparedStatement selectStatement = databaseConnection.prepareStatement("select classification, curation, somatic, updateStat, status from ngs.annotation where chr = ? and pos = ? and ref = ? and alt = ?");
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
	
	public static void createAnnotation(Annotation annotation) throws Exception{
		Coordinate coordinate = annotation.getCoordinate();
		PreparedStatement updateStatement = databaseConnection.prepareStatement("insert into ngs.annotation(chr, pos, ref, alt, status) values(?, ?, ?, ?, ?)");
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
		PreparedStatement updateStatement = databaseConnection.prepareStatement("update ngs.annotation set"
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
	
	public static void setAnnotationStatus(Annotation.STATUS status, Annotation annotation) throws Exception{
		Coordinate coordinate = annotation.getCoordinate();
		PreparedStatement updateStatement = databaseConnection.prepareStatement("update ngs.annotation set status= ? where chr = ? and pos = ? and ref = ? and alt = ?");
		updateStatement.setString(1, status.toString());
		updateStatement.setString(2, coordinate.getChr());
		updateStatement.setString(3, coordinate.getPos());
		updateStatement.setString(4, coordinate.getRef());
		updateStatement.setString(5, coordinate.getAlt());
		updateStatement.executeUpdate();
		updateStatement.close();
	}
	
	public static void deleteAnnotation(Annotation annotation) throws Exception{
		PreparedStatement updateStatement = databaseConnection.prepareStatement("delete from ngs.annotation where chr = ? and pos = ? and ref = ? and alt = ?");
		Coordinate coordinate = annotation.getCoordinate();
		updateStatement.setString(1, coordinate.getChr());
		updateStatement.setString(2, coordinate.getPos());
		updateStatement.setString(3, coordinate.getRef());
		updateStatement.setString(4, coordinate.getAlt());
		updateStatement.executeUpdate();
		updateStatement.close();
	}
}
