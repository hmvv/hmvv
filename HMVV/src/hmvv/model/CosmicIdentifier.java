package hmvv.model;

public class CosmicIdentifier{
    public final String cosmicID;
    public final Coordinate coordinate;
    public final String gene;
    public final String source;

    public CosmicIdentifier(String cosmicID, Coordinate coordinate, String gene, String source){
        this.cosmicID = cosmicID;
        this.coordinate = coordinate;
        this.gene = gene;
        this.source = source;
    }

    public String toString(){
        return cosmicID + "(" + gene + ")";
    }

    public boolean equals(Object o){
        if (o instanceof CosmicIdentifier){
            CosmicIdentifier other = (CosmicIdentifier) o;
            return cosmicID.equals(other.cosmicID)
                && coordinate.equals(other.coordinate)
                && gene.equals(other.gene)
                ;
        }
        return false;
    }

    public int hashCode(){
        return new String(cosmicID + coordinate.getCoordinateAsString() + gene).hashCode();
    }
}
