package hmvv.gui.mutationlist;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
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
import hmvv.gui.mutationlist.tablemodels.*;
import hmvv.gui.mutationlist.tables.*;
import hmvv.gui.sampleList.ReportFrame;
import hmvv.gui.sampleList.SampleListFrame;
import hmvv.io.DatabaseCommands;
import hmvv.io.IGVConnection;
import hmvv.io.MutationReportGenerator;
import hmvv.io.SSHConnection;
import hmvv.main.Configurations;
import hmvv.model.Mutation;
import hmvv.model.Sample;
import hmvv.model.VariantPredictionClass;

public class MutationListFrame extends JDialog {
	private static final long serialVersionUID = 1L;
		
	private BasicTable basicTabTable;
	private BasicTableModel basicTabTableModel;

	private VEPTable vepTabTable;
	private VEPTableModel vepTabTableModel;

	private CosmicTable cosmicTabTable;
	private CosmicTableModel cosmicTabTableModel;

	private G1000Table g1000TabTable;
	private G1000TableModel g1000TabTableModel;

	private ClinVarTable clinVarTabTable;
	private ClinVarTableModel clinVarTabTableModel;

	private GnomadTable gnomadTabTable;
	private GnomadTableModel gnomadTabTableModel;

	private OncokbTable oncokbTabTable;
	private OncokbTableModel oncokbTabTableModel;

	private CivicTable civicTabTable;
	private CivicTableModel civicTabTableModel;

	private JScrollPane basicTabScrollPane;
	private JScrollPane vepTabScrollPane;
	private JScrollPane cosmicTabScrollPane;
	private JScrollPane g1000TabScrollPane;
	private JScrollPane clinVarTabScrollPane;
	private JScrollPane gnomadTabScrollPane;
	private JScrollPane oncokbTabScrollPane;
	private JScrollPane civicTabScrollPane;

	private MutationList mutationList;
	private Sample sample;
	
	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	private CommonTable selectedTable;
	private JScrollPane selectedScrollPane;
	
	private JCheckBox reportedOnlyCheckbox;
	private JCheckBox cosmicOnlyCheckbox;
	private JCheckBox filterNomalCheckbox;
	private JButton shortReportButton;
	private JButton longReportButton;
	private JButton resetButton;
	private JButton exportButton;
	private JButton loadFilteredMutationsButton;
	private LoadIGVButton loadIGVButton;
	
	private JTextField textFreqFrom;
	private JTextField textVarFreqTo;
	private JTextField minReadDepthTextField;
	private JTextField occurenceFromTextField;
	private JTextField maxPopulationFrequencyTextField;
	private JComboBox<VariantPredictionClass> predictionFilterComboBox;
	
	private volatile boolean isWindowClosed;
	private boolean isMutationSelected;
	
	/**
	 * Create the frame.
	 */
	public MutationListFrame(SampleListFrame parent, MutationList mutationList){
		this(parent, null, mutationList);
	}
	
