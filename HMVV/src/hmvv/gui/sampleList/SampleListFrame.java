package hmvv.gui.sampleList;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.mutationlist.MutationListFrame;
import hmvv.gui.mutationlist.MutationGermlineListFrame;
import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.gui.GUICommonTools;
import hmvv.io.DatabaseCommands;
import hmvv.main.Configurations;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.main.HMVVFrame;
import hmvv.model.*;

public class SampleListFrame extends JPanel {
	private static final long serialVersionUID = 1L;

	private TableRowSorter<SampleListTableModel> sorter;

	private HMVVFrame parent;

	//Table
	private JTable table;
	private SampleListTableModel tableModel;
	private JScrollPane tableScrollPane;

	//Buttons
	private JButton sampleSearchButton;
	private JButton resetButton;
	private JButton refreshButton;

	//Filters
	private JLabel assayLabel;
	private JComboBox<Assay> assayComboBox;
	
	private JLabel instrumentLabel;
	private JComboBox<Instrument> instrumentComboBox;
	
	private JLabel mrnLabel;
	private JTextField mrnTextField;
	
	private JLabel runIDLabel;
	private JTextField runIDTextField;
	

	private HMVVTableColumn[] customColumns;
	
	private int loadSampleResultsColumn = 0;
	private int mrnColumn = 3;
	private int runIDColumn = 10;
	private int qcColumn = 16;
	private int sampleEditColumn = 17;

	private volatile ArrayList<Pipeline> pipelines;
	

	/**
	 * Initialize the contents of the frame.
	 */
	public SampleListFrame(HMVVFrame parent, ArrayList<Sample> samples) throws Exception {
		super();
		this.parent = parent;
		tableModel = new SampleListTableModel(samples);
		customColumns = HMVVTableColumn.getCustomColumnArray(tableModel.getColumnCount(), loadSampleResultsColumn, qcColumn, mrnColumn, runIDColumn, sampleEditColumn);
		pipelines = new ArrayList<Pipeline>();//initialize as blank so that if setupPipelineRefreshThread() fails, the object is still instantiated
		

		createComponents();
		layoutComponents();
		activateComponents();
	}
	
	public SampleListTableModel getSampleTabelModel() {
		return this.tableModel;
	}

	public void addSample(Sample sample){
		tableModel.addSample(sample);
		updatePipelinesASynch();
	}
	
	public boolean updatePipelinesASynch() {
		try {
			pipelines = DatabaseCommands.getAllPipelines();
			table.repaint();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	

	public void updateSampleTableModel() throws Exception {
			//tableModel = new SampleListTableModel(DatabaseCommands.getAllSamples());
			tableModel.fireTableDataChanged();
			table.repaint();

			ArrayList<Sample> samples = DatabaseCommands.getAllSamples();
			for(Sample s : samples) {
				tableModel.addOrUpdateSample(s);
		}
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

		assayComboBox = new JComboBox<Assay>();
		try {
			assayComboBox.addItem(Assay.getAssay("All"));
			for(Assay item : DatabaseCommands.getAllAssays()){
				assayComboBox.addItem(item);	
			}
		} catch (Exception e) {
			HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, e);
		}
		
		instrumentComboBox = new JComboBox<Instrument>();
		try {
			instrumentComboBox.addItem(Instrument.getInstrument("All"));
			for(Instrument item : DatabaseCommands.getAllInstruments()){
				instrumentComboBox.addItem(item);	
			}
		} catch (Exception e) {
			HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, e);
		}

		sampleSearchButton = new JButton("Sample Search");
		sampleSearchButton.setToolTipText("Open sample search window");
		sampleSearchButton.setFont(GUICommonTools.TAHOMA_BOLD_12);

		resetButton = new JButton("Reset Filters");
		resetButton.setToolTipText("Reset Sample Filters (remove all filters)");
		resetButton.setFont(GUICommonTools.TAHOMA_BOLD_12);

		refreshButton = new JButton("Refresh");
		refreshButton.setToolTipText("Refresh Sample data");
		refreshButton.setFont(GUICommonTools.TAHOMA_BOLD_12);

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
		GroupLayout groupLayout = new GroupLayout(this);
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
						.addComponent(resetButton)
						.addGap(18)
						.addComponent(refreshButton))
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
												.addComponent(refreshButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(mrnLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(mrnTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(runIDLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(runIDTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
						.addGap(25)
						.addComponent(tableScrollPane, GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
						.addGap(25))
				);
		setLayout(groupLayout);

		if(Configurations.isTestEnvironment()) {
			setBackground(Configurations.TEST_ENV_COLOR);
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
				if(!mutationListLoading) {
					int column = table.columnAtPoint(e.getPoint());
					table.setCursor(customColumns[column].cursor);	
				}
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent c) {
				if(!mutationListLoading) {
					handleTableMousePressed(c);
				}
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
		
		refreshButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				resetFilters();
				refilterTable();
				try {
					updateSampleTableModel();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
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
		if(c.getClickCount() != 1) {
			//ignore multi-clicks
			return;
		}
		
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
			HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, e);
		}
	}

	private void handleMutationClick() throws Exception{
		
		Sample currentSample = getCurrentlySelectedSample();
		
	    if (currentSample instanceof TMBSample){//not ideal to condition using instanceof
		    parent.createTumorMutationBurdenFrame((TMBSample) currentSample);
		} else if (currentSample.assay.assayName.equals("cardiac_exome")){
			MutationListLoaderGermline loader = new MutationListLoaderGermline(parent, currentSample);
			loader.execute();
		}else{
			MutationListLoader loader = new MutationListLoader(parent, currentSample);
			loader.execute();
		}
	}

	private void handleQCClick(){
		try {
			table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			Sample currentSample = getCurrentlySelectedSample();

			if (currentSample instanceof TMBSample){//not ideal to condition using instanceof

			    TumorMutationBurdenQCFrame tmbQC = new TumorMutationBurdenQCFrame(parent, (TMBSample)currentSample);
                tmbQC.setVisible(true);

			}else {

			    ViewAmpliconFrame amplicon = new ViewAmpliconFrame(parent, currentSample);
				amplicon.setVisible(true);

			}
		} catch (Exception e) {

		    HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, e);

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

		EditSampleFrame editSample = new EditSampleFrame(parent, sample);
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
					String result = JOptionPane.showInputDialog(parent, "Type DELETE to delete this sample.", "Delete sample?", JOptionPane.QUESTION_MESSAGE);
					if(result == null) {
						return;
					}
					if(result.equals("DELETE")) {
						DatabaseCommands.deleteSample(sample.sampleID);
						tableModel.deleteSample(modelRow);
						JOptionPane.showMessageDialog(parent, "Sample deleted.");
						editSample.dispose();
					}else {
						JOptionPane.showMessageDialog(parent, result + " is not DELETE. Deletion cancelled.");
					}
				} catch (SQLException e1) {
					HMVVDefectReportFrame.showHMVVDefectReportFrame(editSample, e1);
				}
			}
		});

