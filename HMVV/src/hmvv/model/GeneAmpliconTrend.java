package hmvv.model;

import java.util.TreeMap;

public class GeneAmpliconTrend {
	public final String gene;
	private TreeMap<String, AmpliconTrend> ampliconTrends;
	
	public GeneAmpliconTrend(String gene) {
		this.gene = gene;
		ampliconTrends = new TreeMap<String, AmpliconTrend>();
	}
	
	public void addAmplicon(Amplicon amplicon) {
		AmpliconTrend ampliconTrend = ampliconTrends.get(amplicon.ampliconName);
		if(ampliconTrend == null) {
			ampliconTrend = new AmpliconTrend(amplicon.gene, amplicon.ampliconName);
			ampliconTrends.put(amplicon.ampliconName, ampliconTrend);
		}
		ampliconTrend.addAmplicon(amplicon);
	}
	
	public TreeMap<String, AmpliconTrend> getAmpliconTrends(){
		return ampliconTrends;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof GeneAmpliconTrend) {
			return gene.equals(   ((GeneAmpliconTrend)o).gene );
		}
		return false;
	}
}
