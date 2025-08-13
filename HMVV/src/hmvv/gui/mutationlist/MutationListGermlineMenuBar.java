
package hmvv.gui.mutationlist;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.gui.sampleList.ReportFrame;
import hmvv.io.AsynchronousMutationDataIO;
import hmvv.io.DatabaseCommands;
import hmvv.main.Configurations.REPORT_TYPE;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.model.MutationGermline;
import hmvv.model.Sample;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MutationListGermlineMenuBar extends JMenuBar {

	private static final long serialVersionUID = 1L;

	private JMenu reportMenu;
	private JMenuItem shortReportMenuItem;
	private JMenuItem longReportMenuItem;
	private JMenuItem exportMutationsMenuItem;
	private JMenu filteredMutationsMenu;
	private JMenuItem loadFilteredMutationsMenuItem;

	private MutationGermlineListFrame mutationListPanel;

	private Sample sample;
	private MutationList mutationList;
	private MutationGermlineFilterPanel mutationFilterPanel;

	MutationListGermlineMenuBar(MutationGermlineListFrame mutationListPanel, Sample sample, MutationList mutationList, MutationGermlineFilterPanel mutationFilterPanel) {
		this.mutationListPanel = mutationListPanel;

		this.sample = sample;
		this.mutationList = mutationList;
		this.mutationFilterPanel = mutationFilterPanel;

		constructMenu();
		layoutComponents();
		activateMenuComponents();
	}

	private void constructMenu() {
		reportMenu = new JMenu("Report");
		reportMenu.setFont(GUICommonTools.TAHOMA_BOLD_17);
		shortReportMenuItem = new JMenuItem("Short Report");
		shortReportMenuItem.setToolTipText("Generate a short report for the mutations marked as reported");

		longReportMenuItem = new JMenuItem("Long Report");
		longReportMenuItem.setToolTipText("Generate a long report for the mutations marked as reported");

		exportMutationsMenuItem = new JMenuItem("Export mutations");
		exportMutationsMenuItem.setToolTipText("Export the mutations to a text file");

		filteredMutationsMenu = new JMenu("Filtered Mutations");
		filteredMutationsMenu.setFont(GUICommonTools.TAHOMA_BOLD_17);
		loadFilteredMutationsMenuItem = new JMenuItem("Load Filtered Mutations");
		loadFilteredMutationsMenuItem.setToolTipText("Load mutations from the database that did not meet initial filters");
	}

	private void layoutComponents() {
		add(reportMenu);
		reportMenu.add(shortReportMenuItem);
		reportMenu.add(longReportMenuItem);
		reportMenu.add(exportMutationsMenuItem);

		JMenu separator1 = new JMenu("|");
		separator1.setEnabled(false);
		add(separator1);

		add(filteredMutationsMenu);
		filteredMutationsMenu.add(loadFilteredMutationsMenuItem);
	}

	private void activateMenuComponents(){
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == shortReportMenuItem) {
					showShortReportFrame();
				} else if (e.getSource() == longReportMenuItem) {
					showLongReportFrame();
				} else if (e.getSource() == exportMutationsMenuItem) {
					try {
						exportTable();
					} catch (IOException ex) {
						HMVVDefectReportFrame.showHMVVDefectReportFrame(mutationListPanel, ex);
					}
				} else if(e.getSource() == loadFilteredMutationsMenuItem) {
					loadFilteredMutationsMenuItem.setEnabled(false);
					loadFilteredMutationsAsynchronous();
				}
			}
		};

		shortReportMenuItem.addActionListener(actionListener);
		longReportMenuItem.addActionListener(actionListener);
		exportMutationsMenuItem.addActionListener(actionListener);
		loadFilteredMutationsMenuItem.addActionListener(actionListener);
	}

	private void exportTable() throws IOException {
		JFileChooser saveAs = new JFileChooser();
		saveAs.setAcceptAllFileFilterUsed(false);
		saveAs.addChoosableFileFilter(new FileFilter() {
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
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			mutationListPanel.exportReport(saveAs.getSelectedFile());
		}
	}

	private void showShortReportFrame() {
		try {
			showReportFrameText(REPORT_TYPE.SHORT);
		} catch (Exception e) {
			HMVVDefectReportFrame.showHMVVDefectReportFrame(mutationListPanel, e);
		}
	}

	private void showLongReportFrame() {
		try {
			showReportFrameText(REPORT_TYPE.LONG);
		} catch (Exception e) {
			HMVVDefectReportFrame.showHMVVDefectReportFrame(mutationListPanel, e);
		}
	}
	
	private void showReportFrameText(REPORT_TYPE report_type) throws Exception{
		ReportFrame reportPanel = new ReportFrame(mutationListPanel, report_type, sample, mutationList);
		reportPanel.setVisible(true);
	}

	private void loadFilteredMutationsAsynchronous() {
		createLoadFilteredMutationDataThread();
	}

	private void createLoadFilteredMutationDataThread(){
		Thread loadFilteredMutationDataThread = new Thread(new Runnable(){
			@Override
			public void run() {
				mutationListPanel.disableInputForAsynchronousLoad();
				getFilteredMutationData();
				mutationListPanel.enableInputAfterAsynchronousLoad();
				filteredMutationsMenu.setEnabled(false);
			}
		});
		loadFilteredMutationDataThread.start();
	}

	private void getFilteredMutationData() {
		try{
			filteredMutationsMenu.setText("Loading...");
			ArrayList<MutationGermline> mutations = DatabaseCommands.getExtraGermlineMutationsBySample(sample);
			for(int i = 0; i < mutations.size(); i++) {
				if(mutationListPanel.isCallbackClosed()){
					return;
				}
				filteredMutationsMenu.setText("Loading " + (i+1) + " of " + mutations.size());

				try{
					MutationGermline mutation = mutations.get(i);
					AsynchronousMutationDataIO.getMutationDataGermline(mutation);
					//no need to call parent.mutationListIndexUpdated() here these mutations are not current displayed
					mutationList.addFilteredMutation(mutation);
				}catch(Exception e){
					HMVVDefectReportFrame.showHMVVDefectReportFrame(mutationListPanel, e, "Could not load additional mutation data.");
				}
			}
			mutationFilterPanel.applyRowFilters();
		}catch(Exception ex){
			HMVVDefectReportFrame.showHMVVDefectReportFrame(mutationListPanel, ex);
		}
		filteredMutationsMenu.setText("Filtered mutations loaded");
	}

	void disableInputForAsynchronousLoad() {
		shortReportMenuItem.setEnabled(false);
		longReportMenuItem.setEnabled(false);
		exportMutationsMenuItem.setEnabled(false);
		filteredMutationsMenu.setEnabled(false);
	}

	void enableInputAfterAsynchronousLoad() {
		shortReportMenuItem.setEnabled(true);
		longReportMenuItem.setEnabled(true);
		exportMutationsMenuItem.setEnabled(true);
		filteredMutationsMenu.setEnabled(true);
	}
}
