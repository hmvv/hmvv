package hmvv.gui.mutationlist;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.tablemodels.*;
import hmvv.gui.mutationlist.tables.*;
import hmvv.gui.sampleList.ReportFramePatientHistory;
import hmvv.io.AsynchronousCallback;
import hmvv.io.AsynchronousMutationDataIO;
import hmvv.io.LIS.LISConnection;
import hmvv.io.MutationReportGenerator;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.main.HMVVFrame;
import hmvv.model.PatientHistory;
import hmvv.model.Sample;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MutationListFrameGermline extends JDialog implements AsynchronousCallback{
	private static final long serialVersionUID = 1L;

	public final HMVVFrame parent;

	private MutationListMenuBarGermline mutationListMenuBar;

	private GermlineSNPEFFTable vepTabTable;
	private GermlineSNPEFFTableModel vepTabTableModel;

	private ClinVarTable clinVarTabTable;
	private ClinVarTableModel clinVarTabTableModel;

	private GnomadTable gnomadTabTable;
	private GnomadTableModel gnomadTabTableModel;

	private ReportFramePatientHistory reportFrame;

	private JScrollPane vepTabScrollPane;
	private JScrollPane clinVarTabScrollPane;
	private JScrollPane gnomadTabScrollPane;
	private JScrollPane patientHistoryTabScrollPane;

	private MutationList mutationList;
	private MutationListFilters mutationListFilters;

	private JTabbedPane tabbedPane;
	private CommonTable selectedTable;
	private JScrollPane selectedScrollPane;

	private Sample sample;
	private MutationFilterPanelGermline mutationFilterPanel;

	private volatile boolean isWindowClosed;

	public MutationListFrameGermline(HMVVFrame parent, Sample sample, MutationList mutationList){
		super();
		String title = "Mutation List - " + sample.getLastName() + "," + sample.getFirstName() + "," + sample.getOrderNumber() +
				" (sampleName = "+ sample.sampleName +", sampleID = " + sample.sampleID + ", runID = " + sample.runID + ", assay = " + sample.assay +", instrument = " + sample.instrument +  ")";
		setTitle(title);

		this.parent = parent;
		this.mutationList = mutationList;
		this.sample = sample;
		this.mutationListFilters = new MutationListFilters();
		
		constructComponents();
		layoutComponents();
		createSortChangeListener();


		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*.85), (int)(bounds.height*.85));
		setMinimumSize(new Dimension(700, getHeight()/3));

		setLocationRelativeTo(parent);
		setAlwaysOnTop(true);

		for(int i = 0; i < mutationList.getMutationCount(); i++){
			mutationList.getMutation(i).setCosmicID("LOADING...");
		}

		isWindowClosed = false;
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent arg0) {
				isWindowClosed = true;
			}
		});

//		AsynchronousMutationDataIO.loadMissingDataAsynchronous(mutationList, this);
	}

	private void constructComponents() {
		mutationFilterPanel = new MutationFilterPanelGermline(this,sample, mutationList, mutationListFilters);
		mutationFilterPanel.resetFilters();
		
		mutationListMenuBar = new MutationListMenuBarGermline(this, this, sample, mutationList, mutationFilterPanel);
		setJMenuBar(mutationListMenuBar);
		constructTabs();
	}
	
	private void constructTabs(){

		vepTabTableModel = new GermlineSNPEFFTableModel(mutationList);
		vepTabTable = new GermlineSNPEFFTable(parent, vepTabTableModel);
		vepTabTable.setAutoCreateRowSorter(true);

		clinVarTabTableModel = new ClinVarTableModel(mutationList);
		clinVarTabTable = new ClinVarTable(parent, clinVarTabTableModel);
		clinVarTabTable.setAutoCreateRowSorter(true);

		gnomadTabTableModel = new GnomadTableModel(mutationList);
		gnomadTabTable = new GnomadTable(parent, gnomadTabTableModel);
		gnomadTabTable.setAutoCreateRowSorter(true);

	}
	
	private void layoutComponents(){

		vepTabScrollPane = new JScrollPane(vepTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		clinVarTabScrollPane = new JScrollPane(clinVarTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		gnomadTabScrollPane = new JScrollPane(gnomadTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("VEP", null, vepTabScrollPane, null);
		tabbedPane.addTab("ClinVar", null, clinVarTabScrollPane, null);
		tabbedPane.addTab("Gnomad", null, gnomadTabScrollPane, null);

		if (patientHistoryAvailable()) {
			createPatientHistory();
			patientHistoryTabScrollPane = new JScrollPane(reportFrame, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			tabbedPane.addTab("Patient History", null, patientHistoryTabScrollPane, null);
		}else{
			tabbedPane.addTab("Patient History", null, null);
			tabbedPane.setEnabledAt(3, false);
		}

		selectedTable = vepTabTable;
		selectedScrollPane = vepTabScrollPane;
		
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
					selectedTable = vepTabTable;
					selectedScrollPane = vepTabScrollPane;
				}else if(selectedIndex == 1){
					selectedTable = clinVarTabTable;
					selectedScrollPane = clinVarTabScrollPane;
				}else if(selectedIndex == 2){
					selectedTable = gnomadTabTable;
					selectedScrollPane = gnomadTabScrollPane;
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
		MutationReportGenerator.exportReportGermline(exportFile, clinVarTabTableModel);
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
		vepTabTableModel.fireTableRowsUpdated(index, index);
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
}
