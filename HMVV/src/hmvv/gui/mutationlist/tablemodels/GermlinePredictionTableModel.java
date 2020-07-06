package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class GermlinePredictionTableModel extends CommonTableModelGermline {
	private static final long serialVersionUID = 1L;


	public GermlinePredictionTableModel(MutationListGermline mutationList){
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

		return columns;
	}
}
