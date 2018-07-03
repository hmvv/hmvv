package hmvv.io;

import java.util.ArrayList;


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
			dateList = dateResult.responseLines;
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
		DatabaseCommands.insertDataIntoDatabase(sample);
	 }
}
