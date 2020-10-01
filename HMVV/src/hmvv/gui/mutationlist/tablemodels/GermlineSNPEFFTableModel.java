package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class GermlineSNPEFFTableModel extends GermlineCommonTableModel {

    private static final long serialVersionUID = 1L;


    public GermlineSNPEFFTableModel(MutationList mutationList){
        super(mutationList);
    }

    protected ArrayList<GermlineMutationTableModelColumn> constructColumns(){
        ArrayList<GermlineMutationTableModelColumn> columns = new ArrayList<GermlineMutationTableModelColumn>();
        columns.add(GermlineMutationTableModelColumn.reportedColumn);
        columns.add(GermlineMutationTableModelColumn.otherReportedColumn);
        columns.add(GermlineMutationTableModelColumn.gotoIGVColumn);
        columns.add(GermlineMutationTableModelColumn.igvLoadColumn);
        columns.add(GermlineMutationTableModelColumn.geneColumn);
        columns.add(GermlineMutationTableModelColumn.exonsColumn);
        columns.add(GermlineMutationTableModelColumn.chrColumn);
        columns.add(GermlineMutationTableModelColumn.posColumn);
        columns.add(GermlineMutationTableModelColumn.refColumn);
        columns.add(GermlineMutationTableModelColumn.altColumn);
        columns.add(GermlineMutationTableModelColumn.HGVScColumn);
        columns.add(GermlineMutationTableModelColumn.HGVSpColumn);
        columns.add(GermlineMutationTableModelColumn.variantClassificationColumn);
        columns.add(GermlineMutationTableModelColumn.typeColumn);
        columns.add(GermlineMutationTableModelColumn.altFreqColumn);
        columns.add(GermlineMutationTableModelColumn.readDPColumn);
        columns.add(GermlineMutationTableModelColumn.altReadDPColumn);
        columns.add(GermlineMutationTableModelColumn.ConsequenceColumn);
        columns.add(GermlineMutationTableModelColumn.occurrenceColumn);
        columns.add(GermlineMutationTableModelColumn.annotationColumn);
        return columns;
    }
}
