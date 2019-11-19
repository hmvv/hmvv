package hmvv.gui.adminFrames;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.*;

import hmvv.gui.GUICommonTools;
import hmvv.gui.sampleList.SampleListFrame;
import hmvv.io.DatabaseCommands;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.model.Pipeline;
import hmvv.model.PipelineProgram;
import hmvv.model.PipelineStatus;

public class MonitorPipelines extends JDialog {
	
	private static final long serialVersionUID = 1L;

	private TableRowSorter<MonitorPipelinesTableModel> sorter;

	private JButton refreshPipelines;

	//Table
	private JTable table;
	private MonitorPipelinesTableModel tableModel;
	private JScrollPane tableScrollPane;
	
	/**
	 * Create the frame.
	 * @throws Exception 
	 */
	public MonitorPipelines(SampleListFrame parent) throws Exception {
		super(parent, "Monitor Pipelines");

		tableModel = new MonitorPipelinesTableModel();

		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*.97), (int)(bounds.height*.90));

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);

		createComponents();
		layoutComponents();
		activateComponents();
		setLocationRelativeTo(parent);

		buildModelFromDatabase();
	}

	private void createComponents(){
		table = new JTable(tableModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				try {
					Component c = super.prepareRenderer(renderer, row, column);
					int modelRow = table.convertRowIndexToModel(row);
					Pipeline pipeline = tableModel.getPipeline(modelRow);
					PipelineProgram pipelineProgress = pipeline.pipelineProgram;
	
					if (isCellSelected(row,column)){
						c.setBackground(new Color(51,153,255));
					}else {
						if (pipelineProgress.isCompleteProgram()) {
							c.setBackground(GUICommonTools.COMPLETE_COLOR);
						} else {
							c.setBackground(pipelineProgress.getColor());
						}
					}
					return c;
				}catch(Exception e) {
					//occasionally when the table is first loading from the database and the model is still empty, table.convertRowIndexToModel(row) will throw an Exception.
					return null;
				}
			}
		};
		((DefaultTableCellRenderer)table.getDefaultRenderer(Integer.class)).setHorizontalAlignment(SwingConstants.CENTER);
		((DefaultTableCellRenderer)table.getDefaultRenderer(String.class)).setHorizontalAlignment(SwingConstants.CENTER);
		((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

		table.setAutoCreateRowSorter(true);

		sorter = new TableRowSorter<MonitorPipelinesTableModel>(tableModel);
		table.setRowSorter(sorter);

		//by default, sort from newest to oldest
		ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();

		tableScrollPane = new JScrollPane();
		tableScrollPane.setViewportView(table);

		TableColumn progressColumn = table.getColumnModel().getColumn(9);
		progressColumn.setCellRenderer(new ProgressCellRenderer());

		refreshPipelines = new JButton("Refresh");
		refreshPipelines.setToolTipText("Refresh pipeline status");
		refreshPipelines.setFont(GUICommonTools.TAHOMA_BOLD_12);
	}

	private void layoutComponents(){
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGap(15))
						.addComponent(refreshPipelines)
						.addGap(15)
				.addGroup(groupLayout.createSequentialGroup()
						.addGap(20)
						.addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 969, Short.MAX_VALUE)
						.addGap(20))
				);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
								.addGap(15)
								.addComponent(refreshPipelines)
								.addGap(15)
								.addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
								.addGap(25))
				);
		getContentPane().setLayout(groupLayout);

		resizeColumnWidths();
	}

	private void resizeColumnWidths() {
		TableColumnModel columnModel = table.getColumnModel();    

		for (int column = 0; column < table.getColumnCount(); column++) {
			TableColumn tableColumn = columnModel.getColumn(column);

			TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
			Component headerComp = headerRenderer.getTableCellRendererComponent(table, tableColumn.getHeaderValue(), false, false, 0, 0);

			int minWidth = headerComp.getPreferredSize().width;
			int maxWidth = 150;

			int width = minWidth;
			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer renderer = table.getCellRenderer(row, column);
				Component comp = table.prepareRenderer(renderer, row, column);
				width = Math.max(comp.getPreferredSize().width + 25 , width);
			}
			width = Math.min(maxWidth, width);
			columnModel.getColumn(column).setPreferredWidth(width);
		}
	}


	private void activateComponents() throws Exception {
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent c) {
				try{
					table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					handlePipelineSelectionClick();
				}catch (Exception e){
					HMVVDefectReportFrame.showHMVVDefectReportFrame(MonitorPipelines.this, e);
				}
				table.setCursor(Cursor.getDefaultCursor());
			}
		});

        refreshPipelines.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    buildModelFromDatabase();
                } catch (Exception e) {
                	HMVVDefectReportFrame.showHMVVDefectReportFrame(MonitorPipelines.this, e);
                }
                setCursor(Cursor.getDefaultCursor());
            }
        });
	}

	private void buildModelFromDatabase() throws Exception {
		try {
			ArrayList<Pipeline> pipelines = DatabaseCommands.getAllPipelines();
			for(Pipeline p : pipelines) {
				tableModel.addOrUpdatePipeline(p);
			}
		} catch (Exception e) {
			HMVVDefectReportFrame.showHMVVDefectReportFrame(MonitorPipelines.this, e, "Failure to update pipeline status details. Please contact the administrator.");
		}
	}

	private Pipeline getCurrentlySelectedPipeline(){
		int viewRow = table.getSelectedRow();
		if(viewRow == -1) {
			return null;
		}
		int modelRow = table.convertRowIndexToModel(viewRow);
		return tableModel.getPipeline(modelRow);
	}

	private void handlePipelineSelectionClick() throws Exception{
		Pipeline currentPipeline = getCurrentlySelectedPipeline();
		if(currentPipeline == null) {
			return;
		}
		ArrayList<PipelineStatus> rows = DatabaseCommands.getPipelineDetail(currentPipeline.queueID);

		DefaultTableModel tableModel = new DefaultTableModel(){
			private static final long serialVersionUID = 1L;

			@Override 
			public final boolean isCellEditable(int row, int column) {
				return false;
			}
			
			@Override
			public final int getColumnCount() {
				return 2;
			}
			
			@Override
			public final int getRowCount() {
				return rows.size();
			}
			
			@Override
			public final String getColumnName(int column) {
				if(column == 0) {
					return "Pipeline Status";
				}else {
					return "Update Time";
				}
			}
			
			@Override
			public  final Object getValueAt(int row, int column) {
				PipelineStatus pipelineStatus = rows.get(row);
				if(column == 0) {
					return pipelineStatus.pipelineStatus;
				}else {
					return GUICommonTools.extendedDateFormat2.format(pipelineStatus.dateUpdated);
				}
			}
			
			@Override
			public final Class<?> getColumnClass(int column) {
				return String.class;
			}
		};
		JTable table = new JTable(tableModel);

		((DefaultTableCellRenderer)table.getDefaultRenderer(Integer.class)).setHorizontalAlignment(SwingConstants.CENTER);
		((DefaultTableCellRenderer)table.getDefaultRenderer(String.class)).setHorizontalAlignment(SwingConstants.CENTER);
		((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

		JScrollPane tableSP = new JScrollPane(table);
		tableSP.setPreferredSize(new Dimension(600,300));

		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
		table.setRowSorter(sorter);

		JOptionPane.showMessageDialog(this, tableSP,
				String.format("Pipeline Status (%s %s runID=%s sampleID=%s)",
						currentPipeline.instrumentName, currentPipeline.assayName, currentPipeline.runID, currentPipeline.sampleName),
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	public class ProgressCellRenderer extends JProgressBar implements TableCellRenderer {
		private static final long serialVersionUID = 1L;

		ProgressCellRenderer() {
	        super(0, 100);
	        setOpaque(true);
	        setStringPainted(true);
	        setBackground(GUICommonTools.PROGRESS_BACKGROUND_COLOR);
	        setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
	        setForeground(GUICommonTools.PROGRESS_FOREGROUND_COLOR);
	    }

	    @Override
	    public boolean isDisplayable() { 
	        return true; 
	    }

	    @Override
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	    	try {
	    		int intValue = Integer.parseInt(value.toString());
	    		setValue(intValue);
	    		if(intValue == -1) {
	                setString("ERROR");
	    		}else {
                	setString(intValue+"%");
	    		}
	    	}catch(Exception e) {
	    		setValue(0);
                setString("ERROR");
	    	}
	        return this;
	    }
	}
}
