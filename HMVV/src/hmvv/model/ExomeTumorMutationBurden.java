package hmvv.model;

public class ExomeTumorMutationBurden {

	private int sampleID;
    private String TMBPair;
    private int TMBTotalVariants;
    private float TMBScore;
    private String TMBGroup;

    public ExomeTumorMutationBurden(int sampleID, String TMBPair, int TMBTotalVariants, float TMBScore, String TMBGroup) {
    	this.sampleID = sampleID;
        this.TMBPair = TMBPair;
        this.TMBTotalVariants = TMBTotalVariants;
        this.TMBScore = TMBScore;
        this.TMBGroup = TMBGroup;
    }

    public int getSampleID() {
        return sampleID;
    }

    public void setSampleID(int sampleID) {
        this.sampleID = sampleID;
    }

    public String getTMBPair() {
        return TMBPair;
    }

    public int getTMBTotalVariants() {
        return TMBTotalVariants;
    }

    public float getTMBScore() {
        return TMBScore;
    }

    public String getTMBGroup() {
        return TMBGroup;
    }

    public void setTMBPair(String TMBPair) {
        this.TMBPair = TMBPair;
    }

    public void setTMBTotalVariants(int TMBTotalVariants) {
        this.TMBTotalVariants = TMBTotalVariants;
    }

    public void setTMBScore(float TMBScore) {
        this.TMBScore = TMBScore;
    }

    public void setTMBGroup(String TMBGroup) {
        this.TMBGroup = TMBGroup;
    }
}
