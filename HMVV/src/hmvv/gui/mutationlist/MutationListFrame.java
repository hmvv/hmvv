package hmvv.gui.mutationlist;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

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
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.tablemodels.BasicTableModel;
import hmvv.gui.mutationlist.tablemodels.ClinVarTableModel;
import hmvv.gui.mutationlist.tablemodels.CoordinatesTableModel;
import hmvv.gui.mutationlist.tablemodels.G1000TableModel;
import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.gui.mutationlist.tablemodels.SampleTableModel;
import hmvv.gui.mutationlist.tables.BasicTable;
import hmvv.gui.mutationlist.tables.ClinVarTable;
import hmvv.gui.mutationlist.tables.CommonTable;
import hmvv.gui.mutationlist.tables.CoordinatesTable;
import hmvv.gui.mutationlist.tables.G1000Table;
import hmvv.gui.mutationlist.tables.SampleTable;
import hmvv.gui.sampleList.ReportFrame;
import hmvv.io.DatabaseCommands;
import hmvv.io.MutationReportGenerator;
import hmvv.model.Mutation;

public class MutationListFrame extends JFrame {
	private static final long serialVersionUID = 1L;
		
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
	
	private MutationList mutationList;
	
	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	private CommonTable selectedTable;
	
	private JCheckBox rdbtnShowReportedOnly;
	private JCheckBox rdbtnCosmic;
	private JButton shortReportButton;
	private JButton longReportButton;
	private JButton btnReset;
	private JButton btnExport;
	
	private JTextField textFreqFrom;
	private JTextField textVarFreqTo;
	private JTextField textMinRD;
	private JTextField textOccuranceFrom;
	
