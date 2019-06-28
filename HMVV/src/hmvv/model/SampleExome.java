package hmvv.model;

public class SampleExome extends Sample {

    private String normalRunID;
    private String normalSampleName;

    public SampleExome(int sampleID, String assay, String instrument, String lastName, String firstName, String orderNumber,
                  String pathNumber, String tumorSource, String tumorPercent, String runID, String sampleName,
                  String coverageID, String callerID, String runDate, String patientHistory, String bmDiagnosis, String note, String enteredBy,
                       String normalRunID, String normalSampleName){

        super (sampleID, assay, instrument, lastName, firstName, orderNumber,
                 pathNumber,  tumorSource, tumorPercent, runID, sampleName,
                coverageID, callerID, runDate, patientHistory, bmDiagnosis, note, enteredBy);

        this.normalRunID = normalRunID;
        this.normalSampleName = normalSampleName;

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
}
