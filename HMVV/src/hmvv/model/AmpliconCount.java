package hmvv.model;

public class AmpliconCount {
	public final int sampleID;
	public final int totalAmplicon;
	public final int failedAmplicon;
	
	public AmpliconCount(int sampleID, int totalAmplicon, int failedAmplicon) {
		this.sampleID = sampleID;
		this.totalAmplicon = totalAmplicon;
		this.failedAmplicon = failedAmplicon;
	}

	public float getPercentage(){
		return 100 - ((100 * failedAmplicon) / totalAmplicon);
	}
}
