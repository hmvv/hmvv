package hmvv.model;

public class TMBSampleQC {
	
	public int sampleTMBID;
	public int sampleID;
	public String TMBPair;
	public String TMBTotalVariants;
	public String TMBScore;
	public String TMBGroup;
	public String varscan_strelka;
	public String varscan_mutect;
	public String mutect_strelka;
	public String varscan_strelka_mutect;
	public String Tumor_Total_Reads;
	public String Normal_Total_Reads;
	public String Tumor_Q20;
	public String Normal_Q20;
	public String Tumor_Total_Reads_AQC;
	public String Normal_Total_Reads_AQC;
	public String Tumor_Duplicate;
	public String Normal_Duplicate;
	public String Tumor_Total_Reads_ADup;
	public String Normal_Total_Reads_ADup;
	public String Tumor_Coverage;
	public String Normal_Coverage;
	public String Tumor_Target_Coverage;
	public String Normal_Target_Coverage;
	public String Tumor_Coverage_10X;
	public String Normal_Coverage_10X;
	public String Tumor_Coverage_20X;
	public String Normal_Coverage_20X;
	public String Tumor_Coverage_50X;
	public String Normal_Coverage_50X;
	public String Tumor_Coverage_100X;
	public String Normal_Coverage_100X;
	public String Tumor_Breadth_Coverage;
	public String TiTv_Ratio;






	public TMBSampleQC(int sampleTMBID, int sampleID, String TMBPair,	String TMBTotalVariants,	String TMBScore,	String TMBGroup,	String varscan_strelka,	String varscan_mutect,	String mutect_strelka,
					   String varscan_strelka_mutect,	String Tumor_Total_Reads,	String Normal_Total_Reads,	String Tumor_Q20,	String Normal_Q20,	String Tumor_Total_Reads_AQC,	String Normal_Total_Reads_AQC,
	                   String Tumor_Duplicate,	String Normal_Duplicate,	String Tumor_Total_Reads_ADup,	String Normal_Total_Reads_ADup,	String Tumor_Coverage,	String Normal_Coverage,	String Tumor_Target_Coverage,
	                   String Normal_Target_Coverage,	String Tumor_Coverage_10X,	String Normal_Coverage_10X,	String Tumor_Coverage_20X,	String Normal_Coverage_20X,	String Tumor_Coverage_50X,	String Normal_Coverage_50X,
	                   String Tumor_Coverage_100X,	String Normal_Coverage_100X,	String Tumor_Breadth_Coverage,	String TiTv_Ratio) {

		this.sampleTMBID = sampleTMBID;
		this.sampleID = sampleID;
		this.TMBPair = TMBPair;
		this.TMBTotalVariants = TMBTotalVariants;
		this.TMBScore = TMBScore;
		this.TMBGroup = TMBGroup;
		this.varscan_strelka = varscan_strelka;
		this.varscan_mutect = varscan_mutect;
		this.mutect_strelka = mutect_strelka;
		this.varscan_strelka_mutect = varscan_strelka_mutect;
		this.Tumor_Total_Reads = Tumor_Total_Reads;
		this.Normal_Total_Reads = Normal_Total_Reads;
		this.Tumor_Q20 = addSign(Tumor_Q20);
		this.Normal_Q20 = addSign(Normal_Q20);
		this.Tumor_Total_Reads_AQC = Tumor_Total_Reads_AQC;
		this.Normal_Total_Reads_AQC = Normal_Total_Reads_AQC;
		this.Tumor_Duplicate= addSign(Tumor_Duplicate);
		this.Normal_Duplicate = addSign(Normal_Duplicate);
		this.Tumor_Total_Reads_ADup = addSign(Tumor_Total_Reads_ADup);
		this.Normal_Total_Reads_ADup = addSign(Normal_Total_Reads_ADup);
		this.Tumor_Coverage = Tumor_Coverage;
		this.Normal_Coverage = Normal_Coverage;
		this.Tumor_Target_Coverage = addSign(Tumor_Target_Coverage);
		this.Normal_Target_Coverage = addSign(Normal_Target_Coverage);
		this.Tumor_Coverage_10X = addSign(Tumor_Coverage_10X);
		this.Normal_Coverage_10X = addSign(Normal_Coverage_10X);
		this.Tumor_Coverage_20X = addSign(Tumor_Coverage_20X);
		this.Normal_Coverage_20X = addSign(Normal_Coverage_20X);
		this.Tumor_Coverage_50X = addSign(Tumor_Coverage_50X);
		this.Normal_Coverage_50X = addSign(Normal_Coverage_50X);
		this.Tumor_Coverage_100X = addSign(Tumor_Coverage_100X);
		this.Normal_Coverage_100X = addSign(Normal_Coverage_100X);
		this.Tumor_Breadth_Coverage = addmb(Tumor_Breadth_Coverage);
		this.TiTv_Ratio = fixedNull(TiTv_Ratio);

	}

	private String fixedNull(String TiTv_Ratio){
		return (TiTv_Ratio == null)?"N/A":TiTv_Ratio;
	}

	private String addSign(String testString){
		return (testString == null)?"":testString + "%";
	}

	private String addmb(String Tumor_Breadth_Coverage){
		return (Tumor_Breadth_Coverage == null)?"":Tumor_Breadth_Coverage + " mb";
	}
}
