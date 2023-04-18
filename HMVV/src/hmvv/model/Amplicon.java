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
	private String failed_amplicons;
	private Boolean failed_check; 

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
		Integer cov250x, 
		Integer cov500x,
		Integer cov100xPercent,
		Integer cov250xPercent
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
			failed_check = total_reads < Configurations.getDefaultReadDepthFilter(sample);
			qcMeasureDescription = "Total Reads < " + Configurations.getDefaultReadDepthFilter(sample);
			if(failed_check == true){
				failed = true;
				failed_amplicons = ampliconName;
				qcMeasure = total_reads;
			}
			
		}else if(assay.equals("heme")){
			if(instrument.equals("miseq")){
				failed_check = cumulative_depth <  Configurations.getDefaultReadDepthFilter(sample);
				qcMeasureDescription = "Cumulative Read Depth < " +  Configurations.getDefaultReadDepthFilter(sample);
				if(failed_check == true){
					failed = true;
					failed_amplicons = ampliconName;
					qcMeasure = cumulative_depth;

				}
			}else if(instrument.equals("nextseq") && total_reads != null && Configurations.OLDER_RUN_DATE.compareTo(sample.runDate.toString()) >= 0){ // Older heme runs
				failed_check = average_depth <  Configurations.getDefaultReadDepthFilter(sample);
				qcMeasureDescription = "Average Depth < " +  Configurations.getDefaultReadDepthFilter(sample);
				if(failed_check == true){
					failed = true;
					failed_amplicons = ampliconName;
					qcMeasure = total_reads;

				}
			}else if(instrument.equals("nextseq") && cov250xPercent != null){
				failed_check = cov250xPercent < Configurations.COVERAGE_PERCENTAGE_250X;
				qcMeasureDescription =  Configurations.getqcMeasureDescription(sample) +(Configurations.COVERAGE_PERCENTAGE_250X) + "% of positions";
				if(failed_check == true){
					failed = true;
					failed_amplicons = ampliconName;
					qcMeasure = cov250xPercent;
				}
			}else if(instrument.equals("nextseq") && average_depth != null){
				failed_check = average_depth < Configurations.HISTORICAL_NEXTSEQ_HEME_READ_DEPTH_FILTER;
				qcMeasure = average_depth;
				qcMeasureDescription = "Average Depth < " + Configurations.HISTORICAL_NEXTSEQ_HEME_READ_DEPTH_FILTER;
				if(failed_check == true){
					failed = true;
					failed_amplicons = ampliconName;
					qcMeasure = average_depth;
				}
				
			}else{
				new IllegalArgumentException("Unable to determine amplicon failure.");
			}
		}else{
			new IllegalArgumentException("Unable to determine amplicon failure.");
		}
	}

	

	public static String getQCColumnName(Sample sample){
        if(sample.instrument.instrumentName.equals("proton")){
            return "Total Reads";
        }
		else if ((sample.assay.assayName.equals("heme")) && (sample.instrument.instrumentName.equals("nextseq") && (Configurations.OLDER_RUN_DATE.compareTo(sample.runDate.toString()) >= 0))){
            return "Average Depth";
        }
        else if ((sample.assay.assayName.equals("heme")) && (sample.instrument.instrumentName.equals("nextseq"))){
            return "% Positions";
        }
        else if ((sample.assay.assayName.equals("heme")) && (sample.instrument.instrumentName.equals("miseq"))){
            return "Cumulative Depth";
        }
		
        else{
            return "QC Measure";
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

	public String getAmpliconName(){
		return failed_amplicons;
	}

}
