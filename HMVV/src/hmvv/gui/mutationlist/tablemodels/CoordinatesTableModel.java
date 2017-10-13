package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class CoordinatesTableModel extends CommonTableModel {
	private static final long serialVersionUID = 1L;


	public CoordinatesTableModel(MutationList mutationList){
		super(mutationList);
	}
	
	protected ArrayList<MutationTableModelColumn> constructColumns(){
		ArrayList<MutationTableModelColumn> columns = new ArrayList<MutationTableModelColumn>();
		columns.add(MutationTableModelColumn.reportedColumn);
		columns.add(MutationTableModelColumn.geneColumn);
		columns.add(MutationTableModelColumn.exonsColumn);
		columns.add(MutationTableModelColumn.HGVScColumn);
		columns.add(MutationTableModelColumn.HGVSpColumn);
		columns.add(MutationTableModelColumn.chrColumn);
		columns.add(MutationTableModelColumn.posColumn);
		columns.add(MutationTableModelColumn.refColumn);
		columns.add(MutationTableModelColumn.altColumn);
		columns.add(MutationTableModelColumn.ConsequenceColumn);
		columns.add(MutationTableModelColumn.SiftColumn);
		columns.add(MutationTableModelColumn.PolyPhenColumn);
		return columns;
	}
}
