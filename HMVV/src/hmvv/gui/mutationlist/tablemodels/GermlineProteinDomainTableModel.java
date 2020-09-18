package hmvv.gui.mutationlist.tablemodels;

import java.util.ArrayList;

public class GermlineProteinDomainTableModel extends GermlineCommonTableModel {
	private static final long serialVersionUID = 1L;


	public GermlineProteinDomainTableModel(MutationList mutationList){
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
		columns.add(GermlineMutationTableModelColumn.HGVSpColumn);
		columns.add(GermlineMutationTableModelColumn.protein_start_column);
		columns.add(GermlineMutationTableModelColumn.protein_end_column);
		columns.add(GermlineMutationTableModelColumn.protein_id_column);
		columns.add(GermlineMutationTableModelColumn.protein_type_column);
		columns.add(GermlineMutationTableModelColumn.protein_feature_column);
		columns.add(GermlineMutationTableModelColumn.protein_note_column);


		columns.add(GermlineMutationTableModelColumn.uniprot_variant_column);
		columns.add(GermlineMutationTableModelColumn.nextprot_column);
		columns.add(GermlineMutationTableModelColumn.expasy_column);
		columns.add(GermlineMutationTableModelColumn.uniprot_column);
		columns.add(GermlineMutationTableModelColumn.pfam_column);
		columns.add(GermlineMutationTableModelColumn.scoop_column);
		return columns;
	}
}
