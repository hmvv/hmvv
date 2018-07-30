package hmvv.model;

public class GeneAnnotation {
	
	private Integer geneAnnotationID;
	private String gene;
	private String curation;
	private String enteredBy;
	private String enterDate;

	public GeneAnnotation() {
		super();
	}
	
	public GeneAnnotation(String gene, String curation) {
		super();
        this.gene = gene;
        this.curation = curation;
	}

	public GeneAnnotation(Integer geneAnnotationID, String gene, String curation, String enteredBy, String enterDate) {
		super();
		this.geneAnnotationID = geneAnnotationID;
		this.gene = gene;
		this.curation = curation;
		this.enteredBy = enteredBy;
		this.enterDate = enterDate;
	}

	public GeneAnnotation(String gene, String curation, boolean locked) {
		
		this.gene = gene;
		this.curation = curation;
	}
	
	public void setGene(String gene) {
		this.gene = gene;
	}
	
	public void setCuration(String curation) {
		this.curation = curation;
	}

	public String getGene() {
		return gene;
	}

	public String getCuration() {
		return curation;
	}

	public Integer getGeneAnnotationID() {
		return geneAnnotationID;
	}

	public void setGeneAnnotationID(Integer geneAnnotationID) {
		this.geneAnnotationID = geneAnnotationID;
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
