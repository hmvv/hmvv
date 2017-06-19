package hmvv.gui.mutationlist.tablemodels;

import hmvv.model.Mutation;

public class SampleTableModel extends CommonTableModel {
	private static final long serialVersionUID = 1L;

	private String[] columns = {
			"reported",
			"gene",
			"exons",
			"HGVSc",
			"HGVSp",
			"lastName",
			"firstName",
			"orderNumber",
			"assay",
			"sampleID"
	};

	public SampleTableModel(MutationList mutationList){
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
			return mutation.getLastName();
		case 6:
			return mutation.getFirstName();
		case 7:
			return mutation.getOrderNumber();
		case 8:
			return mutation.getAssay();
		case 9:
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
		case 9:
			return Integer.class;
		default:
			return String.class;
		}
	}
	
	@Override
	public String[] getColumnNames() {
		return columns;
	}
}
