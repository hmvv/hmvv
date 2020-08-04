package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class GermlineProteinDomainTableModel extends CommonTableModelGermline {
	private static final long serialVersionUID = 1L;


	public GermlineProteinDomainTableModel(MutationListGermline mutationList){
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

		columns.add(MutationTableModelColumnGermline.HGVSpColumn);
		columns.add(MutationTableModelColumnGermline.protein_start_column);
		columns.add(MutationTableModelColumnGermline.protein_end_column);
		columns.add(MutationTableModelColumnGermline.protein_id_column);
		columns.add(MutationTableModelColumnGermline.protein_type_column);
		columns.add(MutationTableModelColumnGermline.protein_feature_column);
		columns.add(MutationTableModelColumnGermline.protein_note_column);


		columns.add(MutationTableModelColumnGermline.uniprot_variant_column);
		columns.add(MutationTableModelColumnGermline.nextprot_column);
		columns.add(MutationTableModelColumnGermline.expasy_column);
		columns.add(MutationTableModelColumnGermline.uniprot_column);
		columns.add(MutationTableModelColumnGermline.pfam_column);
		columns.add(MutationTableModelColumnGermline.scoop_column);
		return columns;
	}
}
