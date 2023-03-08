package hmvv.gui.mutationlist.tablemodels;
import hmvv.model.MutationGermlineHGMDGeneLevel;

public class GermlineHGMDGeneLevelSummaryTableModelColumn extends HMVVTableModelColumn {


        private final GermlineHGMDGeneLevelSummaryTableModelColumn.SampleGetValueAtOperation operation;

        public GermlineHGMDGeneLevelSummaryTableModelColumn(String description, String title, Class<?> columnClass, GermlineHGMDGeneLevelSummaryTableModelColumn.SampleGetValueAtOperation operation) {
            super(description, title, columnClass);
            this.operation = operation;
        }

        public Object getValue(MutationGermlineHGMDGeneLevel mutation){
            return operation.getValue(mutation);
        }


        public interface SampleGetValueAtOperation{
            Object getValue(MutationGermlineHGMDGeneLevel mutation);
        }
}

