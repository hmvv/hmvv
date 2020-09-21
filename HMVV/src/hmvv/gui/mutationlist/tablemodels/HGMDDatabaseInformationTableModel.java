package hmvv.gui.mutationlist.tablemodels;

import hmvv.model.MutationGermlineHGMD;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class HGMDDatabaseInformationTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private ArrayList<MutationGermlineHGMD> entries;
    private ArrayList<HGMDDatabaseInformationTableModelColumn> columns;

    public HGMDDatabaseInformationTableModel(){
        this.entries = new ArrayList<MutationGermlineHGMD>();
        constructColumns();
    }

    private void constructColumns(){
        columns = new ArrayList<HGMDDatabaseInformationTableModelColumn>();
        columns.add(new HGMDDatabaseInformationTableModelColumn("The database feature",
                "Feature",
                String.class,
                (MutationGermlineHGMD entry) -> entry.getId()));

        columns.add(new HGMDDatabaseInformationTableModelColumn("The database value for corresponding feature",
                "Value",
                String.class,
                (MutationGermlineHGMD entry) -> entry.getAAchange()));

    }

    public void addHGMDEntry(MutationGermlineHGMD entry){
        entries.add(entry);
        fireTableRowsInserted(entries.size()-1, entries.size()-1);
    }

    public MutationGermlineHGMD getSample(int row){
        return entries.get(row);
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
        return entries.size();
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column).title;
    }

    @Override
    public Object getValueAt(int row, int column) {
        MutationGermlineHGMD entry = entries.get(row);
        return columns.get(column).getValue(entry);
    }

    public String getColumnDescription(int column){
        return columns.get(column).description;
    }
}

