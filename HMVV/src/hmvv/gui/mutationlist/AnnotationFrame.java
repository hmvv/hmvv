package hmvv.gui.mutationlist;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.DocumentFilter;

import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.tables.BasicTable;
import hmvv.gui.sampleList.ContextMenuMouseListener;
import hmvv.io.DatabaseCommands;
import hmvv.io.SSHConnection;
import hmvv.model.Annotation;

public class AnnotationFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private BasicTable parent;
	
	private JPanel contentPanel;
	private JButton okButton;
	private JButton cancelButton;
	
	private JComboBox<String> pathogenicityComboBox;
	private JComboBox<String> mutationTypeComboBox;
	
	private JTextArea annotationTextArea;
	private int maxCharacters = 5000;
	private DefaultStyledDocument defaultStyledDocument;
	
	private JLabel historyLabel;
	private JLabel labelCoordinateInfo;
	
	private Boolean readOnly;
	
	private Annotation currentAnnotation;
	private JLabel lblNewLabel;
	
	/**
	 * Create the dialog.
	 */
	public AnnotationFrame(Boolean readOnly, Annotation annotation, BasicTable parent) {
		super("Annotation");
		this.readOnly = readOnly;
		this.currentAnnotation = annotation;
		this.parent = parent;
		
		createComponents();
		layoutComponents();
		activateComponents();
		
		if(readOnly){
			setTitle("Annotation (read only)");
			annotationTextArea.setEditable(false);
			pathogenicityComboBox.setEnabled(false);
			mutationTypeComboBox.setEnabled(false);
		}
		
		openRecord();
	}

	private void createComponents(){
		contentPanel = new JPanel();
		
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

		annotationTextArea = new JTextArea();
		annotationTextArea.setWrapStyleWord(true);
		annotationTextArea.setLineWrap(true);
		annotationTextArea.setText(currentAnnotation.getCuration());
		
		historyLabel = new JLabel(currentAnnotation.getUpdateStatus());
		labelCoordinateInfo = new JLabel("New label");

		okButton = new JButton("OK");
		getRootPane().setDefaultButton(okButton);

		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");

		lblNewLabel = new JLabel(maxCharacters + " characters remaining");
		defaultStyledDocument = new DefaultStyledDocument();

		//annotationTextArea.setDocument(defaultStyledDocument);

		labelCoordinateInfo.setText(currentAnnotation.getCoordinate().getCoordinateAsString());
	}

	private void layoutComponents(){
		setBounds(100, 100, 554, 504);
		getContentPane().setLayout(new BorderLayout());

		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		pathogenicityComboBox.setBounds(193, 65, 198, 26);
		contentPanel.add(pathogenicityComboBox);

		mutationTypeComboBox.setBounds(191, 115, 200, 28);
		contentPanel.add(mutationTypeComboBox);

		JLabel lblClassification = new JLabel("Classification");
		lblClassification.setBounds(49, 66, 102, 21);
		lblClassification.setFont(GUICommonTools.TAHOMA_BOLD_14);
		contentPanel.add(lblClassification);

		JLabel lblSomatic = new JLabel("Somatic");
		lblSomatic.setBounds(49, 117, 102, 21);
		lblSomatic.setFont(GUICommonTools.TAHOMA_BOLD_14);
		contentPanel.add(lblSomatic);

		JLabel lblAnnotation = new JLabel("Annotation");
		lblAnnotation.setBounds(49, 224, 102, 21);
		lblAnnotation.setFont(GUICommonTools.TAHOMA_BOLD_14);
		contentPanel.add(lblAnnotation);

		lblNewLabel.setBounds(352, 407, 176, 14);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(169, 190, 328, 206);
		scrollPane.setViewportView(annotationTextArea);
		contentPanel.add(scrollPane);

		contentPanel.add(lblNewLabel);

		JLabel lblMaxCharacters = new JLabel("Max " + maxCharacters + " characters");
		lblMaxCharacters.setBounds(31, 256, 141, 14);
		contentPanel.add(lblMaxCharacters);

		historyLabel.setBounds(20, 401, 258, 26);
		contentPanel.add(historyLabel);

		JLabel lblCoordinate = new JLabel("Coordinate");
		lblCoordinate.setFont(GUICommonTools.TAHOMA_BOLD_14);
		lblCoordinate.setBounds(49, 23, 102, 21);
		contentPanel.add(lblCoordinate);

		labelCoordinateInfo.setFont(GUICommonTools.TAHOMA_BOLD_14);
		labelCoordinateInfo.setBounds(193, 23, 198, 19);
		contentPanel.add(labelCoordinateInfo);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		buttonPane.add(okButton);
		buttonPane.add(cancelButton);
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
				}finally{
					closeRecord();
				}
			}
		});

		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				closeRecord();
				AnnotationFrame.this.dispose();
			}
		});

		defaultStyledDocument.setDocumentFilter(new DocumentSizeFilter(maxCharacters));
		defaultStyledDocument.addDocumentListener(new DocumentListener(){
			@Override
			public void changedUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				update();
			}

			private void update(){
				lblNewLabel.setText((maxCharacters - defaultStyledDocument.getLength()) + " characters remaining");
			}
		});

		annotationTextArea.addMouseListener(new ContextMenuMouseListener());

		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				//Called if the user clicks the "X" to close the window
				closeRecord();
			}
		});
	}

	private void openRecord(){
		try {
			if(!readOnly){
				DatabaseCommands.setAnnotationStatus(Annotation.STATUS.open, currentAnnotation);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	private void closeRecord(){
		try {
			if(!readOnly){
				DatabaseCommands.setAnnotationStatus(Annotation.STATUS.close, currentAnnotation);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e);
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
		
		currentAnnotation.setClassification(pathogenicityComboBox.getSelectedItem().toString());
		currentAnnotation.setSomatic(mutationTypeComboBox.getSelectedItem().toString());
		currentAnnotation.setCuration(annotationTextArea.getText());
		currentAnnotation.setUpdateStatus(getUpdateStatus());
		
		if(!currentAnnotation.isAnnotationSet()){
			DatabaseCommands.deleteAnnotation(currentAnnotation);
		}else{
			DatabaseCommands.updateAnnotation(currentAnnotation);			
		}
	}

	private String getUpdateStatus(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Calendar cal = Calendar.getInstance();
		String date = dateFormat.format(cal.getTime());
		String updateStatus = String.format("Last update: %s by %s", date, SSHConnection.getUserName());
		return updateStatus;
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
