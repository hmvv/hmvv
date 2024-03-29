package hmvv.io;

import java.sql.Timestamp;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

import hmvv.main.Configurations;
import hmvv.model.Assay;
import hmvv.model.ExomeTumorMutationBurden;
import hmvv.model.Instrument;
import hmvv.model.RunFolder;
import hmvv.model.Sample;
import hmvv.model.TMBSample;
import hmvv.model.TMBSampleQC;

public class DatabaseCommands_Samples {
	
	private static Connection databaseConnection = null;
	static void setConnection(Connection databaseConnection) {
		DatabaseCommands_Samples.databaseConnection = databaseConnection;
	}
	
	public static void insertbclconvertIntoDatabase(Instrument instrument, RunFolder runFolder) throws Exception{
		String url = "jdbc:mysql://"+SSHConnection.getForwardingHost()+"/";
		String[] credentials = Configurations.READ_WRITE_CREDENTIALS;
		Connection temp_databaseConnection = DriverManager.getConnection(url+Configurations.BCL2FASTQ_DATABASE_NAME+"?noAccessToProcedureBodies=true", credentials[0], credentials[1]);

		String enterBcl2fastq = "insert into bcl_convert_status (instrument, runFolder, enteredBy) values (?, ?, ?) on duplicate key update runFolder = ? ";
		PreparedStatement pstEnterSample = temp_databaseConnection.prepareStatement(enterBcl2fastq);
		pstEnterSample.setString(1, instrument.instrumentName);
		pstEnterSample.setString(2, runFolder.runFolderName);
		pstEnterSample.setString(3, SSHConnection.getUserName());
		pstEnterSample.setString(4, runFolder.runFolderName);
		
		pstEnterSample.executeUpdate();
		pstEnterSample.close();
		temp_databaseConnection.close();
	}

	static void insertDataIntoDatabase(Sample sample) throws Exception{
		Assay assay = sample.assay;
		Instrument instrument = sample.instrument;
		RunFolder runFolder = sample.runFolder;
		String lastName = sample.getLastName();
		String firstName = sample.getFirstName();
		String mrn = sample.getMRN();
		String orderNumber = sample.getOrderNumber();
		String pathologyNumber = sample.getPathNumber();
		String tumorSource = sample.getTumorSource();
		String tumorPercent = sample.getTumorPercent();
		String runID = sample.runID;
		String sampleName = sample.sampleName;
		String coverageID = sample.coverageID;//no coverageID on nextseq
		String variantCallerID = sample.callerID;//no variant caller on nextseq
		Timestamp runDate = sample.runDate;
		String patientHistory = sample.getPatientHistory();
		String diagnosis = sample.getDiagnosis();
		String note = sample.getNote();
		String enteredBy = sample.enteredBy;
		String analyzedBy = sample.analyzedBy;

		//check if sample is already present in data
		String checkSample = "select samples.instrument, samples.runFolderName, samples.sampleName from samples " +
				"where samples.instrument = ? and samples.runFolderName = ? and samples.sampleName = ? ";
		PreparedStatement pstCheckSample = databaseConnection.prepareStatement(checkSample);
		pstCheckSample.setString(1, instrument.instrumentName);
		pstCheckSample.setString(2, runFolder.runFolderName);
		pstCheckSample.setString(3, sampleName);
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
				+ "(assay, instrument, runID, sampleName, coverageID, callerID, lastName, firstName, mrn,orderNumber, pathNumber, tumorSource ,tumorPercent,  runDate, note, enteredBy, patientHistory, bmDiagnosis, runFolderName, analyzedBy) "
				+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
		PreparedStatement pstEnterSample = databaseConnection.prepareStatement(enterSample);
		pstEnterSample.setString(1, assay.assayName);
		pstEnterSample.setString(2, instrument.instrumentName);
		pstEnterSample.setString(3, runID);
		pstEnterSample.setString(4, sampleName);
		pstEnterSample.setString(5, coverageID);
		pstEnterSample.setString(6, variantCallerID);
		pstEnterSample.setString(7, lastName);
		pstEnterSample.setString(8, firstName);
		pstEnterSample.setString(9, mrn);
		pstEnterSample.setString(10, orderNumber);
		pstEnterSample.setString(11, pathologyNumber);
		pstEnterSample.setString(12, tumorSource);
		pstEnterSample.setString(13, tumorPercent);
		pstEnterSample.setTimestamp(14, runDate);
		pstEnterSample.setString(15, note);
		pstEnterSample.setString(16, enteredBy);
		pstEnterSample.setString(17, patientHistory);
		pstEnterSample.setString(18, diagnosis);
		pstEnterSample.setString(19, runFolder.runFolderName);
		pstEnterSample.setString(20, analyzedBy);

		
		pstEnterSample.executeUpdate();
		pstEnterSample.close();

		//get ID
		String findID = "select samples.sampleID from samples " +
				"where samples.instrument = ? and samples.runFolderName = ? and samples.sampleName = ?";
		PreparedStatement pstFindID = databaseConnection.prepareStatement(findID);
		pstFindID.setString(1, instrument.instrumentName);
		pstFindID.setString(2, runFolder.runFolderName);
		pstFindID.setString(3, sampleName);
		
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
		
		if(sample instanceof TMBSample) {//not ideal to condition using instanceof
			insertTumorNormalPair((TMBSample)sample);
		}

		queueSampleForRunningPipeline(sample);
	}


