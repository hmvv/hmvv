package hmvv.model;

public class ExomeTumorMutationBurden {

    private  int sampleID;
    private  String TMBPair;
    private  String TMBTotalVariants;
    private  String TMBScore;
    private  String TMBGroup;

    public ExomeTumorMutationBurden(){}

    public ExomeTumorMutationBurden(String TMBPair, String TMBTotalVariants, String TMBScore, String TMBGroup) {
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

    public String getTMBTotalVariants() {
        return TMBTotalVariants;
    }

    public String getTMBScore() {
        return TMBScore;
    }

    public String getTMBGroup() {
        return TMBGroup;
    }

    public void setTMBPair(String TMBPair) {
        this.TMBPair = TMBPair;
    }

    public void setTMBTotalVariants(String TMBTotalVariants) {
        this.TMBTotalVariants = TMBTotalVariants;
    }

    public void setTMBScore(String TMBScore) {
        this.TMBScore = TMBScore;
    }

    public void setTMBGroup(String TMBGroup) {
        this.TMBGroup = TMBGroup;
    }
}
