package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.MutationListFrame;
import hmvv.gui.mutationlist.tablemodels.GnomadTableModel;

public class GnomadTable extends CommonTable{
        private static final long serialVersionUID = 1L;

        public GnomadTable (MutationListFrame parent, GnomadTableModel model){
            super(parent, model);
        }

        @Override
        protected HMVVTableColumn[] constructCustomColumns(){
            return HMVVTableColumn.getCustomColumnArray(model.getColumnCount());
        }

        @Override
        protected void handleMouseClick(int column) throws Exception{

        }
}
