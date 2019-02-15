package hmvv.main;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import hmvv.io.InternetCommands;
import hmvv.io.SSHConnection;
import hmvv.io.LIS.LISConnection;

public class HMVVLoginFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel panel;
	private JTextField usernameTextField;
	private JPasswordField passwordTextField;
	private JButton loginButton;
	private JLabel lblhmvv_version;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		try {
            String theme = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
            javax.swing.UIManager.setLookAndFeel(theme);
        } catch(Exception e){
            //Error loading windows theme. Using Java default.
        }
		
		InputStream configurationStream = HMVVLoginFrame.class.getResourceAsStream("/config.ini");
		if(configurationStream == null){
			JOptionPane.showMessageDialog(null, "Could not locate configuration file. Application must be compiled with a config.ini file. Shutting down.");
			return;
		}
		
		try {
			Configurations.loadLocalConfigurations(null, configurationStream);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage() + "\nShutting down.");
			return;
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				HMVVLoginFrame window = new HMVVLoginFrame();
				
				window.setVisible(true);
				window.setResizable(false);
				
				if(args.length == 2) {
					String providedUsername = args[0];
					String providedPassword = args[1];
					window.usernameTextField.setText(providedUsername);
					window.passwordTextField.setText(providedPassword);
					window.loginButton.doClick();
				}
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
		setSize(500, 450);
		panel.setLayout(null);
		if(Configurations.isTestEnvironment()) {
			panel.setBackground(Configurations.TEST_ENV_COLOR);
		}
		
		JLabel lblhmvv = new JLabel("Houston Methodist Variant Viewer");
		lblhmvv.setFont(GUICommonTools.TAHOMA_BOLD_20);
		lblhmvv.setBounds(75, 100, 1000, 30);

		lblhmvv_version = new JLabel("<html> <a style=\"text-decoration:none\" href=\"\">version 3.0 </a></html>");
		lblhmvv_version.setFont(GUICommonTools.TAHOMA_BOLD_11);
		lblhmvv_version.setBounds(215, 125, 100, 30);

		JLabel lblUsername = new JLabel("Username");
		lblUsername.setFont(GUICommonTools.TAHOMA_BOLD_14);
		lblUsername.setBounds(125, 200, 100, 30);
		usernameTextField.setBounds(215, 200, 140, 30);
		usernameTextField.setColumns(10);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(GUICommonTools.TAHOMA_BOLD_14);
		lblPassword.setBounds(125, 250, 100, 30);
		passwordTextField.setBounds(215, 250, 140, 30);

		loginButton.setFont(GUICommonTools.TAHOMA_BOLD_14);
		loginButton.setBounds(125, 325, 230, 30);

		panel.add(lblhmvv);
		panel.add(lblhmvv_version);
		panel.add(lblUsername);
		panel.add(usernameTextField);
		panel.add(lblPassword);
		panel.add(passwordTextField);
		panel.add(loginButton);
		add(panel);
		getRootPane().setDefaultButton(loginButton);

		Rectangle bounds = GUICommonTools.getScreenBounds();
		setLocation(bounds.width/2-getSize().width/2, bounds.height/2-getSize().height/2);
	}
	
	private void activateComponents(){
		loginButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					public void run() {
						setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						loginButton.setEnabled(false);
						usernameTextField.setEnabled(false);
						passwordTextField.setEnabled(false);
						
						login();
						
						loginButton.setEnabled(true);
						loginButton.setText("Login");
						usernameTextField.setEnabled(true);
						passwordTextField.setEnabled(true);
						setCursor(Cursor.getDefaultCursor());
					}
				}).start();
			}			
		});

		lblhmvv_version.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					InternetCommands.hmvvHome();
				} catch (Exception e1) {
					HMVVDefectReportFrame.showHMVVDefectReportFrame(HMVVLoginFrame.this, e1);
				}
			}
		});
		lblhmvv_version.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
	
	private void login(){
		String userName = usernameTextField.getText();
		String passwd = new String(passwordTextField.getPassword());
		try{
			loginButton.setText("Logging in...");
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
			loginButton.setText("Getting configuration...");
			Configurations.loadServerConfigurations();
		} catch (Exception e) {
			HMVVDefectReportFrame.showHMVVDefectReportFrame(this, e, "Server configuration load failed. Please contact the system administrator.");
			return;
		}
		
		try {
			loginButton.setText("Connecting to database...");
			DatabaseCommands.connect();
		} catch (Exception e) {
			HMVVDefectReportFrame.showHMVVDefectReportFrame(this, e, "Database connection failed. Please contact the system administrator.");
			return;
		}
		
		try {
			loginButton.setText("Connecting to LIS...");
			LISConnection.connect();
		} catch (Exception e) {
			HMVVDefectReportFrame.showHMVVDefectReportFrame(this, e, "LIS connection failed. Please contact the system administrator.");
			//Don't return, as LIS connection is not critical
		}
		
		try{
			loginButton.setText("Loading Sample list...");
			SampleListFrame sampleList = new SampleListFrame(HMVVLoginFrame.this, DatabaseCommands.getAllSamples());
			sampleList.setVisible(true);
			dispose();
		}catch(Exception e){
			HMVVDefectReportFrame.showHMVVDefectReportFrame(this, e, "Could not construct sample list. Please contact the system administrator.");
		}
	}
}
