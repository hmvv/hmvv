package hmvv.model;

public class Sample {
	
	public int sampleID;
	public final String assay;
	public final String instrument;
	public final String runID;
	public final String sampleName;
	public final String coverageID;
	public final String callerID;
	public final String runDate;
	public final String enteredBy;
	private String lastName;
	private String firstName;
	private String orderNumber;
	private String pathNumber;
	private String tumorSource;
	private String tumorPercent;
	private String patientHistory;
	private String diagnosis ;
	private String note;

	public Sample(int sampleID, String assay, String instrument, String lastName, String firstName, String orderNumber,
			String pathNumber, String tumorSource, String tumorPercent, String runID, String sampleName,
			String coverageID, String callerID, String runDate, String patientHistory, String bmDiagnosis, String note, String enteredBy) {
		this.sampleID = sampleID;
		this.assay = notNull(assay);
		this.instrument = notNull(instrument);
		this.lastName = notNull(lastName);
		this.firstName = notNull(firstName);
		this.orderNumber = notNull(orderNumber);
		this.pathNumber = notNull(pathNumber);
		this.tumorSource = notNull(tumorSource);
		this.tumorPercent = notNull(tumorPercent);
		this.runID = notNull(runID);
		this.sampleName = notNull(sampleName);
		this.coverageID = notNull(coverageID);
		this.callerID = notNull(callerID);
		this.runDate = notNull(runDate);
		this.patientHistory = notNull(patientHistory);
		this.diagnosis = notNull(bmDiagnosis);
		this.note = notNull(note);
		this.enteredBy = notNull(enteredBy);
	}
	
	public int getSampleID(){
		return sampleID;
	}
	
	public void setSampleID(int id){
		this.sampleID = id;
	}
	
	private String notNull(String testString){
		return (testString == null)?"":testString;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getPathNumber() {
		return pathNumber;
	}

	public void setPathNumber(String pathNumber) {
		this.pathNumber = pathNumber;
	}

	public String getTumorSource() {
		return tumorSource;
	}

	public void setTumorSource(String tumorSource) {
		this.tumorSource = tumorSource;
	}

	public String getTumorPercent() {
		return tumorPercent;
	}

	public String getPatientHistory() {
		return patientHistory;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setPatientHistory(String patientHistory) {
		this.patientHistory = patientHistory;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	public void setTumorPercent(String tumorPercent) {
		this.tumorPercent = tumorPercent;
	}

	public String getAssayQCLabel(){
		return "amplicon";
	}
}
