package hmvv.gui.mutationlist.tablemodels;

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

	public MutationTraceModel(MutationList mutationList){
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
			return mutation.getAltFreq();
		case 6:
			return mutation.getReadDP();
		case 7:
			return mutation.getAltReadDP();
		case 8:
			return mutation.getChr();
		case 9:
			return mutation.getPos();
		case 10:
			return mutation.getRef();
		case 11:
			return mutation.getAlt();
		case 12:
			return mutation.getLastName();
		case 13:
			return mutation.getFirstName();
		case 14:
			return mutation.getOrderNumber();
		case 15:
			return mutation.getAssay();
		case 16:
			return mutation.getSampleID();
		default:
			return "UNDEFINED";
		}
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
