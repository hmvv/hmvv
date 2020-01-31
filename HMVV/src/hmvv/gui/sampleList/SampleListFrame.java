package hmvv.gui.sampleList;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.GUICommonTools;
import hmvv.gui.adminFrames.*;
import hmvv.gui.mutationlist.MutationListFrame;
import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.io.DatabaseCommands;
import hmvv.io.SSHConnection;
import hmvv.main.Configurations;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.main.HMVVLoginFrame;
import hmvv.model.*;

public class SampleListFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private TableRowSorter<SampleListTableModel> sorter;

	//Menu
	private JMenuBar menuBar;
	private JMenu adminMenu;
	private JMenuItem enterSampleMenuItem;
	private JMenuItem monitorPipelinesItem;
	private JMenuItem newAssayMenuItem;
	private JMenuItem databaseInformationMenuItem;
	private JMenu qualityControlMenuItem;

    //sample
	private ArrayList<Sample> samples;

	//Table
	private JTable table;
	private SampleListTableModel tableModel;
	private JScrollPane tableScrollPane;

	//Buttons
	private JButton sampleSearchButton;
	private JButton resetButton;

	//Filters
	private JLabel assayLabel;
	private JComboBox<String> assayComboBox;
	
	private JLabel instrumentLabel;
	private JComboBox<String> instrumentComboBox;
	
	private JLabel mrnLabel;
	private JTextField mrnTextField;
	
	private JLabel runIDLabel;
	private JTextField runIDTextField;	
	

	private HMVVTableColumn[] customColumns;

	//Asynchronous sample status updates
	private Thread pipelineRefreshThread;
	private final int secondsToSleep = 60;
	private volatile long timeLastRefreshed = 0;
	private volatile ArrayList<Pipeline> pipelines;
	private volatile JMenuItem refreshLabel;
	private int loadSampleResultsColumn = 0;
	private int mrnColumn = 3;
	private int runIDColumn = 10;
	private int qcColumn = 19;
	private int sampleEditColumn = 20;
	

	/**
	 * Initialize the contents of the frame.
	 */
	public SampleListFrame(HMVVLoginFrame parent, ArrayList<Sample> samples) throws Exception {
		super( Configurations.DATABASE_NAME + " : Sample List");

		this.samples = samples;
        if(SSHConnection.isSuperUser(Configurations.USER_FUNCTION.RESTRICT_SAMPLE_ACCESS)) {
            addExceptionSamples();
        }

		tableModel = new SampleListTableModel(samples);

		Rectangle bounds = GUICommonTools.getScreenBounds(parent);
		setSize((int)(bounds.width*.97), (int)(bounds.height*.90));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				SSHConnection.shutdown();
			}
		});		

		customColumns = HMVVTableColumn.getCustomColumnArray(tableModel.getColumnCount(), loadSampleResultsColumn, qcColumn, mrnColumn, runIDColumn, sampleEditColumn);
		pipelines = new ArrayList<Pipeline>();//initialize as blank so that if setupPipelineRefreshThread() fails, the object is still instantiated

		createMenu();
		createComponents();
		layoutComponents();
		activateComponents();
		setLocationRelativeTo(parent);
		setupPipelineRefreshThread();

	}

	private void setupPipelineRefreshThread() {		
		pipelineRefreshThread = new Thread(new Runnable() {
			@Override
			public void run() {
				//loop forever (will exit when JFrame is closed).
				while(true) {
					long currentTimeInMillis = System.currentTimeMillis();
					if(timeLastRefreshed + (1000 * secondsToSleep) < currentTimeInMillis) {
						refreshLabel.setText("Refreshing table...");
						if(!updatePipelinesASynch()){
							JOptionPane.showMessageDialog(SampleListFrame.this, "Failure to update pipeline status details. Please contact the administrator.");
							refreshLabel.setText("Status refresh disabled");
							return;
						}
					}

					setRefreshLabelText();

					try {
						Thread.sleep(1 * 1000);
					} catch (InterruptedException e) {}
				}
			}
		});
		pipelineRefreshThread.setName("Sample List Pipeline Status Refresh");
		pipelineRefreshThread.start();
	}

	private boolean updatePipelinesASynch() {
		try {
			pipelines = DatabaseCommands.getAllPipelines();
			table.repaint();							
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
		refreshLabel.setText("Status refresh in " + secondsRemaining + "s");
	}

	private void createMenu(){
		menuBar = new JMenuBar();
		adminMenu = new JMenu("Admin");
		enterSampleMenuItem = new JMenuItem("Enter Sample");
		enterSampleMenuItem.setEnabled(SSHConnection.isSuperUser(Configurations.USER_FUNCTION.ENTER_SAMPLE));
		monitorPipelinesItem = new JMenuItem("Monitor Pipelines");
		databaseInformationMenuItem = new JMenuItem("Database Information");
		qualityControlMenuItem = new JMenu("Quality Control");
		newAssayMenuItem = new JMenuItem("Create New Assay");
		newAssayMenuItem.setEnabled(SSHConnection.isSuperUser(Configurations.USER_FUNCTION.ENTER_SAMPLE));
		refreshLabel = new JMenuItem("Loading status refresh...");
		refreshLabel.setEnabled(false);

		menuBar.add(adminMenu);
		adminMenu.add(enterSampleMenuItem);
		adminMenu.add(monitorPipelinesItem);
		adminMenu.add(newAssayMenuItem);

		adminMenu.addSeparator();
		adminMenu.add(qualityControlMenuItem);
		try{
			for(String assay : DatabaseCommands.getAllAssays()){
				if(assay.equals("tmb")) {
					JMenuItem tmbDashboard = new JMenuItem(assay + "_Assay");
					qualityControlMenuItem.add(tmbDashboard);
					tmbDashboard.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
									QualityControlTumorMutationBurden tmbDashboard = new QualityControlTumorMutationBurden(SampleListFrame.this,samples);
									tmbDashboard.setVisible(true);
							} catch (Exception e1) {
								HMVVDefectReportFrame.showHMVVDefectReportFrame(SampleListFrame.this, e1);
							}
						}
					});
				} else {
					JMenuItem variantAlleleFrequencyMenuItem = new JMenuItem(assay + "_VariantAlleleFrequency");
					qualityControlMenuItem.add(variantAlleleFrequencyMenuItem);
					variantAlleleFrequencyMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								TreeMap<String, GeneQCDataElementTrend> ampliconTrends = DatabaseCommands.getSampleQCData(assay);
								QualityControlFrame qcFrame = new QualityControlFrame(SampleListFrame.this, ampliconTrends, assay, "Variant allele freqency over time", "Sample ID", "Variant allele freqency");
								qcFrame.setVisible(true);
							} catch (Exception e1) {
								HMVVDefectReportFrame.showHMVVDefectReportFrame(SampleListFrame.this, e1);
							}
						}
					});
				}
			}
		}catch(Exception e){
			//unable to get assays
		}

		adminMenu.addSeparator();
		adminMenu.add(databaseInformationMenuItem);
		adminMenu.add(refreshLabel);

		setJMenuBar(menuBar);

		ActionListener listener = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(e.getSource() == enterSampleMenuItem){
						EnterSample sampleEnter = new EnterSample(SampleListFrame.this, tableModel);
						sampleEnter.setVisible(true);
					}else if(e.getSource() == monitorPipelinesItem) {
						handleMonitorPipelineClick();
					}else if(e.getSource() == databaseInformationMenuItem){
						handledatabaseInformationClick();
					}else if(e.getSource() == newAssayMenuItem){
						CreateAssay createAssay = new CreateAssay(SampleListFrame.this);
						createAssay.setVisible(true);
						}
				} catch (Exception e1) {
					HMVVDefectReportFrame.showHMVVDefectReportFrame(SampleListFrame.this, e1);
				}
			}
		};

		enterSampleMenuItem.addActionListener(listener);
		newAssayMenuItem.addActionListener(listener);
		monitorPipelinesItem.addActionListener(listener);
		qualityControlMenuItem.addActionListener(listener);
		databaseInformationMenuItem.addActionListener(listener);
	}

	public void addSample(Sample sample){
		tableModel.addSample(sample);
		updatePipelinesASynch();
	}

	private void createComponents(){
		table = new JTable(tableModel){
			private static final long serialVersionUID = 1L;

			//Implement table header tool tips.
			@Override
			protected JTableHeader createDefaultTableHeader() {
				return new JTableHeader(columnModel) {
					private static final long serialVersionUID = 1L;

					public String getToolTipText(MouseEvent e) {
						int index = table.columnAtPoint(e.getPoint());
						int realIndex = table.convertColumnIndexToModel(index);
						return tableModel.getColumnDescription(realIndex);
					}
				};
			}

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
				Component c = super.prepareRenderer(renderer, row, column);
				if (isCellSelected(row, column)){
					c.setForeground(Configurations.TABLE_SELECTION_FONT_COLOR);
					c.setBackground(Configurations.TABLE_SELECTION_COLOR);
				}else {
					c.setForeground(customColumns[column].color);
					c.setBackground(PipelineProgram.DEFAULT_COLOR);//default background to COMPLETE

					Sample currentSample = tableModel.getSample(table.convertRowIndexToModel(row));
					for(Pipeline p : pipelines) {
						if(currentSample.sampleID == p.sampleID) {
							c.setBackground(p.pipelineProgram.getColor());
							break;
						}
					}
				}
				return c;
			}
		};
		table.getTableHeader().setReorderingAllowed(false);

		((DefaultTableCellRenderer)table.getDefaultRenderer(Integer.class)).setHorizontalAlignment(SwingConstants.CENTER);
		((DefaultTableCellRenderer)table.getDefaultRenderer(String.class)).setHorizontalAlignment(SwingConstants.CENTER);
		((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

		table.setAutoCreateRowSorter(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		sorter = new TableRowSorter<SampleListTableModel>(tableModel);
		table.setRowSorter(sorter);
		//by default, sort from newest to oldest
		ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();

		tableScrollPane = new JScrollPane();
		tableScrollPane.setViewportView(table);

		assayComboBox = new JComboBox<String>();
		try {
			assayComboBox.addItem("All");
			for(String item : DatabaseCommands.getAllAssays()){
				assayComboBox.addItem(item);	
			}
		} catch (Exception e) {
			HMVVDefectReportFrame.showHMVVDefectReportFrame(this, e);
		}
		
		instrumentComboBox = new JComboBox<String>();
		try {
			instrumentComboBox.addItem("All");
			for(String item : DatabaseCommands.getAllInstruments()){
				instrumentComboBox.addItem(item);	
			}
		} catch (Exception e) {
			HMVVDefectReportFrame.showHMVVDefectReportFrame(this, e);
		}

		sampleSearchButton = new JButton("Sample Search");
		sampleSearchButton.setToolTipText("Open sample search window");
		sampleSearchButton.setFont(GUICommonTools.TAHOMA_BOLD_12);

		resetButton = new JButton("Reset Filters");
		resetButton.setToolTipText("Reset Sample Filters (remove all filters)");
		resetButton.setFont(GUICommonTools.TAHOMA_BOLD_12);

		assayLabel = new JLabel("Assay");
		assayLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		
		instrumentLabel = new JLabel("Instrument");
		instrumentLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		
		mrnLabel = new JLabel("MRN");
		mrnLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		
		mrnTextField = new JTextField();
		mrnTextField.setColumns(10);
		
		runIDLabel = new JLabel("Run ID");
		runIDLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);

		runIDTextField = new JTextField();
		runIDTextField.setColumns(10);
	}

	private void layoutComponents(){
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGap(59)
						.addComponent(assayLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(assayComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addComponent(instrumentLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(instrumentComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addComponent(mrnLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(mrnTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addComponent(runIDLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(runIDTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addComponent(sampleSearchButton)
						.addGap(18)
						.addComponent(resetButton))
				.addGroup(groupLayout.createSequentialGroup()
						.addGap(20)
						.addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 969, Short.MAX_VALUE)
						.addGap(20))
				);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
										.addGap(25)
										.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
												.addComponent(assayLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(assayComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(instrumentLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(instrumentComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(sampleSearchButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(resetButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(mrnLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(mrnTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(runIDLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(runIDTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
						.addGap(25)
						.addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
						.addGap(25))
				);
		getContentPane().setLayout(groupLayout);

		if(Configurations.isTestEnvironment()) {
			getContentPane().setBackground(Configurations.TEST_ENV_COLOR);
		}

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
		table.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int column = table.columnAtPoint(e.getPoint());
				table.setCursor(customColumns[column].cursor);
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent c) {
				handleTableMousePressed(c);
			}
		});

		assayComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				refilterTable();
			}
		});
		
		instrumentComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				refilterTable();
			}
		});

		sampleSearchButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sampleSearchAction();
			}
		});

		resetButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				resetFilters();
				refilterTable();
			}
		});

		DocumentListener documentListener = new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
				refilterTable();
			}
			public void removeUpdate(DocumentEvent e) {
				refilterTable();
			}
			public void insertUpdate(DocumentEvent e) {
				refilterTable();
			}
		};
		
		mrnTextField.getDocument().addDocumentListener(documentListener);
		runIDTextField.getDocument().addDocumentListener(documentListener);
	}

	private Sample getCurrentlySelectedSample(){
		int viewRow = table.getSelectedRow();
		int modelRow = table.convertRowIndexToModel(viewRow);
		return tableModel.getSample(modelRow);
	}
	
	private void handleTableMousePressed(MouseEvent c) {
		try{
			Point pClick = c.getPoint();
			if(table.columnAtPoint (pClick) == loadSampleResultsColumn){
				handleMutationClick();
			}else if(table.columnAtPoint (pClick) == qcColumn){
				handleQCClick();
			}else if(table.columnAtPoint (pClick) == sampleEditColumn){
				handleEditSampleClick();
			}else if(table.columnAtPoint (pClick) == mrnColumn){
				handleMRNClick();
			}else if(table.columnAtPoint (pClick) == runIDColumn){
				handleRunIDClick();
			}
		}catch (Exception e){
			HMVVDefectReportFrame.showHMVVDefectReportFrame(SampleListFrame.this, e);
		}
	}

	private void handleMonitorPipelineClick() throws Exception{
		Thread monitorPipelineThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					MonitorPipelines monitorpipelines = new MonitorPipelines(SampleListFrame.this);
					monitorpipelines.setVisible(true);
				} catch (Exception e) {
					HMVVDefectReportFrame.showHMVVDefectReportFrame(SampleListFrame.this, e, "Error loading Monitor Pipeline window.");
				}
				setCursor(Cursor.getDefaultCursor());
			}
		});
		monitorPipelineThread.start();
	}

	private void handledatabaseInformationClick() throws Exception {
		DatabaseInformation dbinfo = new DatabaseInformation(this);
		dbinfo.setVisible(true);
	}

	private void handleMutationClick() throws Exception{
		Thread mutationListThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					Sample currentSample = getCurrentlySelectedSample();

					if (currentSample instanceof TMBSample){//not ideal to condition using instanceof

					    TumorMutationBurdenFrame tmbFrame = new TumorMutationBurdenFrame(SampleListFrame.this, (TMBSample)currentSample);
					    tmbFrame.setVisible(true);

					} else{
						ArrayList<Mutation> mutations = DatabaseCommands.getBaseMutationsBySample(currentSample);
						MutationList mutationList = new MutationList(mutations);
						MutationListFrame mutationListFrame = new MutationListFrame(SampleListFrame.this, currentSample, mutationList);
						mutationListFrame.setVisible(true);
					}

				} catch (Exception e) {
					HMVVDefectReportFrame.showHMVVDefectReportFrame(SampleListFrame.this, e, "Error loading mutation data.");
				}
				table.setCursor(Cursor.getDefaultCursor());
			}
		});
		mutationListThread.start();
	}

	private void handleQCClick(){
		try {
			table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			Sample currentSample = getCurrentlySelectedSample();

			if (currentSample instanceof TMBSample){//not ideal to condition using instanceof

			    TumorMutationBurdenQCFrame tmbQC = new TumorMutationBurdenQCFrame(this, (TMBSample)currentSample);
                tmbQC.setVisible(true);

			}else {

			    ViewAmpliconFrame amplicon = new ViewAmpliconFrame(this, currentSample);
				amplicon.setVisible(true);

			}
		} catch (Exception e) {

		    HMVVDefectReportFrame.showHMVVDefectReportFrame(SampleListFrame.this, e);

		}
		table.setCursor(Cursor.getDefaultCursor());
	}

	private void handleMRNClick() {
		Sample sample = getCurrentlySelectedSample();
		mrnTextField.setText(sample.getMRN());
	}
	
	private void handleRunIDClick() {
		Sample sample = getCurrentlySelectedSample();
		runIDTextField.setText(sample.runID);
		assayComboBox.setSelectedItem(sample.assay);
		instrumentComboBox.setSelectedItem(sample.instrument);
	}
	
	private void handleEditSampleClick() throws Exception{
		//Edit sample
		int viewRow = table.getSelectedRow();
		final int modelRow = table.convertRowIndexToModel(viewRow);
		Sample sample = getCurrentlySelectedSample();

		EditSampleFrame editSample = new EditSampleFrame(this, sample);
		editSample.setVisible(true);
		editSample.addConfirmListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					//editSample form submission action
					Sample updatedSample = editSample.getUpdatedSample();
					DatabaseCommands.updateSample(updatedSample);
					tableModel.updateSample(modelRow, updatedSample);
				} catch (Exception e1) {
					HMVVDefectReportFrame.showHMVVDefectReportFrame(editSample, e1);
				}
				editSample.dispose();
			}
		});
		editSample.addDeleteListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String result = JOptionPane.showInputDialog(SampleListFrame.this, "Type DELETE to delete this sample.", "Delete sample?", JOptionPane.QUESTION_MESSAGE);
					if(result == null) {
						return;
					}
					if(result.equals("DELETE")) {
						DatabaseCommands.deleteSample(sample.sampleID);
						tableModel.deleteSample(modelRow);
						JOptionPane.showMessageDialog(SampleListFrame.this, "Sample deleted.");
						editSample.dispose();
					}else {
						JOptionPane.showMessageDialog(SampleListFrame.this, result + " is not DELETE. Deletion cancelled.");
					}
				} catch (SQLException e1) {
					HMVVDefectReportFrame.showHMVVDefectReportFrame(editSample, e1);
				}
			}
		});
	}

	public void refilterTable(){
		RowFilter<SampleListTableModel, Integer> rowFilter = new RowFilter<SampleListTableModel, Integer>(){

			private boolean doesAssayMatch(Entry<? extends SampleListTableModel, ? extends Integer> entry){
				if(assayComboBox.getSelectedIndex() == 0){//index 0 is "All"
					return true;
				}
				int row = entry.getIdentifier();
				Sample sample = tableModel.getSample(row);
				if(assayComboBox.getSelectedItem().toString().equals(sample.assay)){
					return true;
				}else{
					return false;
				}
			}
			
			private boolean doesInstrumentMatch(Entry<? extends SampleListTableModel, ? extends Integer> entry){
				if(instrumentComboBox.getSelectedIndex() == 0){//index 0 is "All"
					return true;
				}
				int row = entry.getIdentifier();
				Sample sample = tableModel.getSample(row);
				if(instrumentComboBox.getSelectedItem().toString().equals(sample.instrument)){
					return true;
				}else{
					return false;
				}
			}
			
			private boolean doesMRNMatch(Entry<? extends SampleListTableModel, ? extends Integer> entry){
				int row = entry.getIdentifier();
				Sample sample = tableModel.getSample(row);
				String mrn = mrnTextField.getText();
				if(mrn.equals("")){
					return true;
				}else{
					return sample.getMRN().contains(mrn);
				}
			}

			private boolean doesRunIDMatch(Entry<? extends SampleListTableModel, ? extends Integer> entry){
				int row = entry.getIdentifier();
				Sample sample = tableModel.getSample(row);
				String runID = runIDTextField.getText();
				if(runID.equals("")){
					return true;
				}else{
					return sample.runID.toLowerCase().contains(runID.toLowerCase());
				}
			}

			@Override
			public boolean include(Entry<? extends SampleListTableModel, ? extends Integer> entry) {
				return doesAssayMatch(entry) && doesInstrumentMatch(entry) && doesMRNMatch(entry) && doesRunIDMatch(entry);
			}
		};
		sorter.setRowFilter(rowFilter);
	}

	private void sampleSearchAction(){
		SampleSearchFrame searchSample = new SampleSearchFrame(this);
		searchSample.setVisible(true);

		searchSample.addConfirmListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RowFilter<SampleListTableModel, Integer> rowFilter = new RowFilter<SampleListTableModel, Integer>(){
					@Override
					public boolean include(Entry<? extends SampleListTableModel , ? extends Integer> entry) {
						SampleListTableModel model = entry.getModel();
						int row = entry.getIdentifier();
						Sample sample = model.getSample(row);
						return searchSample.include(sample);
					}
				};
				sorter.setRowFilter(rowFilter);
				String assay = searchSample.getAssay();
				assayComboBox.setSelectedItem(assay);
				searchSample.dispose();
			}
		});
	}

	private void resetFilters(){
		assayComboBox.setSelectedIndex(0);
		instrumentComboBox.setSelectedIndex(0);
		mrnTextField.setText("");
		runIDTextField.setText("");
	}

	private void addExceptionSamples() throws Exception{

			ArrayList<Sample> exception_samples = new ArrayList<>();

			for (Sample s : this.samples){
				ArrayList<Sample> new_samples = DatabaseCommands.getExceptionSamples(s.sampleID,s.getMRN());
				if (new_samples.size()>0) {
                    exception_samples.addAll(new_samples);
                }
			}
			this.samples.addAll(exception_samples);
	}
}
