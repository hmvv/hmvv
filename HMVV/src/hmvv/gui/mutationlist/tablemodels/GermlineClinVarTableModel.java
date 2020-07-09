package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class GermlineClinVarTableModel extends CommonTableModelGermline {
	private static final long serialVersionUID = 1L;


	public GermlineClinVarTableModel(MutationListGermline mutationList){
		super(mutationList);
	}
	
	protected ArrayList<MutationTableModelColumnGermline> constructColumns(){
		ArrayList<MutationTableModelColumnGermline> columns = new ArrayList<MutationTableModelColumnGermline>();
		columns.add(MutationTableModelColumnGermline.reportedColumn);

		columns.add(MutationTableModelColumnGermline.geneColumn);
		columns.add(MutationTableModelColumnGermline.exonsColumn);

		columns.add(MutationTableModelColumnGermline.chrColumn);
		columns.add(MutationTableModelColumnGermline.posColumn);
		columns.add(MutationTableModelColumnGermline.refColumn);
		columns.add(MutationTableModelColumnGermline.altColumn);

		columns.add(MutationTableModelColumnGermline.clinvarID);

		columns.add(MutationTableModelColumnGermline.originColumn);
		columns.add(MutationTableModelColumnGermline.clinicalDisease);
		columns.add(MutationTableModelColumnGermline.clinicalSignificance);
		columns.add(MutationTableModelColumnGermline.clinicalConsequence);

		return columns;
	}
}
