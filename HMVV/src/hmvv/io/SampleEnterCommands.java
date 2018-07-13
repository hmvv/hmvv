package hmvv.io;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import hmvv.model.Sample;

public class SampleEnterCommands {
	
	public static String getDateString(String instrument, String runID) throws Exception{
		return new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
	}
	
	public static void enterData(Sample sample) throws Exception{
		DatabaseCommands.insertDataIntoDatabase(sample);
	 }
}
