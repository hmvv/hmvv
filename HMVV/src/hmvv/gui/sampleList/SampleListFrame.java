package hmvv.gui.sampleList;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
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
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import hmvv.gui.HMVVTableColumn;
import hmvv.gui.GUICommonTools;
import hmvv.gui.HMVVTableCellRenderer;
import hmvv.gui.adminFrames.CreateAssay;
import hmvv.gui.adminFrames.EnterSample;
import hmvv.gui.mutationlist.MutationListFrame;
import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.io.DatabaseCommands;
import hmvv.io.SSHConnection;
import hmvv.model.Mutation;
import hmvv.model.Sample;

public class SampleListFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private JTextField textRunID;
	private TableRowSorter<SampleListTableModel> sorter;
	
	//Menu
	private JMenuBar menuBar;
	private JMenu adminMenu;
	private JMenuItem enterSampleMenuItem;
	private JMenuItem newAssayMenuItem;
	
	//Table
	private JTable table;
	private SampleListTableModel tableModel;
	private JScrollPane tableScrollPane;
	
	//Buttons
	private JButton sampleSearchButton;
	private JButton resetButton;
	private JButton mutationSearchButton;
	private JButton loadIGVButton;

	//Labels
	private JLabel lblChooseAnAssay;
	private JLabel lblRunid;

	private JComboBox<String> assayComboBox;

	private HMVVTableColumn[] customColumns;
	
	/**
	 * Initialize the contents of the frame.
	 */
	public SampleListFrame(Component parent, ArrayList<Sample> samples) {
		super("Sample List");
		tableModel = new SampleListTableModel(samples);
		
		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*.97), (int)(bounds.height*.90));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		customColumns = HMVVTableColumn.getCustomColumnArray(tableModel.getColumnCount(), 0, 14, 16, 17);
		createMenu();
		createComponents();
		layoutComponents();
		activateComponents();
		setLocationRelativeTo(parent);
	}
	
	private void createMenu(){
		menuBar = new JMenuBar();
		adminMenu = new JMenu("Admin");
		enterSampleMenuItem = new JMenuItem("Enter Sample");
		newAssayMenuItem = new JMenuItem("New Assay (super user only)");
		newAssayMenuItem.setEnabled(SSHConnection.isSuperUser());
		
		menuBar.add(adminMenu);
		adminMenu.add(enterSampleMenuItem);
		adminMenu.add(newAssayMenuItem);
		setJMenuBar(menuBar);
		
		ActionListener listener = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == enterSampleMenuItem){
					EnterSample sampleEnter = new EnterSample(SampleListFrame.this);
					sampleEnter.setVisible(true);
				}else if(e.getSource() == newAssayMenuItem){
					if(SSHConnection.isSuperUser()){
						CreateAssay createAssay = new CreateAssay(SampleListFrame.this);
						createAssay.setVisible(true);
					}else{
						JOptionPane.showMessageDialog(SampleListFrame.this, "Only authorized users can create an assay");
					}
				}
			}
		};
		
		enterSampleMenuItem.addActionListener(listener);
		newAssayMenuItem.addActionListener(listener);
	}
	
	public void addSample(Sample sample){
		tableModel.addSample(sample);
	}

	private void createComponents(){
		table = new JTable(tableModel){
			private static final long serialVersionUID = 1L;

			//Implement table header tool tips.
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
		};

		table.setDefaultRenderer(Object.class, new HMVVTableCellRenderer(customColumns));
		table.setDefaultRenderer(Integer.class, new HMVVTableCellRenderer(customColumns));
		table.setAutoCreateRowSorter(true);
		
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
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
		
		sampleSearchButton = new JButton("Sample Search");
		sampleSearchButton.setToolTipText("Open sample search window");
		sampleSearchButton.setFont(GUICommonTools.TAHOMA_BOLD_12);

		resetButton = new JButton("Refresh");
		resetButton.setToolTipText("Reset Samples (remove all filters)");
		resetButton.setFont(GUICommonTools.TAHOMA_BOLD_12);

		mutationSearchButton = new JButton("Mutation Search");
		mutationSearchButton.setToolTipText("Open mutation search window");
		mutationSearchButton.setFont(GUICommonTools.TAHOMA_BOLD_12);

		loadIGVButton = new JButton("Load IGV");
		loadIGVButton.setToolTipText("Load the selected sample into IGV. IGV needs to be already opened");
		loadIGVButton.setFont(GUICommonTools.TAHOMA_BOLD_14);
		
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
						.addGap(18)
						.addComponent(mutationSearchButton)
						.addGap(18)
						.addComponent(loadIGVButton)
						.addGap(127))
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
												.addComponent(loadIGVButton, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
												.addComponent(mutationSearchButton, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
												.addComponent(sampleSearchButton, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
												.addComponent(resetButton, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
												.addComponent(lblRunid, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
												.addComponent(textRunID, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
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
		
		mutationSearchButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				mutationSearchAction();
			}
		});
		
		loadIGVButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					loadIGV();
				}catch(Exception ex){
					JOptionPane.showMessageDialog(SampleListFrame.this, ex.getMessage());
				}
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
			if(table.columnAtPoint (pClick) == 0){
				handleMutationClick();
			}else if(table.columnAtPoint (pClick) == 14){
				handleEditNoteClick();
			}else if(table.columnAtPoint (pClick) == 16){
				handleShowAmpliconClick();
			}else if(table.columnAtPoint (pClick) == 17){
				handleEditSampleClick();
			}
		}catch (Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	private void handleMutationClick() throws Exception{
		Sample currentSample = getCurrentlySelectedSample();
		ArrayList<Mutation> mutations = DatabaseCommands.getMutationDataByID(currentSample.ID);
		
		for(Mutation m : mutations){
			m.setCosmicID("LOADING...");
		}
		
		String header = currentSample.getLastName() + "," + currentSample.getFirstName() +
				" (runID = " + currentSample.runID + ", sampleID = " + currentSample.sampleID + ", callerID = " + currentSample.callerID + ")";
		MutationList mutationList = new MutationList(mutations);
		MutationListFrame mutationListFrame = new MutationListFrame(SampleListFrame.this, mutationList, header);
		mutationListFrame.setVisible(true);
	}

	private void handleEditNoteClick(){
		//Edit note
		Sample currentSample = getCurrentlySelectedSample();
		final int sampleID = currentSample.ID;
		String lastName = currentSample.getLastName();
		String firstName = currentSample.getFirstName();
		String note = currentSample.getNote();
		String name = String.format("%s,%s", lastName,firstName);

		EditNoteDialog noteDialog = new EditNoteDialog(name, note);
		noteDialog.setVisible(true);
		noteDialog.addConfirmListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					DatabaseCommands.updateSampleNote(sampleID, noteDialog.getNote());
					noteDialog.setVisible(false);
					currentSample.setNote(noteDialog.getNote());
					tableModel.fireTableDataChanged();
				}catch (Exception e1){
					JOptionPane.showMessageDialog(SampleListFrame.this, e1.getMessage());
				}
			}
		});
	}

	private void handleShowAmpliconClick(){
		//Show amplicon
		try {
			Sample currentSample = getCurrentlySelectedSample();
			int sampleID = currentSample.ID;
			ViewAmpliconFrame amplicon = new ViewAmpliconFrame(sampleID);
			amplicon.setVisible(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(SampleListFrame.this, e);
		}
	}

	private void handleEditSampleClick() throws Exception{
		//Edit sample
		int viewRow = table.getSelectedRow();
		final int modelRow = table.convertRowIndexToModel(viewRow);
		Sample currentSample = getCurrentlySelectedSample();
		int sampleID = currentSample.ID;
		String enteredBy = currentSample.enteredBy;
		String currentUser = SSHConnection.getUserName();

		if(!currentUser.equals(enteredBy) && !SSHConnection.isSuperUser()){
			JOptionPane.showMessageDialog(this, "Error: You can only edit samples entered by you!");
			return;
		}
		
		//Start editing sample page
		Sample sample = DatabaseCommands.getSample(sampleID);
		EditSampleFrame editSample = new EditSampleFrame(sample);
		editSample.setVisible(true);
		editSample.addConfirmListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					//editSample form submission action
					Sample updatedSample = editSample.getUpdatedSample();
					DatabaseCommands.updateSample(updatedSample);
					tableModel.updateSample(modelRow, updatedSample);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(SampleListFrame.this, e1);
				}
				editSample.dispose();
			}
		});
		editSample.addDeleteListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					DatabaseCommands.deleteSample(sampleID);
					tableModel.deleteSample(modelRow);
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(editSample, e1);
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
		SampleSearchFrame searchSample = new SampleSearchFrame();
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

	private void mutationSearchAction(){
		MutationSearchDialog searchMutation = new MutationSearchDialog();
		searchMutation.setVisible(true);
		searchMutation.addConfirmListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					ArrayList<Mutation> mutations = searchMutation.getMutationSearchResults();
					MutationList mutationSearchList = new MutationList(mutations);
					MutationListFrame mutationListFrame = new MutationListFrame(SampleListFrame.this, mutationSearchList, "Search Result");
					mutationListFrame.setVisible(true);
					searchMutation.dispose();
				}catch(Exception ex){
					JOptionPane.showMessageDialog(SampleListFrame.this, ex.getMessage());
				}
			}
		});
	}

	private void loadIGV() throws Exception{
		int[] selectedView = table.getSelectedRows();
		int[] selectedModel = new int[selectedView.length];
		if(selectedView.length == 0){
			JOptionPane.showMessageDialog(this, "Please select/highlight at least one sample");
			return;
		}
		for(int i = 0; i < selectedView.length; i++){
			selectedModel[i] = table.convertRowIndexToModel(selectedView[i]);
			String runID = getValueNotNull(selectedModel[i], 9);
			String sampleID = getValueNotNull(selectedModel[i], 10);
			String callerID = getValueNotNull(selectedModel[i], 12);
			String instrument = getValueNotNull(selectedModel[i], 2);
			String httpFile = null;
			if(instrument.equals("pgm")){
				httpFile = SSHConnection.findPGMSample(runID, sampleID, callerID);
			}else if(instrument.equals("proton")){
				httpFile = SSHConnection.findProtonSample(runID, sampleID, callerID);
			}else{
				httpFile = SSHConnection.findIlluminaSample(instrument, runID, sampleID);
			}
			String response = SSHConnection.loadFileIntoIGV(this, httpFile);
			if(!response.equals("")){
				JOptionPane.showMessageDialog(this, response);
			}
		}
	}

	private String getValueNotNull(int row, int column){
		if(table.getModel().getValueAt(row, column) == null){
			return "";
		}
		return (table.getModel().getValueAt(row, column)).toString();
	}
}
