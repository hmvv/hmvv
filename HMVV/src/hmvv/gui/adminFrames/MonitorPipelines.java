package hmvv.gui.adminFrames;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.*;

import hmvv.gui.GUICommonTools;
import hmvv.gui.sampleList.SampleListFrame;
import hmvv.io.DatabaseCommands;
import hmvv.model.Pipeline;
import hmvv.model.PipelineProgram;
import hmvv.model.PipelineStatus;

public class MonitorPipelines extends JDialog {
	
	private static final long serialVersionUID = 1L;

	private JMenuBar menuBar;

	//Asynchronous sample status updates
	private Thread pipelineRefreshThread;
	private final int secondsToSleep = 10;
	private volatile long timeLastRefreshed = 0;
	private volatile JMenuItem refreshLabel;
	
	private TableRowSorter<MonitorPipelinesTableModel> sorter;
	
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
						if (pipelineProgress == PipelineProgram.COMPLETE) {
							c.setBackground(GUICommonTools.COMPLETE_COLOR);
						} else {
							c.setBackground(pipelineProgress.displayColor);
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

		menuBar = new JMenuBar();
		refreshLabel = new JMenuItem("Loading status refresh...");
		refreshLabel.setEnabled(false);
		menuBar.add(refreshLabel);
		setJMenuBar(menuBar);

		TableColumn progressColumn = table.getColumnModel().getColumn(9);
		progressColumn.setCellRenderer(new ProgressCellRenderer());
	}

	private void layoutComponents(){
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGap(127))
				.addGroup(groupLayout.createSequentialGroup()
						.addGap(20)
						.addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 969, Short.MAX_VALUE)
						.addGap(20))
				);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGap(25)
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


	private void activateComponents(){
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent c) {
				try{
					handlePipelineSelectionClick();
				}catch (Exception e){
					JOptionPane.showMessageDialog(MonitorPipelines.this, e.getMessage());
				}
			}
		});		

	}

	private void buildModelFromDatabase() throws Exception {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		pipelineRefreshThread = new Thread(new Runnable() {
			@Override
			public void run() {
				//loop forever (will exit when JFrame is closed).
				while(true) {
					long currentTimeInMillis = System.currentTimeMillis();
					if(timeLastRefreshed + (1000 * secondsToSleep) < currentTimeInMillis) {
						refreshLabel.setText("Refreshing table...");
                        if(!updatePipelinesASynch()) {
                        	JOptionPane.showMessageDialog(MonitorPipelines.this, "Failure to update pipeline status details. Please contact the administrator.");
							refreshLabel.setText("Status refresh disabled");
							return;
                        }
                        setCursor(Cursor.getDefaultCursor());
					}

					setRefreshLabelText();
					
					try {
						Thread.sleep(1 * 1000);
					} catch (InterruptedException e) {}
				}
			}
		});

		pipelineRefreshThread.start();

	}

    private boolean updatePipelinesASynch() {
        try {
            ArrayList<Pipeline> pipelines = DatabaseCommands.getAllPipelines();
            for(Pipeline p : pipelines) {
            	tableModel.addOrUpdatePipeline(p);
            }
            timeLastRefreshed = System.currentTimeMillis();
            return true;
        } catch (Exception e) {
        	return false;
        }
    }

	private void setRefreshLabelText() {
		long currentTimeInMillis = System.currentTimeMillis();
		long timeToRefresh = timeLastRefreshed + (1000 * secondsToSleep);
		long diff = timeToRefresh - currentTimeInMillis;
		long secondsRemaining = diff / 1000;
		refreshLabel.setText("Table will refresh in : " + secondsRemaining + "s");
	}
	private Pipeline getCurrentlySelectedPipeline(){
		int viewRow = table.getSelectedRow();
		int modelRow = table.convertRowIndexToModel(viewRow);
		return tableModel.getPipeline(modelRow);
	}

	private void handlePipelineSelectionClick() throws Exception{
		Pipeline currentPipeline = getCurrentlySelectedPipeline();
		int queueID = currentPipeline.getQueueID();

		ArrayList<PipelineStatus> rows = DatabaseCommands.getPipelineDetail(queueID);

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
