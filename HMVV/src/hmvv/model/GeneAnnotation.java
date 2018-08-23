package hmvv.model;

import java.util.Date;

public class GeneAnnotation extends CommonAnnotation{
	
	public final String gene;
	public final String curation;
	
	public GeneAnnotation(String gene, String curation, String enteredBy, Date enterDate) {
		this(-1, gene, curation, enteredBy, enterDate);
	}

	public GeneAnnotation(Integer geneAnnotationID, String gene, String curation, String enteredBy, Date enterDate) {
		super(geneAnnotationID, enteredBy, enterDate);
		this.gene = gene;
		this.curation = curation;
	}
	
	public boolean equals(Object o) {
		if(o instanceof GeneAnnotation) {
			GeneAnnotation other = (GeneAnnotation) o;
			if(!other.gene.equals(gene)) {
				return false;
			}
			if(!other.curation.equals(curation)) {
				return false;
			}
			return true;
		}
		return false;
	}
}
