package hmvv.gui.sampleList;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import hmvv.gui.GUICommonTools;

public class EditNoteDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private JPanel contentPanel;
	private JTextField textField;
	private JButton okButton;
	private JButton cancelButton;

	/**
	 * Create the dialog.
	 */
	public EditNoteDialog(String name, String currentNote) {
		setBounds(100, 100, 443, 153);
		getContentPane().setLayout(new BorderLayout());
		
		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JLabel lblEditNoteFor = new JLabel("Edit Note for Sample:");
		lblEditNoteFor.setFont(GUICommonTools.TAHOMA_BOLD_13);
		lblEditNoteFor.setBounds(10, 11, 176, 27);
		contentPanel.add(lblEditNoteFor);

		textField = new JTextField();
		textField.setBounds(10, 49, 193, 20);
		contentPanel.add(textField);
		textField.setColumns(10);
		textField.setText(currentNote);

		JLabel lblLastnamefirstname = new JLabel(name);
		lblLastnamefirstname.setFont(GUICommonTools.TAHOMA_BOLD_13);
		lblLastnamefirstname.setBounds(153, 17, 152, 14);
		contentPanel.add(lblLastnamefirstname);


		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		buttonPanel.add(okButton);
		getRootPane().setDefaultButton(okButton);

		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				EditNoteDialog.this.setVisible(false);
			}

		});
		buttonPanel.add(cancelButton);
	}
	
	public String getNote(){
		return textField.getText();
	}

	public void addConfirmListener(ActionListener listener) {
		okButton.addActionListener(listener);
	}
	
	public void addCancelListener(ActionListener listener) {
		cancelButton.addActionListener(listener);
	}
}
