package hmvv.gui.sampleList;
import hmvv.gui.mutationlist.tablemodels.HMVVTableModelColumn;
import hmvv.model.GeneTargetQC;

public class ViewGeneTargetFrameTableModelColumn extends HMVVTableModelColumn {

        private final hmvv.gui.sampleList.ViewGeneTargetFrameTableModelColumn.SampleGetValueAtOperation operation;

        public ViewGeneTargetFrameTableModelColumn(String description, String title, Class<?> columnClass, hmvv.gui.sampleList.ViewGeneTargetFrameTableModelColumn.SampleGetValueAtOperation operation) {
            super(description, title, columnClass);
            this.operation = operation;
        }

        public Object getValue(GeneTargetQC geneTarget){
            return operation.getValue(geneTarget);
        }

        public interface SampleGetValueAtOperation{
            Object getValue(GeneTargetQC geneTarget);
        }
}

