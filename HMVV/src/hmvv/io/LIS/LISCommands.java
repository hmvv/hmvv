package hmvv.io.LIS;

import java.sql.SQLException;
import java.util.ArrayList;

import hmvv.model.PatientHistory;

public interface LISCommands {
	public String getLabOrderNumber(String pathOrderNumber) throws SQLException;
	public String[] getPatientName(String orderNumber) throws SQLException;
	public ArrayList<PatientHistory> getPatientHistory(String orderNumber) throws SQLException;
}
