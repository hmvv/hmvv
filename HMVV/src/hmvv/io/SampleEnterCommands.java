package hmvv.io;

import java.util.ArrayList;
import java.util.Arrays;

import hmvv.model.CommandResponse;
import hmvv.model.Sample;

public class SampleEnterCommands {
	
	public static String getDateString(String instrument, String runID) throws Exception{
		String dateCommand = null;
		if((instrument.equals("pgm")) || (instrument.equals("proton"))){
			dateCommand = String.format("head -n 1 /home/%sAnalysis/*%s/runDate.txt", instrument, runID);
		}else{
			dateCommand = String.format("head -n 1 /home/%sAnalysis/*_%s_*/runDate.txt", instrument, runID);
		}

		CommandResponse dateResult = SSHConnection.executeCommandAndGetOutput(dateCommand);
		ArrayList<String> dateList = new ArrayList<String>();
		String dateString = "";
		if(dateResult.exitStatus == 0){
			dateList = parseServerResult(dateResult.response);
			if(dateList.size() == 1){
				dateString = dateList.get(0);
				return dateString;
			}else{
				throw new Exception("Warning: Run Date for the sample was not found");
			}
		}else{
			throw new Exception("Warning: Run Date for the sample was not found");
		}
	}
	
	public static void enterData(Sample sample) throws Exception{
		if(sample.getLastName().equals("") || sample.getFirstName().equals("") || sample.getOrderNumber().equals("")){
			throw new Exception("firstName, lastName and orderNumber are required");
		}
		
		String[] commands = constructCommandArray(sample);
		
		//Find variant result file
		ArrayList<String> dataFileList = findVariantFile(commands[0]);
		//find total amplicon number
		ArrayList<String> totalAmplicon = findTotalAmpliconNumber(commands[1]);
		//find amplicon file
		ArrayList<String> ampliconFileList = findAmpliconFile(commands[2]);
		//find failed amplicon number
		ArrayList<String> failedAmpliconCountList = findFailedAmpliconNumber(commands[3]);

		//Gather data
		String dataFile = dataFileList.get(0);
		String ampliconFile = ampliconFileList.get(0);
		Integer totalAmpliconCount = Integer.parseInt(totalAmplicon.get(0)) - 1;
		Integer failedAmpliconCount = Integer.parseInt(failedAmpliconCountList.get(0)) - 1;
		
		DatabaseCommands.insertDataIntoDatabase(dataFile, ampliconFile, totalAmpliconCount, failedAmpliconCount, sample);
	}
	
	private static String[] constructCommandArray(Sample sample){
		String instrument = sample.instrument;
		String runID = sample.runID;
		String sampleID = sample.sampleID;
		String coverageID = sample.coverageID;
		String variantCallerID = sample.callerID;
		
		if((sample.instrument.equals("pgm")) || (sample.instrument.equals("proton"))){
			String[] ionDataCommands = {
					//Find variant file
					String.format("ls /home/%sAnalysis/*%s/%s/%s/TSVC_variants.split.vep.parse.newVarView.filter.txt", instrument, runID, variantCallerID, sampleID),
					//find total amplicon number
					String.format("wc -l /home/%sAnalysis/*%s/%s/%s/amplicon.filter.txt | cut -d ' ' -f 1", instrument, runID, coverageID, sampleID),
					//find amplicon file
					String.format("ls /home/%sAnalysis/*%s/%s/%s/amplicon.lessThan100.txt", instrument, runID, coverageID, sampleID),
					//find failed amplicon number
					String.format("wc -l /home/%sAnalysis/*%s/%s/%s/amplicon.lessThan100.txt | cut -d ' ' -f 1", instrument, runID, coverageID, sampleID)
			};
			return ionDataCommands;
		}else{
			String[] illuminaDataCommands = {
					//Find variant result file
					String.format("ls /home/%sAnalysis/*_%s_*/%s.amplicon.vep.parse.filter.txt", instrument, runID, sampleID),
					//find total amplicon number
					String.format("wc -l /home/%sAnalysis/*_%s_*/%s.amplicon.txt | cut -d ' ' -f 1", instrument, runID, sampleID),
					//find amplicon file
					String.format("ls /home/%sAnalysis/*_%s_*/%s.amplicon.lessThan100.txt", instrument, runID, sampleID),
					//find failed amplicon number
					String.format("wc -l /home/%sAnalysis/*_%s_*/%s.amplicon.lessThan100.txt | cut -d ' ' -f 1", instrument, runID, sampleID)
			};
			return illuminaDataCommands;
		}
	}

	private static ArrayList<String> findVariantFile(String command) throws Exception{
		CommandResponse result = SSHConnection.executeCommandAndGetOutput(command);
		ArrayList<String> dataFileList = new ArrayList<String>();
		if(result.exitStatus == 0){
			dataFileList = parseServerResult(result.response);
		}else{
			throw new Exception("Error6: There was a problem locating sample file; data not entered");
		}
		if(dataFileList.size() != 1){
			throw new Exception("Error7: Only 1 file should be present for the sample; data not entered");
		}

		return dataFileList;
	}

	private static ArrayList<String> findAmpliconFile(String command) throws Exception{
		CommandResponse ampliconResult = SSHConnection.executeCommandAndGetOutput(command);
		ArrayList<String> ampliconFileList = new ArrayList<String>();
		if(ampliconResult.exitStatus == 0){
			ampliconFileList = parseServerResult(ampliconResult.response);
		}else{
			throw new Exception("Error4: There was a problem locating amplicon file; data not entered");
		}
		if(ampliconFileList.size() != 1){
			throw new Exception("Error5: Only 1 file should be present for the sample; data not entered");
		}

		return ampliconFileList;
	}

	private static ArrayList<String> findTotalAmpliconNumber(String command) throws Exception{
		CommandResponse totalAmpliconResult = SSHConnection.executeCommandAndGetOutput(command);
		ArrayList<String> totalAmplicon = new ArrayList<String>();
		if(totalAmpliconResult.exitStatus == 0){
			totalAmplicon = parseServerResult(totalAmpliconResult.response);
		}else{
			throw new Exception("Error9: Problem locating amplicon file, data not entered");
		}
		if(totalAmplicon.size() != 1){
			throw new Exception("Error8: There should be only one amplicon file, data not entered");
		}
		return totalAmplicon;
	}

	private static ArrayList<String> findFailedAmpliconNumber(String command) throws Exception{
		CommandResponse failedAmpliconCountResult = SSHConnection.executeCommandAndGetOutput(command);
		ArrayList<String> failedAmpliconCountList = new ArrayList<String>();
		if(failedAmpliconCountResult.exitStatus == 0){
			failedAmpliconCountList = parseServerResult(failedAmpliconCountResult.response);
		}else{
			throw new Exception("Error4: There was a problem locating failed amplicon file; data not entered");
		}
		if(failedAmpliconCountList.size() != 1){
			throw new Exception("Error5: Only 1 file should be present for the sample; data not entered");
		}
		return failedAmpliconCountList;
	}

	private static ArrayList<String> parseServerResult(StringBuilder result){
		String resultString = result.toString();
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(resultString.split("\\r?\\n")));
		return list;
	}
}
