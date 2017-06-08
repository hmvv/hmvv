package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

import hmvv.model.Mutation;

public class MutationTraceModel extends CommonTableModel {
	private static final long serialVersionUID = 1L;

	private String[] columns = {
			"reported",
			"gene",
			"exons",
			"HGVSc",
			"HGVSp",
			"altFreq",
			"readDP",
			"altReadDP",
			"chr",
			"pos",
			"ref",
			"alt",
			"lastName",
			"firstName",
			"orderNumber",
			"assay",
			"sampleID"
	};

	public MutationTraceModel(ArrayList<Mutation> mutations){
		super(mutations);
	}
	
	@Override
	public Class<?> getColumnClass(int columnNo) {
		switch (columnNo) {
		case 0:
			return Boolean.class;
		case 6:
			return Integer.class;
		case 7:
			return Integer.class;
		case 5:
			return Double.class;
		default:
			return String.class;
		}
	}
	
	@Override
	public String[] getColumnNames() {
		return columns;
	}
}
