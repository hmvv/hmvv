package hmvv.gui.mutationlist;

import hmvv.gui.GUICommonTools;
import hmvv.gui.sampleList.ContextMenuMouseListener;
import hmvv.io.DatabaseCommands;
import hmvv.io.MutationReportGenerator;
import hmvv.io.SSHConnection;
import hmvv.main.Configurations;
import hmvv.main.Configurations.MUTATION_SOMATIC_HISTORY;
import hmvv.main.Configurations.MUTATION_TIER;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Calendar;

public class AnnotationFrame extends JDialog {
	private static final long serialVersionUID = 1L;

	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancel");
	private JButton previousGeneAnnotationButton = new JButton("Previous");
	private JButton previousAnnotationButton = new JButton("Previous");
	private JButton previousVariantAnnotationButton = new JButton("Previous");
	private JButton nextGeneAnnotationButton = new JButton("Next");
	private JButton nextAnnotationButton = new JButton("Next");
	private JButton nextVariantAnnotationButton = new JButton("Next");
	private JButton draftButton = new JButton("Variant Annotation Draft");

	private JComboBox<String> classificationComboBox;
	private JComboBox<String> originComboBox;
	private JComboBox<MUTATION_TIER> variantTierComboBox = new JComboBox<MUTATION_TIER>();
	private JComboBox<MUTATION_SOMATIC_HISTORY> variantSomaticHistoryComboBox = new JComboBox<MUTATION_SOMATIC_HISTORY>();
	private JCheckBox germlinCheckBox = new JCheckBox("Possible Germline");

	private JTextArea geneAnnotationTextArea = new JTextArea();
	private JTextArea variantAnnotationTextArea = new JTextArea();
	private JTextArea sampleVariantAnnotationTextArea = new JTextArea();

	private JLabel historyLabelGeneAnnotation = new JLabel("New Gene Annotation");
	private JLabel historyLabelAnnotation = new JLabel("New Annotation");
	private JLabel historyLabelSampleAnnotation = new JLabel("New");

	private Integer annotationHistorySize;

	private Boolean readOnly;
	private Integer currentGeneAnnotationIndex;
	private Integer currentAnnotationIndex;
	private Integer currentSampleVariantAnnotationIndex;
	private String gene;

	private MutationCommon mutation;
	private ArrayList<GeneAnnotation> geneAnnotationHistory;
	private ArrayList<Annotation> annotationHistory;
	private ArrayList<SampleVariantAnnotation> sampleVariantAnnotationHistory;

	private final Color readOnlyColor = new Color(245,245,245);
	private final Color readWriteColor = Color.WHITE;

	/**
	 * Create the dialog.
	 * @throws Exception
	 */
	public AnnotationFrame(JDialog parent, MutationCommon mutation) throws Exception {
		super(parent, "Title Set Later", ModalityType.APPLICATION_MODAL);
		String title = "Annotation - " + mutation.getGene() + " - " + mutation.getCoordinate().getCoordinateAsString();
		setTitle(title);
		this.mutation = mutation;
		this.readOnly = !SSHConnection.isSuperUser(Configurations.USER_FUNCTION.ANNOTATE_MAIN);
		
		gene = mutation.getGene();
		geneAnnotationHistory = DatabaseCommands.getGeneAnnotationHistory(gene, mutation.getMutationType());
		currentGeneAnnotationIndex = geneAnnotationHistory.size() - 1; 

		annotationHistory = DatabaseCommands.getVariantAnnotationHistory(mutation.getCoordinate(),mutation.getMutationType());
		annotationHistorySize = annotationHistory.size();
		currentAnnotationIndex = annotationHistorySize - 1;

		sampleVariantAnnotationHistory = DatabaseCommands.getSampleVariantAnnotationHistory(mutation);
		currentSampleVariantAnnotationIndex = sampleVariantAnnotationHistory.size() - 1;

		
		createComponents();
		layoutComponents();
		activateComponents();
				
		if(readOnly){
			setTitle("Annotation (read only) - " + mutation.getGene() + " - " + mutation.getCoordinate().getCoordinateAsString());
			geneAnnotationTextArea.setEditable(false);
			variantAnnotationTextArea.setEditable(false);
			sampleVariantAnnotationTextArea.setEditable(false);
			classificationComboBox.setEnabled(false);
			originComboBox.setEnabled(false);
			variantTierComboBox.setEnabled(false);
			variantSomaticHistoryComboBox.setEnabled(false);
			germlinCheckBox.setEnabled(false);
			geneAnnotationTextArea.setBackground(new Color(234,240,240));
			variantAnnotationTextArea.setBackground(new Color(234,240,240));
			sampleVariantAnnotationTextArea.setBackground(new Color(234,240,240));
		}
		
		pack();
		setResizable(true);
		Rectangle bounds = GUICommonTools.getBounds(parent);
		//setSize((int)(bounds.width*.70), (int)(bounds.height*.70));
		int width = Math.max((int)(bounds.width * 0.70), 1000);
		int height = Math.max((int)(bounds.height * 0.70), 700);
		
		setSize(width, height);
		setMinimumSize(new Dimension(1000, 700));
		setLocationRelativeTo(parent);
		//setLocationRelativeTo(parent);
	}

