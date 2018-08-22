package hmvv.gui.adminFrames;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import hmvv.model.Pipeline;

public class MonitorPipelinesTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;

	private ArrayList<Pipeline> pipelines;
	private ArrayList<PipelineTableModelColumn> columns;

	public MonitorPipelinesTableModel(){
		this.pipelines = new ArrayList<Pipeline>();
		constructColumns();
	}

	private void constructColumns(){
		columns = new ArrayList<PipelineTableModelColumn>();
		columns.add(new PipelineTableModelColumn("The queue ID",
				"QueueID",
				Integer.class,
				(Pipeline pipeline) -> pipeline.queueID));

		columns.add(new PipelineTableModelColumn("The runID",
				"RunID",
				Integer.class,
				(Pipeline pipeline) -> pipeline.runID));

		columns.add(new PipelineTableModelColumn("The sample used",
				"sampleName",
				String.class,
				(Pipeline pipeline) -> pipeline.sampleName));

		columns.add(new PipelineTableModelColumn("The assay used", 
				"assayID", 
				String.class,
				(Pipeline pipeline) -> pipeline.assayID));

		columns.add(new PipelineTableModelColumn("The instrument used", 
				"instrumentID", 
				String.class,
				(Pipeline pipeline) -> pipeline.instrumentID));

		columns.add(new PipelineTableModelColumn("The pipeline status", 
				"status", 
				String.class,
				(Pipeline pipeline) -> pipeline.status));

		columns.add(new PipelineTableModelColumn("The runtime of pipeline", 
				"runTime", 
				String.class,
				(Pipeline pipeline) -> pipeline.runTime));
		
		columns.add(new PipelineTableModelColumn("The pipeline program", 
				"program", 
				String.class,
				(Pipeline pipeline) -> pipeline.getProgram()));
		
		columns.add(new PipelineTableModelColumn("The pipeline progress", 
				"progress", 
				String.class,
				(Pipeline pipeline) -> {
					try {
						return pipeline.getProgress();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return pipeline;
				}));
	}

	public Pipeline getPipeline(int row){
		return pipelines.get(row);
	}

	public void resetModel() {
		pipelines.clear();
		fireTableDataChanged();
	}
	
	public void addPipeline(Pipeline pipeline){
		pipelines.add(pipeline);
		fireTableRowsInserted(pipelines.size()-1, pipelines.size()-1);
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
		return pipelines.size();
	}

	@Override
	public String getColumnName(int column) {
		return columns.get(column).title;
	}

	@Override
	public Object getValueAt(int row, int column) {
		Pipeline pipeline = pipelines.get(row);
		return columns.get(column).getValue(pipeline);
	}

	public String getColumnDescription(int column){
		return columns.get(column).description;
	}
}
