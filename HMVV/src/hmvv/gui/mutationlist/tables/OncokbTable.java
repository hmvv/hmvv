package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.MutationListFrame;
import hmvv.gui.mutationlist.tablemodels.OncokbTableModel;
import hmvv.io.InternetCommands;
import hmvv.model.Mutation;

public class OncokbTable extends CommonTable {
    private static final long serialVersionUID = 1L;

    public OncokbTable(MutationListFrame parent, OncokbTableModel model) {
        super(parent, model);
    }

    @Override
    protected HMVVTableColumn[] constructCustomColumns() {
        return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 7);
    }

    @Override
    protected void handleMousePressed(int column) throws Exception {
        if (column == 7) {
            searchOncokb();
        }
    }

    private void searchOncokb() throws Exception {
        Mutation mutation = getSelectedMutation();
        String onco_id = mutation.getOncokbID();
        if (!onco_id.equals("") && !onco_id.equals("null")) {
            InternetCommands.searchOncokb(onco_id);
        }
    }
}

