package hmvv.gui.adminFrames;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import hmvv.gui.GUICommonTools;
import hmvv.io.DatabaseCommands;
import hmvv.model.Pipeline;

public class MonitorPipelines extends JFrame {

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
	public MonitorPipelines(Component parent) throws Exception {
		super("Monitor Pipelines");

		tableModel = new MonitorPipelinesTableModel();
		
		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*.97), (int)(bounds.height*.90));

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		createComponents();
		layoutComponents();
		activateComponents();
		setLocationRelativeTo(parent);
		
		buildModelFromDatabase();
	}

	private void createComponents(){
		table = new JTable(tableModel);

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

	public void resizeColumnWidths() {
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
		Pipeline currentPipeline= getCurrentlySelectedPipeline();
		int queueID = currentPipeline.getQueueID();

		String result = DatabaseCommands.getPipelineDetail(queueID);
		JOptionPane.showMessageDialog(MonitorPipelines.this, result);
	}
}
