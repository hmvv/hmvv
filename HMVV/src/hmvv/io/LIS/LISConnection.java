package hmvv.io.LIS;

import java.sql.SQLException;
import java.util.ArrayList;

import hmvv.main.Configurations;
import hmvv.model.Patient;
import hmvv.model.PatientHistory;

public class LISConnection{
	
	private static LISCommands connection;
	
	public static void connect() throws Exception {
		Class<?> c = Class.forName(Configurations.LIS_DRIVER);
		Object o = c.newInstance();
		connection = ((LISCommands)o);
	}

	public static String getBarcodeHelpText() {
		return connection.getBarcodeHelpText();
	}
	
	public static Patient getPatient(String orderNumber) throws SQLException {
		return connection.getPatient(orderNumber);
	}

	public static ArrayList<PatientHistory> getPatientHistory(String orderNumber) throws SQLException{
		return connection.getPatientHistory(orderNumber);
	}
	
	public static ArrayList<PatientHistory> getLegacyPatientHistory(String orderNumber) throws SQLException{
		return connection.getLegacyPatientHistory(orderNumber);
	}

	public static String getLabOrderNumber(String assay, String barcode, String sampleName) throws SQLException{
		return connection.getLabOrderNumber(assay, barcode, sampleName);
	}

	public static ArrayList<String> getPathOrderNumbers(String assay, String labOrderNumber) throws SQLException{
		return connection.getPathOrderNumbers(assay, labOrderNumber);
	}


	public static String[] runLISIntegration(String assay, String barcodeText, String sampleName) throws SQLException{
		//fill order number
		String labOrderNumber = getLabOrderNumber(assay, barcodeText, sampleName);
		if(labOrderNumber.equals("")) {
			return new String[] {labOrderNumber, "", "", "", "", ""};
		}
		
		//fill pathology number
		ArrayList<String> pathOrderNumbers = getPathOrderNumbers(assay, labOrderNumber);
		String pathologyNumber = "";
		if(pathOrderNumbers.size() == 0) {
			//No pathology orders found for this sample
		}else if(pathOrderNumbers.size() == 1) {
			pathologyNumber = pathOrderNumbers.get(0);
		}

		//fill patient name
		Patient patient = LISConnection.getPatient(labOrderNumber);
		if(patient != null) {
			return new String[] {labOrderNumber, pathologyNumber, patient.mrn, patient.firstName, patient.lastName};
		}
		return new String[] {labOrderNumber, pathologyNumber, "", "", "", ""};
    }
}
