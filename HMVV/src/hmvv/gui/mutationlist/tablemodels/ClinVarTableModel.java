package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

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

	public ClinVarTableModel(ArrayList<Mutation> mutations){
		super(mutations);
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
