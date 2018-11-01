package hmvv.gui.adminFrames;
import hmvv.model.Database;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class DatabaseInformationTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private ArrayList<Database> databases;
    private ArrayList<DatabaseInformationTableModelColumn> columns;

    public DatabaseInformationTableModel(){
        this.databases = new ArrayList<Database>();
        constructColumns();
    }

    private void constructColumns(){
        columns = new ArrayList<DatabaseInformationTableModelColumn>();
        columns.add(new DatabaseInformationTableModelColumn("The database name",
                "Name",
                String.class,
                (Database database) -> database.getName()));

        columns.add(new DatabaseInformationTableModelColumn("The database version",
                "Version",
                String.class,
                (Database database) -> database.getVersion()));

        columns.add(new DatabaseInformationTableModelColumn("The database available date",
                "Release ",
                String.class,
                (Database database) -> database.getRelease()));

        columns.add(new DatabaseInformationTableModelColumn("The database age in HMVV",
                "Usage",
                int.class,
                (Database database) -> database.getAge()));
    }

    public void addDatabase(Database database){
        databases.add(database);
        fireTableRowsInserted(databases.size()-1, databases.size()-1);
    }

    public Database getSample(int row){
        return databases.get(row);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return columns.get(column).columnClass;
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public int getRowCount() {
        return databases.size();
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column).title;
    }

    @Override
    public Object getValueAt(int row, int column) {
        Database pipeline = databases.get(row);
        return columns.get(column).getValue(pipeline);
    }

    public String getColumnDescription(int column){
        return columns.get(column).description;
    }
}
