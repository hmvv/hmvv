package hmvv.model;

public class Amplicon {
	public final int sampleID;
	public final String assay;
	public final String ampliconName;
	public final String ampliconCov;
	
	public Amplicon(int sampleID, String assay, String ampliconName, String ampliconCov) {
		this.sampleID = sampleID;
		this.assay = assay;
		this.ampliconName = ampliconName;
		this.ampliconCov = ampliconCov;
	}
}
