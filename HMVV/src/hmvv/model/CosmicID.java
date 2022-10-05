package hmvv.model;

public class CosmicID {
    public final String cosmicID;
    public final Coordinate coordinate;
    public final String gene;
    public final String strand;
    public final String genomic_ID;
    public final String legacyID;
    public final String CDS;
    public final String AA;
    public final String HGVSc;
    public final String HGVSp;
    public final String HGVSg;
    public final String old_variant;

    public CosmicID(String cosmicID, Coordinate coordinate, String gene, String strand, String genomic_ID, String legacyID, String CDS, String AA, String HGVSc, String HGVSp, String HGVSg, String old_variant){
        this.cosmicID = cosmicID;
        this.coordinate = coordinate;
        this.gene = gene;
        this.strand = strand;
        this.genomic_ID = genomic_ID;
        this.legacyID = legacyID;
        this.CDS = CDS;
        this.AA = AA;
        this.HGVSc = HGVSc;
        this.HGVSp = HGVSp;
        this.HGVSg = HGVSg;
        this.old_variant = old_variant;
    }

    public String getTranscript() {
        if(gene.contains("ENST")) {
            String[] split = gene.split("_");
            if(split.length > 1) {
                return split[1];
            }
        }
        return "";
    }
}
