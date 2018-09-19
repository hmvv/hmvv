package hmvv.model;

import java.util.ArrayList;

public class QCTrend {
	public final String gene;
	public final String dataElementName;
	
	private ArrayList<QCDataElement> dataElements;
	
	public QCTrend(String gene, String dataElementName) {
		this.gene = gene;
		this.dataElementName = dataElementName;
		dataElements = new ArrayList<QCDataElement>();
	}
	
	public void addDataElement(QCDataElement dataElement) {
		for(int i = 0; i < dataElements.size(); i++) {
			//Occasionally there are duplicate data element names, so pick the one with the highest value
			if(dataElements.get(i).sampleID == dataElement.sampleID) {
				if(dataElements.get(i).dataElementValue < dataElement.dataElementValue) {
					dataElements.set(i, dataElement);
				}
				return;
			}
		}
		dataElements.add(dataElement);
	}
	
	public ArrayList<QCDataElement> getDataElements() {
		return dataElements;
	}
	
	public int[] getSampleIDs() {
		int[] retval = new int[dataElements.size()];
		for(int i = 0; i < dataElements.size(); i++) {
			retval[i] = dataElements.get(i).sampleID;
		}
		return retval;
	}
	
	public int[] getReadDepths() {
		int[] retval = new int[dataElements.size()];
		for(int i = 0; i < dataElements.size(); i++) {
			retval[i] = dataElements.get(i).dataElementValue;
		}
		return retval;
	}
}
