package hmvv.model;

public class repeatMutations {

    public final String sampleID;
	public final String sampleName;
	public final String lastName;
    public final String firstName;
    public final Double altFreq;
    public final Integer readDP;
    public final Integer altReadDP;

    public repeatMutations(String sampleID, String sampleName, String lastName, String firstName, Double altFreq, Integer readDp, Integer altReadDp ){
        this.sampleID = sampleID;
        this.sampleName = sampleName;
        this.lastName = lastName;
        this.firstName = firstName;
        this.altFreq = altFreq;
        this.readDP = readDp;
        this.altReadDP = altReadDp;

    }
    
}
