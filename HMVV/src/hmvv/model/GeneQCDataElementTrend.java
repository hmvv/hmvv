package hmvv.model;

import java.util.TreeMap;

public class GeneQCDataElementTrend {
	public final String gene;
	private TreeMap<String, QCTrend> qualityControlTrends;
	
	public GeneQCDataElementTrend(String gene) {
		this.gene = gene;
		qualityControlTrends = new TreeMap<String, QCTrend>();
	}
	
	public void addDataElement(QCDataElement dataElement) {
		QCTrend qualityControlTrend = qualityControlTrends.get(dataElement.dataElementName);
		if(qualityControlTrend == null) {
			qualityControlTrend = new QCTrend(dataElement.gene, dataElement.dataElementName);
			qualityControlTrends.put(dataElement.dataElementName, qualityControlTrend);
		}
		qualityControlTrend.addDataElement(dataElement);
	}
	
	public TreeMap<String, QCTrend> getQualityControlTrends(){
		return qualityControlTrends;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof GeneQCDataElementTrend) {
			return gene.equals(   ((GeneQCDataElementTrend)o).gene );
		}
		return false;
	}
}
