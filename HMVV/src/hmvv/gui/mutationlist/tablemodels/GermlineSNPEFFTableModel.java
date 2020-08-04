package hmvv.gui.mutationlist.tablemodels;

import hmvv.gui.mutationlist.tables.CommonTableGermline;

import java.util.ArrayList;

public class GermlineSNPEFFTableModel extends CommonTableModelGermline {

    private static final long serialVersionUID = 1L;


    public GermlineSNPEFFTableModel(MutationListGermline mutationList){
        super(mutationList);
    }

    protected ArrayList<MutationTableModelColumnGermline> constructColumns(){
        ArrayList<MutationTableModelColumnGermline> columns = new ArrayList<MutationTableModelColumnGermline>();
        columns.add(MutationTableModelColumnGermline.reportedColumn);
        columns.add(MutationTableModelColumnGermline.otherReportedColumn);
        columns.add(MutationTableModelColumnGermline.gotoIGVColumn);
        columns.add(MutationTableModelColumnGermline.igvLoadColumn);

        columns.add(MutationTableModelColumnGermline.geneColumn);
        columns.add(MutationTableModelColumnGermline.exonsColumn);

        columns.add(MutationTableModelColumnGermline.chrColumn);
        columns.add(MutationTableModelColumnGermline.posColumn);
        columns.add(MutationTableModelColumnGermline.refColumn);
        columns.add(MutationTableModelColumnGermline.altColumn);

        columns.add(MutationTableModelColumnGermline.HGVScColumn);
        columns.add(MutationTableModelColumnGermline.HGVSpColumn);

        columns.add(MutationTableModelColumnGermline.variantClassificationColumn);
        columns.add(MutationTableModelColumnGermline.typeColumn);
        columns.add(MutationTableModelColumnGermline.altFreqColumn);
        columns.add(MutationTableModelColumnGermline.readDPColumn);
        columns.add(MutationTableModelColumnGermline.altReadDPColumn);

        columns.add(MutationTableModelColumnGermline.ConsequenceColumn);
        columns.add(MutationTableModelColumnGermline.occurrenceColumn);
        columns.add(MutationTableModelColumnGermline.annotationColumn);


        return columns;
    }
}
