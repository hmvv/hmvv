package hmvv.model;

public class Coordinate {
	private String chr;
	private String pos;
	private String ref;
	private String alt;
	private String gene;
	
	public Coordinate(String chr, String pos, String ref, String alt, String gene) {
		this.chr = chr;
		this.pos = pos;
		this.ref = ref;
		this.alt = alt;
		this.gene = gene;
	}
	
	public String getChr() {
		return chr;
	}
	
	public String getPos() {
		return pos;
	}
	
	public String getRef() {
		return ref;
	}
	
	public String getAlt() {
		return alt;
	}
	
    public String getGene(){
		return gene;
	}

	public String getCoordinateAsString(){
		return String.format("%s:%s %s->%s %s", chr, pos, ref, alt, gene);
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Coordinate){
			Coordinate other = (Coordinate)o;
			return chr.equals(other.chr) && pos.equals(other.pos) && ref.equals(other.ref) && alt.equals(other.alt); //&& gene.equals(other.gene) && svFlag.equals(other.svFlag)
		}
		return false;
	}

	public String toString(){
		return getCoordinateAsString();
	}
	
}
