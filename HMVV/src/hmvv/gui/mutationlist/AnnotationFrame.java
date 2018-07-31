package hmvv.gui.mutationlist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.DocumentFilter;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.tables.CommonTable;
import hmvv.gui.sampleList.ContextMenuMouseListener;
import hmvv.io.DatabaseCommands;
import hmvv.io.SSHConnection;
import hmvv.model.Annotation;
import hmvv.model.GeneAnnotation;
import hmvv.model.Mutation;

public class AnnotationFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private CommonTable parent;
	
	private JButton okButton;
	private JButton cancelButton;
	private JButton btnGeneAnnotationPrevious;
	private JButton btnAnnotationPrevious;
	private JButton btnGeneAnnotationNext;
	private JButton btnAnnotationNext;
	
	private JComboBox<String> pathogenicityComboBox;
	private JComboBox<String> mutationTypeComboBox;
	
	private JTextArea geneAnnotationTextArea;
	private JTextArea annotationTextArea;
	private int maxCharacters = 5000;
	private DefaultStyledDocument defaultStyledDocument;
	
	private JLabel historyLabelGeneAnnotation;
	private JLabel historyLabelAnnotation;
	
	private Boolean readOnly;
	private Integer currentGeneAnnotationIndex;
	private Integer currentAnnotationIndex;
	
	private Annotation currentAnnotation;
	private GeneAnnotation geneAnnotation;
	private ArrayList<GeneAnnotation> geneannotations;
	private ArrayList<Annotation> annotations;
	private Mutation mutation;
	
	/**
	 * Create the dialog.
	 * @throws Exception 
	 */
	public AnnotationFrame(Boolean readOnly, Mutation mutation, GeneAnnotation geneAnnotation, Annotation annotation, CommonTable parent, MutationListFrame mutationListFrame) throws Exception {
		super("Annotation - " + mutation.getGene() + " - " + annotation.getCoordinate().getCoordinateAsString());
		this.mutation = mutation;
		this.readOnly = readOnly;
		this.currentAnnotation = annotation;
		this.geneAnnotation = geneAnnotation;
		this.parent = parent;
		this.geneannotations = new ArrayList<GeneAnnotation>();
		this.annotations = new ArrayList<Annotation>();
		
		createComponents();
		layoutComponents();
		activateComponents();
		
		if(readOnly){
			setTitle("Annotation (read only) - " + mutation.getGene() + " - " + annotation.getCoordinate().getCoordinateAsString());
			geneAnnotationTextArea.setEditable(false);
			annotationTextArea.setEditable(false);
			pathogenicityComboBox.setEnabled(false);
			mutationTypeComboBox.setEnabled(false);
			geneAnnotationTextArea.setBackground(new Color(234,240,240));
			annotationTextArea.setBackground(new Color(234,240,240));
		}
		
		pack();
		setResizable(false);
		setLocationRelativeTo(mutationListFrame);
		openRecord();
	}

	private void createComponents(){
		Dimension textAreaDimension = new Dimension(400,400);
		
		pathogenicityComboBox = new JComboBox<String>();
		pathogenicityComboBox.addItem("Not set");
		pathogenicityComboBox.addItem("Benign");
		pathogenicityComboBox.addItem("Likely Benign");
		pathogenicityComboBox.addItem("Unknown");
		pathogenicityComboBox.addItem("Likely Pathogenic");
		pathogenicityComboBox.addItem("Pathogenic");
		pathogenicityComboBox.setSelectedItem(currentAnnotation.getClassification());

		mutationTypeComboBox = new JComboBox<String>();
		mutationTypeComboBox.addItem("Not set");
		mutationTypeComboBox.addItem("Somatic");
		mutationTypeComboBox.addItem("Germline");
		mutationTypeComboBox.addItem("Unknown");
		mutationTypeComboBox.setSelectedItem(currentAnnotation.getSomatic());

		
		btnGeneAnnotationPrevious = new JButton("Previous");
		btnAnnotationPrevious = new JButton("Previous");
		btnGeneAnnotationNext = new JButton("Next");
		btnAnnotationNext = new JButton("Next");
		
		
		geneAnnotationTextArea = new JTextArea();
		geneAnnotationTextArea.setWrapStyleWord(true);
		geneAnnotationTextArea.setLineWrap(true);
		geneAnnotationTextArea.setText(geneAnnotation.getCuration());
		geneAnnotationTextArea.setPreferredSize(textAreaDimension);
		
		annotationTextArea = new JTextArea();
		annotationTextArea.setWrapStyleWord(true);
		annotationTextArea.setLineWrap(true);
		annotationTextArea.setText(currentAnnotation.getCuration());
		annotationTextArea.setPreferredSize(textAreaDimension);
		
		historyLabelGeneAnnotation = new JLabel();
		historyLabelAnnotation = new JLabel();
		
		okButton = new JButton("OK");
		getRootPane().setDefaultButton(okButton);

		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");

		defaultStyledDocument = new DefaultStyledDocument();	
		
	}

	private void layoutComponents(){
		JPanel contentPanel = new JPanel();
		setContentPane(contentPanel);
		
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BorderLayout());
		
		JPanel itemPanel = new JPanel();
		GridLayout itemPanelGridLayout = new GridLayout(0,1);
		itemPanelGridLayout.setVgap(45);
		itemPanel.setLayout(itemPanelGridLayout);
		
		//blank space
		itemPanel.add(new JLabel());
		
		//Gene
		JLabel geneLabel = new JLabel("Gene");
		geneLabel.setFont(GUICommonTools.TAHOMA_BOLD_14);
		JPanel genePanel = new JPanel();
		genePanel.setLayout(new GridLayout(1,0));
		genePanel.add(geneLabel);
		JLabel geneLabelText = new JLabel(mutation.getGene());
		geneLabelText.setFont(GUICommonTools.TAHOMA_BOLD_14);
		genePanel.add(geneLabelText);
		itemPanel.add(genePanel);
				
		//Coordinate
		JLabel lblCoordinate = new JLabel("Coordinate");
		lblCoordinate.setFont(GUICommonTools.TAHOMA_BOLD_14);
		JPanel coordinatePanel = new JPanel();
		coordinatePanel.setLayout(new GridLayout(1,0));
		coordinatePanel.add(lblCoordinate);
		JLabel lblCoordinateText = new JLabel(currentAnnotation.getCoordinate().getCoordinateAsString());
		lblCoordinateText.setFont(GUICommonTools.TAHOMA_BOLD_14);
		coordinatePanel.add(lblCoordinateText);
		itemPanel.add(coordinatePanel);
		
		//Classification
		JLabel lblClassification = new JLabel("Classification");
		lblClassification.setFont(GUICommonTools.TAHOMA_BOLD_14);
		JPanel classificationPanel = new JPanel();
		classificationPanel.setLayout(new GridLayout(1,0));
		classificationPanel.add(lblClassification);
		classificationPanel.add(pathogenicityComboBox);
		itemPanel.add(classificationPanel);
		
		//Somatic
		JLabel lblSomatic = new JLabel("Somatic");
		lblSomatic.setFont(GUICommonTools.TAHOMA_BOLD_14);
		JPanel somaticPanel = new JPanel();
		somaticPanel.setLayout(new GridLayout(1,0));
		somaticPanel.add(lblSomatic);
		somaticPanel.add(mutationTypeComboBox);
		itemPanel.add(somaticPanel);
		
		//blank space
		itemPanel.add(new JLabel());
		itemPanel.add(new JLabel());
		
		JPanel textAreaPanel = new JPanel();
		//Annotation
		JPanel annotationPanel = new JPanel();
		annotationPanel.setLayout(new BoxLayout(annotationPanel, BoxLayout.Y_AXIS));
		JScrollPane annotationScrollPane = new JScrollPane();
		annotationScrollPane.setViewportView(annotationTextArea);
		TitledBorder annotationBorder = BorderFactory.createTitledBorder("Variant Annotation (5000 characters max)");
		annotationBorder.setTitleFont(GUICommonTools.TAHOMA_BOLD_14);
		annotationScrollPane.setBorder(annotationBorder);
		annotationPanel.add(annotationScrollPane);
		JPanel historyPanelA = new JPanel();
		historyPanelA.setLayout(new FlowLayout(FlowLayout.LEFT));
		historyPanelA.add(btnAnnotationPrevious);
		historyPanelA.add(historyLabelAnnotation);
		historyPanelA.add(btnAnnotationNext);
		annotationPanel.add(historyPanelA);
		textAreaPanel.add(annotationPanel);
		
		//GeneAnnotation
		JPanel geneAnnotationPanel = new JPanel();
		geneAnnotationPanel.setLayout(new BoxLayout(geneAnnotationPanel, BoxLayout.Y_AXIS));
		JScrollPane geneScrollPane = new JScrollPane();
		geneScrollPane.setViewportView(geneAnnotationTextArea);
		TitledBorder geneAnnotationBorder = BorderFactory.createTitledBorder("" + mutation.getGene() + " Annotation (5000 characters max)");
		geneAnnotationBorder.setTitleFont(GUICommonTools.TAHOMA_BOLD_14);
		geneAnnotationPanel.setBorder(geneAnnotationBorder);
		geneAnnotationPanel.add(geneScrollPane);
		JPanel historyPanelGA = new JPanel();
		historyPanelGA.setLayout(new FlowLayout(FlowLayout.LEFT));
		historyPanelGA.add(btnGeneAnnotationPrevious);
		historyPanelGA.add(historyLabelGeneAnnotation);
		historyPanelGA.add(btnGeneAnnotationNext);
		geneAnnotationPanel.add(historyPanelGA);
		textAreaPanel.add(geneAnnotationPanel);
		
		//JLabel lblMaxCharacters = new JLabel("Max " + maxCharacters + " characters");
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonPane.add(okButton);
		buttonPane.add(cancelButton);
		
		contentPanel.add(itemPanel, BorderLayout.WEST);
		contentPanel.add(textAreaPanel, BorderLayout.CENTER);
		contentPanel.add(buttonPane, BorderLayout.SOUTH);
	}

	private void activateComponents(){
		okButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					AnnotationFrame.this.setVisible(false);
					updateRecord();
					parent.notifyAnnotationUpdated(currentAnnotation);
				}catch(Exception e){
					JOptionPane.showMessageDialog(AnnotationFrame.this, e.getMessage());
				}
			}
		});

		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AnnotationFrame.this.dispose();
			}
		});

		btnGeneAnnotationPrevious.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			 try {
				showGeneAnnotationPrevious();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(AnnotationFrame.this, e.getMessage());
			}
			}
		});
		
		btnGeneAnnotationNext.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			 try {
				showGeneAnnotationNext();
			 }catch (Exception e) {
				JOptionPane.showMessageDialog(AnnotationFrame.this, e.getMessage());
			 }
			}
		});

		btnAnnotationPrevious.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				try {
					showAnnotationPrevious();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(AnnotationFrame.this, e.getMessage());
				}
			}
		});
		
		btnAnnotationNext.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			 try {
				showAnnotationNext();
			 } catch (Exception e) {
				JOptionPane.showMessageDialog(AnnotationFrame.this, e.getMessage());
			 }
		   }
		});
		
		defaultStyledDocument.setDocumentFilter(new DocumentSizeFilter(maxCharacters));

		annotationTextArea.addMouseListener(new ContextMenuMouseListener());

	}

	private void openRecord() throws Exception{
		
		
		geneannotations = DatabaseCommands.getGeneAnnotationList(geneAnnotation.getGene());
		annotations = DatabaseCommands.getAnnotationList(currentAnnotation.getCoordinate());
		currentGeneAnnotationIndex = geneannotations.size() - 1; 
		currentAnnotationIndex = annotations.size() - 1; 
		
		annotationTextArea.setText(currentAnnotation.getCuration());
		historyLabelAnnotation.setText("Entered By: " + currentAnnotation.getEnteredBy()+"   Date: "+currentAnnotation.getEnterDate());
		pathogenicityComboBox.setSelectedItem(currentAnnotation.getClassification());
		mutationTypeComboBox.setSelectedItem(currentAnnotation.getSomatic());
		
		
		geneAnnotationTextArea.setText(geneAnnotation.getCuration());
		historyLabelGeneAnnotation.setText("Entered By: " + geneAnnotation.getEnteredBy()+"   Date: "+geneAnnotation.getEnterDate());
		
		btnAnnotationNext.setEnabled(false);
		btnGeneAnnotationNext.setEnabled(false);
		
		if ( annotations.size() <= 1) { 
			
			btnAnnotationPrevious.setEnabled(false); 
		}
		
		if ( geneannotations.size() <= 1) { 
			
			btnGeneAnnotationPrevious.setEnabled(false); 
		}

	
	}

	private void updateRecord() throws Exception{
		if(readOnly){
			return;
		}
		
		if(!SSHConnection.isSuperUser()){
			//readOnly should prevent this, but just in case
			JOptionPane.showMessageDialog(null, "Only authorized user can edit annotation");
			return;
		}
		
		if (! currentAnnotation.getCuration().equals(annotationTextArea.getText())) {
			
			currentAnnotation.setClassification(pathogenicityComboBox.getSelectedItem().toString());
			currentAnnotation.setSomatic(mutationTypeComboBox.getSelectedItem().toString());
			currentAnnotation.setCuration(annotationTextArea.getText());
			currentAnnotation.setEnterDate(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
			currentAnnotation.setEnteredBy(SSHConnection.getUserName());
			DatabaseCommands.setAnnotationCuration(currentAnnotation);
		}
		
		if ( ! geneAnnotation.getCuration().equals(geneAnnotationTextArea.getText())) {
			
			geneAnnotation.setCuration(geneAnnotationTextArea.getText());
			geneAnnotation.setEnterDate(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
			geneAnnotation.setEnteredBy(SSHConnection.getUserName());
			DatabaseCommands.setGeneAnnotationCuration(geneAnnotation);
		}
		
	}
    
	private void showGeneAnnotationPrevious() throws Exception{
        
		currentGeneAnnotationIndex = currentGeneAnnotationIndex - 1;
        GeneAnnotation currentGeneAnnotation = geneannotations.get(currentGeneAnnotationIndex);
        geneAnnotationTextArea.setText(currentGeneAnnotation.getCuration());
        btnGeneAnnotationNext.setEnabled(true);
		geneAnnotationTextArea.setEditable(false);
		geneAnnotationTextArea.setBackground(new Color(234,240,240));
		historyLabelGeneAnnotation.setText("Entered By: " + currentGeneAnnotation.getEnteredBy()+" Date: "+currentGeneAnnotation.getEnterDate());
		
			
		if (currentGeneAnnotationIndex == 0) {
			JOptionPane.showMessageDialog(this, "There is no previous gene annotation!");
			btnGeneAnnotationPrevious.setEnabled(false);		
		}	
	
	}

	private void showGeneAnnotationNext() throws Exception{
		
		currentGeneAnnotationIndex = currentGeneAnnotationIndex +1;
        GeneAnnotation currentGeneAnnotation = geneannotations.get(currentGeneAnnotationIndex);
        geneAnnotationTextArea.setText(currentGeneAnnotation.getCuration());
        btnGeneAnnotationPrevious.setEnabled(true);
		geneAnnotationTextArea.setEditable(false);
		geneAnnotationTextArea.setBackground(new Color(234,240,240));
		historyLabelGeneAnnotation.setText("Entered By: " + currentGeneAnnotation.getEnteredBy()+" Date: "+currentGeneAnnotation.getEnterDate());
	
		if ( currentGeneAnnotationIndex == geneannotations.size()-1) { 
			btnGeneAnnotationNext.setEnabled(false);
			geneAnnotationTextArea.setEditable(true);
			geneAnnotationTextArea.setBackground(new Color(255,255,255));
			JOptionPane.showMessageDialog(this, "This is the current gene annotation!");
		}
    }
	
	private void showAnnotationPrevious() throws Exception{
		
		currentAnnotationIndex = currentAnnotationIndex - 1;
        Annotation currentannotation = annotations.get(currentAnnotationIndex);
        annotationTextArea.setText(currentannotation.getCuration());
        btnAnnotationNext.setEnabled(true);
		annotationTextArea.setEditable(false);
		annotationTextArea.setBackground(new Color(234,240,240));
		historyLabelAnnotation.setText("Entered By: " + currentannotation.getEnteredBy()+" Date: "+currentannotation.getEnterDate());
		
			
		if (currentAnnotationIndex == 0) {
			JOptionPane.showMessageDialog(this, "There is no previous annotation!");
			btnAnnotationPrevious.setEnabled(false);		
		}	
	
	}

	private void showAnnotationNext() throws Exception{
		
		currentAnnotationIndex = currentAnnotationIndex +1;
        Annotation currentannotation = annotations.get(currentAnnotationIndex);
        annotationTextArea.setText(currentannotation.getCuration());
        btnAnnotationPrevious.setEnabled(true);
		annotationTextArea.setEditable(false);
		annotationTextArea.setBackground(new Color(234,240,240));
		historyLabelAnnotation.setText("Entered By: " + currentannotation.getEnteredBy()+" Date: "+ currentannotation.getEnterDate());
			
		
		if ( currentAnnotationIndex == annotations.size()-1) { 
			btnAnnotationNext.setEnabled(false);
			annotationTextArea.setEditable(true);
			annotationTextArea.setBackground(new Color(255,255,255));
			JOptionPane.showMessageDialog(this, "This is the current gene annotation!");
		}
        
	}

	private class DocumentSizeFilter extends DocumentFilter {
		int maxCharacters;
		boolean DEBUG = false;

		public DocumentSizeFilter(int maxChars) {
			maxCharacters = maxChars;
		}

		public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
			if (DEBUG) {
				System.out.println("in DocumentSizeFilter's insertString method");
			}

			//This rejects the entire insertion if it would make
			//the contents too long. Another option would be
			//to truncate the inserted string so the contents
			//would be exactly maxCharacters in length.
			if ((fb.getDocument().getLength() + str.length()) <= maxCharacters)
				super.insertString(fb, offs, str, a);
			else
				Toolkit.getDefaultToolkit().beep();
		}

		public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
			if (DEBUG) {
				System.out.println("in DocumentSizeFilter's replace method");
			}
			//This rejects the entire replacement if it would make
			//the contents too long. Another option would be
			//to truncate the replacement string so the contents
			//would be exactly maxCharacters in length.
			if ((fb.getDocument().getLength() + str.length()
			- length) <= maxCharacters)
				super.replace(fb, offs, length, str, a);
			else
				Toolkit.getDefaultToolkit().beep();
		}
	}
}
