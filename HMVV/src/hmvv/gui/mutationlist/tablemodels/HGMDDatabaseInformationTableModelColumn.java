package hmvv.gui.mutationlist.tablemodels;

import hmvv.model.MutationGermlineHGMD;

public class HGMDDatabaseInformationTableModelColumn extends HMVVTableModelColumn {

    /**
     * The Lambda interface object
     */
    private final SampleGetValueAtOperation operation;

    public HGMDDatabaseInformationTableModelColumn(String description, String title, Class<?> columnClass, SampleGetValueAtOperation operation) {
        super(description, title, columnClass);
        this.operation = operation;
    }

    /**
     * Lambda expression function
     */
    public Object getValue(MutationGermlineHGMD database){
        return operation.getValue(database);
    }

    /**
     * Lambda expression interface
     *
     */
    public interface SampleGetValueAtOperation{
        Object getValue(MutationGermlineHGMD database);
    }
}
