package hmvv.model;

import java.util.ArrayList;

public class AmpliconTrend {
	public final String gene;
	public final String ampliconName;
	
	private ArrayList<Amplicon> amplicons;
	
	public AmpliconTrend(String gene, String ampliconName) {
		this.gene = gene;
		this.ampliconName = ampliconName;
		amplicons = new ArrayList<Amplicon>();
	}
	
	public void addAmplicon(Amplicon amplicon) {
		amplicons.add(amplicon);
	}
	
	public ArrayList<Amplicon> getAmplicons() {
		return amplicons;
	}
	
	public int[] getSampleIDs() {
		int[] retval = new int[amplicons.size()];
		for(int i = 0; i < amplicons.size(); i++) {
			retval[i] = amplicons.get(i).sampleID;
		}
		return retval;
	}
	
	public int[] getReadDepths() {
		int[] retval = new int[amplicons.size()];
		for(int i = 0; i < amplicons.size(); i++) {
			retval[i] = amplicons.get(i).readDepth;
		}
		return retval;
	}
}
