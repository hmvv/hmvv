package hmvv.gui.mutationlist;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.gui.mutationlist.tablemodels.MutationListGermline;
import hmvv.gui.sampleList.ReportFrame;
import hmvv.gui.sampleList.ReportFrameText;
import hmvv.io.AsynchronousMutationDataIO;
import hmvv.io.DatabaseCommands;
import hmvv.io.MutationReportGenerator;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.model.GermlineMutation;
import hmvv.model.Mutation;
import hmvv.model.Sample;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MutationListMenuBarGermline extends JMenuBar {

	private static final long serialVersionUID = 1L;

	private JMenu reportMenu;
	private JMenuItem shortReportMenuItem;
	private JMenuItem longReportMenuItem;
	private JMenuItem exportMutationsMenuItem;
	private JMenu filteredMutationsMenu;
	private JMenuItem loadFilteredMutationsMenuItem;

	private MutationListFrameGermline parent;
	private MutationListFrameGermline mutationListPanel;

	private Sample sample;
	private MutationListGermline mutationList;
	private MutationFilterPanelGermline mutationFilterPanel;

	MutationListMenuBarGermline(MutationListFrameGermline parent, MutationListFrameGermline mutationListPanel, Sample sample, MutationListGermline mutationList, MutationFilterPanelGermline mutationFilterPanel) {
		this.parent = parent;
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
//					showShortReportFrame();
				} else if (e.getSource() == longReportMenuItem) {
//					showLongReportFrame();
				} else if (e.getSource() == exportMutationsMenuItem) {
					try {
						exportTable();
					} catch (IOException ex) {
						HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, ex);
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
//			mutationListPanel.exportReport(saveAs.getSelectedFile());
		}
	}
	
//	private void showShortReportFrame() {
//		try {
//			String report = MutationReportGenerator.generateShortReport(mutationList);
//			showReportFrameText("Short Variant Report", report);
//		} catch (Exception e) {
//			HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, e);
//		}
//	}

//	private void showLongReportFrame() {
//		try {
//			String report = MutationReportGenerator.generateLongReport(mutationList);
//			showReportFrameText("Long Variant Report", report);
//		} catch (Exception e) {
//			HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, e);
//		}
//	}
//
	private void showReportFrameText(String title, String report) {
		ReportFrame reportPanel = new ReportFrameText(parent, title, report);
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
            ArrayList<GermlineMutation> mutations = DatabaseCommands.getExtraGermlineMutationsBySample(sample);
            for(int i = 0; i < mutations.size(); i++) {
                if(mutationListPanel.isCallbackClosed()){
                    return;
                }
                filteredMutationsMenu.setText("Loading " + (i+1) + " of " + mutations.size());

                try{
					GermlineMutation mutation = mutations.get(i);
                    AsynchronousMutationDataIO.getMutationDataGermline(mutation);
                    //no need to call parent.mutationListIndexUpdated() here these mutations are not current displayed
                    mutationList.addFilteredMutation(mutation);
                }catch(Exception e){
                	HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, e, "Could not load additional mutation data.");
                }
            }
            mutationFilterPanel.applyRowFilters();
        }catch(Exception ex){
        	HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, ex);
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