	private void createComponents(){
		classificationComboBox = new JComboBox<String>();
		classificationComboBox.addItem("Not set");
		classificationComboBox.addItem("Benign");
		classificationComboBox.addItem("Likely Benign");
		classificationComboBox.addItem("Unknown");
		classificationComboBox.addItem("Likely Pathogenic");
		classificationComboBox.addItem("Pathogenic");		
		
		originComboBox = new JComboBox<String>();
		originComboBox.addItem("Not set");
		originComboBox.addItem("Somatic");
		originComboBox.addItem("Germline");
		originComboBox.addItem("Unknown");
		originComboBox.addItem("Artifact");
		originComboBox.addItem("Both Somatic and Germline");
		originComboBox.addItem("Not Confirmed Somatic");
		
		for(MUTATION_TIER mutation_tier : MUTATION_TIER.values()){
			variantTierComboBox.addItem(mutation_tier);
		}

		for(MUTATION_SOMATIC_HISTORY mutation_somatic_history : MUTATION_SOMATIC_HISTORY.values()){
			variantSomaticHistoryComboBox.addItem(mutation_somatic_history);
		}

		if (geneAnnotationHistory.size() <= 1) { 	
			previousGeneAnnotationButton.setEnabled(false); 
		}

		if (annotationHistorySize <= 1) { 
			previousAnnotationButton.setEnabled(false); 
		}

		if (sampleVariantAnnotationHistory.size() <= 1) { 	
			previousVariantAnnotationButton.setEnabled(false); 
		}

		nextGeneAnnotationButton.setEnabled(false);
		nextAnnotationButton.setEnabled(false);
		nextVariantAnnotationButton.setEnabled(false);
		draftButton.setEnabled(SSHConnection.isSuperUser(Configurations.USER_FUNCTION.ANNOTATE_DRAFT));
		
		geneAnnotationTextArea.setWrapStyleWord(true);
		geneAnnotationTextArea.setLineWrap(true);
		
		variantAnnotationTextArea.setWrapStyleWord(true);
		variantAnnotationTextArea.setLineWrap(true);

		sampleVariantAnnotationTextArea.setWrapStyleWord(true);
		sampleVariantAnnotationTextArea.setLineWrap(true);

		cancelButton.setActionCommand("Cancel");
		getRootPane().setDefaultButton(okButton);
		setDefaultComponentValues();
	}
	
