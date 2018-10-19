package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class GnomadTableModel  extends CommonTableModel  {



        private static final long serialVersionUID = 1L;


        public GnomadTableModel (MutationList mutationList){
            super(mutationList);
        }

        protected ArrayList<MutationTableModelColumn> constructColumns(){
            ArrayList<MutationTableModelColumn> columns = new ArrayList<MutationTableModelColumn>();
            columns.add(MutationTableModelColumn.reportedColumn);

            columns.add(MutationTableModelColumn.geneColumn);
            columns.add(MutationTableModelColumn.exonsColumn);

            columns.add(MutationTableModelColumn.chrColumn);
            columns.add(MutationTableModelColumn.posColumn);
            columns.add(MutationTableModelColumn.altColumn);
            columns.add(MutationTableModelColumn.refColumn);



            return columns;
        }
    }

