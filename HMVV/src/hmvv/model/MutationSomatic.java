package hmvv.model;

import hmvv.main.Configurations;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class MutationSomatic extends MutationCommon {

    //Sample
    private String tumorSource;
    private String tumorPercent;

   //vep
    private String sift;
    private String polyPhen;
    private String dbSNPID;
    private String pubmed;


    //cosmic
    private ArrayList<CosmicIdentifier> pipeline_cosmicIDs;
    private ArrayList<CosmicIdentifier> VEP_cosmicIDs;
    private ArrayList<CosmicIdentifier> linked_cosmicIDs;
    private String uniqueCosmicIDString;
    private boolean cosmicLoadComplete = false;
    
    //G1000
    private Integer altCount;
    private Integer totalCount;
    private Double altGlobalFreq;
    private Double americanFreq;
    private Double eastAsianFreq;
    private Double southAsianFreq;
    private Double afrFreq;
    private Double eurFreq;

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
    private Double VarScanVAF;
    private Double Mutect2VAF;
    private Double freebayesVAF;
    private String pmkb_tumor_type;
    private String pmkb_tissue_type;

    public MutationSomatic(){
        this.pipeline_cosmicIDs = new ArrayList<CosmicIdentifier>();
        this.VEP_cosmicIDs = new ArrayList<CosmicIdentifier>();
        this.linked_cosmicIDs = new ArrayList<CosmicIdentifier>();
        buildUniqueCosmicIDString();
    }

    public String getDbSNPID() {
        return dbSNPID;
    }

    public void setDbSNPID(String dbSNPID) {
        this.dbSNPID = dbSNPID;
    }

    public void addLinkedCosmicIDs(ArrayList<CosmicIdentifier> linkedCosmicIDs){
        this.linked_cosmicIDs.addAll(linkedCosmicIDs);
        buildUniqueCosmicIDString();
    }

    public void addCosmicIDsPipeline(ArrayList<CosmicIdentifier> pipelineCosmicIDs) {
        this.pipeline_cosmicIDs.addAll(pipelineCosmicIDs);
        buildUniqueCosmicIDString();
    }

    public void addVEPCosmicIDs(ArrayList<CosmicIdentifier> vepCosmicIDs) {
        this.VEP_cosmicIDs.addAll(vepCosmicIDs);
        buildUniqueCosmicIDString();
    }

    public ArrayList<CosmicIdentifier> getAllCosmicIDs(){
        ArrayList<CosmicIdentifier> allIDs = new ArrayList<CosmicIdentifier>();
        //allIDs.addAll(linked_cosmicIDs);
        allIDs.addAll(VEP_cosmicIDs);
        allIDs.addAll(pipeline_cosmicIDs);
        
        ArrayList<CosmicIdentifier> all_IDs = new ArrayList<CosmicIdentifier>(new HashSet<CosmicIdentifier>(allIDs));
        all_IDs.sort(new Comparator<CosmicIdentifier>(){
            public int compare(CosmicIdentifier left, CosmicIdentifier right){
                return (left.cosmicID + " " + left.gene).compareTo(right.cosmicID + " " + right.gene);
            }
        });

        return all_IDs;
    }

    private void buildUniqueCosmicIDString(){
        ArrayList<CosmicIdentifier> allIDs = getAllCosmicIDs();
        HashSet<String> uniqueCosmicIDs = new HashSet<String>();
        for (CosmicIdentifier cosmicID : allIDs) {
            uniqueCosmicIDs.add(cosmicID.cosmicID);
        }

        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String cosmicID : uniqueCosmicIDs) {
            sb.append(cosmicID);
            if (i + 1 < uniqueCosmicIDs.size()) {
                sb.append(",");
            }
            i++;
        }
        uniqueCosmicIDString = sb.toString();
    }

    public String cosmicIDsToString() {
        if (cosmicLoadComplete == false){
            return "Loading...";
        }
        return uniqueCosmicIDString;
    }

    public void addCosmicIDLoading() {
        cosmicLoadComplete = false;
    }

    public void removeCosmicIDLoading() {
        cosmicLoadComplete = true;
    }

    public String getPubmed() {
        return pubmed;
    }

    public void setPubmed(String pubmed) {
        this.pubmed = pubmed;
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
    
    public Double getSouthAsianFreq() {
    	return southAsianFreq;
    }

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
    	this.gnomadID = this.getChr().substring(3, this.getChr().length()) + "-" + this.getPos() + "-" + this.getRef() + "-" + this.getAlt();
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
    
    public Double getVarScanVAF() {
        return VarScanVAF;
    }

    public void setVarScanVAF(Double VarScanVAF) {
        this.VarScanVAF = VarScanVAF;
    }

    public Double getMutect2VAF() {
        return Mutect2VAF;
    }

    public void setMutect2VAF(Double Mutect2VAF) {
        this.Mutect2VAF = Mutect2VAF;
    }
    
    public Double getfreebayesVAF() {
        return freebayesVAF;
    }

    public void setfreebayesVAF(Double freebayesVAF) {
        this.freebayesVAF = freebayesVAF;
    }

    public void setPmkb_tumor_type(String pmkb_tumor_type) {
        this.pmkb_tumor_type = pmkb_tumor_type;
    }

    public void setPmkb_tissue_type(String pmkb_tissue_type) {
        this.pmkb_tissue_type = pmkb_tissue_type;
    }
    
    public boolean equals(Object o) {
    	if(o instanceof MutationSomatic) {
    		MutationSomatic m = (MutationSomatic) o;
    		return m.getCoordinate().equals(getCoordinate());
    	}
    	return false;
    }

    public Configurations.MUTATION_TYPE getMutationType(){
        return Configurations.MUTATION_TYPE.SOMATIC;
    }
}
