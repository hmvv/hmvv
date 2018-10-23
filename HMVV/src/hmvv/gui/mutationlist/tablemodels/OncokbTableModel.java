package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class OncokbTableModel  extends CommonTableModel  {

        private static final long serialVersionUID = 1L;


        public OncokbTableModel(MutationList mutationList){
            super(mutationList);
        }

        protected ArrayList<MutationTableModelColumn> constructColumns(){
            ArrayList<MutationTableModelColumn> columns = new ArrayList<MutationTableModelColumn>();
            columns.add(MutationTableModelColumn.reportedColumn);

            columns.add(MutationTableModelColumn.geneColumn);
            columns.add(MutationTableModelColumn.exonsColumn);

            columns.add(MutationTableModelColumn.chrColumn);
            columns.add(MutationTableModelColumn.posColumn);
            columns.add(MutationTableModelColumn.refColumn);
            columns.add(MutationTableModelColumn.altColumn);

            columns.add(MutationTableModelColumn.oncokbID);
            columns.add(MutationTableModelColumn.oncogenicityColumn);
            columns.add(MutationTableModelColumn.oncoMutationEffectColumn);

            return columns;
        }
    }
