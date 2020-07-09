package hmvv.model;

import hmvv.main.Configurations;

import java.util.ArrayList;

public class GermlineMutation{

    //common
    private boolean reported;
    private ArrayList<GermlineMutation> otherMutations;
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

   //snpeff
    private String type;
    private VariantPredictionClass variantPredictionClass;
    private Double altFreq;
    private Integer readDP;
    private Integer altReadDP;
    private String consequence;
    private String HGVSc;
    private String HGVSp;

    //transcript
    private String alt_transcript_start;
    private String alt_transcript_end;
    private String alt_transcript_position;

    //Sample
    private String lastName;
    private String firstName;
    private String orderNumber;
    private String assay;
    private Integer sampleID;

    //ClinVar
    private String clinvarID;
    private String clinicaldisease;
    private String clinicalsignificance;
    private String clinicalconsequence;
    private String clinicalorigin;

    //gnomad
    private Double gnomad_allfreq;
    private Double gnomad_allfreq_afr;
    private Double gnomad_allfreq_amr;
    private Double gnomad_allfreq_asj;
    private Double gnomad_allfreq_eas;
    private Double gnomad_allfreq_fin;
    private Double gnomad_allfreq_nfe;
    private Double gnomad_allfreq_sas;
    private Double gnomad_allfreq_oth;
    private Double gnomad_allfreq_male;
    private Double gnomad_allfreq_female;

    //cardiac_atlas
    private String cds_variant;
    private String protein_variant;
    private String variant_type;


    public String getClinvarID() {
        return clinvarID;
    }

    public void setClinvarID(String clinvarID) {
        this.clinvarID = clinvarID;
    }

    public String getClinicaldisease() {
        return clinicaldisease;
    }

    public void setClinicaldisease(String clinicaldisease) {
        this.clinicaldisease = clinicaldisease;
    }

    public String getClinicalsignificance() {
        return clinicalsignificance;
    }

    public void setClinicalsignificance(String clinicalsignificance) {
        this.clinicalsignificance = clinicalsignificance;
    }

    public String getClinicalconsequence() {
        return clinicalconsequence;
    }

    public void setClinicalconsequence(String clinicalconsequence) {
        this.clinicalconsequence = clinicalconsequence;
    }

    public String getClinicalorigin() {
        return clinicalorigin;
    }

    public void setClinicalorigin(String clinicalorigin) {
        this.clinicalorigin = clinicalorigin;
    }


    public Double getGnomad_allfreq() {
        return gnomad_allfreq;
    }

    public void setGnomad_allfreq(Double gnomad_allfreq) {
        this.gnomad_allfreq = gnomad_allfreq;
    }


    public GermlineMutation() {
    	otherMutations = new ArrayList<GermlineMutation>();
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

    public void addOtherMutation(GermlineMutation otherMutation) {
    	this.otherMutations.add(otherMutation);
    }
    
    public String getOtherMutationsString() {
    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i < otherMutations.size(); i++) {
    		GermlineMutation otherMutation = otherMutations.get(i);
    		if(i != 0) {
    			sb.append(", ");
    		}
    		sb.append(otherMutation.sampleID + "");
    		if(otherMutation.reported) {
    			sb.append("(R)");
    		}
    	}
    	return sb.toString();
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

    public String getAlt_transcript_start() {
        return alt_transcript_start;
    }

    public void setAlt_transcript_start(String alt_transcript_start) {
        this.alt_transcript_start = alt_transcript_start;
    }

    public String getAlt_transcript_end() {
        return alt_transcript_end;
    }

    public void setAlt_transcript_end(String alt_transcript_end) {
        this.alt_transcript_end = alt_transcript_end;
    }

    public String getAlt_transcript_position() {
        return alt_transcript_position;
    }

    public void setAlt_transcript_position(String alt_transcript_position) {
        this.alt_transcript_position = alt_transcript_position;
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

    public Double getGnomad_allfreq_afr() {
        return gnomad_allfreq_afr;
    }

    public void setGnomad_allfreq_afr(Double gnomad_allfreq_afr) {
        this.gnomad_allfreq_afr = gnomad_allfreq_afr;
    }

    public Double getGnomad_allfreq_amr() {
        return gnomad_allfreq_amr;
    }

    public void setGnomad_allfreq_amr(Double gnomad_allfreq_amr) {
        this.gnomad_allfreq_amr = gnomad_allfreq_amr;
    }

    public Double getGnomad_allfreq_asj() {
        return gnomad_allfreq_asj;
    }

    public void setGnomad_allfreq_asj(Double gnomad_allfreq_asj) {
        this.gnomad_allfreq_asj = gnomad_allfreq_asj;
    }

    public Double getGnomad_allfreq_eas() {
        return gnomad_allfreq_eas;
    }

    public void setGnomad_allfreq_eas(Double gnomad_allfreq_eas) {
        this.gnomad_allfreq_eas = gnomad_allfreq_eas;
    }

    public Double getGnomad_allfreq_fin() {
        return gnomad_allfreq_fin;
    }

    public void setGnomad_allfreq_fin(Double gnomad_allfreq_fin) {
        this.gnomad_allfreq_fin = gnomad_allfreq_fin;
    }

    public Double getGnomad_allfreq_nfe() {
        return gnomad_allfreq_nfe;
    }

    public void setGnomad_allfreq_nfe(Double gnomad_allfreq_nfe) {
        this.gnomad_allfreq_nfe = gnomad_allfreq_nfe;
    }

    public Double getGnomad_allfreq_sas() {
        return gnomad_allfreq_sas;
    }

    public void setGnomad_allfreq_sas(Double gnomad_allfreq_sas) {
        this.gnomad_allfreq_sas = gnomad_allfreq_sas;
    }

    public Double getGnomad_allfreq_oth() {
        return gnomad_allfreq_oth;
    }

    public void setGnomad_allfreq_oth(Double gnomad_allfreq_oth) {
        this.gnomad_allfreq_oth = gnomad_allfreq_oth;
    }

    public Double getGnomad_allfreq_male() {
        return gnomad_allfreq_male;
    }

    public void setGnomad_allfreq_male(Double gnomad_allfreq_male) {
        this.gnomad_allfreq_male = gnomad_allfreq_male;
    }

    public Double getGnomad_allfreq_female() {
        return gnomad_allfreq_female;
    }

    public void setGnomad_allfreq_female(Double gnomad_allfreq_female) {
        this.gnomad_allfreq_female = gnomad_allfreq_female;
    }

    public String getCds_variant() {
        return cds_variant;
    }

    public void setCds_variant(String cds_variant) {
        this.cds_variant = cds_variant;
    }

    public String getProtein_variant() {
        return protein_variant;
    }

    public void setProtein_variant(String protein_variant) {
        this.protein_variant = protein_variant;
    }

    public String getVariant_type() {
        return variant_type;
    }

    public void setVariant_type(String variant_type) {
        this.variant_type = variant_type;
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
    	return annotationHistory.get(annotationHistory.size() - 1);
    }


    public boolean equals(Object o) {
    	if(o instanceof GermlineMutation) {
    		GermlineMutation m = (GermlineMutation) o;
    		return m.getCoordinate().equals(getCoordinate());
    	}
    	return false;
    }
}
