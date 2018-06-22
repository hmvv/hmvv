package hmvv.main;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import hmvv.gui.GUICommonTools;
import hmvv.gui.sampleList.SampleListFrame;
import hmvv.io.DatabaseCommands;
import hmvv.io.SSHConnection;

public class HMVVLoginFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel panel;
	private JTextField usernameTextField;
	private JPasswordField passwordTextField;
	private JButton loginButton;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		InputStream configurationStream = HMVVLoginFrame.class.getResourceAsStream("/config.ini");
		if(configurationStream == null){
			JOptionPane.showMessageDialog(null, "Could not locate configuration file. Application must be compiled with a config.ini file. Shutting down.");
			return;
		}
		
		try {
			boolean useLiveEnviroment = true;
			if(args.length > 0) {
				useLiveEnviroment = !args[0].equals("test");
			}
			Configurations.loadConfigurations(null, configurationStream, useLiveEnviroment);
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
	
	private void activateComponents(){
		loginButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				login();
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
		
		try{
			SampleListFrame sampleList = new SampleListFrame(HMVVLoginFrame.this, DatabaseCommands.getAllSamples());
			sampleList.setVisible(true);
			dispose();
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, "Could not construct sample list (" + e.getMessage() + ")." + " Please contact the system administrator.");
		}
	}
}
