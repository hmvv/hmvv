package hmvv.gui.mutationlist;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.io.AsynchronousMutationDataIO;
import hmvv.io.DatabaseCommands;
import hmvv.main.Configurations;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.model.Mutation;
import hmvv.model.Sample;
import hmvv.model.VariantPredictionClass;

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
	private JComboBox<VariantPredictionClass> predictionFilterComboBox;
	
	private Sample sample;
    private MutationListFrame parent;
	private MutationList mutationList;
	private MutationListFilters mutationListFilters;

    private JButton resetButton;
    private JButton loadFilteredMutationsButton;
	
	MutationFilterPanel(MutationListFrame parent,Sample sample, MutationList mutationList, MutationListFilters mutationListFilters){
		this.parent=parent;
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


		resetButton = new JButton("Reset all Filters");
        resetButton.setToolTipText("Reset filters to defaults");
        resetButton.setFont(GUICommonTools.TAHOMA_BOLD_13);

        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetFilters();
            }
        });

        loadFilteredMutationsButton = new JButton("Load Mutations");
        loadFilteredMutationsButton.setToolTipText("Load Mutations that did not meet the quality filter metrics");
        loadFilteredMutationsButton.setFont(GUICommonTools.TAHOMA_BOLD_11);
       // loadFilteredMutationsButton.setEnabled(false);//this will be enabled after the unfiltered variant data is loaded

        loadFilteredMutationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFilteredMutationsButton.setEnabled(false);
                loadFilteredMutationsAsynchronous();
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

		maxPopulationFrequencyGnomadTextField = new JTextField();
		maxPopulationFrequencyGnomadTextField.getDocument().addDocumentListener(documentListener);
		maxPopulationFrequencyGnomadTextField.setColumns(textFieldColumnWidth);

		predictionFilterComboBox = new JComboBox<VariantPredictionClass>(VariantPredictionClass.getAllClassifications());
		predictionFilterComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyRowFilters();
			}
		});
		
		//construct filter objects
		mutationListFilters.addCosmicIDFilter(cosmicOnlyCheckbox);
		mutationListFilters.addMaxG1000FrequencyFilter(maxPopulationFrequencyG1000TextField);
		mutationListFilters.addMaxGnomadFrequencyFilter(maxPopulationFrequencyGnomadTextField);
		mutationListFilters.addMaxOccurrenceFilter(occurenceFromTextField);
		mutationListFilters.addMinReadDepthFilter(minReadDepthTextField);
		mutationListFilters.addReportedOnlyFilter(reportedOnlyCheckbox);
		mutationListFilters.addVariantAlleleFrequencyFilter(textFreqFrom, textVarFreqTo);
		mutationListFilters.addVariantPredicationClassFilter(predictionFilterComboBox);
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
        predictionFilterComponentsPanel.add(loadFilteredMutationsButton);
        predictionFilterPanel.add(predictionFilterComponentsPanel);
        vepPanel.add(predictionFilterPanel);

        JPanel rightFilterPanel = new JPanel();
		rightFilterPanel.add(vepPanel);
		
        leftFilterPanel.setBorder(new EmptyBorder(1, 5, 1, 5));
		add(leftFilterPanel);
        middleFilterPanel.setBorder(new EmptyBorder(1, 5, 1, 5));
        add(middleFilterPanel);
        rightFilterPanel.setBorder(new EmptyBorder(1, 5, 1, 5));
		add(rightFilterPanel);

        setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}

	void resetFilters(){
		cosmicOnlyCheckbox.setSelected(false);
		reportedOnlyCheckbox.setSelected(false);
		selectAllCheckbox.setSelected(false);
		textFreqFrom.setText(Configurations.getAlleleFrequencyFilter(sample)+"");
		textVarFreqTo.setText(Configurations.MAX_ALLELE_FREQ_FILTER+"");
		minReadDepthTextField.setText(Configurations.READ_DEPTH_FILTER+"");
		occurenceFromTextField.setText(Configurations.MIN_OCCURENCE_FILTER+"");
		maxPopulationFrequencyG1000TextField.setText(Configurations.MAX_GLOBAL_ALLELE_FREQ_FILTER+"");
		maxPopulationFrequencyGnomadTextField.setText(Configurations.MAX_GLOBAL_ALLELE_FREQ_FILTER+"");
		predictionFilterComboBox.setSelectedIndex(1);
		applyRowFilters();
		handleSelectAllClick(false);
	}

	void disableInputForAsynchronousLoad() {
		String tooltip = "Disabled while data is loading";
		cosmicOnlyCheckbox.setEnabled(false);
		cosmicOnlyCheckbox.setToolTipText(tooltip);
		reportedOnlyCheckbox.setEnabled(false);
		reportedOnlyCheckbox.setToolTipText(tooltip);
		selectAllCheckbox.setEnabled(false);
		selectAllCheckbox.setToolTipText(tooltip);
		textFreqFrom.setEditable(false);
		textFreqFrom.setToolTipText(tooltip);
		textVarFreqTo.setEditable(false);
		textVarFreqTo.setToolTipText(tooltip);
		minReadDepthTextField.setEditable(false);
		minReadDepthTextField.setToolTipText(tooltip);
		occurenceFromTextField.setEditable(false);
		occurenceFromTextField.setToolTipText(tooltip);
		maxPopulationFrequencyG1000TextField.setEditable(false);
		maxPopulationFrequencyG1000TextField.setToolTipText(tooltip);
		maxPopulationFrequencyGnomadTextField.setEditable(false);
		maxPopulationFrequencyGnomadTextField.setToolTipText(tooltip);
		predictionFilterComboBox.setEnabled(false);
		predictionFilterComboBox.setToolTipText(tooltip);
        resetButton.setEnabled(false);
        resetButton.setToolTipText(tooltip);
        loadFilteredMutationsButton.setEnabled(false);
        loadFilteredMutationsButton.setToolTipText(tooltip);
	}

	void enableInputAfterAsynchronousLoad() {
		cosmicOnlyCheckbox.setEnabled(true);
		cosmicOnlyCheckbox.setToolTipText("");
		reportedOnlyCheckbox.setEnabled(true);
		reportedOnlyCheckbox.setToolTipText("");
		selectAllCheckbox.setEnabled(true);
		selectAllCheckbox.setToolTipText("");
		textFreqFrom.setEditable(true);
		textFreqFrom.setToolTipText("");
		textVarFreqTo.setEditable(true);
		textVarFreqTo.setToolTipText("");
		minReadDepthTextField.setEditable(true);
		minReadDepthTextField.setToolTipText("");
		occurenceFromTextField.setEditable(true);
		occurenceFromTextField.setToolTipText("");
		maxPopulationFrequencyG1000TextField.setEditable(true);
		maxPopulationFrequencyG1000TextField.setToolTipText("");
		maxPopulationFrequencyGnomadTextField.setEditable(true);
		maxPopulationFrequencyGnomadTextField.setToolTipText("");
		predictionFilterComboBox.setEnabled(true);
		predictionFilterComboBox.setToolTipText("");
        resetButton.setEnabled(true);
        resetButton.setToolTipText("");
        loadFilteredMutationsButton.setEnabled(true);
        loadFilteredMutationsButton.setToolTipText("");
	}
	
	void applyRowFilters(){
		mutationList.filterMutations(mutationListFilters);
	}

    private void loadFilteredMutationsAsynchronous() {
        createLoadFilteredMutationDataThread();
    }

    private void createLoadFilteredMutationDataThread(){
        Thread loadFilteredMutationDataThread = new Thread(new Runnable(){
            @Override
            public void run() {
                disableInputForAsynchronousLoad();
                getFilteredMutationData();
                enableInputAfterAsynchronousLoad();
                loadFilteredMutationsButton.setEnabled(false);
            }
        });
        loadFilteredMutationDataThread.start();
    }
    private void getFilteredMutationData() {
        try{
            loadFilteredMutationsButton.setText("Loading...");
            ArrayList<Mutation> mutations = DatabaseCommands.getExtraMutationsBySample(sample);
            for(int i = 0; i < mutations.size(); i++) {
                if(parent.isCallbackClosed()){
                    return;
                }
                loadFilteredMutationsButton.setText("Loading " + (i+1) + " of " + mutations.size());
                try{
                    Mutation mutation = mutations.get(i);
                    AsynchronousMutationDataIO.getMutationData(mutation);
                    //no need to call parent.mutationListIndexUpdated() here these mutations are not current displayed
                    mutationList.addFilteredMutation(mutation);

                }catch(Exception e){
                	HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, e, "Could not load additional mutation data.");
                }
            }
            applyRowFilters();
        }catch(Exception ex){
        	HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, ex);
        }
        loadFilteredMutationsButton.setText("Filtered mutations loaded");
    }

    private void handleSelectAllClick(boolean choice) {
		for (int i = 0; i < mutationList.getMutationCount(); i++) {
			Mutation mutation = mutationList.getMutation(i);
			if (choice==true){mutation.setSelected(true);}
			else {mutation.setSelected(false);}
		}
	}
}
