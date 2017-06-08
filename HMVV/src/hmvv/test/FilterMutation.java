package hmvv.test;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import hmvv.gui.GUICommonTools;

public class FilterMutation extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField textField;
	private JComboBox<String> comboField;
	private JComboBox<String> comboOperation;
	private JTextArea textArea;
	private JRadioButton rdbtnAnd;
	private JRadioButton rdbtnOr;
	private List<String> t1;
	private List<String> t2;
	private List<String> t3;
	private List<String> t4;
	private List<String> t5;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FilterMutation frame = new FilterMutation();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FilterMutation() {
		List<String> t2 = Arrays.asList("gene", "dbSNPID", "type", "genotype", "altFreq", "readDP", "altReadDP", "chr", "pos", "ref", "alt", "consequence", "sift", "polyPhen", "sampleID");
		List<String> t3 = Arrays.asList("cosmicID");
		List<String> t4 = Arrays.asList("altCount", "totalCount", "altGlobalFreq", "altGlobalFreq", "americanFreq", "asianFreq", "afrFreq", "eurFreq");
		List<String> t5 = Arrays.asList("origin", "clinicalAllele", "clinicalSig", "clinicalAcc","pubmed");
		List<String> t1 = Arrays.asList("lastName", "firstName", "orderNumber", "assay");
		this.t1 = t1;
		this.t2 = t2;
		this.t3 = t3;
		this.t4 = t4;
		this.t5 = t5;
		ArrayList<String> total = new ArrayList<String>();
		for(String field : t1){
			total.add(field);
		}
		for(String field : t2){
			total.add(field);
		}
		for(String field : t3){
			total.add(field);
		}
		for(String field : t4){
			total.add(field);
		}
		for(String field : t5){
			total.add(field);
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 613, 569);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnFilter = new JButton("Filter");
		btnFilter.setFont(GUICommonTools.TAHOMA_BOLD_14);
		btnFilter.setBounds(335, 488, 89, 23);
		contentPane.add(btnFilter);
		
		JButton btnSaveFilter = new JButton("Save Filter");
		btnSaveFilter.setFont(GUICommonTools.TAHOMA_BOLD_14);
		btnSaveFilter.setBounds(441, 488, 103, 23);
		contentPane.add(btnSaveFilter);
		comboField = new JComboBox<String>();
		Collections.sort(total);
		for(String field : total){
			comboField.addItem(field);
		}
		

		comboField.setBounds(116, 30, 137, 20);
		contentPane.add(comboField);
		
		comboOperation = new JComboBox<String>();
		comboOperation.addItem("==");
		comboOperation.addItem("!=");
		comboOperation.addItem(">=");
		comboOperation.addItem(">");
		comboOperation.addItem("<=");
		comboOperation.addItem("<");
		comboOperation.setBounds(277, 30, 61, 20);
		contentPane.add(comboOperation);
		
		textField = new JTextField();
		textField.setBounds(356, 30, 152, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addTerm();
			}
		});
		btnAdd.setFont(GUICommonTools.TAHOMA_BOLD_14);
		btnAdd.setBounds(287, 101, 89, 23);
		contentPane.add(btnAdd);
		
		JButton btnCreateGroup = new JButton("Create Group");
		btnCreateGroup.setFont(GUICommonTools.TAHOMA_BOLD_14);
		btnCreateGroup.setBounds(386, 101, 143, 23);
		contentPane.add(btnCreateGroup);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBounds(62, 142, 467, 312);
		contentPane.add(textArea);
		
		rdbtnAnd = new JRadioButton("and");
		rdbtnAnd.setSelected(true);
		rdbtnAnd.setBounds(59, 29, 51, 23);
		contentPane.add(rdbtnAnd);
		
		rdbtnOr = new JRadioButton("or");
		rdbtnOr.setBounds(59, 57, 51, 23);
		contentPane.add(rdbtnOr);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnAnd);
		group.add(rdbtnOr);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				textArea.setText(null);
			}
		});
		
		btnClear.setFont(GUICommonTools.TAHOMA_BOLD_14);
		btnClear.setBounds(226, 488, 89, 23);
		contentPane.add(btnClear);
	}
	
	public void addTerm(){
		String field = comboField.getSelectedItem().toString();
		String operation = comboOperation.getSelectedItem().toString();
		String value = textField.getText();
		String logic = null;
		if (rdbtnAnd.isSelected()){
			logic = "and";
		}
		else{
			logic = "or";
		}
		String query = null;
		if(operation.equals("==") || operation.equals("!=")){
			query = String.format("%s %s '%s' %s ", getTable(field), operation, value, logic);
		}
		else{
			query = String.format("%s %s %s %s ", getTable(field), operation, value, logic);
		}
		textArea.append(query);
	}
	
	public String getTable(String field){
		String out = null;
		if(t1.contains(field)){
			out = "t1." + field;
		}
		else if(t2.contains(field)){
			out = "t2." + field;
		}
		else if(t3.contains(field)){
			out = "t3." + field;
		}
		else if(t4.contains(field)){
			out = "t4." + field;
		}
		else if(t5.contains(field)){
			out = "t5." + field;
		}
		
		return out;
	}
}
