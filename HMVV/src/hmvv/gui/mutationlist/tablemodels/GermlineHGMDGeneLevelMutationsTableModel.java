package hmvv.gui.mutationlist.tablemodels;

import hmvv.model.MutationGermlineHGMD;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class GermlineHGMDGeneLevelMutationsTableModel extends AbstractTableModel{

    private static final long serialVersionUID = 1L;

    private ArrayList<MutationGermlineHGMD> mutations;
    private ArrayList<GermlineHGMDGeneLevelMutationsTableModelColumn> columns;

    public GermlineHGMDGeneLevelMutationsTableModel(ArrayList<MutationGermlineHGMD> mutations){
        this.mutations = mutations;
        constructColumns();
    }

    private void constructColumns() {
        columns = new ArrayList<GermlineHGMDGeneLevelMutationsTableModelColumn>();

        columns.add(new GermlineHGMDGeneLevelMutationsTableModelColumn("The mutation type",
                "type",
                String.class,
                (MutationGermlineHGMD mutation) -> mutation.getMutation_type()));

        columns.add(new GermlineHGMDGeneLevelMutationsTableModelColumn("The mutation position",
                "pos",
                Integer.class,
                (MutationGermlineHGMD mutation) -> mutation.getPosition()));

        columns.add(new GermlineHGMDGeneLevelMutationsTableModelColumn("The mutation ID",
                "ID",
                String.class,
                (MutationGermlineHGMD mutation) -> mutation.getId()));

        columns.add(new GermlineHGMDGeneLevelMutationsTableModelColumn("The variant",
                "variant",
                String.class,
                (MutationGermlineHGMD mutation) -> mutation.getVariant()));

        columns.add(new GermlineHGMDGeneLevelMutationsTableModelColumn("The AA Change",
                "AA change",
                String.class,
                (MutationGermlineHGMD mutation) -> mutation.getAAchange()));
        columns.add(new GermlineHGMDGeneLevelMutationsTableModelColumn("The disease",
                "disease",
                String.class,
                (MutationGermlineHGMD mutation) -> mutation.getDisease()));
        columns.add(new GermlineHGMDGeneLevelMutationsTableModelColumn("The category",
                "category",
                String.class,
                (MutationGermlineHGMD mutation) -> mutation.getCategory()));
        columns.add(new GermlineHGMDGeneLevelMutationsTableModelColumn("The citation",
                "citation",
                String.class,
                (MutationGermlineHGMD mutation) -> mutation.getPmid_info()));
    }

    public void addMutation(MutationGermlineHGMD mutation){
        mutations.add(mutation);
        fireTableRowsInserted(mutations.size()-1, mutations.size()-1);
    }

    public void updateMutations(ArrayList<MutationGermlineHGMD> mutations){
        this.mutations.clear();
        this.mutations = mutations;
        fireTableDataChanged();
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
        MutationGermlineHGMD mutation = mutations.get(row);
        return columns.get(column).getValue(mutation);
    }

    public MutationGermlineHGMD getMutationAt(int row) {
        MutationGermlineHGMD mutation = mutations.get(row);
        return mutation;
    }

}
