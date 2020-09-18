package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class GermlinePredictionTableModel extends GermlineCommonTableModel {
	private static final long serialVersionUID = 1L;


	public GermlinePredictionTableModel(MutationList mutationList){
		super(mutationList);
	}
	
	protected ArrayList<GermlineMutationTableModelColumn> constructColumns(){
		ArrayList<GermlineMutationTableModelColumn> columns = new ArrayList<GermlineMutationTableModelColumn>();
		columns.add(GermlineMutationTableModelColumn.reportedColumn);

		columns.add(GermlineMutationTableModelColumn.geneColumn);
		columns.add(GermlineMutationTableModelColumn.exonsColumn);

		columns.add(GermlineMutationTableModelColumn.chrColumn);
		columns.add(GermlineMutationTableModelColumn.posColumn);
		columns.add(GermlineMutationTableModelColumn.refColumn);
		columns.add(GermlineMutationTableModelColumn.altColumn);

		columns.add(GermlineMutationTableModelColumn.polyphen_column);
		columns.add(GermlineMutationTableModelColumn.sift_column);
		columns.add(GermlineMutationTableModelColumn.revel_column);
		columns.add(GermlineMutationTableModelColumn.cadd_phred_column);
		columns.add(GermlineMutationTableModelColumn.canonical_column);
		return columns;
	}
}
