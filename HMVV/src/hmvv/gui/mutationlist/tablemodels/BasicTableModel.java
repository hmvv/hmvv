package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

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

		columns.add(MutationTableModelColumn.typeColumn);
		columns.add(MutationTableModelColumn.variantClassificationColumn);
		columns.add(MutationTableModelColumn.altFreqColumn);
		columns.add(MutationTableModelColumn.altGlobalFreqColumn);
		columns.add(MutationTableModelColumn.gnomadAltFreqColumn);
		columns.add(MutationTableModelColumn.readDPColumn);
		columns.add(MutationTableModelColumn.altReadDPColumn);

		columns.add(MutationTableModelColumn.cosmicIDColumn);

		columns.add(MutationTableModelColumn.occurrenceColumn);
		columns.add(MutationTableModelColumn.annotationColumn);
		columns.add(MutationTableModelColumn.somaticColumn);
		columns.add(MutationTableModelColumn.classificationColumn);
		columns.add(MutationTableModelColumn.gotoIGVColumn);
		columns.add(MutationTableModelColumn.igvLoadColumn);

		return columns;
	}
}
