package hmvv.model;

public class GeneTargetQC {
    public final Sample sample;
    public final String gene;
    public final int targetsPassedQC;
    public final int totalTargets;

    public GeneTargetQC(Sample sample, String gene, int targetsPassedQC, int totalTargets){
        this.sample = sample;
        this.gene = gene;
        this.targetsPassedQC = targetsPassedQC;
        this.totalTargets = totalTargets;
    }

    public Double getPercentage(){
        if (totalTargets == 0) {
            return null;
        }

        double percentage = (double) targetsPassedQC / totalTargets * 100;
        return percentage;
    }
}
