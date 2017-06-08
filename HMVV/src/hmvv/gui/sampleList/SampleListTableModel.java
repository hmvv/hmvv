package hmvv.gui.sampleList;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import hmvv.model.Sample;

public class SampleListTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;

	private ArrayList<Sample> samples;

	public SampleListTableModel(ArrayList<Sample> samples){
		super();
		this.samples = samples;
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
	public Class<?> getColumnClass(int columnNo) {
		switch (columnNo) {
		case 0:
			return Integer.class;
		default:
			return String.class;
		}
	}

	@Override
	public int getColumnCount() {
		return 18;
	}

	@Override
	public int getRowCount() {
		return samples.size();
	}

	@Override
	public String getColumnName(int column) {
		switch(column) {
		case 0:
			return "ID";
		case 1:
			return "Assay";
		case 2:
			return "Instrument";
		case 3:
			return "Last Name";
		case 4:
			return "First Name";
		case 5:
			return "Order Number";
		case 6:
			return "Path Number";
		case 7:
			return "Tumor Source";
		case 8:
			return "Tumor Percent";
		case 9:
			return "runID";
		case 10:
			return "sampleID";
		case 11:
			return "coverageID";
		case 12:
			return "callerID";
		case 13:
			return "runDate";
		case 14:
			return "note";
		case 15:
			return "enteredBy";
		case 16:
			return "amplicon";
		case 17:
			return "edit";
		default:
			throw new IllegalArgumentException("Column " + column + " does not exist");
		}
	}

	@Override
	public Object getValueAt(int row, int column) {
		Sample sample = samples.get(row);
		switch(column) {
		case 0:
			return sample.ID;
		case 1:
			return sample.assay;
		case 2:
			return sample.instrument;
		case 3:
			return sample.getLastName();
		case 4:
			return sample.getFirstName();
		case 5:
			return sample.getOrderNumber();
		case 6:
			return sample.getPathNumber();
		case 7:
			return sample.getTumorSource();
		case 8:
			return sample.getTumorPercent();
		case 9:
			return sample.runID;
		case 10:
			return sample.sampleID;
		case 11:
			return sample.coverageID;
		case 12:
			return sample.callerID;
		case 13:
			return sample.runDate;
		case 14:
			return sample.getNote();
		case 15:
			return sample.enteredBy;
		case 16:
			return "amplicon";
		case 17:
			return "edit";
		default:
			throw new IllegalArgumentException("Column " + column + " does not exist");
		}
	}
}
