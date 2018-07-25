package hmvv.gui.adminFrames;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import hmvv.gui.GUICommonTools;
import hmvv.gui.sampleList.SampleListFrame;
import hmvv.io.DatabaseCommands;
import hmvv.model.Pipeline;
import hmvv.model.PipelineStatus;

public class MonitorPipelines extends JDialog {

	private static final long serialVersionUID = 1L;

	private JButton btnRefresh;

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
				Component c = super.prepareRenderer(renderer, row, column);
				int modelRow = table.convertRowIndexToModel(row);
				String pipelineProgress = tableModel.getPipeline(modelRow).getProgress();
				if(pipelineProgress.equals("ERROR")) {					
					c.setBackground(new Color(255,51,51));
				}else if(pipelineProgress.equals("Complete")) {					
					c.setBackground(new Color(102,255,102));
				}else {
					c.setBackground(new Color(255,255,204));
				}
				return c;
			}
		};

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

		btnRefresh = new JButton("Refresh");
		btnRefresh.setFont(GUICommonTools.TAHOMA_BOLD_12);
	}

	private void layoutComponents(){
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGap(59)
						.addComponent(btnRefresh)
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
						.addComponent(btnRefresh)
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

		btnRefresh.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					buildModelFromDatabase();
				}catch (Exception e){
					JOptionPane.showMessageDialog(MonitorPipelines.this, e.getMessage());
				}
			}
		});
	}

	private void buildModelFromDatabase() throws Exception {
		tableModel.resetModel();
		ArrayList<Pipeline> pipelines = DatabaseCommands.getAllPipelines();
		for(Pipeline p : pipelines) {
			tableModel.addPipeline(p);
		}

	}

	public void addPipeline(Pipeline pipeline){
		tableModel.addPipeline(pipeline);
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
			
			private final SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/y H:m:ss");
			
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
					return dateFormat.format(pipelineStatus.dateUpdated);
				}
			}
			
			@Override
			public final Class<?> getColumnClass(int column) {
				return String.class;
			}
		};
		JTable table = new JTable(tableModel);
		
		JScrollPane tableSP = new JScrollPane(table);
		tableSP.setPreferredSize(new Dimension(600,300));
		
		JOptionPane.showMessageDialog(this, tableSP,
				String.format("Pipeline Status (%s %s runID=%s sampleID=%s)",
						currentPipeline.instrumentID, currentPipeline.assayID, currentPipeline.runID, currentPipeline.sampleID),
				JOptionPane.INFORMATION_MESSAGE);
	}
}
