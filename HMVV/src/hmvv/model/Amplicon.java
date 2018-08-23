package hmvv.model;

public class Amplicon {
	
	public final int sampleID;
	public final String ampliconName;
	public final String readDepth;
	
	public Amplicon(int sampleID, String ampliconName, String readDepth) {
		this.sampleID = sampleID;
		this.ampliconName = ampliconName;
		this.readDepth = readDepth;
	}
}
