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
}
