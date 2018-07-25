package hmvv.model;

public class Amplicon {
	
	public final int sampleID;
	public final String ampliconName;
	public final String ampliconCov;
	
	public Amplicon(int sampleID, String ampliconName, String ampliconCov) {
		this.sampleID = sampleID;
		this.ampliconName = ampliconName;
		this.ampliconCov = ampliconCov;
	}
}