	private void setDefaultComponentValues() {
		Annotation defaultAnnotation = mutation.getLatestAnnotation();
		if(defaultAnnotation != null) {
			if (defaultAnnotation.classification == null){
				classificationComboBox.setSelectedItem("Not Set");
			}else{
				classificationComboBox.setSelectedItem(defaultAnnotation.classification);
			}
			if (defaultAnnotation.somatic == null){
				originComboBox.setSelectedItem("Not Set");
			}else{
			originComboBox.setSelectedItem(defaultAnnotation.somatic);
			}
			variantAnnotationTextArea.setText(defaultAnnotation.curation);
			setCommonAnnotationLabel(defaultAnnotation, historyLabelAnnotation);
		}
		
		if(geneAnnotationHistory.size() > 0) {
			GeneAnnotation defaultGeneAnnotation = geneAnnotationHistory.get(geneAnnotationHistory.size() - 1);
			geneAnnotationTextArea.setText(defaultGeneAnnotation.curation);
			setCommonAnnotationLabel(defaultGeneAnnotation, historyLabelGeneAnnotation);
		}

		if(sampleVariantAnnotationHistory.size() > 0) {
			SampleVariantAnnotation defaultSampleVariantAnnotation = sampleVariantAnnotationHistory.get(sampleVariantAnnotationHistory.size() - 1);
			variantTierComboBox.setSelectedItem(defaultSampleVariantAnnotation.mutation_tier);
			variantSomaticHistoryComboBox.setSelectedItem(defaultSampleVariantAnnotation.mutation_somatic_history);
			germlinCheckBox.setSelected(defaultSampleVariantAnnotation.possibleGermline);
			sampleVariantAnnotationTextArea.setText(defaultSampleVariantAnnotation.curation);
			setCommonAnnotationLabel(defaultSampleVariantAnnotation, historyLabelSampleAnnotation);
		}
	}
	
	private void setCommonAnnotationLabel(CommonAnnotation commonAnnotation, JLabel label) {
		if(commonAnnotation.enteredBy != null){ 
		label.setText(commonAnnotation.enteredBy + " [" + GUICommonTools.shortDateFormat.format(commonAnnotation.enterDate) + "]");
	}}
	
	private void addItem(JPanel itemPanel, String label, Component content) {
		JLabel geneLabel = new JLabel(label);
		geneLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		itemPanel.add(geneLabel);
		itemPanel.add(content);
		itemPanel.add(new JLabel());//blank space
	}
	
	private void addItem(JPanel itemPanel, String label, String content) {
		JTextField textField = new JTextField(content);
		textField.setEditable(false);
		addItem(itemPanel, label, textField);
	}
	
	private void layoutComponents(){
		JPanel contentPanel = new JPanel();
		setContentPane(contentPanel);
		
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BorderLayout());
		
		JPanel itemPanel = new JPanel();
		GridLayout itemPanelGridLayout = new GridLayout(0,1);
		itemPanelGridLayout.setVgap(1);
		itemPanel.setLayout(itemPanelGridLayout);
		
		addItem(itemPanel, "Gene", mutation.getGene());
		addItem(itemPanel, "Coordinate", mutation.getCoordinate().getCoordinateAsString());
		String HGVSc = "";
		if(mutation.getHGVSc().startsWith("ENST")){
			String[] split = mutation.getHGVSc().split(":");
			if (split.length > 1)
				HGVSc = split[1];
			else
				HGVSc = mutation.getHGVSc();
		}else{
			mutation.getHGVSc();
		}
		addItem(itemPanel, "HGVSc", HGVSc);
		String HGVSp = (mutation.getHGVSp().startsWith("ENSP")) ? mutation.getHGVSp().split(":")[1] : mutation.getHGVSp();
		addItem(itemPanel, "HGVSp", HGVSp);
		addItem(itemPanel, "Classification", classificationComboBox);
		addItem(itemPanel, "Origin", originComboBox);
		addItem(itemPanel, "Tier", variantTierComboBox);
		addItem(itemPanel, "Somatic Hx", variantSomaticHistoryComboBox);
		addItem(itemPanel, "Germline?", germlinCheckBox);
		
		Dimension textAreaDimension = new Dimension(300,550);

		//Variant Annotation Setups
		JPanel textAreaPanel = new JPanel();
		textAreaPanel.setLayout(new GridLayout(1,0));

