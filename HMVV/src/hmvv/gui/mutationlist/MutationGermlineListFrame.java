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

public class MutationGermlineListFrame extends JDialog implements AsynchronousCallback{
	private static final long serialVersionUID = 1L;

	public final HMVVFrame parent;

	private MutationListGermlineMenuBar mutationListMenuBar;

	private GermlineSNPEFFTable vepTabTable;
	private GermlineSNPEFFTableModel vepTabTableModel;

	private GermlineTranscriptTable transcriptTable;
	private GermlineTranscriptTableModel transcriptTableModel;

	private GermlinePredictionTable predictionTable;
	private GermlinePredictionTableModel predictionTableModel;

	private GermlineHGMDTable HGMDTable;
	private GermlineHGMDTableModel HGMDTableModel;

	private GermlineProteinDomainTable proteinDomainTable;
	private GermlineProteinDomainTableModel proteinDomainTableModel;

	private GermlineCardiacAtlasTable cardiacAtlasTable;
	private GermlineCardiacAtlasTableModel cardiacAtlasTableModel;

	private GermlineClinVarTable clinVarTable;
	private GermlineClinVarTableModel clinVarTableModel;

	private GermlineGnomadTable gnomadTable;
	private GermlineGnomadTableModel gnomadTableModel;

	private ReportFramePatientHistory reportFrame;

	private JScrollPane vepTabScrollPane;
	private JScrollPane transcriptScrollPane;
	private JScrollPane predictionScrollPane;
	private JScrollPane proteinDomainScrollPane;
	private JScrollPane patientHistoryTabScrollPane;
	private JScrollPane cardiacAtlasScrollPane;
	private JScrollPane clinVarScrollPane;
	private JScrollPane gnomadScrollPane;
	private JScrollPane HGMDScrollPane;

	private MutationList mutationList;
	private MutationListFilters mutationListFilters;

	private JTabbedPane tabbedPane;
	private CommonTableGermline selectedTable;
	private JScrollPane selectedScrollPane;

	private Sample sample;
	private MutationGermlineFilterPanel mutationFilterPanel;

	private volatile boolean isWindowClosed;

	public MutationGermlineListFrame(HMVVFrame parent, Sample sample, MutationList mutationList){
		super(parent, "Title Set Later", ModalityType.APPLICATION_MODAL);
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
		setSize((int)(bounds.width*.90), (int)(bounds.height*.85));
		setMinimumSize(new Dimension(700, getHeight()/3));

		setLocationRelativeTo(parent);

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
		mutationFilterPanel = new MutationGermlineFilterPanel(this,sample, mutationList, mutationListFilters);
		mutationFilterPanel.resetFilters();
		
		mutationListMenuBar = new MutationListGermlineMenuBar(this, sample, mutationList, mutationFilterPanel);
		setJMenuBar(mutationListMenuBar);
		constructTabs();
	}
	
	private void constructTabs(){

		vepTabTableModel = new GermlineSNPEFFTableModel(mutationList);
		vepTabTable = new GermlineSNPEFFTable(this, vepTabTableModel);
		vepTabTable.setAutoCreateRowSorter(true);

		transcriptTableModel = new GermlineTranscriptTableModel(mutationList);
		transcriptTable = new GermlineTranscriptTable(this, transcriptTableModel);
		transcriptTable.setAutoCreateRowSorter(true);


		predictionTableModel = new GermlinePredictionTableModel(mutationList);
		predictionTable = new GermlinePredictionTable(this, predictionTableModel);
		predictionTable.setAutoCreateRowSorter(true);

		HGMDTableModel = new GermlineHGMDTableModel(mutationList);
		HGMDTable = new GermlineHGMDTable(this, HGMDTableModel);
		HGMDTable.setAutoCreateRowSorter(true);

		proteinDomainTableModel = new GermlineProteinDomainTableModel(mutationList);
		proteinDomainTable = new GermlineProteinDomainTable(this, proteinDomainTableModel);
		proteinDomainTable.setAutoCreateRowSorter(true);

		cardiacAtlasTableModel = new GermlineCardiacAtlasTableModel(mutationList);
		cardiacAtlasTable = new GermlineCardiacAtlasTable(this, cardiacAtlasTableModel);
		cardiacAtlasTable.setAutoCreateRowSorter(true);

		clinVarTableModel = new GermlineClinVarTableModel(mutationList);
		clinVarTable = new GermlineClinVarTable(this, clinVarTableModel);
		clinVarTable.setAutoCreateRowSorter(true);

		gnomadTableModel = new GermlineGnomadTableModel(mutationList);
		gnomadTable = new GermlineGnomadTable(this, gnomadTableModel);
		gnomadTable.setAutoCreateRowSorter(true);

	}
	
	private void layoutComponents(){

		vepTabScrollPane = new JScrollPane(vepTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		cardiacAtlasScrollPane = new JScrollPane(cardiacAtlasTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		transcriptScrollPane = new JScrollPane(transcriptTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		predictionScrollPane = new JScrollPane(predictionTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		proteinDomainScrollPane = new JScrollPane(proteinDomainTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		clinVarScrollPane = new JScrollPane(clinVarTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		gnomadScrollPane = new JScrollPane(gnomadTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		HGMDScrollPane = new JScrollPane(HGMDTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("SNPEFF", null, vepTabScrollPane, null);
		tabbedPane.addTab("ClinVar", null, clinVarScrollPane, null);
		tabbedPane.addTab("Gnomad", null, gnomadScrollPane, null);
		tabbedPane.addTab("Cardiac Atlas", null, cardiacAtlasScrollPane, null);
		tabbedPane.addTab("Transcript", null, transcriptScrollPane, null);
		tabbedPane.addTab("Prediction", null, predictionScrollPane, null);
		tabbedPane.addTab("Protein Domain", null, proteinDomainScrollPane, null);
		tabbedPane.addTab("HGMD", null, HGMDScrollPane, null);


		if (patientHistoryAvailable()) {
			createPatientHistory();
			patientHistoryTabScrollPane = new JScrollPane(reportFrame, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			tabbedPane.addTab("Patient History", null, patientHistoryTabScrollPane, null);
		}else{
			tabbedPane.addTab("Patient History", null, null);
			tabbedPane.setEnabledAt(8, false);
		}

		selectedTable = vepTabTable;
		selectedScrollPane = vepTabScrollPane;
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		northPanel.add(mutationFilterPanel, BorderLayout.WEST);

		setLayout(new BorderLayout());
		add(northPanel, BorderLayout.NORTH);
		add(tabbedPane, BorderLayout.CENTER);

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
					selectedTable = clinVarTable;
					selectedScrollPane = clinVarScrollPane;
				}else if(selectedIndex == 2){
					selectedTable = gnomadTable;
					selectedScrollPane = gnomadScrollPane;
				}else if(selectedIndex == 3){
					selectedTable = cardiacAtlasTable;
					selectedScrollPane = cardiacAtlasScrollPane;
				}else if(selectedIndex == 4){
					selectedTable = transcriptTable;
					selectedScrollPane = transcriptScrollPane;
				}else if(selectedIndex == 5){
					selectedTable = predictionTable;
					selectedScrollPane = predictionScrollPane;
	        	}else if(selectedIndex == 6){
					selectedTable = proteinDomainTable;
					selectedScrollPane = proteinDomainScrollPane;
				}else if(selectedIndex == 7){
					selectedTable = HGMDTable;
					selectedScrollPane = HGMDScrollPane;
				} else{
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
		MutationReportGenerator.exportReportGermline(exportFile,  clinVarTableModel);
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
