package hmvv.model;

public class MutationGermlineHGMD {

    private String id;
    private String mutation_type;
    private String position;
    private String variant;
    private String AAchange;
    private String disease;
    private String category;
    private String pmid;
    private String pmid_info;
    private String extra_pmids;

    public MutationGermlineHGMD(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMutation_type() {
        return mutation_type;
    }

    public void setMutation_type(String mutation_type) {
        this.mutation_type = mutation_type;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getAAchange() {
        return AAchange;
    }

    public void setAAchange(String AAchange) {
        this.AAchange = AAchange;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public String getPmid_info() {
        return pmid_info;
    }

    public void setPmid_info(String pmid_info) {
        this.pmid_info = pmid_info;
    }

    public String getExtra_pmids() {
        return extra_pmids;
    }

    public void setExtra_pmids(String extra_pmids) {
        this.extra_pmids = extra_pmids;
    }
}
