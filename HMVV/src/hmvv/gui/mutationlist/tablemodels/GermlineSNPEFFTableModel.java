package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class GermlineSNPEFFTableModel extends CommonTableModel  {

    private static final long serialVersionUID = 1L;


    public GermlineSNPEFFTableModel(MutationList mutationList){
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

        columns.add(MutationTableModelColumn.HGVScColumn);
        columns.add(MutationTableModelColumn.HGVSpColumn);

        columns.add(MutationTableModelColumn.variantClassificationColumn);
        columns.add(MutationTableModelColumn.typeColumn);
        columns.add(MutationTableModelColumn.altFreqColumn);
        columns.add(MutationTableModelColumn.readDPColumn);
        columns.add(MutationTableModelColumn.altReadDPColumn);

        columns.add(MutationTableModelColumn.ConsequenceColumn);


        return columns;
    }
}
