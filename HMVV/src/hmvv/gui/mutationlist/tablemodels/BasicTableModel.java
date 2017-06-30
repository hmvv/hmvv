package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

import hmvv.model.Mutation;

public class BasicTableModel extends CommonTableModel {
	private static final long serialVersionUID = 1L;

	public BasicTableModel(MutationList mutationList){
		super(mutationList);
	}
	
	protected ArrayList<MutationTableModelColumn> constructColumns(){
		ArrayList<MutationTableModelColumn> columns = new ArrayList<MutationTableModelColumn>();
		columns.add(MutationTableModelColumn.reportedColumn);
		columns.add(MutationTableModelColumn.geneColumn);
		columns.add(MutationTableModelColumn.exonsColumn);
		columns.add(MutationTableModelColumn.HGVScColumn);
		columns.add(MutationTableModelColumn.HGVSpColumn);
		columns.add(MutationTableModelColumn.dbSNPIDColumn);
		columns.add(MutationTableModelColumn.cosmicIDColumn);
		columns.add(MutationTableModelColumn.typeColumn);
		columns.add(MutationTableModelColumn.genotypeColumn);
		columns.add(MutationTableModelColumn.altFreqColumn);
		columns.add(MutationTableModelColumn.readDPColumn);
		columns.add(MutationTableModelColumn.altReadDPColumn);
		columns.add(MutationTableModelColumn.occurrenceColumn);
		columns.add(MutationTableModelColumn.annotationColumn);
		return columns;
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
}
