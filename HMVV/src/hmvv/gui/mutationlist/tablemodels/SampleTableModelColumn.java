package hmvv.gui.mutationlist.tablemodels;

import hmvv.model.Sample;

public class SampleTableModelColumn extends HMVVTableModelColumn {
	
	/**
	 * The Lambda interface object
	 */
	private final SampleGetValueAtOperation operation;
	
	public SampleTableModelColumn(String description, String title, Class<?> columnClass, SampleGetValueAtOperation operation) {
		super(description, title, columnClass);
		this.operation = operation;
	}
	
	/**
	 * Lambda expression function
	 */
	public Object getValue(Sample sample){
		return operation.getValue(sample);
	}
	
	/**
	 * Lambda expression interface
	 *
	 */
	public interface SampleGetValueAtOperation{
		Object getValue(Sample sample);
	}
}
