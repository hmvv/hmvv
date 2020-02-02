package hmvv.gui.mutationlist;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import hmvv.gui.mutationlist.tablemodels.*;
import hmvv.gui.mutationlist.tables.*;
import hmvv.io.AsynchronousCallback;
import hmvv.io.AsynchronousMutationDataIO;
import hmvv.io.MutationReportGenerator;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.main.HMVVFrame;
import hmvv.model.Sample;

public class MutationListFrame extends JPanel implements AsynchronousCallback{
	private static final long serialVersionUID = 1L;
	
	public final HMVVFrame parent;
	
	private MutationListMenuBar mutationListMenuBar;
	
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

	private PmkbTable pmkbTabTable;
	private PmkbTableModel pmkbTabTableModel;

	private JScrollPane basicTabScrollPane;
	private JScrollPane vepTabScrollPane;
	private JScrollPane cosmicTabScrollPane;
	private JScrollPane g1000TabScrollPane;
	private JScrollPane clinVarTabScrollPane;
	private JScrollPane gnomadTabScrollPane;
	private JScrollPane oncokbTabScrollPane;
	private JScrollPane civicTabScrollPane;
	private JScrollPane pmkbTabScrollPane;

	private MutationList mutationList;
	private MutationListFilters mutationListFilters;
	
	private JTabbedPane tabbedPane;
	private CommonTable selectedTable;
	private JScrollPane selectedScrollPane;
	
	private Sample sample;
	private MutationFilterPanel mutationFilterPanel;
	
	private volatile boolean isWindowClosed = false;
		
	public MutationListFrame(HMVVFrame parent, Sample sample, MutationList mutationList){
		super();
		this.parent = parent;
		this.mutationList = mutationList;
		this.sample = sample;
		this.mutationListFilters = new MutationListFilters();
		
		constructComponents();
		layoutComponents();
		createSortChangeListener();
		
		for(int i = 0; i < mutationList.getMutationCount(); i++){
			mutationList.getMutation(i).setCosmicID("LOADING...");
		}
		AsynchronousMutationDataIO.loadMissingDataAsynchronous(mutationList, this);
	}

	private void constructComponents() {		
		mutationFilterPanel = new MutationFilterPanel(this,sample, mutationList, mutationListFilters);
		mutationFilterPanel.resetFilters();
		
		mutationListMenuBar = new MutationListMenuBar(parent, this, sample, mutationList, mutationFilterPanel);
		parent.setJMenuBar(mutationListMenuBar);
		
		constructTabs();
	}
	
	private void constructTabs(){
		basicTabTableModel = new BasicTableModel(mutationList);
		basicTabTable = new BasicTable(parent, basicTabTableModel);
		basicTabTable.setAutoCreateRowSorter(true);

		vepTabTableModel = new VEPTableModel(mutationList);
		vepTabTable = new VEPTable(parent, vepTabTableModel);
		vepTabTable.setAutoCreateRowSorter(true);
		
		cosmicTabTableModel = new CosmicTableModel(mutationList);
		cosmicTabTable = new CosmicTable(parent, cosmicTabTableModel);
		cosmicTabTable.setAutoCreateRowSorter(true);

		g1000TabTableModel = new G1000TableModel(mutationList);
		g1000TabTable = new G1000Table(parent, g1000TabTableModel);
		g1000TabTable.setAutoCreateRowSorter(true);

		clinVarTabTableModel = new ClinVarTableModel(mutationList);
		clinVarTabTable = new ClinVarTable(parent, clinVarTabTableModel);
		clinVarTabTable.setAutoCreateRowSorter(true);

		gnomadTabTableModel = new GnomadTableModel(mutationList);
		gnomadTabTable = new GnomadTable(parent, gnomadTabTableModel);
		gnomadTabTable.setAutoCreateRowSorter(true);

		oncokbTabTableModel = new OncokbTableModel(mutationList);
		oncokbTabTable = new OncokbTable(parent, oncokbTabTableModel);
		oncokbTabTable.setAutoCreateRowSorter(true);

		civicTabTableModel = new CivicTableModel(mutationList);
		civicTabTable = new CivicTable(parent, civicTabTableModel);
		civicTabTable.setAutoCreateRowSorter(true);

		pmkbTabTableModel = new PmkbTableModel(mutationList);
		pmkbTabTable = new PmkbTable(parent, pmkbTabTableModel);
		pmkbTabTable.setAutoCreateRowSorter(true);
	}
	
	private void layoutComponents(){
		basicTabScrollPane = new JScrollPane(basicTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		basicTabTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		
		vepTabScrollPane = new JScrollPane(vepTabTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
		tabbedPane.addTab("Cosmic", null, cosmicTabScrollPane, null);
		tabbedPane.addTab("G1000", null, g1000TabScrollPane, null);
		tabbedPane.addTab("ClinVar", null, clinVarTabScrollPane, null);
		tabbedPane.addTab("Gnomad", null, gnomadTabScrollPane, null);
		tabbedPane.addTab("Oncokb", null, oncokbTabScrollPane, null);
		tabbedPane.addTab("Civic", null, civicTabScrollPane, null);
		tabbedPane.addTab("PMKB", null, pmkbTabScrollPane, null);

		selectedTable = basicTabTable;
		selectedScrollPane = basicTabScrollPane;
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		northPanel.add(mutationFilterPanel, BorderLayout.WEST);

		setLayout(new BorderLayout());
		add(northPanel, BorderLayout.NORTH);
		add(tabbedPane, BorderLayout.CENTER);
		
		setBorder(new EmptyBorder(15, 15, 15, 15));
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
	        	}else if(selectedIndex == 8){
					selectedTable = pmkbTabTable;
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
		MutationReportGenerator.exportReport(exportFile, basicTabTableModel, clinVarTabTableModel, cosmicTabTableModel, g1000TabTableModel);
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
}
