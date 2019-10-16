package hmvv.model;

public class TMBSample extends Sample{

	private String normalInstrumentName;
	private String normalRunID;
	private String normalSampleName;
	
	public TMBSample(int sampleID, String assay, String instrument, String mrn, String lastName, String firstName, String orderNumber,
			String pathNumber, String tumorSource, String tumorPercent, String runID, String sampleName,
			String coverageID, String callerID, String runDate, String patientHistory, String bmDiagnosis, String note, String enteredBy,
			String normalInstrumentName, String normalRunID, String normalSampleName) {
		super(sampleID, assay, instrument, mrn, lastName, firstName, orderNumber, pathNumber, tumorSource, tumorPercent, runID,
				sampleName, coverageID, callerID, runDate, patientHistory, bmDiagnosis, note, enteredBy);
		this.normalInstrumentName = normalInstrumentName;
		this.normalRunID = normalRunID;
		this.normalSampleName = normalSampleName;
	}

    public String getNormalInstrumentName() {
        return normalInstrumentName;
    }

    public void setNormalInstrumentName(String normalInstrumentName) {
        this.normalInstrumentName = normalInstrumentName;
    }

    public String getNormalRunID() {
		return normalRunID;
	}

	public void setNormalRunID(String normalRunID) {
		this.normalRunID = normalRunID;
	}

	public String getNormalSampleName() {
		return normalSampleName;
	}

	public void setNormalSampleName(String normalSampleName) {
		this.normalSampleName = normalSampleName;
	}
	
	@Override
	public String getAssayQCLabel(){
		return "QC";
	}
}
