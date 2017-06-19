package hmvv.gui.mutationlist.tablemodels;

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
			"afrFreq",
			"eurFreq"
	};

	public G1000TableModel(MutationList mutationList){
		super(mutationList);
	}
	
	@Override
	public final Object getValueAt(int row, int column) {
		Mutation mutation = getMutation(row);
		switch(column){
		case 0:
			return mutation.isReported();
		case 1:
			return mutation.getGene();
		case 2:
			return mutation.getExons();
		case 3:
			return mutation.getHGVSc();
		case 4:
			return mutation.getHGVSp();
		case 5:
			return mutation.getAltCount();
		case 6:
			return mutation.getTotalCount();
		case 7:
			return mutation.getAltGlobalFreq();
		case 8:
			return mutation.getAmericanFreq();
		case 9:
			return mutation.getAsianFreq();
		case 10:
			return mutation.getAfricanFreq();
		case 11:
			return mutation.getEurFreq();
		default:
			return "UNDEFINED";
		}
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
