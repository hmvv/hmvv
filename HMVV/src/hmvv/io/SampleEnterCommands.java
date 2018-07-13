package hmvv.io;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import hmvv.model.CommandResponse;
import hmvv.model.Sample;

public class SampleEnterCommands {
	
	public static String getDateString(String instrument, String runID) throws Exception{
		String date = null;
		
		date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
		
		return date;

	}
	

	public static void enterData(Sample sample) throws Exception{

		DatabaseCommands.insertDataIntoDatabase(sample);
	 }
}
