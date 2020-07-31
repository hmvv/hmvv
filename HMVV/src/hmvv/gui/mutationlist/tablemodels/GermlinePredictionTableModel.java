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


		columns.add(MutationTableModelColumnGermline.polyphen_column);
		columns.add(MutationTableModelColumnGermline.sift_column);
		columns.add(MutationTableModelColumnGermline.revel_column);
		columns.add(MutationTableModelColumnGermline.cadd_phred_column);
		columns.add(MutationTableModelColumnGermline.canonical_column);
		return columns;
	}
}
