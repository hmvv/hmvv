package hmvv.gui.mutationlist;
import hmvv.gui.GUICommonTools;
import hmvv.io.InternetCommands;
import hmvv.main.HMVVLoginFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DbVersionCheckFrame extends JDialog {

    private static final long serialVersionUID = 1L;

    private JButton okButton;
    private JLabel messageLabel;
    
    public DbVersionCheckFrame(HMVVLoginFrame parent) throws HeadlessException {
        super(parent, "Your current HMVV version is outdated.", ModalityType.APPLICATION_MODAL);
        
        createComponents();
        layoutComponents();
        activateComponents();

        pack();
        Rectangle bounds = GUICommonTools.getBounds(parent);
        setSize((int)(bounds.width* 0.9), (int)(bounds.height*.22));
        setResizable(false);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }
    
    private void createComponents(){

        okButton = new JButton("OK");
        okButton.setEnabled(true);

        messageLabel = new JLabel("Please click OK to be redirected to the HMVV downloads webpage.", SwingConstants.CENTER);
		messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        messageLabel.setVerticalAlignment(SwingConstants.CENTER); 
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);  
				       

    }

    private void layoutComponents(){

        JPanel contentPanel = new JPanel();
        setContentPane(contentPanel);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);

        JPanel messagePanel = new JPanel();
        messagePanel.add(messageLabel);

        
        contentPanel.add(messagePanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
    }

    private void activateComponents() {


        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {

                    try {
                        InternetCommands.downloadHMVV();
                        System.exit(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    
}
