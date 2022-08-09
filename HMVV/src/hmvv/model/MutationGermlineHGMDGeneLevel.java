package hmvv.model;

public class MutationGermlineHGMDGeneLevel {

    private String category;
    private Integer total;

    public MutationGermlineHGMDGeneLevel(String category, Integer total) {
        this.category = category;
        this.total = total;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
