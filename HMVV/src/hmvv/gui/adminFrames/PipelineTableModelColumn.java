package hmvv.gui.adminFrames;

import hmvv.gui.mutationlist.tablemodels.HMVVTableModelColumn;
import hmvv.model.Pipeline;

public class PipelineTableModelColumn extends HMVVTableModelColumn{
		
		/**
		 * The Lambda interface object
		 */
		private final SampleGetValueAtOperation operation;
		
		public PipelineTableModelColumn(String description, String title, Class<?> columnClass, SampleGetValueAtOperation operation) {
			super(description, title, columnClass);
			this.operation = operation;
		}
		
		/**
		 * Lambda expression function
		 */
		public Object getValue(Pipeline pipeline){
			return operation.getValue(pipeline);
		}
		
		/**
		 * Lambda expression interface
		 *
		 */
		public interface SampleGetValueAtOperation{
			Object getValue(Pipeline pipeline);
		}
	}
