package hmvv.gui.mutationlist;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.tablemodels.MutationList;
import hmvv.main.Configurations;
import hmvv.model.Mutation;
import hmvv.model.Sample;
import hmvv.model.VariantPredictionClass;

public class MutationFilterPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JCheckBox reportedOnlyCheckbox;
	private JCheckBox cosmicOnlyCheckbox;
	private JCheckBox filterNomalCheckbox;


	private JTextField textFreqFrom;
	private JTextField textVarFreqTo;
	private JTextField minReadDepthTextField;
	private JTextField occurenceFromTextField;
	private JTextField maxPopulationFrequencyG1000TextField;
	private JTextField maxPopulationFrequencyGnomadTextField;
	private JComboBox<VariantPredictionClass> predictionFilterComboBox;
	
	private Sample sample;
	private MutationList mutationList;
	private MutationListFilters mutationListFilters;
	private ArrayList<Mutation> mutationsInNormalPair;
	
	MutationFilterPanel(Sample sample, MutationList mutationList, MutationListFilters mutationListFilters, ArrayList<Mutation> mutationsInNormalPair){
		this.sample = sample;
		this.mutationList = mutationList;
		this.mutationListFilters = mutationListFilters;
		this.mutationsInNormalPair = mutationsInNormalPair;
		constructCheckBoxFilters();
		constructTextFieldFilters();
		layoutComponents();
	}

	private void constructCheckBoxFilters(){
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyRowFilters();
			}
		};

		cosmicOnlyCheckbox = new JCheckBox("Show Cosmic Only");
		cosmicOnlyCheckbox.addActionListener(actionListener);
		cosmicOnlyCheckbox.setFont(GUICommonTools.TAHOMA_BOLD_14);

		reportedOnlyCheckbox = new JCheckBox("Show Reported Only");
		reportedOnlyCheckbox.addActionListener(actionListener);
		reportedOnlyCheckbox.setFont(GUICommonTools.TAHOMA_BOLD_14);

		filterNomalCheckbox = new JCheckBox("Filter Normal Pair");
		filterNomalCheckbox.addActionListener(actionListener);
		filterNomalCheckbox.setFont(GUICommonTools.TAHOMA_BOLD_14);
	}

	private void constructTextFieldFilters(){
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
		mutationListFilters.addMinOccurrenceFilter(occurenceFromTextField);
		mutationListFilters.addMinReadDepthFilter(minReadDepthTextField);
		mutationListFilters.addReportedOnlyFilter(reportedOnlyCheckbox);
		mutationListFilters.addTumorNormalFilter(filterNomalCheckbox, mutationsInNormalPair);
		mutationListFilters.addVariantAlleleFrequencyFilter(textFreqFrom, textVarFreqTo);
		mutationListFilters.addVariantPredicationClassFilter(predictionFilterComboBox);
	}

	private void layoutComponents() {
		JPanel leftFilterPanel = new JPanel();
		leftFilterPanel.setLayout(new GridLayout(0,1));
		JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		checkboxPanel.add(cosmicOnlyCheckbox);
		checkboxPanel.add(reportedOnlyCheckbox);

		if(mutationList.getMutationCount() > 1 && sample.assay.equals("exome")){
			checkboxPanel.add(filterNomalCheckbox);
		}

		leftFilterPanel.add(checkboxPanel);

		//variant frequency
		JPanel populationFrequencyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel populationFrequencyLabel = new JLabel("G1000 Max Population Frequency (altGlobalFreq)");
		populationFrequencyLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		populationFrequencyPanel.add(populationFrequencyLabel);
		populationFrequencyPanel.add(maxPopulationFrequencyG1000TextField);
		leftFilterPanel.add(populationFrequencyPanel);

		JPanel gnomadPopulationFrequencyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel gnomadPopulationFrequencyLabel = new JLabel("Gnomad Max Population Frequency (Allele Freq)");
		gnomadPopulationFrequencyLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		gnomadPopulationFrequencyPanel.add(gnomadPopulationFrequencyLabel);
		gnomadPopulationFrequencyPanel.add(maxPopulationFrequencyGnomadTextField);
		leftFilterPanel.add(gnomadPopulationFrequencyPanel);

		JPanel rightFilterPanel = new JPanel();
		rightFilterPanel.setLayout(new GridLayout(0,1));

		//min read depth and occurrence filters
		JPanel readDepthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel minReadDepthLabel = new JLabel("Min Read Depth (readDP)");
		minReadDepthLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		readDepthPanel.add(minReadDepthLabel);
		readDepthPanel.add(minReadDepthTextField);

		JLabel variantFrequencyLabel = new JLabel("Variant Frequency (altFreq)");
		variantFrequencyLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		JPanel frequencyLayoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		frequencyLayoutPanel.add(variantFrequencyLabel);
		frequencyLayoutPanel.add(textFreqFrom);
		frequencyLayoutPanel.add(new JLabel("To"));
		frequencyLayoutPanel.add(textVarFreqTo);

		JPanel occurencePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel occurenceLabel = new JLabel("Min occurence (occurence)");
		occurenceLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		occurencePanel.add(occurenceLabel);
		occurencePanel.add(occurenceFromTextField);

		JPanel predictionFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel predictionFilterLabel = new JLabel("Min Prediction Class (classification)");
		predictionFilterLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		predictionFilterPanel.add(predictionFilterLabel);
		predictionFilterPanel.add(predictionFilterComboBox);

		rightFilterPanel.add(readDepthPanel);
		rightFilterPanel.add(frequencyLayoutPanel);
		rightFilterPanel.add(occurencePanel);
		rightFilterPanel.add(predictionFilterPanel);
		
		setLayout(new GridLayout(1,0));
		add(leftFilterPanel);
		add(rightFilterPanel);
	}

	void resetFilters(){
		cosmicOnlyCheckbox.setSelected(false);
		reportedOnlyCheckbox.setSelected(false);		
		filterNomalCheckbox.setSelected(false);
		textFreqFrom.setText(Configurations.getAlleleFrequencyFilter(sample)+"");
		textVarFreqTo.setText(Configurations.MAX_ALLELE_FREQ_FILTER+"");
		minReadDepthTextField.setText(Configurations.READ_DEPTH_FILTER+"");
		occurenceFromTextField.setText(Configurations.MIN_OCCURENCE_FILTER+"");
		maxPopulationFrequencyG1000TextField.setText(Configurations.MAX_GLOBAL_ALLELE_FREQ_FILTER+"");
		maxPopulationFrequencyGnomadTextField.setText(Configurations.MAX_GLOBAL_ALLELE_FREQ_FILTER+"");
		predictionFilterComboBox.setSelectedIndex(1);
		applyRowFilters();
	}

	void disableInputForAsynchronousLoad() {
		String tooltip = "Disabled while data is loading";
		cosmicOnlyCheckbox.setEnabled(false);
		cosmicOnlyCheckbox.setToolTipText(tooltip);
		reportedOnlyCheckbox.setEnabled(false);
		reportedOnlyCheckbox.setToolTipText(tooltip);
		filterNomalCheckbox.setEnabled(false);
		filterNomalCheckbox.setToolTipText(tooltip);
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
	}

	void enableInputAfterAsynchronousLoad() {
		cosmicOnlyCheckbox.setEnabled(true);
		cosmicOnlyCheckbox.setToolTipText("");
		reportedOnlyCheckbox.setEnabled(true);
		reportedOnlyCheckbox.setToolTipText("");
		filterNomalCheckbox.setEnabled(true);
		filterNomalCheckbox.setToolTipText("");
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
	}
	
	void applyRowFilters(){
		mutationList.filterMutations(mutationListFilters);
	}
}
