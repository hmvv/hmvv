package hmvv.gui.mutationlist.tablemodels;

import hmvv.model.MutationGermlineHGMDGeneLevel;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class GermlineHGMDGeneLevelSummaryTableModel extends AbstractTableModel{

    private static final long serialVersionUID = 1L;

    private ArrayList<MutationGermlineHGMDGeneLevel> mutations;
    private ArrayList<GermlineHGMDGeneLevelSummaryTableModelColumn> columns;

    public GermlineHGMDGeneLevelSummaryTableModel(ArrayList<MutationGermlineHGMDGeneLevel> mutations){
        this.mutations = mutations;
        constructColumns();
    }

    private void constructColumns() {
        columns = new ArrayList<GermlineHGMDGeneLevelSummaryTableModelColumn>();
        columns.add(new GermlineHGMDGeneLevelSummaryTableModelColumn("The mutation category",
                "Category",
                String.class,
                (MutationGermlineHGMDGeneLevel mutation) -> mutation.getCategory()));

        columns.add(new GermlineHGMDGeneLevelSummaryTableModelColumn("The total mutations",
                "variant",
                Integer.class,
                (MutationGermlineHGMDGeneLevel mutation) -> mutation.getTotal()));

    }

    public void addMutation(MutationGermlineHGMDGeneLevel mutation){
        mutations.add(mutation);
        fireTableRowsInserted(mutations.size()-1, mutations.size()-1);
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
        return mutations.size();
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column).title;
    }

    @Override
    public Object getValueAt(int row, int column) {
        MutationGermlineHGMDGeneLevel mutation = mutations.get(row);
        return columns.get(column).getValue(mutation);
    }

}
