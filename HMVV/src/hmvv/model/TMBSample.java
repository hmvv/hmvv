package hmvv.model;

import java.sql.Timestamp;

public class TMBSample extends Sample{

	private String normalInstrumentName;
	private RunFolder normalRunFolder;
	private String normalSampleName;
	
	public TMBSample(int sampleID, Assay assay, Instrument instrument, RunFolder runFolder, String mrn, String lastName, String firstName, String orderNumber,
			String pathNumber, String tumorSource, String tumorPercent, String runID, String sampleName,
			String coverageID, String callerID, Timestamp runDate, String patientHistory, String bmDiagnosis, String note, String enteredBy,
			String normalInstrumentName, RunFolder normalRunFolder, String normalSampleName) {
		super(sampleID, assay, instrument, runFolder, mrn, lastName, firstName, orderNumber, pathNumber, tumorSource, tumorPercent, runID,
				sampleName, coverageID, callerID, runDate, patientHistory, bmDiagnosis, note, enteredBy);
		this.normalInstrumentName = normalInstrumentName;
		this.normalRunFolder = normalRunFolder;
		this.normalSampleName = normalSampleName;
	}

    public String getNormalInstrumentName() {
        return normalInstrumentName;
    }

    public void setNormalInstrumentName(String normalInstrumentName) {
        this.normalInstrumentName = normalInstrumentName;
    }

    public RunFolder getNormalRunFolder() {
		return normalRunFolder;
	}

	public void setNormalRunFolder(RunFolder normalRunFolder) {
		this.normalRunFolder = normalRunFolder;
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
