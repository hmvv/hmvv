package hmvv.main;

import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import hmvv.gui.GUICommonTools;
import hmvv.gui.adminFrames.DatabaseInformation;
import hmvv.gui.adminFrames.EnterSample;
import hmvv.gui.adminFrames.EnterHEMESample;
import hmvv.gui.adminFrames.MonitorPipelines;
import hmvv.gui.adminFrames.QualityControlFrame;
import hmvv.gui.adminFrames.QualityControlTumorMutationBurden;
import hmvv.gui.sampleList.SampleListFrame;
import hmvv.gui.sampleList.TumorMutationBurdenFrame;
import hmvv.io.DatabaseCommands;
import hmvv.io.SSHConnection;
import hmvv.model.Assay;
import hmvv.model.GeneQCDataElementTrend;
import hmvv.model.Sample;
import hmvv.model.TMBSample;

public class HMVVFrame extends JFrame{
	private static final long serialVersionUID = 1L;

	private JTabbedPane tabbedPane;
	private SampleListFrame samplePanel;
	private ArrayList<Sample> samples;
	
	//Menu
	private JMenuBar menuBar;
	private JMenu adminMenu;
	private JMenuItem enterSampleMenuItem;
	private JMenuItem enterHEMESampleMenuItem;
	private JMenuItem monitorPipelinesItem;
	private JMenuItem databaseInformationMenuItem;
	private JMenu qualityControlMenuItem;
	private volatile JMenuItem refreshLabel;
	

	//Asynchronous sample status updates
	private Thread pipelineRefreshThread;
	private final int secondsToSleep = 60*5;
	private volatile long timeLastRefreshed = 0;
	
