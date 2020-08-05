package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class GermlineCardiacAtlasTableModel extends GermlineCommonTableModel {
	private static final long serialVersionUID = 1L;


	public GermlineCardiacAtlasTableModel(MutationList mutationList){
		super(mutationList);
	}
	
	protected ArrayList<GermlineMutationTableModelColumn> constructColumns(){
		ArrayList<GermlineMutationTableModelColumn> columns = new ArrayList<GermlineMutationTableModelColumn>();
		columns.add(GermlineMutationTableModelColumn.reportedColumn);

		columns.add(GermlineMutationTableModelColumn.geneColumn);
		columns.add(GermlineMutationTableModelColumn.exonsColumn);

		columns.add(GermlineMutationTableModelColumn.chrColumn);
		columns.add(GermlineMutationTableModelColumn.posColumn);
		columns.add(GermlineMutationTableModelColumn.refColumn);
		columns.add(GermlineMutationTableModelColumn.altColumn);

		columns.add(GermlineMutationTableModelColumn.cardiacAtlasIDColumn);
		columns.add(GermlineMutationTableModelColumn.cardiac_cdsVariant_Column);
		columns.add(GermlineMutationTableModelColumn.cardiac_proteinVariant_Column);
		columns.add(GermlineMutationTableModelColumn.cardiac_variantType_Column);

		return columns;
	}
}
