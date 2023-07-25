package hmvv.model;

import java.util.Date;

public class Annotation extends CommonAnnotation{
	
	public final Coordinate cordinate;
	public final String classification;
	public final String curation;
	public final String somatic;
	
	public Annotation(Integer annotationID, Coordinate cordinate, String classification, String curation, String somatic, String enteredBy, Date enterDate) {
		super(annotationID, enteredBy, enterDate);
		this.cordinate = cordinate;
		this.classification = classification;
		this.curation = curation;
		this.somatic = somatic;
	}
	
	public Annotation(Coordinate coordinate, String classification, String curation, String somatic, String enteredBy, Date enterDate) {
		this(-1, coordinate, classification, curation, somatic, enteredBy, enterDate);
	}
	
	public String getSomaticDisplayText() {
		if(somatic.equals("Not set")){
			return "";
		}
		if(somatic == null){
			return "";
		}
		return somatic;
	}
	
	public String getClassificationDisplayText() {
		if(classification.equals("Not set")) {
			return "";
		}
		return classification;
	}
	
	public String getDisplayText() {
		if(curation.trim().length() == 0) {
			return "Enter";
		}else {
			return "Annotation";
		}
	}
	
	public boolean equals(Object o) {
		if(o instanceof Annotation) {
			Annotation other = (Annotation) o;
			if(!other.cordinate.equals(cordinate)) {
				return false;
			}
			if(!other.classification.equals(classification)) {
				return false;
			}
			if(!other.curation.equals(curation)) {
				return false;
			}
			if(!other.somatic.equals(somatic)) {
				return false;
			}
			return true;
		}
		return false;
	}
}
