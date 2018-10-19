package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.MutationListFrame;
import hmvv.gui.mutationlist.tablemodels.VEPTableModel;
import hmvv.io.InternetCommands;
import hmvv.model.Mutation;

public class VEPTable extends CommonTable{
        private static final long serialVersionUID = 1L;

        public VEPTable(MutationListFrame parent, VEPTableModel model){
            super(parent, model);
        }

        @Override
        protected HMVVTableColumn[] constructCustomColumns(){
            return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 7,8,9,10);
        }

        @Override
        protected void handleMouseClick(int column) throws Exception{
           if(column == 7){
                searchGoogleForProteinChange();
           }else if(column == 8){
                searchGoogleForProteinChange();
           }else if(column == 9) {
               searchSNP();
           }else if (column ==10){
               searchPubmed();
           }
        }

    private void searchPubmed(){
        Mutation mutation = getSelectedMutation();
        String pubmed = mutation.getPubmed();
        if(!pubmed.equals("") && !pubmed.equals("null")){
            InternetCommands.searchPubmed(pubmed);
        }
    }
}