	/**
	 * Initialize the contents of the frame.
	 */
	public HMVVFrame(HMVVLoginFrame parent, ArrayList<Sample> samples) throws Exception {
		super( "HMVV (" + Configurations.DATABASE_NAME + ")");
		this.samples = samples;
		
		Rectangle bounds = GUICommonTools.getScreenBounds(parent);
		setSize((int)(bounds.width*.97), (int)(bounds.height*.90));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				SSHConnection.shutdown();
			}
		});
		
		createMenu();
		createComponents();
		layoutComponents();
		setupPipelineRefreshThread();
		setLocationRelativeTo(parent);
	}

	private void createComponents() throws Exception {
		samplePanel = new SampleListFrame(this, samples);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(GUICommonTools.TAHOMA_BOLD_14);
		tabbedPane.addTab("Samples", null, samplePanel, null);
	}
	
	private void layoutComponents() {
		add(tabbedPane);
	}
	
	public void createTumorMutationBurdenFrame(TMBSample sample) throws Exception {
		TumorMutationBurdenFrame tmbFrame = new TumorMutationBurdenFrame(HMVVFrame.this, (TMBSample)sample);
	    tmbFrame.setVisible(true);
	}

	private void createMenu(){
		menuBar = new JMenuBar();
		adminMenu = new JMenu("Admin");
		adminMenu.setFont(GUICommonTools.TAHOMA_BOLD_17);

		enterHEMESampleMenuItem = new JMenuItem("Enter HEME Samples");
		enterHEMESampleMenuItem.setEnabled(SSHConnection.isSuperUser(Configurations.USER_FUNCTION.ENTER_SAMPLE));
		enterSampleMenuItem = new JMenuItem("Enter Sample");
		enterSampleMenuItem.setEnabled(SSHConnection.isSuperUser(Configurations.USER_FUNCTION.ENTER_SAMPLE));
		monitorPipelinesItem = new JMenuItem("Monitor Pipelines");
		databaseInformationMenuItem = new JMenuItem("Database Information");
		qualityControlMenuItem = new JMenu("Quality Control");
		refreshLabel = new JMenuItem("Loading status refresh...");
		refreshLabel.setEnabled(false);

		menuBar.add(adminMenu);
		adminMenu.add(enterSampleMenuItem);
		adminMenu.add(enterHEMESampleMenuItem);
		adminMenu.add(monitorPipelinesItem);

		adminMenu.addSeparator();
		adminMenu.add(qualityControlMenuItem);
		try{
			for(Assay assay : DatabaseCommands.getAllAssays()){
				if(assay.assayName.equals("tmb")) {
					JMenuItem tmbDashboard = new JMenuItem(assay + "_Assay");
					qualityControlMenuItem.add(tmbDashboard);
					tmbDashboard.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
									QualityControlTumorMutationBurden tmbDashboard = new QualityControlTumorMutationBurden(HMVVFrame.this);
									tmbDashboard.setVisible(true);
							} catch (Exception e1) {
								HMVVDefectReportFrame.showHMVVDefectReportFrame(HMVVFrame.this, e1);
							}
						}
					});
				} else {
					JMenuItem variantAlleleFrequencyMenuItem = new JMenuItem(assay + "_VariantAlleleFrequency");
					qualityControlMenuItem.add(variantAlleleFrequencyMenuItem);
					variantAlleleFrequencyMenuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								TreeMap<String, GeneQCDataElementTrend> ampliconTrends = DatabaseCommands.getSampleQCData(assay);
								QualityControlFrame qcFrame = new QualityControlFrame(HMVVFrame.this, ampliconTrends, assay.assayName, "Variant allele freqency over time", "Sample ID", "Variant allele freqency");
								qcFrame.setVisible(true);
							} catch (Exception e1) {
								HMVVDefectReportFrame.showHMVVDefectReportFrame(HMVVFrame.this, e1);
							}
						}
					});
				}
			}
		}catch(Exception e){
			//unable to get assays
		}

		adminMenu.addSeparator();
		adminMenu.add(databaseInformationMenuItem);
		adminMenu.add(refreshLabel);

		setJMenuBar(menuBar);

		ActionListener listener = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(e.getSource() == enterSampleMenuItem){
						EnterSample sampleEnter = new EnterSample(HMVVFrame.this, samplePanel);
						sampleEnter.setVisible(true);
					}else if(e.getSource() == enterHEMESampleMenuItem){
						EnterHEMESample sampleHEMEEnter = new EnterHEMESample(HMVVFrame.this, samplePanel);
						sampleHEMEEnter.setVisible(true);
					}else if(e.getSource() == monitorPipelinesItem) {
						handleMonitorPipelineClick();
					}else if(e.getSource() == databaseInformationMenuItem){
						handledatabaseInformationClick();
					}
				} catch (Exception e1) {
					HMVVDefectReportFrame.showHMVVDefectReportFrame(HMVVFrame.this, e1);
				}
			}
		};

		enterSampleMenuItem.addActionListener(listener);
		enterHEMESampleMenuItem.addActionListener(listener);
		monitorPipelinesItem.addActionListener(listener);
		qualityControlMenuItem.addActionListener(listener);
		databaseInformationMenuItem.addActionListener(listener);
	}

	private void setupPipelineRefreshThread() {		
		pipelineRefreshThread = new Thread(new Runnable() {
			@Override
			public void run() {
				//loop forever (will exit when JFrame is closed).
				while(true) {
					long currentTimeInMillis = System.currentTimeMillis();
					if(timeLastRefreshed + (1000 * secondsToSleep) < currentTimeInMillis) {
						refreshLabel.setText("Refreshing table...");
						if(!samplePanel.updatePipelinesASynch()){
							JOptionPane.showMessageDialog(HMVVFrame.this, "Failure to update pipeline status details. Please contact the administrator.");
							refreshLabel.setText("Status refresh disabled");
							return;
						}
						timeLastRefreshed = System.currentTimeMillis();
					}

					setRefreshLabelText();

					try {
						Thread.sleep(1 * 1000);
					} catch (InterruptedException e) {}
				}
			}
		});
		pipelineRefreshThread.setName("Sample List Pipeline Status Refresh");
		pipelineRefreshThread.start();
	}
	
	private void setRefreshLabelText() {
		long currentTimeInMillis = System.currentTimeMillis();
		long timeToRefresh = timeLastRefreshed + (1000 * secondsToSleep);
		long diff = timeToRefresh - currentTimeInMillis;
		long secondsRemaining = diff / 1000;
		refreshLabel.setText("Status refresh in " + secondsRemaining + "s");
	}
	
	private void handledatabaseInformationClick() throws Exception {
		DatabaseInformation dbinfo = new DatabaseInformation(this);
		dbinfo.setVisible(true);
	}
	

	private void handleMonitorPipelineClick() throws Exception{
		Thread monitorPipelineThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					MonitorPipelines monitorpipelines = new MonitorPipelines(HMVVFrame.this);
					monitorpipelines.setVisible(true);
				} catch (Exception e) {
					HMVVDefectReportFrame.showHMVVDefectReportFrame(HMVVFrame.this, e, "Error loading Monitor Pipeline window.");
				}
				setCursor(Cursor.getDefaultCursor());
			}
		});
		monitorPipelineThread.start();
	}
}
