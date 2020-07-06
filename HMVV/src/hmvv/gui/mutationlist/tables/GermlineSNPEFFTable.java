package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.GermlineSNPEFFTableModel;
import hmvv.gui.mutationlist.tablemodels.VEPTableModel;
import hmvv.io.InternetCommands;
import hmvv.main.HMVVFrame;
import hmvv.model.Mutation;

public class GermlineSNPEFFTable extends CommonTableGermline{
        private static final long serialVersionUID = 1L;

        public GermlineSNPEFFTable(HMVVFrame parent, GermlineSNPEFFTableModel model){
            super(parent, model);
        }

        @Override
        protected HMVVTableColumn[] constructCustomColumns(){
            return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 2,3);
        }

        @Override
        protected void handleMousePressed(int column) throws Exception{
           if(column == 4){
               searchGoogleForDNAChange();
           }else if(column == 4){
                searchGoogleForProteinChange();
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
