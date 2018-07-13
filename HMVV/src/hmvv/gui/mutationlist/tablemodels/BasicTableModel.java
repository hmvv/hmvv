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
		columns.add(MutationTableModelColumn.somaticColumn);
		columns.add(MutationTableModelColumn.classificationColumn);
		columns.add(MutationTableModelColumn.gotoIGVColumn);
		return columns;
	}
	
	public void updateModel(int row, ArrayList<String> cosmicIDs, int occurenceCount){
		Mutation mutation = getMutation(row);
		mutation.setCosmicID(cosmicIDs);
		mutation.setOccurrence(occurenceCount);
		fireTableCellUpdated(row, 6);
		fireTableCellUpdated(row, 12);
	}
}
