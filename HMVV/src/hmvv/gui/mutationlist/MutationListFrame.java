package hmvv.gui.mutationlist;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.tablemodels.*;
import hmvv.gui.mutationlist.tables.*;
import hmvv.gui.sampleList.EditSampleFrame;
import hmvv.gui.sampleList.ReportFramePatientHistory;
import hmvv.gui.sampleList.SampleListFrame;
import hmvv.gui.sampleList.EditSampleFrame.RESPONSE_CODE;
import hmvv.io.AsynchronousCallback;
import hmvv.io.AsynchronousMutationDataIO;
import hmvv.io.LIS.LISConnection;
import hmvv.io.MutationReportGenerator;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.main.HMVVFrame;
import hmvv.model.MutationSomatic;
import hmvv.model.PatientHistory;
import hmvv.model.Sample;

public class MutationListFrame extends JDialog implements AsynchronousCallback{
	private static final long serialVersionUID = 1L;
	
	public final HMVVFrame parent;
	
	private MutationListMenuBar mutationListMenuBar;
	
	private BasicTable basicTabTable;
	private BasicTableModel basicTabTableModel;

	private VEPTable vepTabTable;
	private VEPTableModel vepTabTableModel;

	private VCallersTable VCallersTabTable;
	private VCallersTableModel VCallersTabTableModel;

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

	private PmkbTable pmkbTabTable;
	private PmkbTableModel pmkbTabTableModel;

	private ReportFramePatientHistory reportFrame;

	private JScrollPane basicTabScrollPane;
	private JScrollPane vepTabScrollPane;
	private JScrollPane VCallersTabScrollPane;
	private JScrollPane cosmicTabScrollPane;
	private JScrollPane g1000TabScrollPane;
	private JScrollPane clinVarTabScrollPane;
	private JScrollPane gnomadTabScrollPane;
	private JScrollPane oncokbTabScrollPane;
	private JScrollPane civicTabScrollPane;
	private JScrollPane pmkbTabScrollPane;
	private JScrollPane patientHistoryTabScrollPane;

	private MutationList mutationList;
	private MutationListFilters mutationListFilters;

	private JTabbedPane tabbedPane;
	private CommonTable selectedTable;
	private JScrollPane selectedScrollPane;
	
	private Sample sample;
	private MutationFilterPanel mutationFilterPanel;
	private SampleListFrame sampleListFrame;

	private volatile boolean isWindowClosed;
		
