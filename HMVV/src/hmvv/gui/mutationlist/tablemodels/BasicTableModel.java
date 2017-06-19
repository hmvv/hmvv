package hmvv.gui.mutationlist.tablemodels;

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

	public BasicTableModel(MutationList mutationList){
		super(mutationList);
	}
	
	@Override
	public final void setValueAt(Object object, int row, int column){
		super.setValueAt(object, row, column);
		if(column == 6){
			Mutation mutation = getMutation(row);
			mutation.setCosmicID((String)object);
			fireTableCellUpdated(row, column);
		}else if(column == 12){
			Mutation mutation = getMutation(row);
			mutation.setOccurrence((Integer)object);
			fireTableCellUpdated(row, column);
		}
	}
	
	public void updateModel(int row, String cosmicID, int occurenceCount){
		setValueAt(cosmicID, row, 6);
		setValueAt(occurenceCount, row, 12);
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
			return mutation.getDbSNPID();
		case 6:
			return mutation.getCosmicID();
		case 7:
			return mutation.getType();
		case 8:
			return mutation.getGenotype();
		case 9:
			return mutation.getAltFreq();
		case 10:
			return mutation.getReadDP();
		case 11:
			return mutation.getAltReadDP();
		case 12:
			return mutation.getOccurrence();
		case 13:
			return mutation.getAnnotation();
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
