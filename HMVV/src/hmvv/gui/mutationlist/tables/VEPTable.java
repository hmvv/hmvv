package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.VEPTableModel;
import hmvv.io.InternetCommands;
import hmvv.main.HMVVFrame;
import hmvv.model.MutationSomatic;

public class VEPTable extends CommonTable{
        private static final long serialVersionUID = 1L;

        public VEPTable(HMVVFrame parent, VEPTableModel model){
            super(parent, model);
        }

        @Override
        protected HMVVTableColumn[] constructCustomColumns(){
            return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 7, 8, 9, 10);
        }

        @Override
        protected void handleMousePressed(int column) throws Exception{
           if(column == 7){
               searchGoogleForDNAChange();
           }else if(column == 8){
                searchGoogleForProteinChange();
           }else if(column == 9) {
               searchSNP();
           }else if (column == 10){
               searchPubmed();
           }
        }

    private void searchPubmed() throws Exception{
        MutationSomatic mutation = getSelectedMutation();
        String pubmed = mutation.getPubmed();
        if(!pubmed.equals("") && !pubmed.equals("null")){
            InternetCommands.searchPubmed(pubmed);
        }
    }
}
