package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;


import hmvv.model.Mutation;

public class BasicTableModel extends CommonTableModel {
	private static final long serialVersionUID = 1L;

	private String[] columns = {
			"reported",
			"gene",
			"exons",
			"HGVSc",
			"HGVSp",
			"dbSNPID",
			"cosmicID",
			"type",
			"genotype",
			"altFreq",
			"readDP",
			"altReadDP",
			"occurrence",
			"annotation"
	};

	public BasicTableModel(ArrayList<Mutation> mutations){
		super(mutations);
	}
	
	public void updateAnnotationText(String text, int row){
		Mutation mutation = getMutation(row);
		mutation.addData("annotation", text);
		fireTableRowsUpdated(row, row);
	}
	
	@Override
	public final void setValueAt(Object object, int row, int column){
		Mutation mutation = getMutation(row);
		mutation.addData(columns[column], object);
		fireTableRowsUpdated(row, row);
	}
	
	@Override
	public Class<?> getColumnClass(int columnNo) {
		switch (columnNo) {
		case 0:
			return Boolean.class;
		case 9:
			return Double.class;
		case 10:
			return Integer.class;
		case 11:
			return Integer.class;
		case 12:
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
