package hmvv.model;

public class Amplicon {
	
	public final int sampleID;
	public final String gene;
	public final String ampliconName;
	public final int readDepth;
	
	public Amplicon(int sampleID, String gene, String ampliconName, int readDepth) {
		this.sampleID = sampleID;
		this.gene = gene;
		this.ampliconName = ampliconName;
		this.readDepth = readDepth;
	}
}
