package hmvv.main;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import hmvv.gui.GUICommonTools;
import hmvv.gui.adminFrames.CreateAssay;
import hmvv.gui.adminFrames.EnterSample;
import hmvv.gui.sampleList.SampleListFrame;
import hmvv.io.DatabaseCommands;
import hmvv.io.SSHConnection;

public class HMVVLoginFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel panel;
	private JTextField usernameTextField;
	private JPasswordField passwordTextField;
	private JButton loginButton;
	private JButton enterSampleButton;
	private JButton createAssayButton;
	private JButton viewResultsButton;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		File configFile = new File("config.ini");
		if(!configFile.exists()){
			configFile = new File("config-sample.ini");
		}
		if(!configFile.exists()){
			JOptionPane.showMessageDialog(null, "Could not locate config.ini. Shutting down.");
			return;
		}
		try {
			Configurations.loadConfigurations(null, configFile);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage() + "\nShutting down.");
			return;
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				HMVVLoginFrame window = new HMVVLoginFrame();
				window.setVisible(true);
				window.setResizable(false);
			}
		});
	}

	/**
	 * Create the application.
	 */
	public HMVVLoginFrame() {
		super("HMVV Gateway");		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createComponents();
		layoutLoginComponents();
		activateComponents();
	}

	private void createComponents(){
		panel = new JPanel();
		loginButton = new JButton("Login");
		usernameTextField = new JTextField();
		passwordTextField = new JPasswordField();
		
		enterSampleButton = new JButton("Enter Sample");
		createAssayButton = new JButton();
		viewResultsButton = new JButton("View Results");
	}
	
	private void layoutLoginComponents(){
		setSize(440, 345);
		panel.setLayout(null);
		
		JLabel lblUsername = new JLabel("UserName");
		lblUsername.setFont(GUICommonTools.TAHOMA_BOLD_14);
		lblUsername.setBounds(72, 71, 101, 33);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(GUICommonTools.TAHOMA_BOLD_14);
		lblPassword.setBounds(72, 145, 74, 14);
		
		usernameTextField.setBounds(160, 74, 142, 30);
		usernameTextField.setColumns(10);
		
		passwordTextField.setBounds(160, 139, 142, 30);
		
		loginButton.setFont(GUICommonTools.TAHOMA_BOLD_14);
		loginButton.setBounds(181, 214, 89, 23);
		
		panel.add(lblUsername);
		panel.add(usernameTextField);
		panel.add(lblPassword);
		panel.add(passwordTextField);
		panel.add(loginButton);
		add(panel);
		getRootPane().setDefaultButton(loginButton);
		
		Rectangle bounds = GUICommonTools.getBounds(this);
		setLocation(bounds.width/2-getSize().width/2, bounds.height/2-getSize().height/2);
	}
	
	private void layoutPostLoginComponents(){
		panel.removeAll();
		int xLocation = 125;
		viewResultsButton.setFont(GUICommonTools.TAHOMA_BOLD_14);
		viewResultsButton.setBounds(xLocation, 20, 166, 68);
		
		enterSampleButton.setFont(GUICommonTools.TAHOMA_BOLD_14);
		enterSampleButton.setBounds(xLocation, 115, 166, 68);
		
		createAssayButton.setLayout(new BorderLayout());
		createAssayButton.setEnabled(SSHConnection.isSuperUser());
		JLabel label1 = new JLabel("Create Assay");
		label1.setHorizontalAlignment(JLabel.CENTER);
		JLabel label2 = new JLabel("(Super User Only)");
		label2.setHorizontalAlignment(JLabel.CENTER);
		label1.setFont(GUICommonTools.TAHOMA_BOLD_14);
		label2.setFont(GUICommonTools.TAHOMA_BOLD_12);
		createAssayButton.add(BorderLayout.NORTH, label1);
		createAssayButton.add(BorderLayout.CENTER, label2);
		
		createAssayButton.setFont(GUICommonTools.TAHOMA_BOLD_14);
		createAssayButton.setBounds(xLocation, 210, 166, 68);
		createAssayButton.setEnabled(SSHConnection.isSuperUser());
		
		panel.add(viewResultsButton);
		panel.add(enterSampleButton);
		panel.add(createAssayButton);
		
		panel.revalidate();
		panel.repaint();
	}
	
	private void activateComponents(){
		loginButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				login();
			}			
		});
		
		viewResultsButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					SampleListFrame sampleList = new SampleListFrame(HMVVLoginFrame.this, DatabaseCommands.getSamplesByAssay("All"));
					sampleList.setVisible(true);
				}catch(Exception e){
					JOptionPane.showMessageDialog(HMVVLoginFrame.this, e.getClass().toString() + " - " + e.getMessage());
				}
			}
		});
		
		enterSampleButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				EnterSample sampleEnter = new EnterSample(HMVVLoginFrame.this);
				sampleEnter.setVisible(true);
			}
		});
		
		createAssayButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(SSHConnection.isSuperUser()){
					CreateAssay createAssay = new CreateAssay(HMVVLoginFrame.this);
					createAssay.setVisible(true);
				}else{
					JOptionPane.showMessageDialog(HMVVLoginFrame.this, "Only authorized users can create an assay");
				}
			}
		});
	}
	
	private void login(){
		String userName= usernameTextField.getText();
		String passwd = new String(passwordTextField.getPassword());
		
		try{
			SSHConnection.connect(userName, passwd);
		} catch(Exception e){
			if(e.getMessage().equals("Auth fail")){
				JOptionPane.showMessageDialog(this, "Incorrect username or password. Please try again.");
			}else{
				JOptionPane.showMessageDialog(this, "Login failed: " + e.getMessage());
			}
			return;
		}
		
		try {
			DatabaseCommands.connect(userName);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Database connection failed (" + e.getMessage() + ")." + " Please contact the system administrator.");
			return;
		}
		
		layoutPostLoginComponents();
	}
}