	/**
	 * Create the frame.
	 */
	public MutationListFrame(Component parent, MutationList mutationList, String title) throws Exception{
		super("Mutation List - " + title);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*.85), (int)(bounds.height*.85));
		
		this.mutationList = mutationList;
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		
		constructFilterPanel();
		
		constructTabs();
		layoutComponents();
		createSortChangeListener();
		setLocationRelativeTo(parent);
		loadMissingDataAsynchronous();
	}
	
	private void constructFilterPanel() {
		constructCheckBoxFilters();
		constructTextFieldFilters();
		constructButtons();
	}

	private void constructTabs(){
		basicTabTableModel = new BasicTableModel(mutationList);
		basicTabTable = new BasicTable(basicTabTableModel);
		basicTabTable.setAutoCreateRowSorter(true);
		
		coordinatesTabTableModel = new CoordinatesTableModel(mutationList);
		coordinatesTabTable = new CoordinatesTable(coordinatesTabTableModel);
		coordinatesTabTable.setAutoCreateRowSorter(true);
		
		g1000TabTableModel = new G1000TableModel(mutationList);
		g1000TabTable = new G1000Table(g1000TabTableModel);
		g1000TabTable.setAutoCreateRowSorter(true);
		
		clinVarTabTableModel = new ClinVarTableModel(mutationList);
		clinVarTabTable = new ClinVarTable(clinVarTabTableModel);
		clinVarTabTable.setAutoCreateRowSorter(true);

		sampleTabTableModel = new SampleTableModel(mutationList);
		sampleTabTable = new SampleTable(sampleTabTableModel);
		sampleTabTable.setAutoCreateRowSorter(true);
	}

	private void constructCheckBoxFilters(){
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyRowFilters();
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
				applyRowFilters();
			}
			public void removeUpdate(DocumentEvent e) {
				applyRowFilters();
			}
			public void insertUpdate(DocumentEvent e) {
				applyRowFilters();
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
		shortReportButton = new JButton("Short Report");
		shortReportButton.setToolTipText("Generate a short report for the mutations marked as reported");
		shortReportButton.setFont(GUICommonTools.TAHOMA_BOLD_13);
		
		longReportButton = new JButton("Long Report");
		longReportButton.setToolTipText("Generate a long report for the mutations marked as reported");
		longReportButton.setFont(GUICommonTools.TAHOMA_BOLD_13);
		
		btnReset = new JButton("Reset");
		btnReset.setToolTipText("Clear all filters and reset table");
		btnReset.setFont(GUICommonTools.TAHOMA_BOLD_13);

		btnExport = new JButton("Export");
		btnExport.setToolTipText("Export the current table to file");
		btnExport.setFont(GUICommonTools.TAHOMA_BOLD_13);
		
		ActionListener actionListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == shortReportButton){
					showShortReportFrame();
				}else if(e.getSource() == longReportButton){
					showLongReportFrame();
				}else if(e.getSource() == btnReset){
					reset();
				}else if(e.getSource() == btnExport){
					try{
						exportTable();
					}catch(IOException ex){
						ex.printStackTrace();
						JOptionPane.showMessageDialog(MutationListFrame.this, ex.getMessage());
					}
				}
			}
		};
		
		shortReportButton.addActionListener(actionListener);
		longReportButton.addActionListener(actionListener);
		btnReset.addActionListener(actionListener);
		btnExport.addActionListener(actionListener);
	}

	private void showShortReportFrame(){
		try{
			String report = MutationReportGenerator.generateShortReport(mutationList);
			showReportFrame(report);
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}
	
	private void showLongReportFrame(){
		try{
			String report = MutationReportGenerator.generateLongReport(mutationList);
			showReportFrame(report);
		}catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}
	
	private void showReportFrame(String report){
		ReportFrame reportPanel = new ReportFrame(this, report);
		reportPanel.setVisible(true);
	}
	
	private void layoutComponents(){
		JScrollPane basicTabScrollPane = new JScrollPane(basicTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane coordinatesTabScrollPane = new JScrollPane(coordinatesTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane g1000TabScrollPane = new JScrollPane(g1000TabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane clinVarTabScrollPane = new JScrollPane(clinVarTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane sampleTabScrollPane = new JScrollPane(sampleTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Basic", null, basicTabScrollPane, null);
		tabbedPane.addTab("Coordinates", null, coordinatesTabScrollPane, null);
		tabbedPane.addTab("G1000", null, g1000TabScrollPane, null);
		tabbedPane.addTab("ClinVar", null, clinVarTabScrollPane, null);
		tabbedPane.addTab("Sample", null, sampleTabScrollPane, null);
		selectedTable = basicTabTable;
		
		JLabel variantFrequencyLabel = new JLabel("Variant Frequency (altFreq)");
		variantFrequencyLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		
		JLabel variantFrequencyToLabel = new JLabel("To");
		
		JLabel minReadDepthLabel = new JLabel("Min Read Depth (readDP)");
		minReadDepthLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		
		JLabel occurenceLabel = new JLabel("Min occurence");
		occurenceLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		
		GroupLayout groupLayout = new GroupLayout(contentPane);
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(rdbtnCosmic)
						.addGap(28)
						.addComponent(rdbtnShowReportedOnly, GroupLayout.PREFERRED_SIZE, 194, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(784, Short.MAX_VALUE))
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(variantFrequencyLabel)
						.addGap(10)
						.addComponent(textFreqFrom, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
						.addGap(6)
						.addComponent(variantFrequencyToLabel, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
						.addGap(6)
						.addComponent(textVarFreqTo, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
						.addGap(45)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
										.addGap(182)
										.addComponent(textMinRD, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
								.addComponent(minReadDepthLabel, GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE))
						.addGap(45)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(occurenceLabel, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
								.addGroup(groupLayout.createSequentialGroup()
										.addGap(105)
										.addComponent(textOccuranceFrom, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))))
				.addGroup(groupLayout.createSequentialGroup()
						.addGap(884)
						.addComponent(shortReportButton)
						.addGap(18)
						.addComponent(longReportButton)
						.addGap(18)
						.addComponent(btnReset, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addComponent(btnExport, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 1158, Short.MAX_VALUE))
				);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addGroup(groupLayout.createSequentialGroup()
										.addGap(20)
										.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
												.addComponent(rdbtnCosmic)
												.addComponent(rdbtnShowReportedOnly, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
										.addGap(18)
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
												.addComponent(variantFrequencyLabel)
												.addComponent(textFreqFrom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(textVarFreqTo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(textMinRD, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
												.addComponent(minReadDepthLabel)
												.addComponent(occurenceLabel)
												.addComponent(textOccuranceFrom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
								.addGroup(groupLayout.createSequentialGroup()
										.addGap(66)
										.addComponent(variantFrequencyToLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
						.addGap(11)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(shortReportButton)
								.addComponent(longReportButton)
								.addComponent(btnReset)
								.addComponent(btnExport))
						.addGap(6)
						.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
						.addGap(20))
				);
		contentPane.setLayout(groupLayout);
	}
	
	private void createSortChangeListener(){
		tabbedPane.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
	        	int selectedIndex = tabbedPane.getSelectedIndex();
	        	
	        	int[] modelRowToViewRow = new int[selectedTable.getRowCount()];
	        	for(int i = 0; i < selectedTable.getRowCount(); i++){
	        		modelRowToViewRow[i] = selectedTable.convertRowIndexToView(i);
	        	}
	        	mutationList.sortModel(modelRowToViewRow);
	        	selectedTable.getRowSorter().setSortKeys(null);
	        	if(selectedIndex == 0){
	        		selectedTable = basicTabTable;
	        	}else if(selectedIndex == 1){
	        		selectedTable = coordinatesTabTable;
	        	}else if(selectedIndex == 2){
	        		selectedTable = g1000TabTable;
	        	}else if(selectedIndex == 3){
	        		selectedTable = clinVarTabTable;
	        	}else if(selectedIndex == 4){
	        		selectedTable = sampleTabTable;
	        	}else{
	        		//undefined
	        		return;
	        	}
	        	selectedTable.resizeColumnWidths();
	        }
	    });
	}
	
	private void applyRowFilters(){
		boolean includeCosmicOnly = rdbtnCosmic.isSelected();
		boolean includeReportedOnly = rdbtnShowReportedOnly.isSelected();
		int frequencyFrom =  getNumber(textFreqFrom, 0);
		int frequencyTo = getNumber(textVarFreqTo, 100);
		int minOccurence = getNumber(textOccuranceFrom, 0);
		int minReadDepth = getNumber(textMinRD, 0);
		mutationList.filterMutations(includeCosmicOnly, includeReportedOnly, frequencyFrom, frequencyTo, minOccurence, minReadDepth);
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
		textFreqFrom.setText("");
		textVarFreqTo.setText("");
		textMinRD.setText("");
		textOccuranceFrom.setText("");
		applyRowFilters();
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
			MutationReportGenerator.exportReport(saveAs.getSelectedFile(), basicTabTableModel, clinVarTabTableModel, coordinatesTabTableModel, g1000TabTableModel, sampleTabTableModel);
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
		
		String tooltip = "Disabled while cosmic data is loading";
		rdbtnCosmic.setEnabled(false);
		rdbtnCosmic.setToolTipText(tooltip);
		rdbtnShowReportedOnly.setEnabled(false);
		rdbtnShowReportedOnly.setToolTipText(tooltip);
		btnReset.setEnabled(false);
		btnReset.setToolTipText(tooltip);
		textFreqFrom.setEditable(false);
		textFreqFrom.setToolTipText(tooltip);
		textVarFreqTo.setEditable(false);
		textVarFreqTo.setToolTipText(tooltip);
		textMinRD.setEditable(false);
		textMinRD.setToolTipText(tooltip);
		textOccuranceFrom.setEditable(false);
		textOccuranceFrom.setToolTipText(tooltip);
		final Thread one = createExtraMutationDataThread(0, 4);
		final Thread two = createExtraMutationDataThread(1, 4);
		final Thread three = createExtraMutationDataThread(2, 4);
		final Thread four = createExtraMutationDataThread(3, 4);
		
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
				rdbtnShowReportedOnly.setEnabled(true);
				rdbtnShowReportedOnly.setToolTipText("");
				btnReset.setEnabled(true);
				btnReset.setToolTipText("");
				textFreqFrom.setEditable(true);
				textFreqFrom.setToolTipText("");
				textVarFreqTo.setEditable(true);
				textVarFreqTo.setToolTipText("");
				textMinRD.setEditable(true);
				textMinRD.setToolTipText("");
				textOccuranceFrom.setEditable(true);
				textOccuranceFrom.setToolTipText("");
			}
		});
		waitingThread.start();
	}
	
	private Thread createExtraMutationDataThread(int startIndex, int incrementIndex){
		Thread missingDataThread = new Thread(new Runnable(){
			@Override
			public void run() {
				getExtraMutationData(startIndex, incrementIndex);
			}
		});
		missingDataThread.start();
		return missingDataThread;
	}
	
	private volatile boolean isWindowClosed;
	private void getExtraMutationData(int startIndex, int incrementIndex){
		for(int index = startIndex; index < basicTabTableModel.getRowCount(); index += incrementIndex){
			if(isWindowClosed){
				return;
			}
			
			try{
				Mutation mutation = basicTabTableModel.getMutation(index);
				String cosmicID = DatabaseCommands.getCosmicID(mutation);
				int count = DatabaseCommands.getOccurrenceCount(mutation);
				basicTabTableModel.updateModel(index, cosmicID, count);
			}catch(Exception e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, e.getMessage() + " : " + e.getClass().getName() + ": Could not load additional mutation data.");
			}
		}
	}
}
