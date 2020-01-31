package hmvv.gui.sampleList;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import hmvv.model.Sample;

public class SampleListTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;

	private ArrayList<Sample> samples;
	private ArrayList<SampleTableModelColumn> columns;
	
	public SampleListTableModel(ArrayList<Sample> samples){
		super();
		this.samples = samples;
		constructColumns();
	}
	
	private void constructColumns(){
		columns = new ArrayList<SampleTableModelColumn>();
		columns.add(new SampleTableModelColumn("The unique ID",
				"Sample ID",
				Integer.class,
				(Sample sample) -> sample.sampleID));
		
		columns.add(new SampleTableModelColumn("The assay used",
				"Assay",
				String.class,
				(Sample sample) -> sample.assay));
		
		columns.add(new SampleTableModelColumn("The instrument used",
				"Instrument",
				String.class,
				(Sample sample) -> sample.instrument));
		
		columns.add(new SampleTableModelColumn("The patient's MRN (and the number of samples for that patient)", 
				"MRN (#)", 
				String.class,
				(Sample sample) -> sample.getMRN() + sample.getLinkedPatientSampleSize()));
		
		columns.add(new SampleTableModelColumn("The patient's last name", 
				"Last Name", 
				String.class,
				(Sample sample) -> sample.getLastName()));
		
		columns.add(new SampleTableModelColumn("The patient's first name", 
				"First Name", 
				String.class,
				(Sample sample) -> sample.getFirstName()));
		
		columns.add(new SampleTableModelColumn("The lab order number", 
				"Order Number", 
				 String.class,
				(Sample sample) -> sample.getOrderNumber()));
		
		columns.add(new SampleTableModelColumn("The surgical case accession number", 
				"Path Number", 
				String.class,
				(Sample sample) -> sample.getPathNumber()));
		
		columns.add(new SampleTableModelColumn("The source organ/tissue of the tumor", 
				"Tumor Source", 
				String.class,
				(Sample sample) -> sample.getTumorSource()));
		
		columns.add(new SampleTableModelColumn("The percent of tumor in the sample", 
				"Tumor Percent", 
				String.class,
				(Sample sample) -> sample.getTumorPercent()));
		
		columns.add(new SampleTableModelColumn("The instrument generated run ID", 
				"Run ID", 
				String.class,
				(Sample sample) -> sample.runID));
		
		columns.add(new SampleTableModelColumn("The instrument generated sample ID", 
				"Sample Name",
				String.class,
				(Sample sample) -> sample.sampleName));
		
		columns.add(new SampleTableModelColumn("The instrument generated coverage ID (Life Technologies instruments only)", 
				"Coverage ID", 
				String.class,
				(Sample sample) -> sample.coverageID));
		
		columns.add(new SampleTableModelColumn("The instrument generated caller ID (Life Technologies instruments only)", 
				"Caller ID", 
				String.class,
				(Sample sample) -> sample.callerID));
		
		columns.add(new SampleTableModelColumn("The date the sample was run", 
				"Run Date", 
				String.class,
				(Sample sample) -> sample.runDate));

		columns.add(new SampleTableModelColumn("The patient's clinical history",
				"Patient History",
				String.class,
				(Sample sample) -> sample.getPatientHistory()));

		columns.add(new SampleTableModelColumn("The diagnosis of the tissue sample",
				"Sample Diagnosis",
				String.class,
				(Sample sample) -> sample.getDiagnosis()));

		columns.add(new SampleTableModelColumn("Additional user entered information", 
				"Note", 
				String.class,
				(Sample sample) -> sample.getNote()));
		
		columns.add(new SampleTableModelColumn("User ID of the individual who entered this sample", 
				"Entered By", 
				String.class,
				(Sample sample) -> sample.enteredBy));
		
		columns.add(new SampleTableModelColumn("Quality control metrics of this assay.",
				"AssayQC",
				String.class,
				(Sample sample) -> sample.getAssayQCLabel()));
		
		columns.add(new SampleTableModelColumn("Click to edit metadata associated with this sample", 
				"Edit Sample", 
				String.class,
				(Sample sample) -> "edit"));
	}
	
	public Sample getSample(String instrument, String runID, String coverageID, String variantCallerID, String sampleName){
		for(Sample s : samples){
			//only check sampleID because the coverageID and variantCallerID can be blank depending on the instrument
			if(s.runID.equals("") || s.sampleName.equals("")){
				continue;
			}
			if(s.instrument.equals(instrument) && s.runID.equals(runID) && s.coverageID.equals(coverageID) && s.callerID.equals(variantCallerID) && s.sampleName.equals(sampleName)){
				return s;
			}
		}
		return null;
	}
	
	public void addSample(Sample sample){
		samples.add(sample);
		fireTableRowsInserted(samples.size()-1, samples.size()-1);
	}

	public Sample getSample(int row){
		return samples.get(row);
	}

	public void deleteSample(int row){
		samples.remove(row);
		fireTableRowsDeleted(row, row);
	}
	
	public void updateSample(int row, Sample updatedSample) {
		samples.set(row, updatedSample);
		for(int i = 0; i < getColumnCount(); i++){
			fireTableCellUpdated(row, i);
		}
	}

	@Override 
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return columns.get(column).columnClass;
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public int getRowCount() {
		return samples.size();
	}

	@Override
	public String getColumnName(int column) {
		return columns.get(column).title;
	}

	@Override
	public Object getValueAt(int row, int column) {
		Sample sample = samples.get(row);
		return columns.get(column).getValue(sample);
	}
	
	public String getColumnDescription(int column){
		return columns.get(column).description;
	}
}
