package hmvv.model;

public class QCDataElement {
	
	public final int sampleID;
	public final String gene;
	public final String dataElementName;
	public final int dataElementValue;
	
	public QCDataElement(int sampleID, String gene, String dataElementName, int dataElementValue) {
		this.sampleID = sampleID;
		this.gene = gene;
		this.dataElementName = dataElementName;
		this.dataElementValue = dataElementValue;
	}
}
