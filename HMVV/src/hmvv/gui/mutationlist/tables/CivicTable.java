package hmvv.gui.mutationlist.tables;

import javax.swing.JDialog;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.CivicTableModel;
import hmvv.io.InternetCommands;
import hmvv.model.MutationSomatic;

public class CivicTable extends CommonTable {
    private static final long serialVersionUID = 1L;

    public CivicTable(JDialog parent, CivicTableModel model) {
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
        MutationSomatic mutation = getSelectedMutation();
        String civic_url = mutation.getCivic_variant_url();
        if (!civic_url.equals("") && !civic_url.equals("null")) {
            InternetCommands.searchCivic(civic_url);
        }
    }
}