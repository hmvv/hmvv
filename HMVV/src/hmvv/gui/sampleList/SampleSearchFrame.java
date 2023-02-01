package hmvv.gui.sampleList;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;

import hmvv.gui.GUICommonTools;
import hmvv.io.DatabaseCommands;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.main.HMVVFrame;
import hmvv.model.Assay;
import hmvv.model.Sample;


public class SampleSearchFrame extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private JTextField textOrderNumber;
	private JTextField textPathNumber;
	private JTextField textlastName;
	private JTextField textfirstName;
	private DatePicker datePickerFrom;
	private DatePicker datePickerTo;
	private JComboBox<Assay> assayComboBox;
	
	private JButton okButton;
	private JButton cancelButton;
	
	/**
	 * Create the dialog.
	 */
	public SampleSearchFrame(HMVVFrame parent) {
		super(parent, "Sample Search", ModalityType.APPLICATION_MODAL);
		JPanel contentPanel = new JPanel();
		
		setBounds(100, 100, 438, 348);
		setLocationRelativeTo(parent);
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		datePickerFrom = new DatePicker();
		datePickerFrom.setBounds(140, 188, 183, 25);
		contentPanel.add(datePickerFrom);
		
		datePickerTo = new DatePicker();
		datePickerTo.setBounds(140, 231, 183, 25);
		contentPanel.add(datePickerTo);
		
		contentPanel.setLayout(null);
		{
			JLabel lblAssay = new JLabel("Assay");
			lblAssay.setBounds(95, 39, 40, 17);
			lblAssay.setFont(GUICommonTools.TAHOMA_BOLD_14);
			contentPanel.add(lblAssay);
		}
		{
			JLabel lblOrderNumber = new JLabel("Order Number");
			lblOrderNumber.setBounds(35, 69, 100, 17);
			lblOrderNumber.setFont(GUICommonTools.TAHOMA_BOLD_14);
			contentPanel.add(lblOrderNumber);
		}
		{
			textOrderNumber = new JTextField();
			textOrderNumber.setBounds(140, 67, 259, 20);
			contentPanel.add(textOrderNumber);
			textOrderNumber.setColumns(10);
		}
		{
			JLabel lblPathNumber = new JLabel("Path Number");
			lblPathNumber.setBounds(43, 99, 100, 17);
			lblPathNumber.setFont(GUICommonTools.TAHOMA_BOLD_14);
			contentPanel.add(lblPathNumber);
		}
		{
			textPathNumber = new JTextField();
			textPathNumber.setBounds(140, 97, 259, 20);
			contentPanel.add(textPathNumber);
			textPathNumber.setColumns(10);
		}
		{
			JLabel lblLastName = new JLabel("Last Name");
			lblLastName.setBounds(62, 129, 73, 17);
			lblLastName.setFont(GUICommonTools.TAHOMA_BOLD_14);
			contentPanel.add(lblLastName);
		}
		{
			textlastName = new JTextField();
			textlastName.setBounds(140, 127, 259, 20);
			contentPanel.add(textlastName);
			textlastName.setColumns(10);
		}
		{
			JLabel lblFirstName = new JLabel("First Name");
			lblFirstName.setBounds(61, 159, 74, 17);
			lblFirstName.setFont(GUICommonTools.TAHOMA_BOLD_14);
			contentPanel.add(lblFirstName);
		}
		{
			textfirstName = new JTextField();
			textfirstName.setBounds(140, 157, 259, 20);
			contentPanel.add(textfirstName);
			textfirstName.setColumns(10);
		}
		{
			JLabel lblRunDate = new JLabel("Run Date");
			lblRunDate.setBounds(21, 207, 64, 17);
			lblRunDate.setFont(GUICommonTools.TAHOMA_BOLD_14);
			contentPanel.add(lblRunDate);
		}
		{
			JLabel lblTo = new JLabel("TO");
			lblTo.setFont(GUICommonTools.TAHOMA_BOLD_13);
			lblTo.setBounds(95, 231, 27, 17);
			contentPanel.add(lblTo);
		}

		assayComboBox = new JComboBox<Assay>();
		assayComboBox.setBounds(140, 39, 183, 20);
		assayComboBox.addItem(Assay.getAssay("All"));
		
		try {
			for(Assay assay : DatabaseCommands.getAllAssays()){
				assayComboBox.addItem(assay);
			}
		} catch (Exception e) {
			HMVVDefectReportFrame.showHMVVDefectReportFrame(this, e);
			return;
		}
		
		contentPanel.add(assayComboBox);

		JLabel lblFrom = new JLabel("FROM");
		lblFrom.setFont(GUICommonTools.TAHOMA_BOLD_13);
		lblFrom.setBounds(95, 188, 35, 17);
		contentPanel.add(lblFrom);
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		okButton = new JButton("Search");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		
		cancelButton = new JButton("Cancel");
		buttonPane.add(cancelButton);
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				SampleSearchFrame.this.setVisible(false);
			}
		});
		
		DocumentListener documentListener = new DocumentListener(){
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateOkButton();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateOkButton();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				updateOkButton();
			}
		};
		
		DateChangeListener dateChangeListener = new DateChangeListener(){
			@Override
			public void dateChanged(DateChangeEvent arg0) {
				updateOkButton();
			}
		};
		textOrderNumber.getDocument().addDocumentListener(documentListener);
		textPathNumber.getDocument().addDocumentListener(documentListener);
		textlastName.getDocument().addDocumentListener(documentListener);
		textfirstName.getDocument().addDocumentListener(documentListener);
		datePickerFrom.addDateChangeListener(dateChangeListener);
		datePickerTo.addDateChangeListener(dateChangeListener);
		updateOkButton();//fire the event to set the ok button appropriately
	}
	
	private void updateOkButton(){
		if(textOrderNumber.getText().length() == 0 && textPathNumber.getText().length() == 0 && textlastName.getText().length() == 0 && textfirstName.getText().length() == 0){
			if(datePickerFrom.getText().length() == 0 && datePickerTo.getText().length() == 0){
				okButton.setEnabled(false);
			}else{
				okButton.setEnabled(true);
			}
		}else{
			okButton.setEnabled(true);
		}
	}
	
	
	public boolean include(Sample sample){
		Assay assay = (Assay)assayComboBox.getSelectedItem();
		if(!assay.assayName.equals("All") && !assay.equals(sample.assay)){
			return false;
		}
		
		String orderNumber = textOrderNumber.getText();
		if(exclude(orderNumber, sample.getOrderNumber())){
			return false;
		}
		
		String pathNumber = textPathNumber.getText();
		if(exclude(pathNumber, sample.getPathNumber())){
			return false;
		}
		
		String lastName = textlastName.getText();
		if(exclude(lastName, sample.getLastName())){
			return false;
		}
		
		String firstName = textfirstName.getText();
		if(exclude(firstName, sample.getFirstName())){
			return false;
		}
		
		String dateStringFrom = datePickerFrom.getDateStringOrSuppliedString("NA");
		if(!dateStringFrom.equals("NA")){
			try {
				Date filterDate = GUICommonTools.shortDateFormat.parse(dateStringFrom);
				Date sampleDate = GUICommonTools.shortDateFormat.parse(sample.runDate);
				if(filterDate.after(sampleDate)){
					return false;
				}
			} catch (ParseException e) {
				return false;
			}
		}
		
		String dateStringTo = datePickerTo.getDateStringOrSuppliedString("NA");
		if(!dateStringTo.equals("NA")){
			try {
				Date filterDate = GUICommonTools.shortDateFormat.parse(dateStringTo);
				Date sampleDate = GUICommonTools.shortDateFormat.parse(sample.runDate);
				if(filterDate.before(sampleDate)){
					return false;
				}
			} catch (ParseException e) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean exclude(String searchTerm, String sampleTerm){
		if(searchTerm.equals("")){
			return false;
		}
		if(sampleTerm.toLowerCase().contains(searchTerm.toLowerCase())){
			return false;
		}
		return true;
	}
	
	public void addConfirmListener(ActionListener listener) {
		okButton.addActionListener(listener);
	}
	
	public void addCancelListener(ActionListener listener) {
		cancelButton.addActionListener(listener);
	}
	
	public String getAssay(){
		return assayComboBox.getSelectedItem().toString();
	}
}
