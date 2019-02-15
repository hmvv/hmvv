package hmvv.model;

import java.sql.Timestamp;

public class PatientHistory {
	public final String orderNumber;
	public final String interpretation;
	public final String reportType;
	public final String reportNumber;
	public final String reportRevisionNumber;
	public final Timestamp reportSignoutDate;
	
	public PatientHistory(String orderNumber, String interpretation, String reportType, String reportNumber, String reportRevisionNumber, Timestamp reportSignoutDate) {
		this.orderNumber = orderNumber;
		this.interpretation = interpretation;
		this.reportType = reportType;
		this.reportNumber = reportNumber;
		this.reportRevisionNumber = reportRevisionNumber;
		this.reportSignoutDate = reportSignoutDate;
	}
}
