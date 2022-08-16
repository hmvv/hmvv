package hmvv.model;
import hmvv.main.Configurations;
import java.util.ArrayList;

public class MutationCommon {

    //common
    private boolean reported;
    private ArrayList<MutationCommon> otherMutations;
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

    //Sample
    private String lastName;
    private String firstName;
    private String orderNumber;
    private Assay assay;
    private Integer sampleID;

    //annotation basic
    private String type;
    private VariantPredictionClass variantPredictionClass;
    private Double altFreq;
    private Integer readDP;
    private Integer altReadDP;
    private String consequence;
    private String HGVSc;
    private String HGVSp;

    //ClinVar
    private String clinvarID;
    private String clinicaldisease;
    private String clinicalsignificance;
    private String clinicalconsequence;
    private String clinicalorigin;


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

    public MutationCommon() {
    	otherMutations = new ArrayList<MutationCommon>();
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

    public void addOtherMutation(MutationCommon otherMutation) {
    	this.otherMutations.add(otherMutation);
    }
    
    public String getOtherMutationsString() {
    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i < otherMutations.size(); i++) {
    		MutationCommon otherMutation = otherMutations.get(i);
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

    public Assay getAssay() {
        return assay;
    }

    public void setAssay(Assay assay) {
        this.assay = assay;
    }

    public Integer getSampleID() {
        return sampleID;
    }

    public void setSampleID(Integer sampleID) {
        this.sampleID = sampleID;
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
    	if(o instanceof MutationCommon) {
    		MutationCommon m = (MutationCommon) o;
    		return m.getCoordinate().equals(getCoordinate());
    	}
    	return false;
    }

    public Configurations.MUTATION_TYPE getMutationType(){
        return Configurations.MUTATION_TYPE.COMMON;
    }

}
