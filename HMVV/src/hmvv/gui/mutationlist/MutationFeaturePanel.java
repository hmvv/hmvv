package hmvv.gui.mutationlist;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.gui.sampleList.ReportFrame;
import hmvv.io.IGVConnection;
import hmvv.io.MutationReportGenerator;
import hmvv.io.SSHConnection;
import hmvv.model.Sample;

public class MutationFeaturePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JButton shortReportButton;
	private JButton longReportButton;
	private JButton exportButton;
	private LoadIGVButton loadIGVButton;

	private MutationListFrame parent;
	private Sample sample;
	private MutationList mutationList;

	MutationFeaturePanel(MutationListFrame parent, Sample sample, MutationList mutationList) {
		this.parent = parent;
		this.sample = sample;
		this.mutationList = mutationList;

		constructButtons();
		layoutComponents();
	}

	private void constructButtons() {
		shortReportButton = new JButton("Short Report");
		shortReportButton.setToolTipText("Generate a short report for the mutations marked as reported");
		shortReportButton.setFont(GUICommonTools.TAHOMA_BOLD_13);

		longReportButton = new JButton("Long Report");
		longReportButton.setToolTipText("Generate a long report for the mutations marked as reported");
		longReportButton.setFont(GUICommonTools.TAHOMA_BOLD_13);

		exportButton = new JButton("Export");
		exportButton.setToolTipText("Export the current table to file");
		exportButton.setFont(GUICommonTools.TAHOMA_BOLD_13);

		loadIGVButton = new LoadIGVButton();
		loadIGVButton.setToolTipText("Load the sample into IGV. IGV needs to be already opened");
		loadIGVButton.setFont(GUICommonTools.TAHOMA_BOLD_14);
		//loadIGVButton.setEnabled(false);

		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == shortReportButton) {
					showShortReportFrame();
				} else if (e.getSource() == longReportButton) {
					showLongReportFrame();
				} else if (e.getSource() == exportButton) {
					try {
						exportTable();
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(parent, ex.getMessage());
					}
				} else if (e.getSource() == loadIGVButton) {
					new Thread(new Runnable() {
						public void run() {
							try {
								loadIGVButton.setEnabled(false);
								handleIGVButtonClickAsynchronous();
							} catch (Exception ex) {
								JOptionPane.showMessageDialog(parent, ex.getMessage());
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
		exportButton.addActionListener(actionListener);
		loadIGVButton.addActionListener(actionListener);
	}

	private void layoutComponents() {
		Dimension buttonSize = new Dimension(125, 30);
		shortReportButton.setPreferredSize(buttonSize);

		longReportButton.setPreferredSize(buttonSize);
		exportButton.setPreferredSize(buttonSize);

		FlowLayout flowLayout = new FlowLayout(FlowLayout.LEADING);
		setLayout(flowLayout);


		JPanel gridPanel = new JPanel();
		GridLayout buttonPanelGridLayout = new GridLayout(0, 1);
		buttonPanelGridLayout.setVgap(10);
		gridPanel.setLayout(buttonPanelGridLayout);
		gridPanel.add(shortReportButton);
		gridPanel.add(longReportButton);
		gridPanel.add(exportButton);
		gridPanel.add(loadIGVButton);
		add(gridPanel);
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
			parent.exportReport(saveAs.getSelectedFile());
		}
	}

	private void showShortReportFrame() {
		try {
			String report = MutationReportGenerator.generateShortReport(mutationList);
			showReportFrame(report);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	private void showLongReportFrame() {
		try {
			String report = MutationReportGenerator.generateLongReport(mutationList);
			showReportFrame(report);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	private void showReportFrame(String report) {
		ReportFrame reportPanel = new ReportFrame(parent, report);
		reportPanel.setVisible(true);
	}

	private void handleIGVButtonClickAsynchronous() throws Exception {

		String msg = "You have selected "+ mutationList.getSelectedMutationCount() + " mutations to load on IGV.";
		int request = JOptionPane.showConfirmDialog(parent, msg, "Load IGV Confirmation", JOptionPane.YES_NO_OPTION);

		if (request == JOptionPane.YES_OPTION) {
			if (mutationList.getSelectedMutationCount() == 0 ){
				JOptionPane.showMessageDialog(null, "IGV cannot be loaded for 0 mutations.");
			}else if(mutationList.getMutationCount() == mutationList.getSelectedMutationCount()){
				loadIGVAsynchronous();
			}else{
				loadIGVAsynchronous_Filtered();
			}
		}
	}

	//TODO disable HTTP access to BAM files
	private void loadIGVAsynchronous() throws Exception {

		loadIGVButton.setText("Finding BAM File...");
		File bamFile = SSHConnection.loadBAMForIGV(sample, loadIGVButton);

		loadIGVButton.setText("Loading File Into IGV...");
		String response = IGVConnection.loadFileIntoIGV(this, bamFile);

		if (response.equals("OK")) {
			JOptionPane.showMessageDialog(this, "BAM file successfully loaded into IGV");
		} else if (!response.equals("")) {
			JOptionPane.showMessageDialog(this, response);
		}
	}


	private void loadIGVAsynchronous_Filtered() throws Exception {

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		ServerTask serverTask = new ServerTask(0);
		loadIGVButtonTimerLabel(serverTask);
		String bamServerFileName = SSHConnection.createTempParametersFile(sample, mutationList, loadIGVButton, serverTask);
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		loadIGVButton.setText("Finding BAM File...");
		File bamLocalFile = SSHConnection.copyTempBamFileONLocal(sample, loadIGVButton, bamServerFileName);

		loadIGVButton.setText("Loading File Into IGV...");
		String response = IGVConnection.loadFileIntoIGV(this, bamLocalFile);

		if (response.equals("OK")) {
			JOptionPane.showMessageDialog(this, "BAM file successfully loaded into IGV");
		} else if (!response.equals("")) {
			JOptionPane.showMessageDialog(this, response);
		}
	}

	private void loadIGVButtonTimerLabel(ServerTask task) {
		int seconds = 3 * mutationList.getSelectedMutationCount();
		final long duration = seconds * 1000;   // calculate to milliseconds
		final Timer timer = new Timer(1, new ActionListener() {
			long startTime = -1;

			@Override
			public void actionPerformed(ActionEvent event) {
				if (startTime < 0) {
					startTime = System.currentTimeMillis();
				}
				long now = System.currentTimeMillis();
				long clockTime = now - startTime;
				if (task.getStatus() == 1) {
					((Timer) event.getSource()).stop();
					return;
				}
				SimpleDateFormat df = new SimpleDateFormat("mm:ss");
				loadIGVButton.setText(String.format("Processing...%s", df.format(duration - clockTime)));
			}
		});
		timer.start();
	}


	void disableInputForAsynchronousLoad() {
		String tooltip = "Disabled while data is loading";
		shortReportButton.setToolTipText(tooltip);
		shortReportButton.setEnabled(false);
		longReportButton.setToolTipText(tooltip);
		longReportButton.setEnabled(false);
		exportButton.setToolTipText(tooltip);
		exportButton.setEnabled(false);
		loadIGVButton.setToolTipText(tooltip);
		loadIGVButton.setEnabled(false);
	}

	void enableInputAfterAsynchronousLoad() {
		String tooltip = "";
		shortReportButton.setToolTipText(tooltip);
		shortReportButton.setEnabled(true);
		longReportButton.setToolTipText(tooltip);
		longReportButton.setEnabled(true);
		exportButton.setToolTipText(tooltip);
		exportButton.setEnabled(true);
		loadIGVButton.setToolTipText(tooltip);
		loadIGVButton.setEnabled(true);
	}

	public class ServerTask {

		int status;

		public ServerTask(int s) {
			this.status = s;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int s) {
			this.status = s;
		}
	}
}
