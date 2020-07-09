package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class GermlineCardiacAtlasTableModel extends CommonTableModelGermline {
	private static final long serialVersionUID = 1L;


	public GermlineCardiacAtlasTableModel(MutationListGermline mutationList){
		super(mutationList);
	}
	
	protected ArrayList<MutationTableModelColumnGermline> constructColumns(){
		ArrayList<MutationTableModelColumnGermline> columns = new ArrayList<MutationTableModelColumnGermline>();
		columns.add(MutationTableModelColumnGermline.reportedColumn);

		columns.add(MutationTableModelColumnGermline.geneColumn);
		columns.add(MutationTableModelColumnGermline.exonsColumn);

		columns.add(MutationTableModelColumnGermline.chrColumn);
		columns.add(MutationTableModelColumnGermline.posColumn);
		columns.add(MutationTableModelColumnGermline.refColumn);
		columns.add(MutationTableModelColumnGermline.altColumn);

		columns.add(MutationTableModelColumnGermline.cardiac_cdsVariant_Column);
		columns.add(MutationTableModelColumnGermline.cardiac_proteinVariant_Column);
		columns.add(MutationTableModelColumnGermline.cardiac_variantType_Column);

		return columns;
	}
}
