package hmvv.gui.adminFrames;

import hmvv.gui.mutationlist.tablemodels.HMVVTableModelColumn;
import hmvv.model.Database;

public class DatabaseInformationTableModelColumn extends HMVVTableModelColumn {

    /**
     * The Lambda interface object
     */
    private final SampleGetValueAtOperation operation;

    public DatabaseInformationTableModelColumn(String description, String title, Class<?> columnClass, SampleGetValueAtOperation operation) {
        super(description, title, columnClass);
        this.operation = operation;
    }

    /**
     * Lambda expression function
     */
    public Object getValue(Database database){
        return operation.getValue(database);
    }

    /**
     * Lambda expression interface
     *
     */
    public interface SampleGetValueAtOperation{
        Object getValue(Database database);
    }
}
