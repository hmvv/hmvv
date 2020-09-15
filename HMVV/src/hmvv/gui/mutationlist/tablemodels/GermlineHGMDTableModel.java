package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class GermlineHGMDTableModel extends GermlineCommonTableModel {
	private static final long serialVersionUID = 1L;


	public GermlineHGMDTableModel(MutationList mutationList){
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


		columns.add(GermlineMutationTableModelColumn.HGMDID_column);

		return columns;
	}
}
