package hmvv.model;

public class GeneAnnotation {
	private String gene;
	private String curation;
	private boolean locked;
	
	public GeneAnnotation(String gene, String curation, boolean locked) {
		this.gene = gene;
		this.curation = curation;
		this.locked = locked;
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

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
}