	private static void queueSampleForRunningPipeline(Sample sample) throws Exception{
		String queueSample = 
				"INSERT INTO pipelineQueue "
				+ "( instrument, runFolderName, sampleName, assay) VALUES (?, ?, ?, ? )";
		PreparedStatement pstQueueSample = databaseConnection.prepareStatement(queueSample);
		pstQueueSample.setString(1, sample.instrument.instrumentName);
		pstQueueSample.setString(2, sample.runFolder.runFolderName);
		pstQueueSample.setString(3, sample.sampleName);
		pstQueueSample.setString(4, sample.assay.assayName);
		
		pstQueueSample.executeUpdate();
		pstQueueSample.close();
	}

	private static void insertTumorNormalPair(TMBSample sampleTMB)throws Exception{
		String enterSampleNormalPair = " insert into sampleNormalPair "
		        + " (sampleID, normalPairInstrument, normalPairRunfolder, normalSampleName, enterDate)  "
		        + " values ( ? , ? , ? , ? , now() )";

		PreparedStatement pstEnterSampleNormalPair = databaseConnection.prepareStatement(enterSampleNormalPair);
		pstEnterSampleNormalPair.setInt(1, sampleTMB.sampleID);
		pstEnterSampleNormalPair.setString(2, sampleTMB.getNormalInstrumentName());
		pstEnterSampleNormalPair.setString(3, sampleTMB.getNormalRunFolder().runFolderName);
		pstEnterSampleNormalPair.setString(4, sampleTMB.getNormalSampleName());
		pstEnterSampleNormalPair.executeUpdate();
		pstEnterSampleNormalPair.close();
	}
	
	static ArrayList<Sample> getAllSamples() throws Exception{
		// TODO: watch out for delay due to subquery as sample number increases.
		// Tested on test env with 2806 samples copied from ngs_live, minor difference

		String query = "select s.sampleID, s.assay, s.instrument, s.mrn, s.runFolderName, s.lastName, s.firstName, s.orderNumber, " +
				" s.pathNumber, s.tumorSource, s.tumorPercent, s.runID, s.sampleName, s.coverageID, s.callerID, " +
				" s.runDate, s.patientHistory, s.bmDiagnosis, s.note, s.enteredBy, s.analyzedBy," +
				" t2.normalPairInstrument, t2.normalPairRunFolder, t2.normalSampleName " +
				" from samples as s " +
				" left join sampleNormalPair as t2 on s.sampleID = t2.sampleID and s.instrument = t2.normalPairInstrument " ;

		if(SSHConnection.isSuperUser(Configurations.USER_FUNCTION.RESTRICT_SAMPLE_ACCESS)){
			query = query + "  where s.runDate >= DATE_SUB(NOW(), INTERVAL ? day) ";
		}

		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);

        if(SSHConnection.isSuperUser(Configurations.USER_FUNCTION.RESTRICT_SAMPLE_ACCESS)){
            preparedStatement.setInt(1, Configurations.RESTRICT_SAMPLE_DAYS);
        }

		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<Sample> samples = new ArrayList<Sample>();
		while(rs.next()){
			Sample s = getSample(rs);
			samples.add(s);
		}
		preparedStatement.close();
		