		editSample.setVisible(true);
	}

	public void refilterTable(){
		RowFilter<SampleListTableModel, Integer> rowFilter = new RowFilter<SampleListTableModel, Integer>(){

			private boolean doesAssayMatch(Entry<? extends SampleListTableModel, ? extends Integer> entry){
				if(assayComboBox.getSelectedIndex() == 0){//index 0 is "All"
					return true;
				}
				int row = entry.getIdentifier();
				Sample sample = tableModel.getSample(row);
				if(assayComboBox.getSelectedItem().toString().equals(sample.assay.assayName)){
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
				if(instrumentComboBox.getSelectedItem().toString().equals(sample.instrument.instrumentName)){
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
		SampleSearchFrame searchSample = new SampleSearchFrame(parent);
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

		searchSample.setVisible(true);
	}

	private void resetFilters(){
		assayComboBox.setSelectedIndex(0);
		instrumentComboBox.setSelectedIndex(0);
		mrnTextField.setText("");
		runIDTextField.setText("");
	}
	
	private volatile boolean mutationListLoading = false;
	class MutationListLoader extends SwingWorker<MutationList, Void>{
//
		private final HMVVFrame parent;
		private final Sample sample;

		public MutationListLoader(HMVVFrame parent, Sample sample) {
			table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			this.parent = parent;
			this.sample = sample;
			mutationListLoading = true;
		}
		
		@Override
		protected MutationList doInBackground() throws Exception {

				ArrayList<MutationSomatic>  mutations = new ArrayList<MutationSomatic>();
				mutations = DatabaseCommands.getBaseMutationsBySample(sample);

                // trying to find a better way than copying, confirmed via reference address in both arraylists
				ArrayList<MutationCommon> common_mutations = new ArrayList<MutationCommon>();
				common_mutations.addAll(mutations);
				return new MutationList(common_mutations,Configurations.MUTATION_TYPE.SOMATIC);

		}

		@Override
	    public void done() {
			table.setCursor(Cursor.getDefaultCursor());
			mutationListLoading = false;
			try {
				MutationListFrame mutationListFrame = new MutationListFrame(parent, sample, get());
				mutationListFrame.setVisible(true);
				mutationListFrame.addWindowListener(new WindowAdapter() {
					public void windowClosed(WindowEvent evt) {
						parent.setEnabled(true);
					}
						
					public void windowClosing(WindowEvent evt) {
						parent.setEnabled(true);
					}
				}); 

			} catch (Exception e) {
				HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, e, "Error loading mutation data.");
			}
	    }	
	}

	class MutationListLoaderGermline extends SwingWorker<MutationList, Void>{
		//
		private final HMVVFrame parent;
		private final Sample sample;


		public MutationListLoaderGermline(HMVVFrame parent, Sample sample) {
			table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			this.parent = parent;
			this.sample = sample;
			mutationListLoading = true;
		}

		@Override
		protected MutationList doInBackground() throws Exception {

				ArrayList<MutationGermline>  mutations = new ArrayList<MutationGermline>();
				mutations = DatabaseCommands.getBaseGermlineMutationsBySample(sample);

			    ArrayList<MutationCommon>  common_mutations = new ArrayList<MutationCommon>();
			    common_mutations.addAll(mutations);
				return new MutationList(common_mutations,Configurations.MUTATION_TYPE.GERMLINE);

		}

		@Override
		public void done() {
			table.setCursor(Cursor.getDefaultCursor());
			mutationListLoading = false;
			try {
				MutationGermlineListFrame mutationListFrame = new MutationGermlineListFrame(parent, sample, get());
				mutationListFrame.setVisible(true);
			} catch (Exception e) {
				HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, e, "Error loading mutation data.");
			}
		}
	}
}
