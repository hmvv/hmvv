package hmvv.model;

import java.util.ArrayList;

public class Mutation {
	
	//common
	private boolean reported;
	private String gene;
	private String exons;
	private String HGVSc;
	private String HGVSp;
	
	//basic
	private String dbSNPID;
	private ArrayList<String> cosmicIDs;
	private String type;
	private String genotype;
	private Double altFreq;
	private Integer readDP;
	private Integer altReadDP;
	private Integer occurrence;
	private String annotation;
	
	//ClinVar
	private String origin;
	private String clinicalAllele;
	private String clinicalSig;
	private String clinicalAcc;
	private String pubmed;
	
	//Coordinates
	private String chr;
	private String pos;
	private String ref;
	private String alt;
	private String consequence;
	private String sift;
	private String polyPhen;
	
	//G1000
	private Integer altCount;
	private Integer totalCount;
	private Double altGlobalFreq;
	private Double americanFreq;
	private Double asianFreq;
	private Double afrFreq;
	private Double eurFreq;
	
	//Sample
	private String lastName;
	private String firstName;
	private String orderNumber;
	private String assay;
	private Integer sampleID;
	private String tumorSource;
	private String tumorPercent;
	
	public Mutation(){
		
	}
	
	/**
	 * Assumes a chr, pos, ref, and alt key are set
	 * @return
	 */
	public Coordinate getCoordinate(){
		return new Coordinate(chr, pos, ref, alt);
	}

	public boolean isReported() {
		return reported;
	}

	public void setReported(boolean reported) {
		this.reported = reported;
	}

	public String getGene() {
		return gene;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}

	public String getExons() {
		return exons;
	}

	public void setExons(String exons) {
		this.exons = exons;
	}

	public String getHGVSc() {
		return HGVSc;
	}

	public void setHGVSc(String hGVSc) {
		HGVSc = hGVSc;
	}

	public String getHGVSp() {
		return HGVSp;
	}

	public void setHGVSp(String hGVSp) {
		HGVSp = hGVSp;
	}

	public String getDbSNPID() {
		return dbSNPID;
	}

	public void setDbSNPID(String dbSNPID) {
		this.dbSNPID = dbSNPID;
	}

	public ArrayList<String> getCosmicID() {
		return cosmicIDs;
	}
	
	public String cosmicIDsToString(String separator){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < cosmicIDs.size(); i++){
			sb.append(cosmicIDs.get(i));
			if(i+1 < cosmicIDs.size()){
				sb.append(separator);
			}
		}
		return sb.toString();
	}

	public void setCosmicID(ArrayList<String> cosmicID) {
		this.cosmicIDs = cosmicID;
	}
	
	public void setCosmicID(String cosmicID) {
		this.cosmicIDs = new ArrayList<String>();
		this.cosmicIDs.add(cosmicID);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getGenotype() {
		return genotype;
	}

	public void setGenotype(String genotype) {
		this.genotype = genotype;
	}

	public Double getAltFreq() {
		return altFreq;
	}

	public void setAltFreq(Double altFreq) {
		this.altFreq = altFreq;
	}

	public Integer getReadDP() {
		return readDP;
	}

	public void setReadDP(Integer readDP) {
		this.readDP = readDP;
	}

	public Integer getAltReadDP() {
		return altReadDP;
	}

	public void setAltReadDP(Integer altReadDP) {
		this.altReadDP = altReadDP;
	}

	public Integer getOccurrence() {
		return occurrence;
	}

	public void setOccurrence(Integer occurrence) {
		this.occurrence = occurrence;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getClinicalAllele() {
		return clinicalAllele;
	}

	public void setClinicalAllele(String clinicalAllele) {
		this.clinicalAllele = clinicalAllele;
	}

	public String getClinicalSig() {
		return clinicalSig;
	}

	public void setClinicalSig(String clinicalSig) {
		this.clinicalSig = clinicalSig;
	}

	public String getClinicalAcc() {
		return clinicalAcc;
	}

	public void setClinicalAcc(String clinicalAcc) {
		this.clinicalAcc = clinicalAcc;
	}

	public String getPubmed() {
		return pubmed;
	}

	public void setPubmed(String pubmed) {
		this.pubmed = pubmed;
	}

	public String getChr() {
		return chr;
	}

	public void setChr(String chr) {
		this.chr = chr;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getConsequence() {
		return consequence;
	}

	public void setConsequence(String consequence) {
		this.consequence = consequence;
	}

	public String getSift() {
		return sift;
	}

	public void setSift(String sift) {
		this.sift = sift;
	}

	public String getPolyPhen() {
		return polyPhen;
	}

	public void setPolyPhen(String polyPhen) {
		this.polyPhen = polyPhen;
	}

	public Integer getAltCount() {
		return altCount;
	}

	public void setAltCount(Integer altCount) {
		this.altCount = altCount;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Double getAltGlobalFreq() {
		return altGlobalFreq;
	}

	public void setAltGlobalFreq(Double altGlobalFreq) {
		this.altGlobalFreq = altGlobalFreq;
	}

	public Double getAmericanFreq() {
		return americanFreq;
	}

	public void setAmericanFreq(Double americanFreq) {
		this.americanFreq = americanFreq;
	}

	public Double getAsianFreq() {
		return asianFreq;
	}

	public void setAsianFreq(Double asianFreq) {
		this.asianFreq = asianFreq;
	}

	public Double getAfricanFreq() {
		return afrFreq;
	}

	public void setAfricanFreq(Double afrFreq) {
		this.afrFreq = afrFreq;
	}
	
	public Double getEurFreq() {
		return eurFreq;
	}

	public void setEurFreq(Double eurFreq) {
		this.eurFreq = eurFreq;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getAssay() {
		return assay;
	}

	public void setAssay(String assay) {
		this.assay = assay;
	}

	public Integer getSampleID() {
		return sampleID;
	}

	public void setSampleID(Integer sampleID) {
		this.sampleID = sampleID;
	}

	public String getTumorSource() {
		return tumorSource;
	}

	public void setTumorSource(String tumorSource) {
		this.tumorSource = tumorSource;
	}

	public String getTumorPercent() {
		return tumorPercent;
	}

	public void setTumorPercent(String tumorPercent) {
		this.tumorPercent = tumorPercent;
	}
}