		//Include historical samples when sample access is restricted
		if(SSHConnection.isSuperUser(Configurations.USER_FUNCTION.RESTRICT_SAMPLE_ACCESS)) {
			ArrayList<Sample> exception_samples = new ArrayList<>();
			HashSet<String> queriedMRNs = new HashSet<String>();
			for (Sample s : samples){
				if(queriedMRNs.contains(s.getMRN())) {
					continue;
				}
				queriedMRNs.add(s.getMRN());
				ArrayList<Sample> new_samples = getExceptionSamples(s.sampleID, s.getMRN());
				if (new_samples.size() > 0) {
                    exception_samples.addAll(new_samples);
                }
			}
			samples.addAll(exception_samples);
		}
		
		//Link samples with the same MRN together
		for(int i = 0; i < samples.size(); i++) {
			Sample currentSample = samples.get(i);
			if(currentSample.getMRN().equals("")) {
				continue;
			}
			
			for(int j = 0; j < samples.size(); j++) {
				if(i == j) {
					continue;
				}
				
				Sample compSample = samples.get(j);
				if(currentSample.getMRN().equals(compSample.getMRN())) {
					currentSample.addLinkedPatientSample(compSample);
				}
			}
		}
		
		
		return samples;
	}

	private static ArrayList<Sample> getExceptionSamples(int sampleID , String sampleMRN ) throws Exception{
		String query = "select s.sampleID, s.assay, s.instrument, s.mrn, s.runFolderName, s.lastName, s.firstName, s.orderNumber, " +
				" s.pathNumber, s.tumorSource, s.tumorPercent, s.runID, s.sampleName, s.coverageID, s.callerID, " +
				" s.runDate, s.patientHistory, s.bmDiagnosis, s.note, s.enteredBy, s.analyzedBy," +
				" t2.normalPairInstrument, t2.normalPairRunFolder, t2.normalSampleName " +
				" from samples as s " +
				" left join sampleNormalPair as t2 on s.sampleID = t2.sampleID and s.instrument = t2.normalPairInstrument " +
				" where s.mrn = ? and s.sampleID != ? and " +
                " s.runDate < DATE_SUB(NOW(), INTERVAL ? day)";

		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, sampleMRN);
		preparedStatement.setInt(2, sampleID);
        preparedStatement.setInt(3, Configurations.RESTRICT_SAMPLE_DAYS);
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
		String assay = row.getString("assay");
		if (assay.equals("tmb")){
			TMBSample sample = new TMBSample(
					Integer.parseInt(row.getString("sampleID")),
					Assay.getAssay(row.getString("assay")),
					Instrument.getInstrument(row.getString("instrument")),
					new RunFolder (row.getString("runFolderName")),
					row.getString("mrn"),
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
					row.getTimestamp("runDate"),
					row.getString("patientHistory"),
					row.getString("bmDiagnosis"),
					row.getString("note"),
					row.getString("enteredBy"),
					row.getString("analyzedBy"),
					row.getString("normalPairInstrument"),
					new RunFolder(row.getString("normalPairRunFolder")),
					row.getString("normalSampleName")
					);
			return sample;
		}else {
			Sample sample = new Sample(
					Integer.parseInt(row.getString("sampleID")),
					Assay.getAssay(row.getString("assay")),
					Instrument.getInstrument(row.getString("instrument")),
					new RunFolder (row.getString("runFolderName")),
					row.getString("mrn"),
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
					row.getTimestamp("runDate"),
					row.getString("patientHistory"),
					row.getString("bmDiagnosis"),
					row.getString("note"),
					row.getString("enteredBy"),
					row.getString("analyzedBy")
					);
			return sample;
		}		
	}
	
	static void updateSampleNote(int sampleID, String newNote) throws Exception{
		PreparedStatement updateStatement = databaseConnection.prepareStatement("update samples set note = ? where sampleID = ?");
		updateStatement.setString(1, newNote);
		updateStatement.setString(2, ""+sampleID);
		updateStatement.executeUpdate();
		updateStatement.close();
	}

	static void updateSample(Sample sample) throws Exception{
		PreparedStatement updateStatement = databaseConnection.prepareStatement("update samples set lastName = ?, firstName = ?, mrn = ?, orderNumber = ?, pathNumber = ?, "
				+ "tumorSource = ?, tumorPercent = ?, patientHistory = ?, bmDiagnosis = ?, note = ? where sampleID = ?");
		updateStatement.setString(1, sample.getLastName());
		updateStatement.setString(2, sample.getFirstName());
        updateStatement.setString(3, sample.getMRN());
		updateStatement.setString(4, sample.getOrderNumber());
		updateStatement.setString(5, sample.getPathNumber());
		updateStatement.setString(6, sample.getTumorSource());
		updateStatement.setString(7, sample.getTumorPercent());
		updateStatement.setString(8, sample.getPatientHistory());
		updateStatement.setString(9, sample.getDiagnosis());
		updateStatement.setString(10, sample.getNote());
		updateStatement.setString(11, sample.sampleID+"");
		updateStatement.executeUpdate();
		updateStatement.close();
	}

	static void deleteSample(int sampleID) throws SQLException{
		CallableStatement callableStatement = databaseConnection.prepareCall("{call deleteSample(?)}");
		callableStatement.setString(1, ""+sampleID);
		callableStatement.executeUpdate();
		callableStatement.close();
	}
	
	static ExomeTumorMutationBurden getSampleTumorMutationBurden(TMBSample sample) throws Exception{
        PreparedStatement preparedStatement = databaseConnection.prepareStatement("select TMBPair, TMBTotalVariants, TMBScore, TMBGroup from sampleTumorMutationBurden where sampleID = ? ");
        preparedStatement.setInt(1, sample.sampleID);
        ResultSet rs = preparedStatement.executeQuery();
        
        ExomeTumorMutationBurden exomeTumorMutationBurden = null;
        if(rs.next()){
        	exomeTumorMutationBurden = new ExomeTumorMutationBurden(
        		sample.sampleID,
        		rs.getString("TMBPair"),
        		rs.getInt("TMBTotalVariants"),
        		rs.getFloat("TMBScore"),
        		rs.getString("TMBGroup")
        	);
        }
        preparedStatement.close();

        return exomeTumorMutationBurden;
    }

	static TMBSampleQC getTMBSampleQC(TMBSample sample) throws Exception{
        PreparedStatement preparedStatement = databaseConnection.prepareStatement("select * from sampleTumorMutationBurden where sampleID = ? ");
        preparedStatement.setInt(1, sample.sampleID);
        ResultSet rs = preparedStatement.executeQuery();
        
        TMBSampleQC TMBSampleQC = null;
        if(rs.next()){
        	TMBSampleQC = new TMBSampleQC(
        		rs.getInt("sampleTMBID"),
        		rs.getInt("sampleID"),
        		rs.getString("TMBPair"),
        		rs.getString("TMBTotalVariants"),
        		rs.getString("TMBScore"),
				rs.getString("TMBGroup"),
				rs.getString("varscan_strelka"),
				rs.getString("varscan_mutect"),
				rs.getString("mutect_strelka"),
				rs.getString("varscan_strelka_mutect"),
				rs.getString("Tumor_Total_Reads"),
				rs.getString("Normal_Total_Reads"),
				rs.getString("Tumor_Q20"),
				rs.getString("Normal_Q20"),
				rs.getString("Tumor_Total_Reads_AQC"),
				rs.getString("Normal_Total_Reads_AQC"),
				rs.getString("Tumor_Duplicate"),
				rs.getString("Normal_Duplicate"),
				rs.getString("Tumor_Total_Reads_ADup"),
				rs.getString("Normal_Total_Reads_ADup"),
				rs.getString("Tumor_Coverage"),
				rs.getString("Normal_Coverage"),
				rs.getString("Tumor_Target_Coverage"),
				rs.getString("Normal_Target_Coverage"),
				rs.getString("Tumor_Coverage_10X"),
				rs.getString("Normal_Coverage_10X"),
				rs.getString("Tumor_Coverage_20X"),
				rs.getString("Normal_Coverage_20X"),
				rs.getString("Tumor_Coverage_50X"),
				rs.getString("Normal_Coverage_50X"),
				rs.getString("Tumor_Coverage_100X"),
				rs.getString("Normal_Coverage_100X"),
				rs.getString("Tumor_Breadth_Coverage"),
				rs.getString("TiTv_Ratio")

				
        	);
        }
        preparedStatement.close();
        return TMBSampleQC;
    }
}
