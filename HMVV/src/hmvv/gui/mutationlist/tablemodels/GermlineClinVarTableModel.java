package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class GermlineClinVarTableModel extends GermlineCommonTableModel {
	private static final long serialVersionUID = 1L;


	public GermlineClinVarTableModel(MutationList mutationList){
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

		columns.add(GermlineMutationTableModelColumn.clinvarID);
		columns.add(GermlineMutationTableModelColumn.originColumn);
		columns.add(GermlineMutationTableModelColumn.clinicalDisease);
		columns.add(GermlineMutationTableModelColumn.clinicalSignificance);
		columns.add(GermlineMutationTableModelColumn.clinicalConsequence);

		return columns;
	}
}
