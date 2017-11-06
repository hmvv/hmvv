package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class SampleTableModel extends CommonTableModel {
	private static final long serialVersionUID = 1L;
	
	public SampleTableModel(MutationList mutationList){
		super(mutationList);
	}
	
	protected ArrayList<MutationTableModelColumn> constructColumns(){
		ArrayList<MutationTableModelColumn> columns = new ArrayList<MutationTableModelColumn>();
		columns.add(MutationTableModelColumn.reportedColumn);
		columns.add(MutationTableModelColumn.geneColumn);
		columns.add(MutationTableModelColumn.exonsColumn);
		columns.add(MutationTableModelColumn.HGVScColumn);
		columns.add(MutationTableModelColumn.HGVSpColumn);
		columns.add(MutationTableModelColumn.lastNameColumn);
		columns.add(MutationTableModelColumn.firstNameColumn);
		columns.add(MutationTableModelColumn.orderNumberColumn);
		columns.add(MutationTableModelColumn.assayColumn);
		columns.add(MutationTableModelColumn.IDColumn);
		columns.add(MutationTableModelColumn.tumorSourceColumn);
		columns.add(MutationTableModelColumn.tumorPercentColumn);
		return columns;
	}
}
