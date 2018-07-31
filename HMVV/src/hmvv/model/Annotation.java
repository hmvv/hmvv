package hmvv.model;

public class Annotation {
	
	private Integer annotationID;
	private Coordinate coordinate;
	private String classification;
	private String curation;
	private String somatic;
	private String enteredBy;
	private String enterDate;
	
	public Annotation(Integer  annotationID, Coordinate coordinate, String classification, String curation, String somatic, String enteredBy, String enterDate) {
		this.annotationID = annotationID;
		this.coordinate = coordinate;
		this.classification = classification;
		this.curation = curation;
		this.somatic = somatic;
		this.enteredBy = enteredBy;
		this.enterDate = enterDate;
	}
	
	public Annotation( Coordinate coordinate) {
		this.coordinate = coordinate;
	}
	
	public void setClassification(String classification) {
		this.classification = classification;
	}

	public void setCuration(String curation) {
		this.curation = curation;
	}

	public void setSomatic(String somatic) {
		this.somatic = somatic;
	}

	public Coordinate getCoordinate(){
		return coordinate;
	}

	public String getClassification() {
		return classification;
	}

	public String getCuration() {
		return curation;
	}

	public String getSomatic() {
		return somatic;
	}

	public String getSomaticDisplayText() {
		if(somatic.equals("Not set")) {
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
	
	public String getEnteredBy() {
		return enteredBy;
	}

	public void setEnteredBy(String enteredBy) {
		this.enteredBy = enteredBy;
	}
    
	public String getEnterDate() {
		return enterDate;
	}

	public void setEnterDate(String enterDate) {
		this.enterDate = enterDate;
	}
}
