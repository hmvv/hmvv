package hmvv.io.LIS;

import java.sql.SQLException;
import java.util.ArrayList;

import hmvv.model.PatientHistory;

public interface LISCommands {
	
	public String[] getPatientName(String orderNumber) throws SQLException;
	public ArrayList<PatientHistory> getPatientHistory(String orderNumber) throws SQLException;
	public String getLabOrderNumber(String assay, String enteredPathNumber, String sampleName) throws SQLException;
	public ArrayList<String> getPathOrderNumbers(String assay, String labOrderNumber, String enteredPathologyNumber) throws SQLException;
}
