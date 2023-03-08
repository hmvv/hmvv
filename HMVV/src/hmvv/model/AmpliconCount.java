package hmvv.model;

public class AmpliconCount {
	public final int sampleID;
	public final String totalAmplicon;
	public final String failedAmplicon;
	
	public AmpliconCount(int sampleID, String totalAmplicon, String failedAmplicon) {
		this.sampleID = sampleID;
		this.totalAmplicon = totalAmplicon;
		this.failedAmplicon = failedAmplicon;
	}
}
