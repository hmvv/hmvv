package hmvv.model;

public class Coordinate {
	private String chr;
	private String pos;
	private String ref;
	private String alt;
	
	public Coordinate(String chr, String pos, String ref, String alt) {
		this.chr = chr;
		this.pos = pos;
		this.ref = ref;
		this.alt = alt;
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
	
	public String getCoordinateAsString(){
		return String.format("%s:%s %s->%s", chr, pos, ref, alt);
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Coordinate){
			Coordinate other = (Coordinate)o;
			return chr.equals(other.chr) && pos.equals(other.pos) && ref.equals(other.ref) && alt.equals(other.alt);
		}
		return false;
	}
	
}
