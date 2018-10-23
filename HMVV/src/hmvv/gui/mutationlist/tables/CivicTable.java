package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.MutationListFrame;
import hmvv.gui.mutationlist.tablemodels.CivicTableModel;
import hmvv.io.InternetCommands;
import hmvv.model.Mutation;

public class CivicTable extends CommonTable {
    private static final long serialVersionUID = 1L;

    public CivicTable(MutationListFrame parent, CivicTableModel model) {
        super(parent, model);
    }

    @Override
    protected HMVVTableColumn[] constructCustomColumns() {
        return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 7);
    }

    @Override
    protected void handleMouseClick(int column) throws Exception {
        if (column == 7) {
            searchCivic();
        }
    }

    private void searchCivic() {
        Mutation mutation = getSelectedMutation();
        String civic_url = mutation.getCivic_variant_url();
        if (!civic_url.equals("") && !civic_url.equals("null")) {
            InternetCommands.searchCivic(civic_url);
        }
    }
}