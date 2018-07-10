package hmvv.model;

public class Annotation {
	
	public enum STATUS{open,close};
	
	private Coordinate coordinate;
	private String classification;
	private String curation;
	private String somatic;
	private String updateStatus;
	private STATUS editStatus;
	
	public Annotation(Coordinate coordinate, String classification, String curation, String somatic, String updateStatus, STATUS editStatus) {
		this.coordinate = coordinate;
		this.classification = classification;
		this.curation = curation;
		this.somatic = somatic;
		this.updateStatus = updateStatus;
		this.editStatus = editStatus;
	}
	
	public Annotation(Coordinate coordinate, STATUS editStatus) {
		this(coordinate, "Not set", "", "Not set", "", editStatus);
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

	public void setUpdateStatus(String updateStatus) {
		this.updateStatus = updateStatus;
	}
	
	public void setEditStatus(STATUS editStatus){
		this.editStatus = editStatus;
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

	public String getUpdateStatus() {
		return updateStatus;
	}
	
	public STATUS getEditStatus(){
		return editStatus;
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
	
	public boolean isAnnotationSet(){
		if(!classification.equals("Not set")){
			return true;
		}
		if(!somatic.equals("Not set")){
			return true;
		}
		if(!curation.equals("")){
			return true;
		}
		return false;
	}
}
