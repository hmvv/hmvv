package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class G1000TableModel extends CommonTableModel {
	private static final long serialVersionUID = 1L;

	public G1000TableModel(MutationList mutationList){
		super(mutationList);
	}
	
	protected ArrayList<MutationTableModelColumn> constructColumns(){
		ArrayList<MutationTableModelColumn> columns = new ArrayList<MutationTableModelColumn>();
		columns.add(MutationTableModelColumn.reportedColumn);

		columns.add(MutationTableModelColumn.geneColumn);
		columns.add(MutationTableModelColumn.exonsColumn);

		columns.add(MutationTableModelColumn.chrColumn);
		columns.add(MutationTableModelColumn.posColumn);
		columns.add(MutationTableModelColumn.refColumn);
		columns.add(MutationTableModelColumn.altColumn);

		columns.add(MutationTableModelColumn.altCountColumn);
		columns.add(MutationTableModelColumn.totalCountColumn);
		columns.add(MutationTableModelColumn.altGlobalFreqColumn);
		columns.add(MutationTableModelColumn.americanFreqColumn);
		columns.add(MutationTableModelColumn.asianFreqColumn);
		columns.add(MutationTableModelColumn.afrFreqColumn);
		columns.add(MutationTableModelColumn.eurFreqColumn);
		return columns;
	}
}