		//Sample Variant Annotation
		JPanel sampleVariantAnnotationPanel = new JPanel();
		sampleVariantAnnotationPanel.setLayout(new BoxLayout(sampleVariantAnnotationPanel, BoxLayout.Y_AXIS));
		JScrollPane sampleVariantAnnotationScrollPane = new JScrollPane(sampleVariantAnnotationTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sampleVariantAnnotationScrollPane.setPreferredSize(textAreaDimension);
		TitledBorder sampleVariantAnnotationBorder = BorderFactory.createTitledBorder("Sample Variant Annotation");
		sampleVariantAnnotationBorder.setTitleFont(GUICommonTools.TAHOMA_BOLD_14);
		sampleVariantAnnotationPanel.setBorder(sampleVariantAnnotationBorder);
		sampleVariantAnnotationPanel.add(sampleVariantAnnotationScrollPane);
		JPanel historyPanelSA = new JPanel();
		historyPanelSA.setLayout(new FlowLayout(FlowLayout.LEFT));
		historyPanelSA.add(previousVariantAnnotationButton);
		historyPanelSA.add(historyLabelSampleAnnotation);
		historyPanelSA.add(nextVariantAnnotationButton);
		sampleVariantAnnotationPanel.add(historyPanelSA);
		textAreaPanel.add(sampleVariantAnnotationPanel);
		
		//Variant Annotation
		JPanel annotationPanel = new JPanel();
		annotationPanel.setLayout(new BoxLayout(annotationPanel, BoxLayout.Y_AXIS));
		JScrollPane annotationScrollPane = new JScrollPane(variantAnnotationTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		annotationScrollPane.setPreferredSize(textAreaDimension);
		TitledBorder annotationBorder = BorderFactory.createTitledBorder("Variant Annotation");
		annotationBorder.setTitleFont(GUICommonTools.TAHOMA_BOLD_14);
		annotationPanel.setBorder(annotationBorder);
		annotationPanel.add(annotationScrollPane);
		JPanel historyPanelA = new JPanel();
		historyPanelA.setLayout(new FlowLayout(FlowLayout.LEFT));
		historyPanelA.add(previousAnnotationButton);
		historyPanelA.add(historyLabelAnnotation);
		historyPanelA.add(nextAnnotationButton);
		annotationPanel.add(historyPanelA);
		textAreaPanel.add(annotationPanel);
		
		//GeneAnnotation
		JPanel geneAnnotationPanel = new JPanel();
		geneAnnotationPanel.setLayout(new BoxLayout(geneAnnotationPanel, BoxLayout.Y_AXIS));
		JScrollPane geneScrollPane = new JScrollPane(geneAnnotationTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		geneScrollPane.setPreferredSize(textAreaDimension);
		TitledBorder geneAnnotationBorder = BorderFactory.createTitledBorder("" + mutation.getGene() + " Annotation");
		geneAnnotationBorder.setTitleFont(GUICommonTools.TAHOMA_BOLD_14);
		geneAnnotationPanel.setBorder(geneAnnotationBorder);
		geneAnnotationPanel.add(geneScrollPane);
		JPanel historyPanelGA = new JPanel();
		historyPanelGA.setLayout(new FlowLayout(FlowLayout.LEFT));
		historyPanelGA.add(previousGeneAnnotationButton);
		historyPanelGA.add(historyLabelGeneAnnotation);
		historyPanelGA.add(nextGeneAnnotationButton);
		geneAnnotationPanel.add(historyPanelGA);
		textAreaPanel.add(geneAnnotationPanel);
				
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPane.add(draftButton);
		buttonPane.add(okButton);
		buttonPane.add(cancelButton);
		
		contentPanel.add(itemPanel, BorderLayout.WEST);
		contentPanel.add(textAreaPanel, BorderLayout.CENTER);
		contentPanel.add(buttonPane, BorderLayout.SOUTH);
	}

	private void activateComponents(){
		ActionListener listener = new ActionListener(){
            @Override
			public void actionPerformed(ActionEvent action) {
                try {
					if(action.getSource() == okButton){
						AnnotationFrame.this.setVisible(false);
						saveRecord();
					}else if(action.getSource() == cancelButton){
						AnnotationFrame.this.dispose();
					}else if(action.getSource() == draftButton){
						showAnnotationDraftFrame();
					}else if(action.getSource() == variantTierComboBox){
						updateReportText();
					}else if(action.getSource() == variantSomaticHistoryComboBox){
						updateReportText();
					}
                } catch (Exception e) {
                    HMVVDefectReportFrame.showHMVVDefectReportFrame(AnnotationFrame.this, e);
                }
            }
        };
		okButton.addActionListener(listener);
		cancelButton.addActionListener(listener);
		draftButton.addActionListener(listener);
		variantTierComboBox.addActionListener(listener);
		variantSomaticHistoryComboBox.addActionListener(listener);

		ItemListener itemListener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                try {
                    updateReportText();
                } catch (Exception e) {
                    HMVVDefectReportFrame.showHMVVDefectReportFrame(AnnotationFrame.this, e);
                }
            }
        };
        germlinCheckBox.addItemListener(itemListener);


		ActionListener historyActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(e.getSource() == previousGeneAnnotationButton) {
						showGeneAnnotationPrevious();
					}else if(e.getSource() == nextGeneAnnotationButton) {
						showGeneAnnotationNext();
					}else if(e.getSource() == previousAnnotationButton) {
						showAnnotationPrevious();
					}else if(e.getSource() == nextAnnotationButton) {
						showAnnotationNext();
					}else if(e.getSource() == previousVariantAnnotationButton) {
						showSampleVariantAnnotationPrevious();
					}else if(e.getSource() == nextVariantAnnotationButton) {
						showSampleVariantAnnotationNext();
					}
				} catch (Exception exception) {
					HMVVDefectReportFrame.showHMVVDefectReportFrame(AnnotationFrame.this, exception);
				}
			}
		};
		previousGeneAnnotationButton.addActionListener(historyActionListener);
		nextGeneAnnotationButton.addActionListener(historyActionListener);
		previousAnnotationButton.addActionListener(historyActionListener);
		nextAnnotationButton.addActionListener(historyActionListener);
		previousVariantAnnotationButton.addActionListener(historyActionListener);
		nextVariantAnnotationButton.addActionListener(historyActionListener);
		variantAnnotationTextArea.addMouseListener(new ContextMenuMouseListener());
	}

	private void saveRecord() throws Exception{
		if(readOnly){
			return;
		}
		
		if(!SSHConnection.isSuperUser(Configurations.USER_FUNCTION.ANNOTATE_MAIN)){
			//readOnly should prevent this, but just in case
			JOptionPane.showMessageDialog(this, "Only authorized user can edit annotation");
			return;
		}
		
		saveSampleVariantAnnotationRecord();
		saveAnnotationRecord();
		saveGeneAnnotationRecord();
	}
	
	private void saveSampleVariantAnnotationRecord() throws Exception{
		MUTATION_TIER tier_selected = (MUTATION_TIER)variantTierComboBox.getSelectedItem();
        MUTATION_SOMATIC_HISTORY choice_selected = (MUTATION_SOMATIC_HISTORY)variantSomaticHistoryComboBox.getSelectedItem();
        boolean possibleGermline = germlinCheckBox.isSelected();
        String thisUpdatedReport = sampleVariantAnnotationTextArea.getText();
		SampleVariantAnnotation newSampleVariantAnnotation = new SampleVariantAnnotation(mutation, tier_selected, choice_selected, possibleGermline, thisUpdatedReport,SSHConnection.getUserName(), Calendar.getInstance().getTime());
		SampleVariantAnnotation latestSampleVariantAnnotation = mutation.getMutationAnnotation();
		if (latestSampleVariantAnnotation == null || !latestSampleVariantAnnotation.equals(newSampleVariantAnnotation)) {
			DatabaseCommands.addSampleVariantAnnotation(newSampleVariantAnnotation);
			mutation.setMutationAnnotation(latestSampleVariantAnnotation);
		}
	}

	private void saveAnnotationRecord() throws Exception {
		if(currentAnnotationIndex == annotationHistorySize - 1 || annotationHistorySize == 0) {//only consider saving annotation if we are at the most recent one, or there never has been an annotation
			//annotation update
			Annotation newAnnotation = new Annotation(
					mutation.getCoordinate(),
					classificationComboBox.getSelectedItem().toString(),
					variantAnnotationTextArea.getText(),
					originComboBox.getSelectedItem().toString(),
					SSHConnection.getUserName(),
					Calendar.getInstance().getTime()
					);
			Annotation latestAnnotation = mutation.getLatestAnnotation();
			if (latestAnnotation == null || !latestAnnotation.equals(newAnnotation)) {
				DatabaseCommands.addVariantAnnotationCuration(newAnnotation,Configurations.MUTATION_TYPE.SOMATIC);
				mutation.setLatestAnnotation(newAnnotation);
			}
		}
	}
	
	private void saveGeneAnnotationRecord() throws Exception {
		if(currentGeneAnnotationIndex == geneAnnotationHistory.size() - 1 || geneAnnotationHistory.isEmpty()) {//only consider saving annotation if we are at the most recent one, or there never has been an annotation
			//gene annotation update
			GeneAnnotation newGeneAnnotation = new GeneAnnotation(
					mutation.getGene(),
					geneAnnotationTextArea.getText(),
					SSHConnection.getUserName(),
					Calendar.getInstance().getTime()
					);
			GeneAnnotation latestGeneAnnotation = (geneAnnotationHistory.isEmpty()) ? null : geneAnnotationHistory.get(geneAnnotationHistory.size() - 1);		
			if(latestGeneAnnotation == null || !latestGeneAnnotation.equals(newGeneAnnotation)) {
				DatabaseCommands.addGeneAnnotationCuration(newGeneAnnotation,Configurations.MUTATION_TYPE.SOMATIC);
				geneAnnotationHistory.add(newGeneAnnotation);//may not be necessary as no GUI object currently stores this list
			}
		}
	}
    
	private void showGeneAnnotationPrevious(){
		currentGeneAnnotationIndex = currentGeneAnnotationIndex - 1;
		updateGeneAnnotation();
	}
	
	private void showGeneAnnotationNext(){
		currentGeneAnnotationIndex = currentGeneAnnotationIndex + 1;
		updateGeneAnnotation();	
    }
	
	private void updateGeneAnnotation() {
		GeneAnnotation currentGeneAnnotation = geneAnnotationHistory.get(currentGeneAnnotationIndex);
		geneAnnotationTextArea.setText(currentGeneAnnotation.curation);
		setCommonAnnotationLabel(currentGeneAnnotation, historyLabelGeneAnnotation);
		
		updateCommon(currentGeneAnnotationIndex, geneAnnotationHistory.size(), geneAnnotationTextArea, previousGeneAnnotationButton, nextGeneAnnotationButton);
	}
	
	private void showAnnotationPrevious(){
		currentAnnotationIndex = currentAnnotationIndex - 1;
		updateAnnotation();
	}

	private void showAnnotationNext(){
		currentAnnotationIndex = currentAnnotationIndex + 1;
		updateAnnotation();
	}

	private void showSampleVariantAnnotationPrevious(){
		currentSampleVariantAnnotationIndex = currentSampleVariantAnnotationIndex - 1;
		updateSampleVariantAnnotation();
	}

	private void showSampleVariantAnnotationNext(){
		currentSampleVariantAnnotationIndex = currentSampleVariantAnnotationIndex + 1;
		updateSampleVariantAnnotation();
	}

	private void updateSampleVariantAnnotation() {
		SampleVariantAnnotation currentSampleVariantAnnotation = sampleVariantAnnotationHistory.get(currentSampleVariantAnnotationIndex);
		sampleVariantAnnotationTextArea.setText(currentSampleVariantAnnotation.curation);
		variantTierComboBox.setSelectedItem(currentSampleVariantAnnotation.mutation_tier);
		variantSomaticHistoryComboBox.setSelectedItem(currentSampleVariantAnnotation.mutation_somatic_history);
		germlinCheckBox.setSelected(currentSampleVariantAnnotation.possibleGermline);

		setCommonAnnotationLabel(currentSampleVariantAnnotation, historyLabelSampleAnnotation);
		
		updateCommon(currentSampleVariantAnnotationIndex, sampleVariantAnnotationHistory.size(), sampleVariantAnnotationTextArea, previousVariantAnnotationButton, nextVariantAnnotationButton);
		
		if (currentSampleVariantAnnotationIndex == sampleVariantAnnotationHistory.size() - 1) {
			if(!readOnly) {
				variantTierComboBox.setEnabled(true);
				variantSomaticHistoryComboBox.setEnabled(true);
				germlinCheckBox.setEnabled(true);
			}
		}else if(sampleVariantAnnotationHistory.size() != 1) {
			variantTierComboBox.setEnabled(false);
				variantSomaticHistoryComboBox.setEnabled(false);
				germlinCheckBox.setEnabled(false);
		}
	}
	
	private void updateAnnotation() {
		Annotation currentannotation = annotationHistory.get(currentAnnotationIndex); //mutation.getAnnotation(currentAnnotationIndex);
		variantAnnotationTextArea.setText(currentannotation.curation);
		classificationComboBox.setSelectedItem(currentannotation.classification);
		originComboBox.setSelectedItem(currentannotation.somatic);
		setCommonAnnotationLabel(currentannotation, historyLabelAnnotation);
		
		updateCommon(currentAnnotationIndex, annotationHistorySize, variantAnnotationTextArea, previousAnnotationButton, nextAnnotationButton);
		
		if (currentAnnotationIndex == annotationHistorySize - 1) {
			if(!readOnly) {
				classificationComboBox.setEnabled(true);
				originComboBox.setEnabled(true);
			}
		}else if(annotationHistorySize != 1) {
			classificationComboBox.setEnabled(false);
			originComboBox.setEnabled(false);
		}
	}
	
	private void updateCommon(int currentIndex, int historySize, JTextArea textArea, JButton previousButton, JButton nextButton) {		
		//default to read only, and switch below if we are at the most present annotation
		textArea.setBackground(readOnlyColor);
		textArea.setEditable(false);
		
		//update previousAnnotationButton
		if (currentIndex == 0) {
			previousButton.setEnabled(false);		
		}else if(historySize > 1) {
			previousButton.setEnabled(true);
		}
		
		//update nextAnnotationButton
		if ( currentIndex == historySize - 1) {
			//we are at the end of the gene annotation history
			nextButton.setEnabled(false);
			if(!readOnly) {
				textArea.setEditable(true);		
				textArea.setBackground(readWriteColor);
			}
		}else if(historySize != 1) {
			nextButton.setEnabled(true);
		}
	}

	private void updateReportText() throws Exception{
		MUTATION_TIER tier_selected = (MUTATION_TIER) variantTierComboBox.getSelectedItem();
        MUTATION_SOMATIC_HISTORY choice_selected = (MUTATION_SOMATIC_HISTORY) variantSomaticHistoryComboBox.getSelectedItem();
        boolean possibleGermline = germlinCheckBox.isSelected();
		String thisUpdatedReport = MutationReportGenerator.generateShortReport(mutation, tier_selected, choice_selected, possibleGermline);
        sampleVariantAnnotationTextArea.setText(thisUpdatedReport);
    }

	private void showAnnotationDraftFrame(){
		AnnotationDraftFrame annotationdraftframe = new AnnotationDraftFrame(this, mutation);	
		annotationdraftframe.setVisible(true);
	}	
}