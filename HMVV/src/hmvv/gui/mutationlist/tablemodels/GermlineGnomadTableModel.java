package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class GermlineGnomadTableModel extends GermlineCommonTableModel {
	private static final long serialVersionUID = 1L;


	public GermlineGnomadTableModel(MutationList mutationList){
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

		columns.add(GermlineMutationTableModelColumn.gnomadIDColumn);
		columns.add(GermlineMutationTableModelColumn.gnomadAltFreqColumn);
		columns.add(GermlineMutationTableModelColumn.gnomadAltFreq_afrColumn);
		columns.add(GermlineMutationTableModelColumn.gnomadAltFreq_amrColumn);
		columns.add(GermlineMutationTableModelColumn.gnomadAltFreq_asjColumn);
		columns.add(GermlineMutationTableModelColumn.gnomadAltFreq_easColumn);
		columns.add(GermlineMutationTableModelColumn.gnomadAltFreq_finColumn);
		columns.add(GermlineMutationTableModelColumn.gnomadAltFreq_nfeColumn);
		columns.add(GermlineMutationTableModelColumn.gnomadAltFreq_sasColumn);
		columns.add(GermlineMutationTableModelColumn.gnomadAltFreq_othColumn);
		columns.add(GermlineMutationTableModelColumn.gnomadAltFreq_maleColumn);
		columns.add(GermlineMutationTableModelColumn.gnomadAltFreq_femaleColumn);

		return columns;
	}
}
