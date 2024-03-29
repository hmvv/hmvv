package hmvv.gui.mutationlist;

import hmvv.gui.GUICommonTools;
import hmvv.gui.LoadFileButton;
import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.io.IGVConnection;
import hmvv.io.SSHConnection;
import hmvv.main.Configurations;
import hmvv.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;

public class MutationGermlineFilterPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JCheckBox reportedOnlyCheckbox;
	private JCheckBox transcriptFlagCheckbox;
	private JCheckBox synonymousFlagCheckbox;
	private JCheckBox selectAllCheckbox;

	private JTextField textFreqFrom;
	private JTextField textVarFreqTo;
	private JTextField minReadDepthTextField;
	private JTextField maxGnomadGlobalFreqTextField;
	private JComboBox<VariantPredictionClass> predictionFilterComboBox;

	private Sample sample;
    private MutationGermlineListFrame parent;
	private MutationList mutationList;
	private MutationListFilters mutationListFilters;

    private JButton resetButton;
    private LoadFileButton loadIGVButton;

	MutationGermlineFilterPanel(MutationGermlineListFrame parent, Sample sample, MutationList mutationList, MutationListFilters mutationListFilters){
		this.parent = parent;
	    this.sample = sample;
		this.mutationList = mutationList;
		this.mutationListFilters = mutationListFilters;
        constructComponents();
		layoutComponents();
	}

	private void constructComponents(){
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyRowFilters();
			}
		};

		reportedOnlyCheckbox = new JCheckBox("Show Reported Mutations Only");
		reportedOnlyCheckbox.addActionListener(actionListener);
		reportedOnlyCheckbox.setFont(GUICommonTools.TAHOMA_BOLD_14);

		transcriptFlagCheckbox = new JCheckBox("Show Flagged Transcripts Only");
		transcriptFlagCheckbox.addActionListener(actionListener);
		transcriptFlagCheckbox.setFont(GUICommonTools.TAHOMA_BOLD_14);
		transcriptFlagCheckbox.setHorizontalTextPosition(SwingConstants.LEFT);

		synonymousFlagCheckbox = new JCheckBox("Show Nonsynonymous Variants Only");
		synonymousFlagCheckbox.addActionListener(actionListener);
		synonymousFlagCheckbox.setFont(GUICommonTools.TAHOMA_BOLD_14);
		synonymousFlagCheckbox.setHorizontalTextPosition(SwingConstants.LEFT);

		selectAllCheckbox = new JCheckBox("Select all mutations for IGV");
		selectAllCheckbox.addActionListener(actionListener);
		selectAllCheckbox.setFont(GUICommonTools.TAHOMA_BOLD_14);

		selectAllCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleSelectAllClick(selectAllCheckbox.isSelected());
			}
		});
		
		loadIGVButton = new LoadFileButton("Load IGV");
		loadIGVButton.setToolTipText("Load the sample into IGV. IGV needs to be already opened");
		loadIGVButton.setFont(GUICommonTools.TAHOMA_BOLD_13);
		loadIGVButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
        });
		
		resetButton = new JButton("Reset all Filters");
        resetButton.setToolTipText("Reset filters to defaults");
        resetButton.setFont(GUICommonTools.TAHOMA_BOLD_13);
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetFilters();
            }
        });

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

		maxGnomadGlobalFreqTextField = new JTextField();
		maxGnomadGlobalFreqTextField.getDocument().addDocumentListener(documentListener);
		maxGnomadGlobalFreqTextField.setColumns(textFieldColumnWidth);


		predictionFilterComboBox = new JComboBox<VariantPredictionClass>(VariantPredictionClass.getAllClassifications());
		predictionFilterComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyRowFilters();
			}
		});
		
		//construct filter objects
		mutationListFilters.addMaxGnomadFrequencyGermlineFilter(maxGnomadGlobalFreqTextField);
		mutationListFilters.addMinReadDepthGermlineFilter(minReadDepthTextField);
		mutationListFilters.addReportedOnlyFilter(reportedOnlyCheckbox);
		mutationListFilters.addTranscriptFlagGermlineFilter(transcriptFlagCheckbox);
		mutationListFilters.addSynonymousFlagGermlineFilter(synonymousFlagCheckbox);
		mutationListFilters.addVariantAlleleFrequencyGermlineFilter(textFreqFrom, textVarFreqTo);
		mutationListFilters.addVariantPredicationClassFilter(predictionFilterComboBox);
	}

	private void layoutComponents() {

		JPanel leftFilterPanel = new JPanel();
		JPanel checkboxPanel = new JPanel(new GridLayout(0,1));
		JPanel resetButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		resetButtonPanel.add(resetButton);
		
        checkboxPanel.add(resetButtonPanel);
		checkboxPanel.add(reportedOnlyCheckbox);
		checkboxPanel.add(selectAllCheckbox);
		
		JPanel igvButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		igvButtonPanel.add(loadIGVButton);
		checkboxPanel.add(igvButtonPanel);
		
		leftFilterPanel.add(checkboxPanel);

        JPanel vepPanel = new JPanel(new GridLayout(0,1));
        vepPanel.add(new JLabel("Annotation Filters:"));

        JPanel readDepthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel minReadDepthLabel = new JLabel("Read Depth Min-");
        minReadDepthLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
        readDepthPanel.add(minReadDepthLabel);
        readDepthPanel.add(minReadDepthTextField);
        vepPanel.add(readDepthPanel);

        JLabel variantFrequencyLabel = new JLabel("Variant Allele Frequency Range-");
        variantFrequencyLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
        JPanel frequencyLayoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        frequencyLayoutPanel.add(variantFrequencyLabel);
        frequencyLayoutPanel.add(textFreqFrom);
        frequencyLayoutPanel.add(new JLabel("To"));
        frequencyLayoutPanel.add(textVarFreqTo);
        vepPanel.add(frequencyLayoutPanel);

        JPanel predictionFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel predictionFilterLabel = new JLabel("Prediction Class Include-");
        predictionFilterLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
        predictionFilterPanel.add(predictionFilterLabel);
        JPanel predictionFilterComponentsPanel =  new JPanel(new FlowLayout(FlowLayout.CENTER));
        predictionFilterComponentsPanel.add(predictionFilterComboBox);
        predictionFilterPanel.add(predictionFilterComponentsPanel);
        vepPanel.add(predictionFilterPanel);

		JPanel gnomadGlobalFreqPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel gnomadGlobalFreqLabel = new JLabel("Gnomad Global Frequency Max-");
		gnomadGlobalFreqLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		gnomadGlobalFreqPanel.add(gnomadGlobalFreqLabel);
		gnomadGlobalFreqPanel.add(maxGnomadGlobalFreqTextField);

        JPanel rightFilterPanel = new JPanel();
		rightFilterPanel.add(vepPanel);

		JPanel lastrightFilterPanel = new JPanel();

		JPanel extraFilterPanel = new JPanel(new GridLayout(0,1));
		extraFilterPanel.add(gnomadGlobalFreqPanel);
		extraFilterPanel.add(transcriptFlagCheckbox);
		extraFilterPanel.add(synonymousFlagCheckbox);
		lastrightFilterPanel.add(extraFilterPanel);

        leftFilterPanel.setBorder(new EmptyBorder(1, 5, 1, 5));
		add(leftFilterPanel);
        rightFilterPanel.setBorder(new EmptyBorder(1, 5, 1, 5));
		add(rightFilterPanel);
		lastrightFilterPanel.setBorder(new EmptyBorder(1, 5, 1, 5));
		add(lastrightFilterPanel);

        setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}

	void resetFilters(){
		reportedOnlyCheckbox.setSelected(false);
		transcriptFlagCheckbox.setSelected(false);
		synonymousFlagCheckbox.setSelected(false);
		selectAllCheckbox.setSelected(false);
		textFreqFrom.setText(Configurations.GERMLINE_ALLELE_FREQ_FILTER+"");
		textVarFreqTo.setText(Configurations.MAX_ALLELE_FREQ_FILTER+"");
		minReadDepthTextField.setText(Configurations.GERMLINE_READ_DEPTH_FILTER+"");
		maxGnomadGlobalFreqTextField.setText(Configurations.GERMLINE_GNOMAD_MAX_GLOBAL_ALLELE_FREQ_FILTER+"");
		predictionFilterComboBox.setSelectedIndex(4);
		applyRowFilters();
		handleSelectAllClick(false);
	}

	void disableInputForAsynchronousLoad() {
		reportedOnlyCheckbox.setEnabled(false);
		transcriptFlagCheckbox.setEnabled(false);
		synonymousFlagCheckbox.setEnabled(false);
		loadIGVButton.setEnabled(false);
		selectAllCheckbox.setEnabled(false);
		textFreqFrom.setEditable(false);
		textVarFreqTo.setEditable(false);
		minReadDepthTextField.setEditable(false);
		maxGnomadGlobalFreqTextField.setEditable(false);
		predictionFilterComboBox.setEnabled(false);
        resetButton.setEnabled(false);
	}

	void enableInputAfterAsynchronousLoad() {
		reportedOnlyCheckbox.setEnabled(true);
		transcriptFlagCheckbox.setEnabled(true);
		synonymousFlagCheckbox.setEnabled(true);
		loadIGVButton.setEnabled(true);
		selectAllCheckbox.setEnabled(true);
		textFreqFrom.setEditable(true);
		textVarFreqTo.setEditable(true);
		minReadDepthTextField.setEditable(true);
		maxGnomadGlobalFreqTextField.setEditable(true);
		predictionFilterComboBox.setEnabled(true);
        resetButton.setEnabled(true);
	}
	
	void applyRowFilters(){
		mutationList.filterMutations(mutationListFilters);
	}    

    private void handleSelectAllClick(boolean choice) {
		for (int i = 0; i < mutationList.getMutationCount(); i++) {
			MutationCommon mutation = mutationList.getMutation(i);
			if (choice){mutation.setSelected(true);}
			else {mutation.setSelected(false);}
		}
	}
    
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
    
    private void handleIGVButtonClickAsynchronous() throws Exception {
		if (mutationList.getSelectedMutationCount() == 0 ){
			String msg = "You have not selected any mutations. Would you like to load the entire BAM file?";
			int request = JOptionPane.showConfirmDialog(parent, msg, "Load the entire BAM file Confirmation", JOptionPane.YES_NO_OPTION);
			if (request == JOptionPane.YES_OPTION) {
				loadIGVAsynchronous();
			}
			return;
		}
		
		String msg = "You have selected " + mutationList.getSelectedMutationCount() + " mutation(s) to load into IGV. Would you like to load the BAM file for these coordinates?";
		int request = JOptionPane.showConfirmDialog(parent, msg, "Load IGV Confirmation", JOptionPane.YES_NO_OPTION);
		
		if (request == JOptionPane.YES_OPTION) {
			loadIGVAsynchronous_Filtered();
		}
	}
    
    private void loadIGVAsynchronous_Filtered() throws Exception {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		ServerWorker serverWorker = new ServerWorker(0);
		loadIGVButtonTimerLabel(serverWorker);
		String bamServerFileName = SSHConnection.createTempParametersFile(sample, mutationList, loadIGVButton, serverWorker);
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
    
    private void loadIGVButtonTimerLabel(ServerWorker task) {
		int seconds = 3 * mutationList.getSelectedMutationCount();
		final long duration = seconds * 1000;   // calculate to milliseconds
		final Timer timer = new Timer(1, new ActionListener() {
			long startTime = -1;
			private SimpleDateFormat minuteSecondDateFormat = new SimpleDateFormat("mm:ss");
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
				long interval = duration - clockTime;
				if(interval < 0) {
					interval = 1000;
				}
				loadIGVButton.setText(String.format("Processing...%s", minuteSecondDateFormat.format(interval)));
			}
		});
		timer.start();
	}

}
