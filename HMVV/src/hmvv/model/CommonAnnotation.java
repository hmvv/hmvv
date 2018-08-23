package hmvv.model;

import java.util.Date;

public class CommonAnnotation {
	private Integer annotationID;
	public final String enteredBy;
	public final Date enterDate;
	
	public CommonAnnotation(String enteredBy, Date enterDate) {
		this(-1, enteredBy, enterDate);
	}

	public CommonAnnotation(Integer annotationID, String enteredBy, Date enterDate) {
		this.annotationID = annotationID;
		this.enteredBy = enteredBy;
		this.enterDate = enterDate;
	}
	
	public void setAnnotationID(Integer annotationID) {
		this.annotationID = annotationID;
	}
	
	public Integer getAnnotationID() {
		return annotationID;
	}
}
