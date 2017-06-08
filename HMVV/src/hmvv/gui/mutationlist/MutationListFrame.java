package hmvv.gui.mutationlist;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableRowSorter;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.tablemodels.BasicTableModel;
import hmvv.gui.mutationlist.tablemodels.ClinVarTableModel;
import hmvv.gui.mutationlist.tablemodels.CoordinatesTableModel;
import hmvv.gui.mutationlist.tablemodels.G1000TableModel;
import hmvv.gui.mutationlist.tablemodels.SampleTableModel;
import hmvv.gui.mutationlist.tables.BasicTable;
import hmvv.gui.mutationlist.tables.ClinVarTable;
import hmvv.gui.mutationlist.tables.CoordinatesTable;
import hmvv.gui.mutationlist.tables.G1000Table;
import hmvv.gui.mutationlist.tables.SampleTable;
import hmvv.gui.sampleList.ReportFrame;
import hmvv.io.DatabaseCommands;
import hmvv.io.MutationReportGenerator;
import hmvv.model.Mutation;

public class MutationListFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Mutation> mutations;
	
	private BasicTable basicTabTable;
	private BasicTableModel basicTabTableModel;

	private CoordinatesTable coordinatesTabTable;
	private CoordinatesTableModel coordinatesTabTableModel;
	
	private G1000Table g1000TabTable;
	private G1000TableModel g1000TabTableModel;
	
	private ClinVarTable clinVarTabTable;
	private ClinVarTableModel clinVarTabTableModel;
	
	private SampleTable sampleTabTable;
	private SampleTableModel sampleTabTableModel;
	
	private JPanel contentPane;
	
	private JCheckBox rdbtnShowReportedOnly;
	private JCheckBox rdbtnCosmic;
	private JButton btnReport;
	private JButton btnReset;
	private JButton btnExport;
	
	private JTextField textFreqFrom;
	private JTextField textVarFreqTo;
	private JTextField textMinRD;
	private JTextField textOccuranceFrom;
	
	private TableRowSorter<BasicTableModel> sorter;
	private List<RowFilter<BasicTableModel, Integer>> filters;
	
	/**
	 * Create the frame.
	 */
	public MutationListFrame(Component parent, ArrayList<Mutation> mutations, String title) throws Exception{
		super("Mutation List " + title);
		this.mutations = mutations;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*.85), (int)(bounds.height*.85));
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		constructFilterPanel();
		
		constructTabs();
		constructListSelectionListener();
		constructRowSorter();
		layoutComponents();
		createRowFilter();
		setLocationRelativeTo(parent);
		loadMissingDataAsynchronous();
	}
	
	private void constructFilterPanel() {
		constructCheckBoxFilters();
		constructTextFieldFilters();
		constructButtons();
	}
	
	private void constructTabs(){
		basicTabTableModel = new BasicTableModel(mutations);
		basicTabTable = new BasicTable(basicTabTableModel);
		
		coordinatesTabTableModel = new CoordinatesTableModel(mutations);
		coordinatesTabTable = new CoordinatesTable(coordinatesTabTableModel);
		
		g1000TabTableModel = new G1000TableModel(mutations);
		g1000TabTable = new G1000Table(g1000TabTableModel);
		
		clinVarTabTableModel = new ClinVarTableModel(mutations);
		clinVarTabTable = new ClinVarTable(clinVarTabTableModel);
		
		sampleTabTableModel = new SampleTableModel(mutations);
		sampleTabTable = new SampleTable(sampleTabTableModel);
	}
	
	private void constructCheckBoxFilters(){
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sorter.sort();
			}
		};
		
		rdbtnCosmic = new JCheckBox("Show Cosmic Only");
		rdbtnCosmic.addActionListener(actionListener);
		rdbtnCosmic.setFont(GUICommonTools.TAHOMA_BOLD_14);
		
		rdbtnShowReportedOnly = new JCheckBox("Show Reported Only");
		rdbtnShowReportedOnly.addActionListener(actionListener);
		rdbtnShowReportedOnly.setFont(GUICommonTools.TAHOMA_BOLD_14);
	}
	
	private void constructTextFieldFilters(){
		DocumentListener documentListener = new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
				sorter.sort();
			}
			public void removeUpdate(DocumentEvent e) {
				sorter.sort();
			}
			public void insertUpdate(DocumentEvent e) {
				sorter.sort();
			}
		};
		
		textFreqFrom = new JTextField();
		textFreqFrom.getDocument().addDocumentListener(documentListener);
		textFreqFrom.setColumns(10);

		textVarFreqTo = new JTextField();
		textVarFreqTo.getDocument().addDocumentListener(documentListener);
		textVarFreqTo.setColumns(10);
		
		textMinRD = new JTextField();
		textMinRD.getDocument().addDocumentListener(documentListener);
		textMinRD.setColumns(10);
		
		textOccuranceFrom = new JTextField();
		textOccuranceFrom.getDocument().addDocumentListener(documentListener);
		textOccuranceFrom.setColumns(10);
	}
	
	private void constructButtons(){
		btnReport = new JButton("Report");
		btnReport.setToolTipText("Generate report for the mutations marked as reported");
		btnReport.setFont(GUICommonTools.TAHOMA_BOLD_13);
		
		btnReset = new JButton("Reset");
		btnReset.setToolTipText("Clear all filters and reset table");
		btnReset.setFont(GUICommonTools.TAHOMA_BOLD_13);

		btnExport = new JButton("Export");
		btnExport.setToolTipText("Export the current table to file");
		btnExport.setFont(GUICommonTools.TAHOMA_BOLD_13);
		
		ActionListener actionListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == btnReport){
					showReportPanel();
				}else if(e.getSource() == btnReset){
					reset();
				}else if(e.getSource() == btnExport){
					try{
						exportTable();
					}catch(IOException ex){
						JOptionPane.showMessageDialog(MutationListFrame.this, ex.getMessage());
					}
				}
			}
		};
		
		btnReport.addActionListener(actionListener);
		btnReset.addActionListener(actionListener);
		btnExport.addActionListener(actionListener);
	}

	private void showReportPanel(){
		try{
			ArrayList<HashMap<String, String>> report = MutationReportGenerator.generateReport(basicTabTableModel, clinVarTabTableModel,
				coordinatesTabTableModel, g1000TabTableModel, sampleTabTableModel);
			ReportFrame reportPanel = new ReportFrame(report);
			reportPanel.setVisible(true);
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}
	private void constructListSelectionListener() {
		ListSelectionListener listSelectionListener = new ListSelectionListener(){
			private volatile boolean updating = false;
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(updating){
					return;
				}
				
				int[] selected = null;
				if(e.getSource() == basicTabTable.getSelectionModel()){
					selected = basicTabTable.getSelectedRows();
				}else if(e.getSource() == coordinatesTabTable.getSelectionModel()){
					selected = coordinatesTabTable.getSelectedRows();
				}else if(e.getSource() == g1000TabTable.getSelectionModel()){
					selected = g1000TabTable.getSelectedRows();
				}else if(e.getSource() == clinVarTabTable.getSelectionModel()){
					selected = clinVarTabTable.getSelectedRows();
				}else if(e.getSource() == sampleTabTable.getSelectionModel()){
					selected = sampleTabTable.getSelectedRows();
				}else{
					return;
				}
				
				updating = true;
				basicTabTable.clearSelection();
				basicTabTable.getSelectionModel().clearSelection();
				
				coordinatesTabTable.clearSelection();
				coordinatesTabTable.getSelectionModel().clearSelection();

				g1000TabTable.clearSelection();
				g1000TabTable.getSelectionModel().clearSelection();

				clinVarTabTable.clearSelection();
				clinVarTabTable.getSelectionModel().clearSelection();

				sampleTabTable.clearSelection();
				sampleTabTable.getSelectionModel().clearSelection();
				
				for (int i : selected){
					basicTabTable.addRowSelectionInterval(i, i);
					coordinatesTabTable.addRowSelectionInterval(i, i);
					g1000TabTable.addRowSelectionInterval(i, i);
					clinVarTabTable.addRowSelectionInterval(i, i);
					sampleTabTable.addRowSelectionInterval(i, i);
				}
				updating = false;
			}
		};
		
		basicTabTable.getSelectionModel().addListSelectionListener(listSelectionListener);
		coordinatesTabTable.getSelectionModel().addListSelectionListener(listSelectionListener);
		g1000TabTable.getSelectionModel().addListSelectionListener(listSelectionListener);
		clinVarTabTable.getSelectionModel().addListSelectionListener(listSelectionListener);
		sampleTabTable.getSelectionModel().addListSelectionListener(listSelectionListener);
	}
	
	private void constructRowSorter() {
		sorter = new TableRowSorter<BasicTableModel>(basicTabTableModel);
		basicTabTable.setRowSorter(sorter);
		coordinatesTabTable.setRowSorter(sorter);
		g1000TabTable.setRowSorter(sorter);
		clinVarTabTable.setRowSorter(sorter);
		sampleTabTable.setRowSorter(sorter);
		
		RowSorterListener rowSorterListener = new RowSorterListener() {
			@Override
			public void sorterChanged(RowSorterEvent e) {
				if (RowSorterEvent.Type.SORT_ORDER_CHANGED == e.getType()) {
					RowSorter<?> sorter = e.getSource();
					basicTabTable.getRowSorter().setSortKeys(sorter.getSortKeys());
					coordinatesTabTable.getRowSorter().setSortKeys(sorter.getSortKeys());
					g1000TabTable.getRowSorter().setSortKeys(sorter.getSortKeys());
					clinVarTabTable.getRowSorter().setSortKeys(sorter.getSortKeys());
					sampleTabTable.getRowSorter().setSortKeys(sorter.getSortKeys());
				}
			}
		};
		sorter.addRowSorterListener(rowSorterListener);
	}

	private void layoutComponents(){
		JScrollPane basicTabScrollPane = new JScrollPane();
		JScrollPane coordinatesTabScrollPane = new JScrollPane();
		JScrollPane g1000TabScrollPane = new JScrollPane();
		JScrollPane clinVarTabScrollPane = new JScrollPane();
		JScrollPane sampleTabScrollPane = new JScrollPane();
		basicTabScrollPane.setViewportView(basicTabTable);
		coordinatesTabScrollPane.setViewportView(coordinatesTabTable);
		g1000TabScrollPane.setViewportView(g1000TabTable);
		clinVarTabScrollPane.setViewportView(clinVarTabTable);
		sampleTabScrollPane.setViewportView(sampleTabTable);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Basic", null, basicTabScrollPane, null);
		tabbedPane.addTab("Coordinates", null, coordinatesTabScrollPane, null);
		tabbedPane.addTab("G1000", null, g1000TabScrollPane, null);
		tabbedPane.addTab("ClinVar", null, clinVarTabScrollPane, null);
		tabbedPane.addTab("Sample", null, sampleTabScrollPane, null);
		coordinatesTabScrollPane.getVerticalScrollBar().setModel(basicTabScrollPane.getVerticalScrollBar().getModel());
		g1000TabScrollPane.getVerticalScrollBar().setModel(basicTabScrollPane.getVerticalScrollBar().getModel());
		clinVarTabScrollPane.getVerticalScrollBar().setModel(basicTabScrollPane.getVerticalScrollBar().getModel());
		sampleTabScrollPane.getVerticalScrollBar().setModel(basicTabScrollPane.getVerticalScrollBar().getModel());
		
		JLabel variantFrequencyLabel = new JLabel("Variant Frequency (altFreq)");
		variantFrequencyLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		
		JLabel variantFrequencyToLabel = new JLabel("To");
		
		JLabel minReadDepthLabel = new JLabel("Min Read Depth (readDP)");
		minReadDepthLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		
		JLabel occurenceLabel = new JLabel("Min occurence");
		occurenceLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
				gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addGap(46)
						.addComponent(rdbtnCosmic)
						.addGap(28)
						.addComponent(rdbtnShowReportedOnly, GroupLayout.PREFERRED_SIZE, 194, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(784, Short.MAX_VALUE))
				.addGroup(gl_contentPane.createSequentialGroup()
						.addGap(46)
						.addComponent(variantFrequencyLabel)
						.addGap(10)
						.addComponent(textFreqFrom, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
						.addGap(6)
						.addComponent(variantFrequencyToLabel, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
						.addGap(6)
						.addComponent(textVarFreqTo, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
						.addGap(45)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
										.addGap(182)
										.addComponent(textMinRD, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
								.addComponent(minReadDepthLabel, GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE))
						.addGap(45)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(occurenceLabel, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_contentPane.createSequentialGroup()
										.addGap(105)
										.addComponent(textOccuranceFrom, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))))
				.addGroup(gl_contentPane.createSequentialGroup()
						.addGap(884)
						.addComponent(btnReport)
						.addGap(18)
						.addComponent(btnReset, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addComponent(btnExport, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_contentPane.createSequentialGroup()
						.addGap(47)
						.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 1158, Short.MAX_VALUE))
				);
		gl_contentPane.setVerticalGroup(
				gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
								.addGroup(gl_contentPane.createSequentialGroup()
										.addGap(20)
										.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
												.addComponent(rdbtnCosmic)
												.addComponent(rdbtnShowReportedOnly, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
										.addGap(18)
										.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
												.addComponent(variantFrequencyLabel)
												.addComponent(textFreqFrom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(textVarFreqTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(textMinRD, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(minReadDepthLabel)
												.addComponent(occurenceLabel)
												.addComponent(textOccuranceFrom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
								.addGroup(gl_contentPane.createSequentialGroup()
										.addGap(66)
										.addComponent(variantFrequencyToLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
						.addGap(11)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(btnReport)
								.addComponent(btnReset)
								.addComponent(btnExport))
						.addGap(6)
						.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
						.addGap(20))
				);
		contentPane.setLayout(gl_contentPane);
	}
	
	private void createRowFilter(){
		filters = new ArrayList<RowFilter<BasicTableModel, Integer>>();

		RowFilter<BasicTableModel, Integer> cosmicFilter = new RowFilter<BasicTableModel, Integer>(){
			@Override
			public boolean include(Entry<? extends BasicTableModel , ? extends Integer> entry) {
				if(!rdbtnCosmic.isSelected()){
					return true;
				}
				BasicTableModel model = entry.getModel();
				int row = entry.getIdentifier();
				Mutation mutation = model.getMutation(row);
				if(mutation.getValue("cosmicID") == null){
					return false;
				}
				String cosmicID = mutation.getValue("cosmicID").toString();
				return !cosmicID.equals("");
			}
		};
		filters.add(cosmicFilter);
		
		RowFilter<BasicTableModel, Integer> reportedFilter = new RowFilter<BasicTableModel, Integer>(){
			@Override
			public boolean include(Entry<? extends BasicTableModel, ? extends Integer> entry) {
				if(!rdbtnShowReportedOnly.isSelected()){
					return true;
				}
				BasicTableModel model = entry.getModel();
				int row = entry.getIdentifier();
				Mutation mutation = model.getMutation(row);
				if(mutation.getValue("reported") == null){
					return false;
				}
				return (Boolean)mutation.getValue("reported");
			}
		};
		filters.add(reportedFilter);
		
		RowFilter<BasicTableModel, Integer> frequencyFilter = new RowFilter<BasicTableModel, Integer>(){
			
			private boolean includeHelper(Entry<? extends BasicTableModel, ? extends Integer> entry){
				BasicTableModel model = entry.getModel();
				int row = entry.getIdentifier();
				Mutation mutation = model.getMutation(row);
				
				if(mutation.getValue("altFreq") != null){
					double variantFrequency = Double.parseDouble(mutation.getValue("altFreq").toString());
					
					int varFreqFromInt = getNumber(textFreqFrom, 0);
					if(varFreqFromInt > variantFrequency){
						return false;
					}
					
					int varFreqToInt = getNumber(textVarFreqTo, 100);
					if(varFreqToInt < variantFrequency){
						return false;
					}
				}

				if(mutation.getValue("occurrence") != null){
					try{
						int occurrence = Integer.parseInt(mutation.getValue("occurrence").toString());
						int occuranceFromInt = getNumber(textOccuranceFrom, 0);
						if(occuranceFromInt > occurrence){
							return false;
						}
					}catch(NumberFormatException exception){
						//unable to parse. This is likely because the occurrence is still "Loading..."
					}
				}

				if(mutation.getValue("readDP") != null){
					int readDepth = Integer.parseInt(mutation.getValue("readDP").toString());
					int minReadDepth = getNumber(textMinRD, 0);
					if(minReadDepth > readDepth){
						return false;
					}
				}
				
				return true;
			}
			
			@Override
			public boolean include(Entry<? extends BasicTableModel, ? extends Integer> entry) {
				try{
					return includeHelper(entry);
				}catch(Exception e){
					e.printStackTrace();
					return true;//default to include
				}
			}
		};
		filters.add(frequencyFilter);
		
		try{
			RowFilter<BasicTableModel, Integer> rf = RowFilter.andFilter(filters);
			sorter.setRowFilter(rf);
			basicTabTable.setRowSorter(sorter);
			coordinatesTabTable.setRowSorter(sorter);
			g1000TabTable.setRowSorter(sorter);
			clinVarTabTable.setRowSorter(sorter);
			sampleTabTable.setRowSorter(sorter);
		}catch(Exception e1){
			//return;
		}
	}
	private int getNumber(JTextField field, Integer defaultInt){
		String value = field.getText();
		Integer valueInt = null;
		if(value.equals("")){
			valueInt = defaultInt;
		}else{
			try{
				valueInt = Integer.parseInt(value);
			}catch(Exception e){
				return defaultInt;
			}
		}
		return valueInt;
	}

	private void reset(){
		rdbtnCosmic.setSelected(false);
		rdbtnShowReportedOnly.setSelected(false);
		sorter.setRowFilter(null);
		
		textFreqFrom.setText("");
		textVarFreqTo.setText("");
		textMinRD.setText("");
		textOccuranceFrom.setText("");
		
		basicTabTable.setRowSorter(sorter);
		coordinatesTabTable.setRowSorter(sorter);
		g1000TabTable.setRowSorter(sorter);
		clinVarTabTable.setRowSorter(sorter);
		sampleTabTable.setRowSorter(sorter);
	}

	private void exportTable() throws IOException{
		JFileChooser saveAs = new JFileChooser();
		saveAs.setAcceptAllFileFilterUsed(false);
		saveAs.addChoosableFileFilter(new FileFilter(){
			@Override
			public boolean accept(File file) {
				return file.isDirectory() || file.getName().endsWith(".txt");
			}

			@Override
			public String getDescription() {
				return ".txt file";
			}
		});
		
		int returnValue = saveAs.showSaveDialog(this);
		if(returnValue == JFileChooser.APPROVE_OPTION){
			MutationReportGenerator.exportReport(saveAs.getSelectedFile(), basicTabTableModel,
					clinVarTabTableModel, coordinatesTabTableModel, g1000TabTableModel, sampleTabTableModel);
		}
	}
	
	/*
	 * 
	 * This section of the class handles the multi-threaded
	 * interactions between database loading, GUI updating,
	 * and window closing events. The goal is to keep the
	 * GUI highly responsive to the user.
	 * 
	 */
	private void loadMissingDataAsynchronous(){
		isWindowClosed = false; 
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent arg0) {
				isWindowClosed = true;
			}
		});
		
		rdbtnCosmic.setEnabled(false);
		rdbtnCosmic.setToolTipText("Disabled while cosmic data is loading");
		textOccuranceFrom.setEditable(false);
		textOccuranceFrom.setToolTipText("Disabled while cosmic data is loading");
		final Thread one = createExtraMutationDataThread(mutations, 0, 4);
		final Thread two = createExtraMutationDataThread(mutations, 1, 4);
		final Thread three = createExtraMutationDataThread(mutations, 2, 4);
		final Thread four = createExtraMutationDataThread(mutations, 3, 4);
		
		Thread waitingThread = new Thread(new Runnable(){
			@Override
			public void run() {
				try{
					one.join();
					two.join();
					three.join();
					four.join();
				}catch(Exception e){}
				rdbtnCosmic.setEnabled(true);
				rdbtnCosmic.setToolTipText("");
				textOccuranceFrom.setEditable(true);
				textOccuranceFrom.setToolTipText("");
			}
		});
		waitingThread.start();
	}
	
	private Thread createExtraMutationDataThread(ArrayList<Mutation> mutations, int startIndex, int incrementIndex){
		Thread missingDataThread = new Thread(new Runnable(){
			@Override
			public void run() {
				getExtraMutationData(mutations, startIndex, incrementIndex);
			}
		});
		missingDataThread.start();
		return missingDataThread;
	}

	private volatile boolean isWindowClosed;
	private void getExtraMutationData(ArrayList<Mutation> mutations, int startIndex, int incrementIndex){
		for(int index = startIndex; index < mutations.size(); index += incrementIndex){
			if(isWindowClosed){
				return;
			}
			Mutation mutation = mutations.get(index);
			try{
				String cosmicID = DatabaseCommands.getCosmicID(mutation);
				int count = DatabaseCommands.getOccurrenceCount(mutation);

				//because we created each model with the same ArrayList, we only have to update it here
				//TODO don't hardcode these key values
				mutations.get(index).addData("occurrence", count);
				mutations.get(index).addData("cosmicID", cosmicID);
				
				basicTabTableModel.fireTableRowsUpdated(index, index);
				coordinatesTabTableModel.fireTableRowsUpdated(index, index);
				g1000TabTableModel.fireTableRowsUpdated(index, index);
				clinVarTabTableModel.fireTableRowsUpdated(index, index);
				sampleTabTableModel.fireTableRowsUpdated(index, index);
			}catch(Exception e){
				JOptionPane.showMessageDialog(this, e.getMessage() + " : " + e.getClass().getName() + ": Could not load additional mutation data.");
			}
		}
	}
}
