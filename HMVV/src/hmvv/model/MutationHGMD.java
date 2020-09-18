package hmvv.model;

import hmvv.gui.GUICommonTools;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MutationHGMD {

    private String hgmd_id;
    private String category;
    private String hgvs;
    private String description;
    private String disease;
    private String tag;
    private String pmid1;
    private String pmid1_info;
    private String pmid2;
    private String pmid2_info;

    public MutationHGMD(String hgmd_id, String category, String hgvs, String description, String disease, String tag, String pmid1, String pmid1_info, String pmid2, String pmid2_info) {
        this.hgmd_id = hgmd_id;
        this.category = category;
        this.hgvs = hgvs;
        this.description = description;
        this.disease = disease;
        this.tag = tag;
        this.pmid1 = pmid1;
        this.pmid1_info = pmid1_info;
        this.pmid2 = pmid2;
        this.pmid2_info = pmid2_info;
    }

    public String getHgmd_id() {
        return hgmd_id;
    }

    public void setHgmd_id(String hgmd_id) {
        this.hgmd_id = hgmd_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getHgvs() {
        return hgvs;
    }

    public void setHgvs(String hgvs) {
        this.hgvs = hgvs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPmid1() {
        return pmid1;
    }

    public void setPmid1(String pmid1) {
        this.pmid1 = pmid1;
    }

    public String getPmid1_info() {
        return pmid1_info;
    }

    public void setPmid1_info(String pmid1_info) {
        this.pmid1_info = pmid1_info;
    }

    public String getPmid2() {
        return pmid2;
    }

    public void setPmid2(String pmid2) {
        this.pmid2 = pmid2;
    }

    public String getPmid2_info() {
        return pmid2_info;
    }

    public void setPmid2_info(String pmid2_info) {
        this.pmid2_info = pmid2_info;
    }
}