	public MutationListFrame(HMVVFrame parent,SampleListFrame sampleListFrame, Sample sample, MutationList mutationList){
		super(parent, "Title Set Later", ModalityType.APPLICATION_MODAL);
		String title = "Mutation List - " + sample.getLastName() + "," + sample.getFirstName() + "," + sample.getOrderNumber() +
				" (sampleName = "+ sample.sampleName +", sampleID = " + sample.sampleID + ", runID = " + sample.runID + ", assay = " + sample.assay +", instrument = " + sample.instrument +  ")";
		setTitle(title);

		this.parent = parent;
		this.mutationList = mutationList;
		this.sample = sample;
		this.mutationListFilters = new MutationListFilters();
		this.sampleListFrame = sampleListFrame;
		
		constructComponents();
		layoutComponents();
		createSortChangeListener();


		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*.85), (int)(bounds.height*.85));
		setMinimumSize(new Dimension(700, getHeight()/3));

		setLocationRelativeTo(parent);		

		for(int i = 0; i < mutationList.getMutationCount(); i++){
			MutationSomatic current_mutation = (MutationSomatic)mutationList.getMutation(i);
			current_mutation.addCosmicIDLoading();
		}

		isWindowClosed = false;
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent arg0) {
				isWindowClosed = true;
			}
		});

		AsynchronousMutationDataIO.loadMissingDataAsynchronous(sample,mutationList, this);


	}

	private void constructComponents() {
		mutationFilterPanel = new MutationFilterPanel(this,sample, mutationList, mutationListFilters);
		mutationFilterPanel.resetFilters();
		
		mutationListMenuBar = new MutationListMenuBar(this, this, sample, mutationList, mutationFilterPanel);
		setJMenuBar(mutationListMenuBar);
		constructTabs();
	}
	
	private void constructTabs(){
		basicTabTableModel = new BasicTableModel(mutationList);
		basicTabTable = new BasicTable(this, basicTabTableModel);
		basicTabTable.setAutoCreateRowSorter(true);

		vepTabTableModel = new VEPTableModel(mutationList);
		vepTabTable = new VEPTable(this, vepTabTableModel);
		vepTabTable.setAutoCreateRowSorter(true);

		VCallersTabTableModel = new VCallersTableModel(mutationList);
		VCallersTabTable = new VCallersTable(this, VCallersTabTableModel);
		VCallersTabTable.setAutoCreateRowSorter(true);
		
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

		pmkbTabTableModel = new PmkbTableModel(mutationList);
		pmkbTabTable = new PmkbTable(this, pmkbTabTableModel);
		pmkbTabTable.setAutoCreateRowSorter(true);

	}
	
	private void layoutComponents(){
		basicTabScrollPane = new JScrollPane(basicTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		basicTabTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		
		vepTabScrollPane = new JScrollPane(vepTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		VCallersTabScrollPane = new JScrollPane(VCallersTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		cosmicTabScrollPane = new JScrollPane(cosmicTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		g1000TabScrollPane = new JScrollPane(g1000TabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		clinVarTabScrollPane = new JScrollPane(clinVarTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		gnomadTabScrollPane = new JScrollPane(gnomadTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		oncokbTabScrollPane = new JScrollPane(oncokbTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		civicTabScrollPane = new JScrollPane(civicTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pmkbTabScrollPane =  new JScrollPane(pmkbTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Basic", null, basicTabScrollPane, null);
		tabbedPane.addTab("VEP", null, vepTabScrollPane, null);
		tabbedPane.addTab("VCallers", null, VCallersTabScrollPane, null);
		tabbedPane.addTab("Cosmic", null, cosmicTabScrollPane, null);
		tabbedPane.addTab("G1000", null, g1000TabScrollPane, null);
		tabbedPane.addTab("ClinVar", null, clinVarTabScrollPane, null);
		tabbedPane.addTab("Gnomad", null, gnomadTabScrollPane, null);
		tabbedPane.addTab("Oncokb", null, oncokbTabScrollPane, null);
		tabbedPane.addTab("Civic", null, civicTabScrollPane, null);
		tabbedPane.addTab("PMKB", null, pmkbTabScrollPane, null);

		if (patientHistoryAvailable()) {
			createPatientHistory();
			patientHistoryTabScrollPane = new JScrollPane(reportFrame, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			tabbedPane.addTab("Patient History", null, patientHistoryTabScrollPane, null);
		}else{
			tabbedPane.addTab("Patient History", null, null);
			tabbedPane.setEnabledAt(10, false);
		}

		selectedTable = basicTabTable;
		selectedScrollPane = basicTabScrollPane;
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		northPanel.add(mutationFilterPanel, BorderLayout.WEST);

		setLayout(new BorderLayout());
		add(northPanel, BorderLayout.NORTH);
		add(tabbedPane, BorderLayout.CENTER);
		
//		setBorder(new EmptyBorder(15, 15, 15, 15));
	}

	public void createTab(String title, JPanel panel) {
		tabbedPane.addTab(title, null, panel, null);
		tabbedPane.setSelectedComponent(panel);
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
	        		selectedTable = VCallersTabTable;
	        		selectedScrollPane = VCallersTabScrollPane;
				}else if(selectedIndex == 3){
	        		selectedTable = cosmicTabTable;
	        		selectedScrollPane = cosmicTabScrollPane;
	        	}else if(selectedIndex == 4){
	        		selectedTable = g1000TabTable;
	        		selectedScrollPane = g1000TabScrollPane;
				}else if(selectedIndex == 5){
					selectedTable = clinVarTabTable;
					selectedScrollPane = clinVarTabScrollPane;
				}else if(selectedIndex == 6){
					selectedTable = gnomadTabTable;
					selectedScrollPane = gnomadTabScrollPane;
				}else if(selectedIndex == 7){
					selectedTable = oncokbTabTable;
					selectedScrollPane = oncokbTabScrollPane;
				}else if(selectedIndex == 8){
					selectedTable = civicTabTable;
					selectedScrollPane = civicTabScrollPane;
	        	}else if(selectedIndex == 9){
					selectedTable = pmkbTabTable;
					selectedScrollPane = pmkbTabScrollPane;
				}else if(selectedIndex == 10){
					selectedScrollPane = pmkbTabScrollPane;
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
	
	void exportReport(File exportFile) throws IOException {
		MutationReportGenerator.exportReportSomatic(exportFile, basicTabTableModel, clinVarTabTableModel, cosmicTabTableModel, g1000TabTableModel);
	}	
	
	public void disableInputForAsynchronousLoad() {
		if(sample != null) {
			mutationListMenuBar.disableInputForAsynchronousLoad();
			mutationFilterPanel.disableInputForAsynchronousLoad();
		}
	}
	
	public void enableInputAfterAsynchronousLoad() {
		if(sample != null) {
			mutationListMenuBar.enableInputAfterAsynchronousLoad();
			mutationFilterPanel.enableInputAfterAsynchronousLoad();
		}
	}
	
	public void mutationListIndexUpdated(int index) {
		basicTabTableModel.fireTableRowsUpdated(index, index);
	}
	
	public void showErrorMessage(Exception e, String message) {
		HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, e, message);
	}
	
	public void setClosed() {
		isWindowClosed = true;
	}
	
	public boolean isCallbackClosed() {
		return isWindowClosed;
	}

	private boolean patientHistoryAvailable() {
		if (sample.getOrderNumber().length() == 0 && sample.getPathNumber().length() == 0) {
			return false;
		} else {
			return true;
		}
	}

	private void createPatientHistory() {
		try {
				String labOrderNumber = sample.getOrderNumber();
					if (labOrderNumber.length() == 0) {
						labOrderNumber = LISConnection.getLabOrderNumber(sample.assay, sample.getPathNumber(), sample.sampleName);
					}
					ArrayList<PatientHistory> history = LISConnection.getPatientHistory(labOrderNumber);
					reportFrame = new ReportFramePatientHistory(this, sample, labOrderNumber, history);
		} catch (Exception e) {
			HMVVDefectReportFrame.showHMVVDefectReportFrame(this, e);
		}
	}

	public void showEditSampleFrame(){
		EditSampleFrame editSample = new EditSampleFrame(parent, sample);
		editSample.setVisible(true);
		RESPONSE_CODE responseCode = editSample.getResponseCode();
		try {
			sampleListFrame.handleEditSampleResponse(sample, responseCode);
			if (responseCode == RESPONSE_CODE.SAMPLE_DELETED){
				dispose();
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(parent, e.getMessage());
		}
	}
}