	public MutationListFrame(SampleListFrame parent, Sample sample, MutationList mutationList){
		super(parent);
		//TODO this class is too complicated as a host for both the search results and the sample mutation list. Refactor.
		if(sample == null) {
			String title = "Mutation List Search Results"; 
			setTitle(title);
		}else {
			String title = "Mutation List - " + sample.getLastName() + "," + sample.getFirstName() + "," + sample.getOrderNumber() + "," + sample.getOrderNumber() +
					" (sampleID = " + sample.sampleID + ", runID = " + sample.runID + ", assay = " + sample.assay + ")";
			setTitle(title);
		}
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*.85), (int)(bounds.height*.85));
		setMinimumSize(new Dimension(900, getHeight()/2));
		
		this.sample = sample;
		this.mutationList = mutationList;
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		
		constructFilterPanel();
		
		constructTabs();
		layoutComponents();
		createSortChangeListener();
		setLocationRelativeTo(parent);
		resetFilters();
		
		isWindowClosed = false;
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent arg0) {
				isWindowClosed = true;
			}
		});
		
		loadMissingDataAsynchronous();
	}
	
	private void constructFilterPanel() {
		constructCheckBoxFilters();
		constructTextFieldFilters();
		constructButtons();
	}

	private void constructTabs(){
		basicTabTableModel = new BasicTableModel(mutationList);
		basicTabTable = new BasicTable(this, basicTabTableModel);
		basicTabTable.setAutoCreateRowSorter(true);

		vepTabTableModel = new VEPTableModel(mutationList);
		vepTabTable = new VEPTable(this, vepTabTableModel);
		vepTabTable.setAutoCreateRowSorter(true);
		
		cosmicTabTableModel = new CosmicTableModel(mutationList);
		cosmicTabTable = new CosmicTable(this, cosmicTabTableModel);
		cosmicTabTable.setAutoCreateRowSorter(true);

		g1000TabTableModel = new G1000TableModel(mutationList);
		g1000TabTable = new G1000Table(this, g1000TabTableModel);
		g1000TabTable.setAutoCreateRowSorter(true);

		clinVarTabTableModel = new ClinVarTableModel(mutationList);
		clinVarTabTable = new ClinVarTable(this, clinVarTabTableModel);
		clinVarTabTable.setAutoCreateRowSorter(true);

		gnomadTabTableModel = new GnomadTableModel(mutationList);
		gnomadTabTable = new GnomadTable(this, gnomadTabTableModel);
		gnomadTabTable.setAutoCreateRowSorter(true);

		oncokbTabTableModel = new OncokbTableModel(mutationList);
		oncokbTabTable = new OncokbTable(this, oncokbTabTableModel);
		oncokbTabTable.setAutoCreateRowSorter(true);

		civicTabTableModel = new CivicTableModel(mutationList);
		civicTabTable = new CivicTable(this, civicTabTableModel);
		civicTabTable.setAutoCreateRowSorter(true);

	}

	private void constructCheckBoxFilters(){
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyRowFilters();
			}
		};
		
		cosmicOnlyCheckbox = new JCheckBox("Show Cosmic Only");
		cosmicOnlyCheckbox.addActionListener(actionListener);
		cosmicOnlyCheckbox.setFont(GUICommonTools.TAHOMA_BOLD_14);
		
		reportedOnlyCheckbox = new JCheckBox("Show Reported Only");
		reportedOnlyCheckbox.addActionListener(actionListener);
		reportedOnlyCheckbox.setFont(GUICommonTools.TAHOMA_BOLD_14);
		
		filterNomalCheckbox = new JCheckBox("Filter Normal Pair");
		filterNomalCheckbox.addActionListener(actionListener);
		filterNomalCheckbox.setFont(GUICommonTools.TAHOMA_BOLD_14);
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
		
		int textFieldColumnWidth = 5;
		textFreqFrom = new JTextField();
		textFreqFrom.getDocument().addDocumentListener(documentListener);
		textFreqFrom.setColumns(textFieldColumnWidth);

		textVarFreqTo = new JTextField();
		textVarFreqTo.getDocument().addDocumentListener(documentListener);
		textVarFreqTo.setColumns(textFieldColumnWidth);
		
		minReadDepthTextField = new JTextField();
		minReadDepthTextField.getDocument().addDocumentListener(documentListener);
		minReadDepthTextField.setColumns(textFieldColumnWidth);
		
		occurenceFromTextField = new JTextField();
		occurenceFromTextField.getDocument().addDocumentListener(documentListener);
		occurenceFromTextField.setColumns(textFieldColumnWidth);
		
		maxPopulationFrequencyTextField = new JTextField();
		maxPopulationFrequencyTextField.getDocument().addDocumentListener(documentListener);
		maxPopulationFrequencyTextField.setColumns(textFieldColumnWidth);
		
		predictionFilterComboBox = new JComboBox<VariantPredictionClass>(VariantPredictionClass.getAllClassifications());
		predictionFilterComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		        applyRowFilters();
		    }
		});
	}
	
	private void constructButtons(){
		shortReportButton = new JButton("Short Report");
		shortReportButton.setToolTipText("Generate a short report for the mutations marked as reported");
		shortReportButton.setFont(GUICommonTools.TAHOMA_BOLD_13);
		
		longReportButton = new JButton("Long Report");
		longReportButton.setToolTipText("Generate a long report for the mutations marked as reported");
		longReportButton.setFont(GUICommonTools.TAHOMA_BOLD_13);
		
		resetButton = new JButton("Reset Filters");
		resetButton.setToolTipText("Reset filters to defaults");
		resetButton.setFont(GUICommonTools.TAHOMA_BOLD_13);

		exportButton = new JButton("Export");
		exportButton.setToolTipText("Export the current table to file");
		exportButton.setFont(GUICommonTools.TAHOMA_BOLD_13);
		
		loadFilteredMutationsButton = new JButton("Load Filtered Mutations");
		loadFilteredMutationsButton.setToolTipText("Load Mutations that did not meet the quality filter metrics");
		loadFilteredMutationsButton.setFont(GUICommonTools.TAHOMA_BOLD_13);
		loadFilteredMutationsButton.setEnabled(false);//this will be enabled after the unfiltered variant data is loaded
		
		loadIGVButton = new LoadIGVButton();
		loadIGVButton.setToolTipText("Load the sample into IGV. IGV needs to be already opened");
		loadIGVButton.setFont(GUICommonTools.TAHOMA_BOLD_14);
		if(sample == null) {
			//If no sample provided, this object is for the search results display.
			loadFilteredMutationsButton.setEnabled(false);
			loadIGVButton.setEnabled(false);
		}
		
		ActionListener actionListener = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == shortReportButton){
					showShortReportFrame();
				}else if(e.getSource() == longReportButton){
					showLongReportFrame();
				}else if(e.getSource() == resetButton){
					resetFilters();
				}else if(e.getSource() == exportButton){
					try{
						exportTable();
					}catch(IOException ex){
						JOptionPane.showMessageDialog(MutationListFrame.this, ex.getMessage());
					}
				}else if(e.getSource() == loadFilteredMutationsButton){
					loadFilteredMutationsButton.setEnabled(false);
					loadFilteredMutationsAsynchronous();
				}else if(e.getSource() == loadIGVButton) {
					new Thread(new Runnable() {
						public void run() {
							try{
								if(sample == null) {
									throw new Exception("No sample was provided. Please contact developer to debug this error.");
								}
								loadIGVButton.setEnabled(false);
								loadIGVAsynchronous();
							}catch(Exception ex){
								JOptionPane.showMessageDialog(MutationListFrame.this, ex.getMessage());
							}
					    	loadIGVButton.setEnabled(true);
					    	loadIGVButton.resetText();
						}
					}).start();
				}
			}
		};
		
		shortReportButton.addActionListener(actionListener);
		longReportButton.addActionListener(actionListener);
		resetButton.addActionListener(actionListener);
		exportButton.addActionListener(actionListener);
		loadFilteredMutationsButton.addActionListener(actionListener);
		loadIGVButton.addActionListener(actionListener);
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
		basicTabScrollPane = new JScrollPane(basicTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		vepTabScrollPane = new JScrollPane(vepTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		cosmicTabScrollPane = new JScrollPane(cosmicTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		g1000TabScrollPane = new JScrollPane(g1000TabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		clinVarTabScrollPane = new JScrollPane(clinVarTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		gnomadTabScrollPane = new JScrollPane(gnomadTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		oncokbTabScrollPane = new JScrollPane(oncokbTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		civicTabScrollPane = new JScrollPane(civicTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Basic", null, basicTabScrollPane, null);
		tabbedPane.addTab("VEP", null, vepTabScrollPane, null);
		tabbedPane.addTab("Cosmic", null, cosmicTabScrollPane, null);
		tabbedPane.addTab("G1000", null, g1000TabScrollPane, null);
		tabbedPane.addTab("ClinVar", null, clinVarTabScrollPane, null);
		tabbedPane.addTab("Gnomad", null, gnomadTabScrollPane, null);
		tabbedPane.addTab("Oncokb", null, oncokbTabScrollPane, null);
		tabbedPane.addTab("Civic", null, civicTabScrollPane, null);

		selectedTable = basicTabTable;
		selectedScrollPane = basicTabScrollPane;
		
		JPanel leftFilterPanel = new JPanel();
		leftFilterPanel.setLayout(new GridLayout(0,1));
		JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		checkboxPanel.add(cosmicOnlyCheckbox);
		checkboxPanel.add(reportedOnlyCheckbox);
		
		if(sample != null) {
			//if null, this is for the mutation search
			if(mutationList.getMutationCount() > 1 && sample.assay.equals("exome")){
				checkboxPanel.add(filterNomalCheckbox);
			}
		}
		leftFilterPanel.add(checkboxPanel);
		
		//variant frequency
		JPanel populationFrequencyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel populationFrequencyLabel = new JLabel("Max Population Frequency (altGlobalFreq)");
		populationFrequencyLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		populationFrequencyPanel.add(populationFrequencyLabel);
		populationFrequencyPanel.add(maxPopulationFrequencyTextField);
		leftFilterPanel.add(populationFrequencyPanel);
		leftFilterPanel.add(new JLabel(""));//take up a space so same number of elements as right filter panel
		
		JPanel rightFilterPanel = new JPanel();
		rightFilterPanel.setLayout(new GridLayout(0,1));
		
		//min read depth and occurence filters
		JPanel readDepthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel minReadDepthLabel = new JLabel("Min Read Depth (readDP)");
		minReadDepthLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		readDepthPanel.add(minReadDepthLabel);
		readDepthPanel.add(minReadDepthTextField);
		
		JLabel variantFrequencyLabel = new JLabel("Variant Frequency (altFreq)");
		variantFrequencyLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		JPanel frequencyLayoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		frequencyLayoutPanel.add(variantFrequencyLabel);
		frequencyLayoutPanel.add(textFreqFrom);
		frequencyLayoutPanel.add(new JLabel("To"));
		frequencyLayoutPanel.add(textVarFreqTo);
		
		JPanel occurencePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel occurenceLabel = new JLabel("Min occurence (occurence)");
		occurenceLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		occurencePanel.add(occurenceLabel);
		occurencePanel.add(occurenceFromTextField);
		
		JPanel predictionFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel predictionFilterLabel = new JLabel("Min Prediction Class (classification)");
		predictionFilterLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		predictionFilterPanel.add(predictionFilterLabel);
		predictionFilterPanel.add(predictionFilterComboBox);
		
		rightFilterPanel.add(readDepthPanel);
		rightFilterPanel.add(frequencyLayoutPanel);
		rightFilterPanel.add(occurencePanel);
		rightFilterPanel.add(predictionFilterPanel);

		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new GridLayout(1,0));
		filterPanel.add(leftFilterPanel);
		filterPanel.add(rightFilterPanel);
		
		//Buttons
		JPanel buttonPanel = new JPanel();
		GridLayout buttonPanelGridLayout = new GridLayout(0,1);
		buttonPanelGridLayout.setVgap(5);
		buttonPanel.setLayout(buttonPanelGridLayout);
		buttonPanel.add(shortReportButton);
		
		buttonPanel.add(longReportButton);
		buttonPanel.add(resetButton);
		buttonPanel.add(exportButton);
		buttonPanel.add(loadFilteredMutationsButton);
		buttonPanel.add(loadIGVButton);
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		northPanel.add(filterPanel, BorderLayout.CENTER);
		northPanel.add(buttonPanel, BorderLayout.EAST);
		
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(northPanel, BorderLayout.NORTH);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
		setContentPane(contentPane);
	}
	
	private void createSortChangeListener(){
		tabbedPane.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
	        	int selectedIndex = tabbedPane.getSelectedIndex();

				int selectedRow = selectedTable.getSelectedRow();
	        	
	        	int[] modelRowToViewRow = new int[selectedTable.getRowCount()];
	        	for(int i = 0; i < selectedTable.getRowCount(); i++){
	        		modelRowToViewRow[i] = selectedTable.convertRowIndexToView(i);
	        	}
	        	mutationList.sortModel(modelRowToViewRow);
	        	selectedTable.getRowSorter().setSortKeys(null);
	        	int currentVerticalScrollValue = selectedScrollPane.getVerticalScrollBar().getValue();
	        	if(selectedIndex == 0){
	        		selectedTable = basicTabTable;
	        		selectedScrollPane = basicTabScrollPane;
				}else if(selectedIndex == 1){
					selectedTable = vepTabTable;
					selectedScrollPane = vepTabScrollPane;
	        	}else if(selectedIndex == 2){
	        		selectedTable = cosmicTabTable;
	        		selectedScrollPane = cosmicTabScrollPane;
	        	}else if(selectedIndex == 3){
	        		selectedTable = g1000TabTable;
	        		selectedScrollPane = g1000TabScrollPane;
				}else if(selectedIndex == 4){
					selectedTable = clinVarTabTable;
					selectedScrollPane = clinVarTabScrollPane;
				}else if(selectedIndex == 5){
					selectedTable = gnomadTabTable;
					selectedScrollPane = gnomadTabScrollPane;
				}else if(selectedIndex == 6){
					selectedTable = oncokbTabTable;
					selectedScrollPane = oncokbTabScrollPane;
				}else if(selectedIndex == 7){
					selectedTable = civicTabTable;
					selectedScrollPane = civicTabScrollPane;
	        	}else{
	        		//undefined
	        		return;
	        	}
	        	
	        	selectedScrollPane.getVerticalScrollBar().setValue(currentVerticalScrollValue);
	        	selectedTable.resizeColumnWidths();

				if (selectedRow != -1) {
					selectedTable.addRowSelectionInterval(selectedRow, selectedRow);
				}

	        }
	    });
	}
	
	private void applyRowFilters(){
		boolean includeCosmicOnly = cosmicOnlyCheckbox.isSelected();
		boolean includeReportedOnly = reportedOnlyCheckbox.isSelected();
		boolean filterNormalPair = filterNomalCheckbox.isSelected();
		int sampleID = (mutationList.getMutationCount() > 0) ? mutationList.getMutation(0).getSampleID() : -1;
		int frequencyFrom =  getNumber(textFreqFrom, Configurations.getAlleleFrequencyFilter(sample));
		int frequencyTo = getNumber(textVarFreqTo, Configurations.MAX_ALLELE_FREQ_FILTER);
		int minOccurence = getNumber(occurenceFromTextField, Configurations.MIN_OCCURENCE_FILTER);
		int minReadDepth = getNumber(minReadDepthTextField, Configurations.READ_DEPTH_FILTER);
		int maxPopulationFrequency = getNumber(maxPopulationFrequencyTextField, Configurations.MAX_GLOBAL_ALLELE_FREQ_FILTER);
		VariantPredictionClass minPredictionClass = (VariantPredictionClass)predictionFilterComboBox.getSelectedItem();
		try {
			mutationList.filterMutations(includeCosmicOnly, includeReportedOnly, filterNormalPair, sampleID, frequencyFrom, frequencyTo, minOccurence, minReadDepth, maxPopulationFrequency, minPredictionClass);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error applying filter:" + e.getMessage());
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

	private void resetFilters(){
		cosmicOnlyCheckbox.setSelected(false);
		reportedOnlyCheckbox.setSelected(false);		
		filterNomalCheckbox.setSelected(false);
		textFreqFrom.setText(Configurations.getAlleleFrequencyFilter(sample)+"");
		textVarFreqTo.setText(Configurations.MAX_ALLELE_FREQ_FILTER+"");
		minReadDepthTextField.setText(Configurations.READ_DEPTH_FILTER+"");
		occurenceFromTextField.setText(Configurations.MIN_OCCURENCE_FILTER+"");
		maxPopulationFrequencyTextField.setText(Configurations.MAX_GLOBAL_ALLELE_FREQ_FILTER+"");
		predictionFilterComboBox.setSelectedIndex(1);
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
			MutationReportGenerator.exportReport(saveAs.getSelectedFile(), basicTabTableModel, clinVarTabTableModel, cosmicTabTableModel, g1000TabTableModel);
		}
	}
	
	private void disableInputForAsynchronousLoad() {
		String tooltip = "Disabled while data is loading";
		cosmicOnlyCheckbox.setEnabled(false);
		cosmicOnlyCheckbox.setToolTipText(tooltip);
		reportedOnlyCheckbox.setEnabled(false);
		reportedOnlyCheckbox.setToolTipText(tooltip);
		filterNomalCheckbox.setEnabled(false);
		filterNomalCheckbox.setToolTipText(tooltip);
		resetButton.setEnabled(false);
		resetButton.setToolTipText(tooltip);
		textFreqFrom.setEditable(false);
		textFreqFrom.setToolTipText(tooltip);
		textVarFreqTo.setEditable(false);
		textVarFreqTo.setToolTipText(tooltip);
		minReadDepthTextField.setEditable(false);
		minReadDepthTextField.setToolTipText(tooltip);
		occurenceFromTextField.setEditable(false);
		occurenceFromTextField.setToolTipText(tooltip);
		maxPopulationFrequencyTextField.setEditable(false);
		maxPopulationFrequencyTextField.setToolTipText(tooltip);
		predictionFilterComboBox.setEnabled(false);
		predictionFilterComboBox.setToolTipText(tooltip);
	}
	
	private void enableInputAfterAsynchronousLoad() {
		cosmicOnlyCheckbox.setEnabled(true);
		cosmicOnlyCheckbox.setToolTipText("");
		reportedOnlyCheckbox.setEnabled(true);
		reportedOnlyCheckbox.setToolTipText("");
		filterNomalCheckbox.setEnabled(true);
		filterNomalCheckbox.setToolTipText("");
		resetButton.setEnabled(true);
		resetButton.setToolTipText("");
		textFreqFrom.setEditable(true);
		textFreqFrom.setToolTipText("");
		textVarFreqTo.setEditable(true);
		textVarFreqTo.setToolTipText("");
		minReadDepthTextField.setEditable(true);
		minReadDepthTextField.setToolTipText("");
		occurenceFromTextField.setEditable(true);
		occurenceFromTextField.setToolTipText("");
		maxPopulationFrequencyTextField.setEditable(true);
		maxPopulationFrequencyTextField.setToolTipText("");
		predictionFilterComboBox.setEnabled(true);
		predictionFilterComboBox.setToolTipText("");
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
		createExtraMutationDataThread();
	}
	
	private void loadFilteredMutationsAsynchronous() {
		createLoadFilteredMutationDataThread();
	}
	
	private void createExtraMutationDataThread(){
		Thread missingDataThread = new Thread(new Runnable(){
			@Override
			public void run() {
				disableInputForAsynchronousLoad();
				getDatabaseMutationData();
				getOccurrenceMutationData();
				enableInputAfterAsynchronousLoad();
				if(sample != null) {
					loadFilteredMutationsButton.setEnabled(true);//now that the unfiltered data is loaded, enable the option to load the filtered data			
				}
			}
		});
		missingDataThread.start();
	}
	
	private void getDatabaseMutationData(){
		for(int index = 0; index < basicTabTableModel.getRowCount(); index++){
			if(isWindowClosed){
				return;
			}
			try{

				Mutation mutation = basicTabTableModel.getMutation(index);

				//cosmic
				ArrayList<String> cosmicIDs = DatabaseCommands.getCosmicIDs(mutation);
				basicTabTableModel.updateModel(index, cosmicIDs);

				//gnomad
				mutation.setGnomadID();

				//oncokb
				DatabaseCommands.updateOncokbInfo(mutation);
				mutation.setOncokbID();

				//civic
				DatabaseCommands.updateCivicInfo(mutation);
				mutation.setCivicID();

			}catch(Exception e){
				JOptionPane.showMessageDialog(this, e.getMessage() + " : " + e.getClass().getName() + ": Could not load cosmic data.");
			}
		}
		
		for(int index = 0; index < mutationList.getFilteredMutationCount(); index++){
			if(isWindowClosed){
				return;
			}

			try{
				Mutation mutation = mutationList.getFilteredMutation(index);

				//cosmic
				ArrayList<String> cosmicIDs = DatabaseCommands.getCosmicIDs(mutation);
				basicTabTableModel.updateModel(index, cosmicIDs);

				//gnomad
				mutation.setGnomadID();

				//oncokb
				DatabaseCommands.updateOncokbInfo(mutation);
				mutation.setOncokbID();

				//civic
				DatabaseCommands.updateCivicInfo(mutation);
				mutation.setCivicID();
				//do this here since these mutations are not in the basicTabTableModel
				mutation.setCosmicID(cosmicIDs);

			}catch(Exception e){
				JOptionPane.showMessageDialog(this, e.getMessage() + " : " + e.getClass().getName() + ": Could not load cosmic data.");
			}
		}
	}
	
	private void getOccurrenceMutationData(){
		for(int index = 0; index < basicTabTableModel.getRowCount(); index++){
			if(isWindowClosed){
				return;
			}
			try{
				Mutation mutation = basicTabTableModel.getMutation(index);
				int count = DatabaseCommands.getOccurrenceCount(mutation);
				basicTabTableModel.updateModel(index,  count);
			}catch(Exception e){
				JOptionPane.showMessageDialog(this, e.getMessage() + " : " + e.getClass().getName() + ": Could not load occurrence count data.");
			}
		}
		
		for(int index = 0; index < mutationList.getFilteredMutationCount(); index++){
			if(isWindowClosed){
				return;
			}
			
			try{
				Mutation mutation = mutationList.getFilteredMutation(index);
				int count = DatabaseCommands.getOccurrenceCount(mutation);
				
				//do this here since these mutations are not in the basicTabTableModel
				mutation.setOccurrence(count);
			}catch(Exception e){
				JOptionPane.showMessageDialog(this, e.getMessage() + " : " + e.getClass().getName() + ": Could not load occurrence count data.");
			}
		}
	}
	
	private void createLoadFilteredMutationDataThread(){
		Thread loadFilteredMutationDataThread = new Thread(new Runnable(){
			@Override
			public void run() {
				disableInputForAsynchronousLoad();
				getFilteredMutationData();
				enableInputAfterAsynchronousLoad();
			}
		});
		loadFilteredMutationDataThread.start();
	}
	
	private void getFilteredMutationData() {
		try{
			loadFilteredMutationsButton.setText("Loading...");
			ArrayList<Mutation> mutations = DatabaseCommands.getFilteredMutationDataByID(sample);
			for(int i = 0; i < mutations.size(); i++) {
				if(isWindowClosed){
					return;
				}
				loadFilteredMutationsButton.setText("Loading " + (i+1) + " of " + mutations.size());
				try{
					Mutation mutation = mutations.get(i);
					ArrayList<String> cosmicIDs = DatabaseCommands.getCosmicIDs(mutation);
					int count = DatabaseCommands.getOccurrenceCount(mutation);

					//can update here because basicTabTableModel.addFilteredMutation() adds to non-visible list of mutations
					mutation.setCosmicID(cosmicIDs);
					mutation.setGnomadID();
					mutation.setOccurrence(count);

					basicTabTableModel.addFilteredMutation(mutation);
				}catch(Exception e){
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, e.getMessage() + " : " + e.getClass().getName() + ": Could not load additional mutation data.");
				}
			}
			applyRowFilters();
		}catch(Exception ex){
			JOptionPane.showMessageDialog(MutationListFrame.this, ex.getMessage());
		}
		loadFilteredMutationsButton.setText("Filtered mutations loaded");
	}
	
	//TODO disable HTTP access to BAM files
	private void loadIGVAsynchronous() throws Exception{
		loadIGVButton.setText("Finding BAM File...");
		File bamFile = SSHConnection.loadBAMForIGV(sample, loadIGVButton);
		
		loadIGVButton.setText("Loading File Into IGV...");
		String response = IGVConnection.loadFileIntoIGV(this, bamFile);
		
		if(response.equals("OK")) {
			JOptionPane.showMessageDialog(this, "BAM file successfully loaded into IGV");
		}else if(!response.equals("")){
			JOptionPane.showMessageDialog(this, response);
		}
	}
}
