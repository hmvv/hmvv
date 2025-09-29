package hmvv.gui.mutationlist;

import hmvv.gui.GUICommonTools;
import hmvv.gui.LoadFileButton;
import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.gui.sampleList.ViewAmpliconFrame;
import hmvv.io.IGVConnection;
import hmvv.io.SSHConnection;
import hmvv.main.Configurations;
import hmvv.model.MutationSomatic;
import hmvv.model.Sample;
import hmvv.model.VariantPredictionClass;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MutationFilterPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JCheckBox reportedOnlyCheckbox;
	private JCheckBox cosmicOnlyCheckbox;
	private JCheckBox selectAllCheckbox;

	private JTextField textFreqFrom;
	private JTextField textVarFreqTo;
	private JTextField minReadDepthTextField;
	private JTextField occurenceFromTextField;
	private JTextField maxPopulationFrequencyG1000TextField;
	private JTextField maxPopulationFrequencyGnomadTextField;
	private JTextField variantCallerTextField;
	private JComboBox<VariantPredictionClass> predictionFilterComboBox;
	private JTextField searchTextgeneField;
	private JTextField searchTextVariantField;

	private JButton variantCallerButton1;
	private JButton variantCallerButton2;

	private Sample sample;
    private MutationListFrame parent;
	private MutationList mutationList;
	private MutationListFilters mutationListFilters;

    private JButton resetButton;
    private LoadFileButton loadIGVButton;
	private JButton editSampleButton;
	private JButton assayQCButton;

	private LoadFileButton loadSVIGVButton;


	MutationFilterPanel(MutationListFrame parent,Sample sample, MutationList mutationList, MutationListFilters mutationListFilters){
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

		cosmicOnlyCheckbox = new JCheckBox("Show Mutations with CosmicIDs Only");
		cosmicOnlyCheckbox.addActionListener(actionListener);
		cosmicOnlyCheckbox.setFont(GUICommonTools.TAHOMA_BOLD_14);

		reportedOnlyCheckbox = new JCheckBox("Show Reported Mutations Only");
		reportedOnlyCheckbox.addActionListener(actionListener);
		reportedOnlyCheckbox.setFont(GUICommonTools.TAHOMA_BOLD_14);

		selectAllCheckbox = new JCheckBox("Select all mutations for IGV");
		selectAllCheckbox.addActionListener(actionListener);
		selectAllCheckbox.setFont(GUICommonTools.TAHOMA_BOLD_14);

		selectAllCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleSelectAllClick(selectAllCheckbox.isSelected());
			}
		});

		editSampleButton = new JButton("Edit Sample");
		editSampleButton.setToolTipText("Edit the current sample");
		editSampleButton.setFont(GUICommonTools.TAHOMA_BOLD_13);
		editSampleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	new Thread(new Runnable() {
					public void run() {
						try {
							editSampleButton.setEnabled(false);
							parent.showEditSampleFrame();
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(parent, ex.getMessage());
						}
						editSampleButton.setEnabled(true);
					}
				}).start();
            }
        });

		assayQCButton = new JButton("Amplicon");
		assayQCButton.setToolTipText("Edit the current sample");
		assayQCButton.setFont(GUICommonTools.TAHOMA_BOLD_13);
		assayQCButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	new Thread(new Runnable() {
					public void run() {
						try {
							ViewAmpliconFrame amplicon = new ViewAmpliconFrame(parent, sample);
							amplicon.setVisible(true);
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(parent, ex.getMessage());
						}
						editSampleButton.setEnabled(true);
					}
				}).start();
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
							handleIGVButtonClickAsynchronous("No");
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(parent, ex.getMessage());
						}
						loadIGVButton.setEnabled(true);
						loadIGVButton.resetText();
					}
				}).start();
            }
        });


		// NEW: Load SV IGV button 
        loadSVIGVButton = new LoadFileButton("Load SV IGV");
        loadSVIGVButton.setToolTipText("Load the Structural Variant BAM into IGV. IGV should be already opened");
		loadSVIGVButton.setFont(GUICommonTools.TAHOMA_BOLD_13);
        loadSVIGVButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	new Thread(new Runnable() {
					public void run() {
						try {
							loadSVIGVButton.setEnabled(false);
							handleIGVButtonClickAsynchronous("Yes");
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(parent, ex.getMessage());
						}
						loadSVIGVButton.setEnabled(true);
						loadSVIGVButton.resetText();
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


		variantCallerButton1 = new JButton("3");
        variantCallerButton1.setToolTipText("Choose only 3 Variant calls");
        variantCallerButton1.setFont(GUICommonTools.TAHOMA_BOLD_13);
		variantCallerButton2 = new JButton("2+");
        variantCallerButton2.setToolTipText("Choose only 2 Variant calls");
        variantCallerButton2.setFont(GUICommonTools.TAHOMA_BOLD_13);
		variantCallerButton1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				variantCallerTextField.setText(Configurations.MAX_VARIANT_CALLERS_COUNT);
				variantCallerButton2.setEnabled(true);
				variantCallerButton1.setEnabled(false);
				
			}
		});

		variantCallerButton2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				variantCallerTextField.setText(Configurations.MIN_VARIANT_CALLERS_COUNT);
				variantCallerButton1.setEnabled(true);
				variantCallerButton2.setEnabled(false);
				
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

		occurenceFromTextField = new JTextField();
		occurenceFromTextField.getDocument().addDocumentListener(documentListener);
		occurenceFromTextField.setColumns(textFieldColumnWidth);

		maxPopulationFrequencyG1000TextField = new JTextField();
		maxPopulationFrequencyG1000TextField.getDocument().addDocumentListener(documentListener);
		maxPopulationFrequencyG1000TextField.setColumns(textFieldColumnWidth);

		searchTextgeneField = new JTextField();
		searchTextgeneField.getDocument().addDocumentListener(documentListener);
		searchTextgeneField.setColumns(textFieldColumnWidth*4);

		searchTextVariantField = new JTextField();
		searchTextVariantField.getDocument().addDocumentListener(documentListener);
		searchTextVariantField.setColumns(textFieldColumnWidth*4);
		

		maxPopulationFrequencyGnomadTextField = new JTextField();
		maxPopulationFrequencyGnomadTextField.getDocument().addDocumentListener(documentListener);
		maxPopulationFrequencyGnomadTextField.setColumns(textFieldColumnWidth);

		variantCallerTextField = new JTextField();
		variantCallerTextField.getDocument().addDocumentListener(documentListener);
		variantCallerTextField.setColumns(textFieldColumnWidth);

		predictionFilterComboBox = new JComboBox<VariantPredictionClass>(VariantPredictionClass.getAllClassifications());
		predictionFilterComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyRowFilters();
			}
		});
		
		//construct filter objects
		mutationListFilters.addCosmicIDFilter(cosmicOnlyCheckbox);
		mutationListFilters.addMaxG1000FrequencyFilter(maxPopulationFrequencyG1000TextField);
		mutationListFilters.addSearchGeneFilter(searchTextgeneField);
		mutationListFilters.addSearchVariantFilter(searchTextVariantField);
		mutationListFilters.addMaxGnomadFrequencyFilter(maxPopulationFrequencyGnomadTextField, Configurations.getDefaultAlleleFrequencyFilter(sample));
		mutationListFilters.addMaxOccurrenceFilter(occurenceFromTextField);
		mutationListFilters.addMinReadDepthFilter(minReadDepthTextField, Configurations.getDefaultReadDepthFilter(sample));
		mutationListFilters.addReportedOnlyFilter(reportedOnlyCheckbox);
		mutationListFilters.addVariantAlleleFrequencyFilter(sample, textFreqFrom, textVarFreqTo);
		mutationListFilters.addVariantPredicationClassFilter(predictionFilterComboBox);

		if (sample.assay.assayName.equals("heme") && sample.instrument.instrumentName.equals("nextseq")){
		mutationListFilters.addvariantCallerFilter(variantCallerTextField);
		}
	}

	private void layoutComponents() {

		JPanel leftFilterPanel = new JPanel();
        //leftFilterPanel.setPreferredSize(new Dimension(50, 100));
		JPanel checkboxPanel = new JPanel(new GridLayout(0,1));
		JPanel resetButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		resetButtonPanel.add(resetButton);


		
        checkboxPanel.add(resetButtonPanel);
		checkboxPanel.add(cosmicOnlyCheckbox);
		checkboxPanel.add(reportedOnlyCheckbox);
		checkboxPanel.add(selectAllCheckbox);
		
		JPanel igvButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		igvButtonPanel.add(loadIGVButton);
		igvButtonPanel.add(loadSVIGVButton);
		igvButtonPanel.add(editSampleButton);
		igvButtonPanel.add(assayQCButton);
		checkboxPanel.add(igvButtonPanel);
		
		leftFilterPanel.add(checkboxPanel);

		JPanel middleFilterPanel = new JPanel(new GridLayout(0,1));

		//variant frequency
        JPanel g1000Panel = new JPanel(new GridLayout(0,1));
        g1000Panel.add(new JLabel("G1000:"));

        JPanel populationFrequencyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel populationFrequencyLabel = new JLabel("Global Allele Frequency Max-");
		populationFrequencyLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		populationFrequencyPanel.add(populationFrequencyLabel);
		populationFrequencyPanel.add(maxPopulationFrequencyG1000TextField);
        g1000Panel.add(populationFrequencyPanel);


        JPanel gnomadPanel = new JPanel(new GridLayout(0,1));
        gnomadPanel.add(new JLabel("Gnomad:"));

		JPanel gnomadPopulationFrequencyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel gnomadPopulationFrequencyLabel = new JLabel("Global Allele Frequency Max-");
		gnomadPopulationFrequencyLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		gnomadPopulationFrequencyPanel.add(gnomadPopulationFrequencyLabel);
		gnomadPopulationFrequencyPanel.add(maxPopulationFrequencyGnomadTextField);
        gnomadPanel.add(gnomadPopulationFrequencyPanel);

        JPanel hmvvPanel = new JPanel(new GridLayout(0,1));
        hmvvPanel.add(new JLabel("HMVV:"));
        JPanel occurencePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel occurenceLabel = new JLabel("Occurence Max-");
        occurenceLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
        occurencePanel.add(occurenceLabel);
        occurencePanel.add(occurenceFromTextField);
        hmvvPanel.add(occurencePanel);

        middleFilterPanel.add(g1000Panel);
        middleFilterPanel.add(gnomadPanel);
        middleFilterPanel.add(hmvvPanel);


        JPanel vepPanel = new JPanel(new GridLayout(0,1));
        vepPanel.add(new JLabel("VEP:"));

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


		JLabel variantCallerLabel = new JLabel("Variant Callers: ");
        variantCallerLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		JPanel variantCallerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        variantCallerPanel.add(variantCallerLabel);
		variantCallerPanel.add(variantCallerButton1);
		variantCallerPanel.add(variantCallerButton2);

		if (sample.assay.assayName.equals("heme") && sample.instrument.instrumentName.equals("nextseq")){
			vepPanel.add(variantCallerPanel);
			}
        

        JPanel rightFilterPanel = new JPanel();
		rightFilterPanel.add(vepPanel);


		JPanel SearchPanel = new JPanel(new GridLayout(0,1));
        SearchPanel.add(new JLabel("Search:"));

        JPanel searchPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel searchLabel1 = new JLabel("Search by Gene:     "); //Spaces are present for alignment
		searchLabel1.setFont(GUICommonTools.TAHOMA_BOLD_14);
		searchPanel1.add(searchLabel1);
		searchPanel1.add(searchTextgeneField);

		JPanel searchPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel searchLabel2 = new JLabel("Search by Variant: ");
		searchLabel2.setFont(GUICommonTools.TAHOMA_BOLD_14);
		searchPanel2.add(searchLabel2);
		searchPanel2.add(searchTextVariantField);

		SearchPanel.add(searchPanel1);
		SearchPanel.add(searchPanel2);
		SearchPanel.add(new JPanel(new FlowLayout(FlowLayout.LEFT)));
		SearchPanel.add(new JPanel(new FlowLayout(FlowLayout.LEFT)));
		SearchPanel.add(new JPanel(new FlowLayout(FlowLayout.LEFT)));

		
        leftFilterPanel.setBorder(new EmptyBorder(1, 5, 1, 5));
		add(leftFilterPanel);
        middleFilterPanel.setBorder(new EmptyBorder(1, 5, 1, 5));
        add(middleFilterPanel);
        rightFilterPanel.setBorder(new EmptyBorder(1, 5, 1, 5));
		add(rightFilterPanel);
		SearchPanel.setBorder(new EmptyBorder(1, 5, 1, 5));
		add(SearchPanel);

        setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}

	void resetFilters(){
		cosmicOnlyCheckbox.setSelected(false);
		reportedOnlyCheckbox.setSelected(false);
		selectAllCheckbox.setSelected(false);
		textFreqFrom.setText(Configurations.getDefaultAlleleFrequencyFilter(sample)+"");
		textVarFreqTo.setText(Configurations.MAX_ALLELE_FREQ_FILTER+"");
		minReadDepthTextField.setText(Configurations.getDefaultReadDepthFilter(sample)+"");
		occurenceFromTextField.setText(Configurations.MAX_OCCURENCE_FILTER+"");
		maxPopulationFrequencyG1000TextField.setText(Configurations.MAX_GLOBAL_ALLELE_FREQ_FILTER+"");
		maxPopulationFrequencyGnomadTextField.setText(Configurations.getDefaultGNOMADFrequencyFilter(sample)+"");
		predictionFilterComboBox.setSelectedIndex(1);
		variantCallerTextField.setText(Configurations.MIN_VARIANT_CALLERS_COUNT);
		searchTextgeneField.setText("");
		searchTextVariantField.setText("");
		applyRowFilters();
		handleSelectAllClick(false);
	}

	void disableInputForAsynchronousLoad() {
		cosmicOnlyCheckbox.setEnabled(false);
		reportedOnlyCheckbox.setEnabled(false);
		loadIGVButton.setEnabled(false);
		editSampleButton.setEnabled(false);
		assayQCButton.setEnabled(false);
		selectAllCheckbox.setEnabled(false);
		textFreqFrom.setEditable(false);
		textVarFreqTo.setEditable(false);
		minReadDepthTextField.setEditable(false);
		occurenceFromTextField.setEditable(false);
		maxPopulationFrequencyG1000TextField.setEditable(false);
		maxPopulationFrequencyGnomadTextField.setEditable(false);
		predictionFilterComboBox.setEnabled(false);
		variantCallerTextField.setEditable(false);
        resetButton.setEnabled(false);
		variantCallerButton1.setEnabled(false);
		variantCallerButton2.setEnabled(false);
		searchTextgeneField.setEditable(false);
		searchTextVariantField.setEditable(false);
	}

	void enableInputAfterAsynchronousLoad() {
		cosmicOnlyCheckbox.setEnabled(true);
		reportedOnlyCheckbox.setEnabled(true);
		loadIGVButton.setEnabled(true);
		selectAllCheckbox.setEnabled(true);
		textFreqFrom.setEditable(true);
		textVarFreqTo.setEditable(true);
		minReadDepthTextField.setEditable(true);
		occurenceFromTextField.setEditable(true);
		maxPopulationFrequencyG1000TextField.setEditable(true);
		maxPopulationFrequencyGnomadTextField.setEditable(true);
		predictionFilterComboBox.setEnabled(true);
		variantCallerTextField.setEditable(true);
		variantCallerButton1.setEnabled(true);
        resetButton.setEnabled(true);
		editSampleButton.setEnabled(true);
		assayQCButton.setEnabled(true);
		searchTextgeneField.setEditable(true);
		searchTextVariantField.setEditable(true);
	}
	
	void applyRowFilters(){
		mutationList.filterMutations(mutationListFilters);
	}    

    private void handleSelectAllClick(boolean choice) {
		for (int i = 0; i < mutationList.getMutationCount(); i++) {
			MutationSomatic mutation = (MutationSomatic)mutationList.getMutation(i);
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

    private void handleIGVButtonClickAsynchronous(String svFlag) throws Exception {
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
			loadIGVAsynchronous_Filtered(svFlag);
		}
	}
    
    private void loadIGVAsynchronous_Filtered(String svFlag) throws Exception {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		ServerWorker serverWorker = new ServerWorker(0);
		loadIGVButtonTimerLabel(serverWorker, svFlag);
		String bamServerFileName = SSHConnection.createTempParametersFile(sample, mutationList, loadIGVButton, serverWorker, svFlag);
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        
		if (svFlag.equals("Yes")) {
			loadSVIGVButton.setText("Finding BAM File...");
		} else {
			loadIGVButton.setText("Finding BAM File...");
		}
		
        if (svFlag.equals("Yes")){
			File bamLocalFile = SSHConnection.copyTempBamFileONLocal(sample, loadSVIGVButton, bamServerFileName);
			loadSVIGVButton.setText("Loading File Into IGV...");
			String response = IGVConnection.loadFileIntoIGV(this, bamLocalFile);
			if (response.equals("OK")) {
				JOptionPane.showMessageDialog(this, "BAM file successfully loaded into IGV");
			} else if (!response.equals("")) {
				JOptionPane.showMessageDialog(this, response);
			}
		} else {
			File bamLocalFile = SSHConnection.copyTempBamFileONLocal(sample, loadIGVButton, bamServerFileName);
			loadIGVButton.setText("Loading File Into IGV...");
			String response = IGVConnection.loadFileIntoIGV(this, bamLocalFile);
			if (response.equals("OK")) {
				JOptionPane.showMessageDialog(this, "BAM file successfully loaded into IGV");
			} else if (!response.equals("")) {
				JOptionPane.showMessageDialog(this, response);
			}
		}
	}
    
    private void loadIGVButtonTimerLabel(ServerWorker task, String svFlag) {
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

				if (svFlag.equals("Yes")) {
					loadSVIGVButton.setText(String.format("Processing...%s", minuteSecondDateFormat.format(interval)));
				} else {
					loadIGVButton.setText(String.format("Processing...%s", minuteSecondDateFormat.format(interval)));
				}
			}
		});
		timer.start();
	}

}
