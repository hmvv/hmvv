package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class ClinVarTableModel extends CommonTableModel {
	private static final long serialVersionUID = 1L;

	public ClinVarTableModel(MutationList mutationList){
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

		columns.add(MutationTableModelColumn.originColumn);
		columns.add(MutationTableModelColumn.clinicalAlleleColumn);
		columns.add(MutationTableModelColumn.clinicalSigColumn);
		columns.add(MutationTableModelColumn.clinicalAccColumn);

		return columns;
	}
}
