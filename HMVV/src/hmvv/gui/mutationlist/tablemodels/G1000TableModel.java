package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

import hmvv.model.Mutation;

public class G1000TableModel extends CommonTableModel {
	private static final long serialVersionUID = 1L;

	private String[] columns = {
			"reported",
			"gene",
			"exons",
			"HGVSc",
			"HGVSp",
			"altCount",
			"totalCount",
			"altGlobalFreq",
			"americanFreq",
			"asianFreq",
			"altFreq",
			"eurFreq"
	};

	public G1000TableModel(ArrayList<Mutation> mutations){
		super(mutations);
	}
	
	@Override
	public Class<?> getColumnClass(int columnNo) {
		switch (columnNo) {
		case 0:
			return Boolean.class;
		case 5:
			return Integer.class;
		case 6:
			return Integer.class;
		case 7:
			return Double.class;
		case 8:
			return Double.class;
		case 9:
			return Double.class;
		case 10:
			return Double.class;
		case 11:
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
