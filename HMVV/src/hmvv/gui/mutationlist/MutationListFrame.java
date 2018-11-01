package hmvv.gui.mutationlist;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.tablemodels.*;
import hmvv.gui.mutationlist.tables.*;
import hmvv.gui.sampleList.SampleListFrame;
import hmvv.io.AsynchronousCallback;
import hmvv.io.AsynchronousMutationDataIO;
import hmvv.io.MutationReportGenerator;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.model.Sample;

public class MutationListFrame extends JDialog implements AsynchronousCallback{
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
	private MutationListFilters mutationListFilters;
	
	private JPanel contentPane;
	private JTabbedPane tabbedPane;
	private CommonTable selectedTable;
	private JScrollPane selectedScrollPane;
	
	private Sample sample;
	private MutationFilterPanel mutationFilterPanel;
	private MutationFeaturePanel mutationFeaturePanel;
	
	private volatile boolean isWindowClosed;
	
	//TODO Refactor common code isntead of using sample == null so can be used for MutationList or MutationSearch.
	public MutationListFrame(SampleListFrame parent, MutationList mutationList){
		super(parent);
		String title = "Mutation Search Results";
		setTitle(title);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*.85), (int)(bounds.height*.85));
		setMinimumSize(new Dimension(900, getHeight()/2));
		
		this.mutationList = mutationList;
		
		constructComponents();
		layoutComponents();
		createSortChangeListener();
		setLocationRelativeTo(parent);
		
		isWindowClosed = false;
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent arg0) {
				isWindowClosed = true;
			}
		});
		
		AsynchronousMutationDataIO.loadMissingDataAsynchronous(mutationList, this);
	}
	
	public MutationListFrame(SampleListFrame parent, Sample sample, MutationList mutationList){
		super(parent);
		String title = "Mutation List - " + sample.getLastName() + "," + sample.getFirstName() + "," + sample.getOrderNumber() +
				" (sampleName = "+ sample.sampleName +", sampleID = " + sample.sampleID + ", runID = " + sample.runID + ", assay = " + sample.assay +", instrument = " + sample.instrument +  ")";
		setTitle(title);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*.85), (int)(bounds.height*.85));
		setMinimumSize(new Dimension(1220, getHeight()/2));
		
		this.sample = sample;
		this.mutationList = mutationList;
		this.mutationListFilters = new MutationListFilters();
		
		constructComponents();
		layoutComponents();
		createSortChangeListener();
		setLocationRelativeTo(parent);
		
		mutationFilterPanel.resetFilters();
		
		isWindowClosed = false;
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent arg0) {
				isWindowClosed = true;
			}
		});
		
		AsynchronousMutationDataIO.loadMissingDataAsynchronous(mutationList, this);
	}

	private void constructComponents() {
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		if(sample != null) {
			mutationFilterPanel = new MutationFilterPanel(this,sample, mutationList, mutationListFilters);
			mutationFeaturePanel = new MutationFeaturePanel(this, sample, mutationList);
		}
		constructTabs();
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
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		if(sample != null) {
			northPanel.add(mutationFilterPanel, BorderLayout.WEST);
			northPanel.add(mutationFeaturePanel, BorderLayout.CENTER);
		}
		
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
	
	void exportReport(File exportFile) throws IOException {
		MutationReportGenerator.exportReport(exportFile, basicTabTableModel, clinVarTabTableModel, cosmicTabTableModel, g1000TabTableModel);
	}	
	
	public void disableInputForAsynchronousLoad() {
		if(sample != null) {
			mutationFeaturePanel.disableInputForAsynchronousLoad();
			mutationFilterPanel.disableInputForAsynchronousLoad();
		}
	}
	
	public void enableInputAfterAsynchronousLoad() {
		if(sample != null) {
			mutationFeaturePanel.enableInputAfterAsynchronousLoad();
			mutationFilterPanel.enableInputAfterAsynchronousLoad();
		}
	}
	
	public void mutationListIndexUpdated(int index) {
		basicTabTableModel.fireTableRowsUpdated(index, index);
	}
	
	public void showErrorMessage(Exception e, String message) {
		HMVVDefectReportFrame.showHMVVDefectReportFrame(this, e, message);
	}
	
	public boolean isCallbackClosed() {
		return this.isWindowClosed;
	}
}
