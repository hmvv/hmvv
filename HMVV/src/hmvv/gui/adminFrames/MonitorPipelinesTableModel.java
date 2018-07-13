package hmvv.gui.adminFrames;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import hmvv.model.Pipeline;
import hmvv.model.Sample;

public class MonitorPipelinesTableModel extends AbstractTableModel{
	private static final long serialVersionUID = 1L;

	private ArrayList<Pipeline> pipelines;
	private ArrayList<PipelineTableModelColumn> columns;
	
	public MonitorPipelinesTableModel(ArrayList<Pipeline> pipelines){
		super();
		this.pipelines = pipelines;
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
				"sampleID",
				String.class,
				(Pipeline pipeline) -> pipeline.sampleID));
		
		columns.add(new PipelineTableModelColumn("The assay used", 
				"assayID", 
				String.class,
				(Pipeline pipeline) -> pipeline.assayID));
		
		columns.add(new PipelineTableModelColumn("The instrument used", 
				"instrumentID", 
				String.class,
				(Pipeline pipeline) -> pipeline.instrumentID));
		
		columns.add(new PipelineTableModelColumn("The environment used", 
				"environmentID", 
				 String.class,
				 (Pipeline pipeline) -> pipeline.environmentID));
		
		columns.add(new PipelineTableModelColumn("The pipeline status", 
				"status", 
				String.class,
				(Pipeline pipeline) -> pipeline.status));
		
		columns.add(new PipelineTableModelColumn("The runtime of pipeline", 
				"runTime", 
				String.class,
				(Pipeline pipeline) -> pipeline.runTime));
		
		columns.add(new PipelineTableModelColumn("The pipeline progress", 
				"progress", 
				String.class,
				(Pipeline pipeline) -> pipeline.progress));
		

	}
	
	
	public Pipeline getPipeline(int row){
		return pipelines.get(row);
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
