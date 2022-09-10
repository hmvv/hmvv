package hmvv.model;

import hmvv.main.Configurations;

public class Amplicon {
	
	public final Sample sample;
	public final String gene;
	public final String ampliconName;
	public final String chr;
	public final Integer chr_start;
	public final Integer chr_end;
	public final Integer length;
	public final Integer total_reads;
	public final Integer average_depth;
	public final Integer cumulative_depth;
	public final Integer cov20x;
	public final Integer cov100x;
	public final Integer cov500x;
	public final Integer cov100xPercent;

	private boolean failed;
	private Integer qcMeasure;
	private String qcMeasureDescription;
	
	public Amplicon(
		Sample sample,
		String gene,
		String ampliconName,
		String chr,
		Integer chr_start,
		Integer chr_end,
		Integer length,
		Integer total_reads,
		Integer average_depth,
		Integer cumulative_depth,
		Integer cov20x,
		Integer cov100x,
		Integer cov500x,
		Integer cov100xPercent
	) {
		this.sample = sample;
		this.gene = gene;
		this.ampliconName = ampliconName;
		this.chr = chr;
		this.chr_start = chr_start;
		this.chr_end = chr_end;
		this.length = length;
		this.total_reads = total_reads;
		this.average_depth = average_depth;
		this.cumulative_depth = cumulative_depth;
		this.cov20x = cov20x;
		this.cov100x = cov100x;
		this.cov500x = cov500x;
		this.cov100xPercent = cov100xPercent;
		
		String instrument = sample.instrument.instrumentName;
		String assay = sample.assay.assayName;
		
		if(instrument.equals("proton") && total_reads != null){
			failed = total_reads < Configurations.READ_DEPTH_FILTER;
			qcMeasure = total_reads;
			qcMeasureDescription = "Total Reads < " + Configurations.READ_DEPTH_FILTER;
		}else if(assay.equals("heme")){
			if(instrument.equals("miseq")){
				failed = cumulative_depth < Configurations.READ_DEPTH_FILTER;
				qcMeasure = cumulative_depth;
				qcMeasureDescription = "Cumulative Read Depth < " + Configurations.READ_DEPTH_FILTER;
			}else if(instrument.equals("nextseq") && cov100xPercent != null){
				failed = cov100xPercent < Configurations.COVERAGE_PERCENTAGE_100X;
				qcMeasure = cov100xPercent;
				qcMeasureDescription = "100x Coverage > " +( 100 - Configurations.COVERAGE_PERCENTAGE_100X) + "% of positions";
			}else if(instrument.equals("nextseq") && average_depth != null){
				failed = average_depth < Configurations.READ_DEPTH_FILTER;
				qcMeasure = average_depth;
				qcMeasureDescription = "Average depth < " + Configurations.READ_DEPTH_FILTER;
			}else{
				new IllegalArgumentException("Unable to determine amplicon failure.");
			}
		}else{
			new IllegalArgumentException("Unable to determine amplicon failure.");
		}
	}

	public boolean isFailedAmplicon(){
		return failed;
	}

	public Integer getQCMeasure(){
		return qcMeasure;
	}

	public String getQCMeasureDescription(){
		return qcMeasureDescription;
	}
}
