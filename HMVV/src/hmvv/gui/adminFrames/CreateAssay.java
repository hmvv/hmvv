package hmvv.gui.adminFrames;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import hmvv.gui.GUICommonTools;
import hmvv.io.DatabaseCommands;

public class CreateAssay extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField textField;
	private JCheckBox chckbxMiseq;
	private JCheckBox chckbxNextseq;
	private JCheckBox chckbxPgm;
	private JCheckBox chckbxProton;

	/**
	 * Create the frame.
	 */
	public CreateAssay(Component parent) {
		super("Create Assay");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 468, 271);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblAssayName = new JLabel("Assay Name");
		lblAssayName.setFont(GUICommonTools.TAHOMA_BOLD_13);
		lblAssayName.setBounds(45, 42, 95, 20);
		contentPane.add(lblAssayName);
		
		textField = new JTextField();
		textField.setBounds(150, 43, 114, 19);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblCheckAllInstruments = new JLabel("Check all instruments that this assay will be run on");
		lblCheckAllInstruments.setFont(GUICommonTools.TAHOMA_BOLD_13);
		lblCheckAllInstruments.setBounds(45, 73, 347, 20);
		contentPane.add(lblCheckAllInstruments);
		
		chckbxMiseq = new JCheckBox("miseq");
		chckbxMiseq.setFont(GUICommonTools.TAHOMA_BOLD_13);
		chckbxMiseq.setBounds(43, 107, 69, 23);
		contentPane.add(chckbxMiseq);
		
		chckbxNextseq = new JCheckBox("nextseq");
		chckbxNextseq.setFont(GUICommonTools.TAHOMA_PLAIN_13);
		chckbxNextseq.setBounds(114, 107, 82, 23);
		contentPane.add(chckbxNextseq);
		
		chckbxPgm = new JCheckBox("pgm");
		chckbxPgm.setFont(GUICommonTools.TAHOMA_PLAIN_13);
		chckbxPgm.setBounds(198, 107, 60, 23);
		contentPane.add(chckbxPgm);
		
		chckbxProton = new JCheckBox("proton");
		chckbxProton.setFont(GUICommonTools.TAHOMA_PLAIN_13);
		chckbxProton.setBounds(260, 107, 74, 23);
		contentPane.add(chckbxProton);
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				createAssayInDatabase();
			}
		});
		
		btnSubmit.setFont(GUICommonTools.TAHOMA_BOLD_13);
		btnSubmit.setBounds(175, 183, 89, 23);
		contentPane.add(btnSubmit);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				CreateAssay.this.setVisible(false);
			}
		});
		btnCancel.setFont(GUICommonTools.TAHOMA_BOLD_13);
		btnCancel.setBounds(284, 183, 89, 23);
		contentPane.add(btnCancel);
		
		setLocationRelativeTo(parent);
	}
	
	private void createAssayInDatabase(){
		String assay = textField.getText();
		try{
			if(chckbxMiseq.isSelected()){
				DatabaseCommands.createAssay("miseq", assay);
			}
			if(chckbxNextseq.isSelected()){
				DatabaseCommands.createAssay("nextseq", assay);
			}
			if(chckbxPgm.isSelected()){
				DatabaseCommands.createAssay("pgm", assay);
			}
			if(chckbxProton.isSelected()){
				DatabaseCommands.createAssay("proton", assay);
			}
			JOptionPane.showMessageDialog(this, "Done");
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(this, e);
		}
	}
}
