package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class GermlineTranscriptTableModel extends GermlineCommonTableModel {
	private static final long serialVersionUID = 1L;


	public GermlineTranscriptTableModel(MutationList mutationList){
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

		columns.add(GermlineMutationTableModelColumn.HGVScColumn);

		columns.add(GermlineMutationTableModelColumn.TRANSCRIPT_STRAND);
		columns.add(GermlineMutationTableModelColumn.ALT_TRANSCRIPT_START);
		columns.add(GermlineMutationTableModelColumn.ALT_TRANSCRIPT_END);
		columns.add(GermlineMutationTableModelColumn.ALT_TRANSCRIPT_POSITION);
		return columns;
	}
}
