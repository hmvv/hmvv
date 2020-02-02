package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.CivicTableModel;
import hmvv.io.InternetCommands;
import hmvv.main.HMVVFrame;
import hmvv.model.Mutation;

public class CivicTable extends CommonTable {
    private static final long serialVersionUID = 1L;

    public CivicTable(HMVVFrame parent, CivicTableModel model) {
        super(parent, model);
    }

    @Override
    protected HMVVTableColumn[] constructCustomColumns() {
        return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 7);
    }

    @Override
    protected void handleMousePressed(int column) throws Exception {
        if (column == 7) {
            searchCivic();
        }
    }

    private void searchCivic() throws Exception {
        Mutation mutation = getSelectedMutation();
        String civic_url = mutation.getCivic_variant_url();
        if (!civic_url.equals("") && !civic_url.equals("null")) {
            InternetCommands.searchCivic(civic_url);
        }
    }
}