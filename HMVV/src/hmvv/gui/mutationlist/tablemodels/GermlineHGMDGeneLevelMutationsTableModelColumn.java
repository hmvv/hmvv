package hmvv.gui.mutationlist.tablemodels;
import hmvv.model.MutationGermlineHGMD;

public class GermlineHGMDGeneLevelMutationsTableModelColumn extends HMVVTableModelColumn {


        private final GermlineHGMDGeneLevelMutationsTableModelColumn.SampleGetValueAtOperation operation;

        public GermlineHGMDGeneLevelMutationsTableModelColumn(String description, String title, Class<?> columnClass, GermlineHGMDGeneLevelMutationsTableModelColumn.SampleGetValueAtOperation operation) {
            super(description, title, columnClass);
            this.operation = operation;
        }

        public Object getValue(MutationGermlineHGMD mutation){
            return operation.getValue(mutation);
        }


        public interface SampleGetValueAtOperation{
            Object getValue(MutationGermlineHGMD mutation);
        }
}

