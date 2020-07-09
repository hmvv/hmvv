package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class GermlineGnomadTableModel extends CommonTableModelGermline {
	private static final long serialVersionUID = 1L;


	public GermlineGnomadTableModel(MutationListGermline mutationList){
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

		columns.add(MutationTableModelColumnGermline.gnomadAltFreqColumn);
		columns.add(MutationTableModelColumnGermline.gnomadAltFreq_afrColumn);
		columns.add(MutationTableModelColumnGermline.gnomadAltFreq_amrColumn);
		columns.add(MutationTableModelColumnGermline.gnomadAltFreq_asjColumn);
		columns.add(MutationTableModelColumnGermline.gnomadAltFreq_easColumn);
		columns.add(MutationTableModelColumnGermline.gnomadAltFreq_finColumn);
		columns.add(MutationTableModelColumnGermline.gnomadAltFreq_nfeColumn);
		columns.add(MutationTableModelColumnGermline.gnomadAltFreq_sasColumn);
		columns.add(MutationTableModelColumnGermline.gnomadAltFreq_othColumn);
		columns.add(MutationTableModelColumnGermline.gnomadAltFreq_maleColumn);
		columns.add(MutationTableModelColumnGermline.gnomadAltFreq_femaleColumn);

		return columns;
	}
}
