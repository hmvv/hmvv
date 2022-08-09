package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.PmkbTableModel;
import hmvv.io.InternetCommands;
import hmvv.main.HMVVFrame;
import hmvv.model.MutationSomatic;

public class PmkbTable extends CommonTable {
    private static final long serialVersionUID = 1L;

    public PmkbTable(HMVVFrame parent, PmkbTableModel model) {
        super(parent, model);
    }

    @Override
    protected HMVVTableColumn[] constructCustomColumns() {
        return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 7);
    }

    @Override
    protected void handleMousePressed(int column) throws Exception {
        if (column == 7) {
            searchPmkb();
        }
    }

    private void searchPmkb() throws Exception {
        MutationSomatic mutation = getSelectedMutation();
        String onco_id = mutation.getPmkbID();
        if (!onco_id.equals("") && !onco_id.equals("null")) {
            InternetCommands.searchPmkb(onco_id);
        }
    }
}

