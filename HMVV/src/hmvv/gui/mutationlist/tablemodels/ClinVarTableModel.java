package hmvv.gui.mutationlist.tablemodels;

import hmvv.model.Mutation;

public class ClinVarTableModel extends CommonTableModel {
	private static final long serialVersionUID = 1L;

	private String[] columns = {
			"reported",
			"gene",
			"exons",
			"HGVSc",
			"HGVSp",
			"origin",
			"clinicalAllele",
			"clinicalSig",
			"clinicalAcc",
			"pubmed"
	};

	public ClinVarTableModel(MutationList mutationList){
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
			return mutation.getOrigin();
		case 6:
			return mutation.getClinicalAllele();
		case 7:
			return mutation.getClinicalSig();
		case 8:
			return mutation.getClinicalAcc();
		case 9:
			return mutation.getPubmed();
		default:
			return "UNDEFINED";
		}
	}	
	
	@Override
	public Class<?> getColumnClass(int columnNo) {
		switch (columnNo) {
		case 0:
			return Boolean.class;
		default:
			return String.class;
		}
	}
	
	@Override
	public String[] getColumnNames() {
		return columns;
	}

}
