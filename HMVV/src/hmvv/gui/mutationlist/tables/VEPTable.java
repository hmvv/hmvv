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
            return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 15, 16, 17, 18);
        }

        @Override
        protected void handleMouseClick(int column) throws Exception{
           if(column == 15){
                searchGoogleForProteinChange();
           }else if(column == 16){
                searchGoogleForProteinChange();
           }else if(column == 17) {
               searchSNP();
           }else if (column == 18){
               searchPubmed();
           }
        }

    private void searchPubmed() throws Exception{
        Mutation mutation = getSelectedMutation();
        String pubmed = mutation.getPubmed();
        if(!pubmed.equals("") && !pubmed.equals("null")){
            InternetCommands.searchPubmed(pubmed);
        }
    }
}
