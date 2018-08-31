package hmvv.gui.sampleList;
import hmvv.gui.mutationlist.tablemodels.HMVVTableModelColumn;
import hmvv.model.Amplicon;

public class ViewAmpliconFrameTableModelColumn extends HMVVTableModelColumn {


        private final hmvv.gui.sampleList.ViewAmpliconFrameTableModelColumn.SampleGetValueAtOperation operation;

        public ViewAmpliconFrameTableModelColumn(String description, String title, Class<?> columnClass, hmvv.gui.sampleList.ViewAmpliconFrameTableModelColumn.SampleGetValueAtOperation operation) {
            super(description, title, columnClass);
            this.operation = operation;
        }

        public Object getValue(Amplicon amplicon){
            return operation.getValue(amplicon);
        }


        public interface SampleGetValueAtOperation{
            Object getValue(Amplicon amplicon);
        }
}

