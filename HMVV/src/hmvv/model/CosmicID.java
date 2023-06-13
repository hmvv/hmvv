package hmvv.model;

public class CosmicID{
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
    public final String source;

    public CosmicID(String cosmicID, Coordinate coordinate, String gene, String strand, String genomic_ID, String legacyID, String CDS, String AA, String HGVSc, String HGVSp, String HGVSg, String old_variant, String source){
        this.cosmicID = cosmicID;
        this.coordinate = coordinate;
        this.gene = gene;
        this.source = source;
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
            String[] split = gene.split(" ");
            if(split.length > 1) {
                return split[1];
            }
        }
        return "";
    }

    public String toString(){
        return cosmicID + "(" + gene + ")";
    }

    public boolean equals(Object o){
        if (o instanceof CosmicID){
            CosmicID other = (CosmicID) o;
            return cosmicID.equals(other.cosmicID)
                && coordinate.equals(other.coordinate)
                && gene.equals(other.gene)
                && strand.equals(other.strand)
                && legacyID.equals(other.legacyID)
                && gene.equals(other.gene)
                && HGVSc.equals(other.HGVSc)
                && HGVSp.equals(other.HGVSp)
                ;
        }
        return false;
    }

    public int hashCode(){
        return new String(cosmicID + coordinate.getCoordinateAsString() + gene + strand + legacyID + gene + HGVSc + HGVSp).hashCode();
    }
}
