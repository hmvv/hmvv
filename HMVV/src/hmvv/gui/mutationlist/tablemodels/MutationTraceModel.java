package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class MutationTraceModel extends CommonTableModel {
	private static final long serialVersionUID = 1L;


	public MutationTraceModel(MutationList mutationList){
		super(mutationList);
	}
	
	protected ArrayList<MutationTableModelColumn> constructColumns(){
		ArrayList<MutationTableModelColumn> columns = new ArrayList<MutationTableModelColumn>();
		columns.add(MutationTableModelColumn.reportedColumn);
		columns.add(MutationTableModelColumn.geneColumn);
		columns.add(MutationTableModelColumn.exonsColumn);
		columns.add(MutationTableModelColumn.HGVScColumn);
		columns.add(MutationTableModelColumn.HGVSpColumn);
		columns.add(MutationTableModelColumn.altFreqColumn);
		columns.add(MutationTableModelColumn.readDPColumn);
		columns.add(MutationTableModelColumn.altReadDPColumn);
		columns.add(MutationTableModelColumn.chrColumn);
		columns.add(MutationTableModelColumn.posColumn);
		columns.add(MutationTableModelColumn.refColumn);
		columns.add(MutationTableModelColumn.altColumn);
		columns.add(MutationTableModelColumn.lastNameColumn);
		columns.add(MutationTableModelColumn.firstNameColumn);
		columns.add(MutationTableModelColumn.orderNumberColumn);
		columns.add(MutationTableModelColumn.assayColumn);
		columns.add(MutationTableModelColumn.sampleIDColumn);
		return columns;
	}
}
