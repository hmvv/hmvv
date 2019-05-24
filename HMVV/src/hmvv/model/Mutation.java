package hmvv.model;

import hmvv.gui.GUICommonTools;
import hmvv.main.Configurations;

import java.util.ArrayList;

public class Mutation {

    //common
    private boolean reported;
    private String chr;
    private String pos;
    private String ref;
    private String alt;
    private String gene;
    private String exons;

    // custom
    private boolean selected;
    private Integer occurrence;
    private ArrayList<Annotation> annotationHistory;

   //vep
    private String type;
    private VariantPredictionClass variantPredictionClass;
    private Double altFreq;
    private Integer readDP;
    private Integer altReadDP;
    private String consequence;
    private String sift;
    private String polyPhen;
    private String dbSNPID;
    private String HGVSc;
    private String HGVSp;
    private String pubmed;

    //ClinVar
    private String clinvarID;
    private String clinicaldisease;
    private String clinicalsignificance;
    private String clinicalconsequence;
    private String clinicalorigin;

    //cosmic
    private ArrayList<String> cosmicIDs;

    //G1000
    private Integer altCount;
    private Integer totalCount;
    private Double altGlobalFreq;
    private Double americanFreq;
    private Double eastAsianFreq;
    private Double southAsianFreq;
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

    //gnomad
    private String gnomadID;
    private Double gnomad_allfreq;

    //oncokb
    private String oncokbID;
    private String onco_Protein_Change;
    private String onco_Protein_Change_LF;
    private String oncogenicity;
    private String onco_MutationEffect;

    //civic
    private String civicID;
    private String civic_variant_origin;
    private String civic_variant_url;

    //pmkb
    private String pmkbID;
    private String pmkb_tumor_type;
    private String pmkb_tissue_type;

    public Mutation() {

    }

    /**
     * Assumes a chr, pos, ref, and alt key are set
     *
     * @return
     */
    public Coordinate getCoordinate() {
        return new Coordinate(chr, pos, ref, alt);
    }

    public boolean isReported() {
        return reported;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
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

    public String cosmicIDsToString(String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cosmicIDs.size(); i++) {
            sb.append(cosmicIDs.get(i));
            if (i + 1 < cosmicIDs.size()) {
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

    public VariantPredictionClass getVariantPredictionClass() {
        return variantPredictionClass;
    }

    public void setVariantPredictionClass(VariantPredictionClass variantPredictionClass) {
        this.variantPredictionClass = variantPredictionClass;
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

    public String getClinvarID() {
        return clinvarID;
    }
    public String getClinicaldisease() {
        return clinicaldisease;
    }

    public String getClinicalsignificance() {
        return clinicalsignificance;
    }

    public String getClinicalconsequence() {
        return clinicalconsequence;
    }

    public String getClinicalorigin() {
        return clinicalorigin;
    }

    public void setClinvarID(String id) {
        this.clinvarID = id ;
    }

    public void setClinicaldisease(String clinicaldisease) {
        this.clinicaldisease = clinicaldisease;
    }

    public void setClinicalsignificance(String clinicalsignificance) {
        this.clinicalsignificance = clinicalsignificance;
    }

    public void setClinicalconsequence(String clinicalconsequence) {
        this.clinicalconsequence = clinicalconsequence;
    }

    public void setClinicalorigin(String clinicalorigin) {
        this.clinicalorigin = clinicalorigin;
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

    public Double getEastAsianFreq() {
        return eastAsianFreq;
    }
    public Double getSouthAsianFreq() { return southAsianFreq; }

    public void setEastAsianFreq(Double eastAsianFreq) {
        this.eastAsianFreq = eastAsianFreq;
    }
    public void setSouthAsianFreq(Double southAsianFreq) {
        this.southAsianFreq = southAsianFreq;
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

    public void addAnnotation(Annotation annotation) {
        annotationHistory.add(annotation);
    }

    public int getAnnotationHistorySize() {
        return annotationHistory.size();
    }

    public Annotation getAnnotation(int index) {
        return annotationHistory.get(index);
    }

    public void setAnnotationHistory(ArrayList<Annotation> annotationHistory) {
        this.annotationHistory = annotationHistory;
    }
    
    public Annotation getLatestAnnotation() {
    	if (annotationHistory.size() == 0) {
            return null;
        }
    	return  annotationHistory.get(annotationHistory.size() - 1);
    }

    public Double getGnomad_allfreq() {
        return gnomad_allfreq;
    }

    public void setGnomad_allfreq(Double gnomad_allfreq) {
        this.gnomad_allfreq = gnomad_allfreq;
    }

    public String getGnomadID() {
        return gnomadID;
    }

    public void setGnomadID() {
       // if (this.getGnomad_allfreq() == null) {    	//TODO it seems like this check doesn't always work. I find variants online that don't have a gnomad_allfreq in our database
            this.gnomadID = this.chr.substring(3, this.chr.length()) + "-" + this.pos + "-" + this.ref + "-" + this.alt;
        //}
    }

    public String getOncokbID() {
        return oncokbID;
    }

    public String getOncogenicity() {
        return oncogenicity;
    }

    public String getOnco_MutationEffect() {
        return onco_MutationEffect;
    }
    public String getOnco_Protein_Change() {
        return onco_Protein_Change;
    }

    public String getOnco_Protein_Change_LF() {
        return onco_Protein_Change_LF;
    }

    public void setOncokbID() {
       if (this.getOnco_Protein_Change() != null) {
            this.oncokbID = this.getGene() + "-" + this.getOnco_Protein_Change();
       } else{
           String[] getHGVSpArray = this.getHGVSp().split("\\.");
           String ENSP="None";
           if(getHGVSpArray.length > 1) {
               ENSP = getHGVSpArray[2];
           }
           this.oncokbID = this.getGene() + "-" + Configurations.abbreviationtoLetter(ENSP);
       }
    }

    public void setOncogenicity(String oncogenicity) {

        this.oncogenicity = oncogenicity;
    }

    public void setOnco_MutationEffect(String onco_MutationEffect) {
        this.onco_MutationEffect = onco_MutationEffect;
    }

    public void setOnco_Protein_Change(String onco_Protein_Change) {
        this.onco_Protein_Change = onco_Protein_Change;
    }

    public void setOnco_Protein_Change_LF(String onco_Protein_Change_LF) {
        this.onco_Protein_Change_LF = onco_Protein_Change_LF;
    }

    public String getCivicID() {
        return civicID;
    }

    public String getCivic_variant_origin() {
        return civic_variant_origin;
    }

    public String getCivic_variant_url() {
        return civic_variant_url;
    }

    public void setCivicID() {
        if (this.getCivic_variant_url() != null){
            this.civicID = this.getGene();
        }
    }

    public void setCivic_variant_origin(String civic_variant_origin) {
        this.civic_variant_origin = civic_variant_origin;
    }

    public void setCivic_variant_url(String civic_variant_url) {
        this.civic_variant_url = civic_variant_url;
    }

    public String getPmkbID() {
        return pmkbID;
    }

    public String getPmkb_tumor_type() {
        return pmkb_tumor_type;
    }

    public String getPmkb_tissue_type() {
        return pmkb_tissue_type;
    }

    public void setPmkbID() {
        this.pmkbID = this.getOncokbID();
    }

    public void setPmkb_tumor_type(String pmkb_tumor_type) {
        this.pmkb_tumor_type = pmkb_tumor_type;
    }

    public void setPmkb_tissue_type(String pmkb_tissue_type) {
        this.pmkb_tissue_type = pmkb_tissue_type;
    }
}
