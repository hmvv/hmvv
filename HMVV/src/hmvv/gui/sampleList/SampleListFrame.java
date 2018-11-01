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
import hmvv.model.GeneQCDataElementTrend;
import hmvv.model.Mutation;
import hmvv.model.Pipeline;
import hmvv.model.PipelineProgram;
import hmvv.model.Sample;

public class SampleListFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private JTextField textRunID;
	private TableRowSorter<SampleListTableModel> sorter;
	
	//Menu
	private JMenuBar menuBar;
	private JMenu adminMenu;
	private JMenuItem enterSampleMenuItem;
	private JMenuItem monitorPipelinesItem;
	private JMenuItem newAssayMenuItem;
    private JMenuItem databaseInformationMenuItem;
	private JMenu qualityControlMenuItem;

	
	//Table
	private JTable table;
	private SampleListTableModel tableModel;
	private JScrollPane tableScrollPane;
	
	//Buttons
	private JButton sampleSearchButton;
	private JButton resetButton;
	
	//Labels
	private JLabel lblChooseAnAssay;
	private JLabel lblRunid;

	private JComboBox<String> assayComboBox;

	private HMVVTableColumn[] customColumns;
	
	//Asynchronous sample status updates
	private Thread pipelineRefreshThread;
	private final int secondsToSleep = 60;
	private volatile long timeLastRefreshed = 0;
	private volatile ArrayList<Pipeline> pipelines;
	private volatile JMenuItem refreshLabel;
	private int mutationLinkColumn =0;
	private int ampliconColumn = 18;
	private int sampleEditColumn = 19;
	
	/**
	 * Initialize the contents of the frame.
	 */
	public SampleListFrame(HMVVLoginFrame parent, ArrayList<Sample> samples) {
		super( Configurations.DATABASE_NAME + " : Sample List");
		tableModel = new SampleListTableModel(samples);
		
		Rectangle bounds = GUICommonTools.getScreenBounds(parent);
		setSize((int)(bounds.width*.97), (int)(bounds.height*.90));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
		    public void windowClosing(WindowEvent e){
		    	SSHConnection.shutdown();
		    }
		});		
		
		customColumns = HMVVTableColumn.getCustomColumnArray(tableModel.getColumnCount(), mutationLinkColumn, ampliconColumn, sampleEditColumn);
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
		monitorPipelinesItem = new JMenuItem("Monitor Pipelines");
        databaseInformationMenuItem = new JMenuItem("Database Information");
		qualityControlMenuItem = new JMenu("Quality Control");
		newAssayMenuItem = new JMenuItem("New Assay (super user only)");
		newAssayMenuItem.setEnabled(SSHConnection.isSuperUser());
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
				//TODO support exome QC
				if(assay.equals("exome")) {
					continue;
				}
				
				JMenuItem assayMenuItem = new JMenuItem(assay + "_AmpliconCoverageDepth");
				qualityControlMenuItem.add(assayMenuItem);
				assayMenuItem.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							TreeMap<String, GeneQCDataElementTrend> ampliconTrends = DatabaseCommands.getAmpliconQCData(assay);
							QualityControlFrame.showQCChart(SampleListFrame.this, ampliconTrends, assay, "Coverage depth over time", "Sample ID", "Coverage Depth");
						} catch (Exception e1) {
							HMVVDefectReportFrame.showHMVVDefectReportFrame(SampleListFrame.this, e1);
						}
					}
				});
				if(assay.equals("neuro")) {
					assayMenuItem.setEnabled(false);//TODO the amplicon names do not have the gene in them. Need to find the mapping if we need this feature.
				}
				assayMenuItem = new JMenuItem(assay + "_VariantAlleleFrequency");
				qualityControlMenuItem.add(assayMenuItem);
				assayMenuItem.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							TreeMap<String, GeneQCDataElementTrend> ampliconTrends = DatabaseCommands.getSampleQCData(assay);
							QualityControlFrame.showQCChart(SampleListFrame.this, ampliconTrends, assay, "Variant allele freqency over time", "Sample ID", "Variant allele freqency");
						} catch (Exception e1) {
							HMVVDefectReportFrame.showHMVVDefectReportFrame(SampleListFrame.this, e1);
						}
					}
				});
				
				qualityControlMenuItem.addSeparator();
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
						if(SSHConnection.isSuperUser()){
							CreateAssay createAssay = new CreateAssay(SampleListFrame.this);
							createAssay.setVisible(true);
						}else{
							JOptionPane.showMessageDialog(SampleListFrame.this, "Only authorized users can create an assay");
						}
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
				if(row == table.getSelectedRow()) {
					return c;
				}
				c.setForeground(customColumns[column].color);
				Sample currentSample = tableModel.getSample(table.convertRowIndexToModel(row));
				for(Pipeline p : pipelines) {
					if(currentSample.sampleID == p.sampleID) {
						c.setBackground(p.pipelineProgram.displayColor);
						return c;
					}
				}
				
				//If the sampleID was not found in the list of recent pipelines. Revert to default color.
				//TODO store pipeline program in database to allow display of failed runs from the distant past
				c.setBackground(PipelineProgram.COMPLETE.displayColor);
				return c;
			}
		};
		
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
		
		sampleSearchButton = new JButton("Sample Search");
		sampleSearchButton.setToolTipText("Open sample search window");
		sampleSearchButton.setFont(GUICommonTools.TAHOMA_BOLD_12);

		resetButton = new JButton("Reset Filters");
		resetButton.setToolTipText("Reset Sample Filters (remove all filters)");
		resetButton.setFont(GUICommonTools.TAHOMA_BOLD_12);
		
		lblChooseAnAssay = new JLabel("Choose an assay");
		lblChooseAnAssay.setFont(GUICommonTools.TAHOMA_BOLD_14);
		
		lblRunid = new JLabel("runID");
		lblRunid.setFont(GUICommonTools.TAHOMA_BOLD_14);
		
		textRunID = new JTextField();
		textRunID.setColumns(10);
	}

	private void layoutComponents(){
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGap(59)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblChooseAnAssay, GroupLayout.PREFERRED_SIZE, 129, GroupLayout.PREFERRED_SIZE))
						.addGap(7)
						.addComponent(assayComboBox, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addComponent(lblRunid, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(textRunID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addComponent(sampleSearchButton)
						.addGap(18)
						.addComponent(resetButton)
						.addGap(145))
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
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
												.addGroup(groupLayout.createSequentialGroup()
														.addComponent(lblChooseAnAssay, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
												.addGroup(groupLayout.createSequentialGroup()
														.addComponent(assayComboBox, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))))
								.addGroup(groupLayout.createSequentialGroup()
										.addGap(25)
										.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
												.addComponent(sampleSearchButton, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
												.addComponent(resetButton, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
												.addComponent(lblRunid, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
												.addComponent(textRunID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
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
			public void mouseClicked(MouseEvent c) {
				handleTableMouseClick(c);
			}
		});
		
		assayComboBox.addItemListener(new ItemListener() {
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
		
		textRunID.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
				refilterTable();
			}
			public void removeUpdate(DocumentEvent e) {
				refilterTable();
			}
			public void insertUpdate(DocumentEvent e) {
				refilterTable();
			}
		});
	}

	private Sample getCurrentlySelectedSample(){
		int viewRow = table.getSelectedRow();
		int modelRow = table.convertRowIndexToModel(viewRow);
		return tableModel.getSample(modelRow);
	}

	private void handleTableMouseClick(MouseEvent c){
		try{
			Point pClick = c.getPoint();
			if(table.columnAtPoint (pClick) == mutationLinkColumn){
				handleMutationClick();
			}else if(table.columnAtPoint (pClick) == ampliconColumn){
				handleShowAmpliconClick();
			}else if(table.columnAtPoint (pClick) == sampleEditColumn){
				handleEditSampleClick();
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
					ArrayList<Mutation> mutations = DatabaseCommands.getBaseMutationsBySample(currentSample);
					for(Mutation m : mutations){
						m.setCosmicID("LOADING...");
					}
					MutationList mutationList = new MutationList(mutations);
					MutationListFrame mutationListFrame = new MutationListFrame(SampleListFrame.this, currentSample, mutationList);
					mutationListFrame.setVisible(true);
				} catch (Exception e) {
					HMVVDefectReportFrame.showHMVVDefectReportFrame(SampleListFrame.this, e, "Error loading mutation data.");
				}
				table.setCursor(Cursor.getDefaultCursor());
			}
		});
		mutationListThread.start();
	}

	private void handleShowAmpliconClick(){
		//Show amplicon
		try {
            table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			Sample currentSample = getCurrentlySelectedSample();
			ViewAmpliconFrame amplicon = new ViewAmpliconFrame(this,currentSample);
			amplicon.setVisible(true);
		} catch (Exception e) {
			HMVVDefectReportFrame.showHMVVDefectReportFrame(SampleListFrame.this, e);
		}
        table.setCursor(Cursor.getDefaultCursor());
	}

	private void handleEditSampleClick() throws Exception{
		//Edit sample
		int viewRow = table.getSelectedRow();
		final int modelRow = table.convertRowIndexToModel(viewRow);
		Sample sample = getCurrentlySelectedSample();
		
		String currentUser = SSHConnection.getUserName();
		if(!currentUser.equals(sample.enteredBy) && !SSHConnection.isSuperUser()){
			JOptionPane.showMessageDialog(this, "Error: You can only edit samples entered by you!");
			return;
		}
		
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
					DatabaseCommands.deleteSample(sample.sampleID);
					tableModel.deleteSample(modelRow);
				} catch (SQLException e1) {
					HMVVDefectReportFrame.showHMVVDefectReportFrame(editSample, e1);
				}
				editSample.dispose();
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
			
			private boolean doesRunIDMatch(Entry<? extends SampleListTableModel, ? extends Integer> entry){
				int row = entry.getIdentifier();
				Sample sample = tableModel.getSample(row);
				String runID = textRunID.getText();
				if(runID.equals("")){
					return true;
				}else{
					return sample.runID.toLowerCase().contains(runID.toLowerCase());
				}
			}
			
			@Override
			public boolean include(Entry<? extends SampleListTableModel, ? extends Integer> entry) {
				return doesAssayMatch(entry) && doesRunIDMatch(entry);
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
		textRunID.setText("");
	}
}
