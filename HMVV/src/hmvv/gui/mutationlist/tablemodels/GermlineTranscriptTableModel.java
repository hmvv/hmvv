package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class GermlineTranscriptTableModel extends CommonTableModelGermline {
	private static final long serialVersionUID = 1L;


	public GermlineTranscriptTableModel(MutationListGermline mutationList){
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

		columns.add(MutationTableModelColumnGermline.HGVScColumn);

		columns.add(MutationTableModelColumnGermline.ALT_TRANSCRIPT_START);
		columns.add(MutationTableModelColumnGermline.ALT_TRANSCRIPT_END);
		columns.add(MutationTableModelColumnGermline.ALT_TRANSCRIPT_POSITION);
		return columns;
	}
}
