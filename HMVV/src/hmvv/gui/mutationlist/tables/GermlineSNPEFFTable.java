package hmvv.gui.mutationlist.tables;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.tablemodels.GermlineSNPEFFTableModel;
import hmvv.io.IGVConnection;
import hmvv.model.MutationGermline;
import javax.swing.*;

public class GermlineSNPEFFTable extends CommonTableGermline{
        private static final long serialVersionUID = 1L;

        public GermlineSNPEFFTable(JDialog parent, GermlineSNPEFFTableModel model){
            super(parent, model);
        }

        @Override
        protected HMVVTableColumn[] constructCustomColumns(){
            return HMVVTableColumn.getCustomColumnArray(model.getColumnCount(), 2,3,4,10,11,19);
        }

        @Override
        protected void handleMousePressed(int column) throws Exception {
            if (column == 2) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            handleIGVClick();
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(GermlineSNPEFFTable.this, e.getMessage());
                        }
                    }
                }).start();
            } else if (column == 3) {
                handleLoadIGVCheckBoxClick();
            } else if (column == 4) {
                searchGoogleForGene();
            } else if (column == 10) {
                searchGoogleForDNAChange();
            } else if (column == 11) {
                searchGoogleForProteinChange();
            } else if (column == 19) {
                handleAnnotationClick();
            }
        }



    private void handleIGVClick() throws Exception{
        MutationGermline mutation = getSelectedMutation();
        String result = IGVConnection.loadCoordinateIntoIGV(this, mutation.getCoordinate());
        if(result.length() > 0) {
            JOptionPane.showMessageDialog(this, result);
        }
    }

    private void handleLoadIGVCheckBoxClick(){
        MutationGermline mutation = getSelectedMutation();
        if (mutation.isSelected()){
            mutation.setSelected(false);
        }else{
            mutation.setSelected(true);
        }
    }
}
