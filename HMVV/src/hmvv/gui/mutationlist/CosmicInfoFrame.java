package hmvv.gui.mutationlist;
import hmvv.gui.GUICommonTools;
import hmvv.gui.mutationlist.CosmicInfoPopup.CosmicInfo;
import hmvv.io.DatabaseCommands_Mutations;
import hmvv.io.InternetCommands;
import hmvv.main.HMVVDefectReportFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class CosmicInfoFrame extends JDialog {

    private static final long serialVersionUID = 1L;

    private JButton yesButton;
    private JButton noButton;
    private JScrollPane tableScrollPane;
    private JLabel noComicMatchLabel1;
    private JLabel noComicMatchLabel2;
    private ArrayList<CosmicInfo> comsicInfoList;
    
    private JTable table;
    private Boolean cosmicIdMatch;
    
    public CosmicInfoFrame(JDialog parent, JTable table, ArrayList<CosmicInfo> comsicInfoList, Boolean cosmicIdMatch) throws HeadlessException {
        super(parent, "Open CosmicID's in Web Browser? Be sure to use GRCh37 on the COSMIC website.", ModalityType.APPLICATION_MODAL);
        this.table = table;
        this.comsicInfoList = comsicInfoList;
        this.cosmicIdMatch = cosmicIdMatch;
        
        createComponents();
        layoutComponents();
        activateComponents();

        pack();
        Rectangle bounds = GUICommonTools.getBounds(parent);
        setSize((int)(bounds.width), (int)(bounds.height*.60));
        setResizable(true);
        setLocationRelativeTo(parent);
    }
    
    private void createComponents(){

        yesButton = new JButton("Yes");
        yesButton.setEnabled(true);

        noButton = new JButton("No");
        getRootPane().setDefaultButton(noButton);

        tableScrollPane = new JScrollPane(table);
		tableScrollPane.setPreferredSize(new Dimension(1000,350));

        noComicMatchLabel1 = new JLabel("There are no exact COSMIC ID matches available for the selected variant.", SwingConstants.CENTER);
		noComicMatchLabel1.setFont(GUICommonTools.TAHOMA_BOLD_14);
        noComicMatchLabel1.setForeground(Color.RED);
        noComicMatchLabel1.setVerticalAlignment(SwingConstants.CENTER); 
        noComicMatchLabel1.setHorizontalAlignment(SwingConstants.CENTER); 
        noComicMatchLabel1.setVisible(false);   
        
        noComicMatchLabel2 = new JLabel("The following options only represent close matches.", SwingConstants.CENTER);
		noComicMatchLabel2.setFont(GUICommonTools.TAHOMA_BOLD_14);
        noComicMatchLabel2.setForeground(Color.RED);
        noComicMatchLabel2.setVerticalAlignment(SwingConstants.CENTER); 
        noComicMatchLabel2.setHorizontalAlignment(SwingConstants.CENTER); 
        noComicMatchLabel2.setVisible(false);    
				
        if(!cosmicIdMatch) {
            
            noComicMatchLabel1.setVisible(true);
            noComicMatchLabel2.setVisible(true);
        }
       

    }

    private void layoutComponents(){

        JPanel contentPanel = new JPanel();
        setContentPane(contentPanel);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        JPanel tablePanel = new JPanel();
        tablePanel.add(tableScrollPane);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);

        JPanel messagePanel1 = new JPanel();
        messagePanel1.add(noComicMatchLabel1);

        JPanel messagePanel2 = new JPanel();
        messagePanel2.add(noComicMatchLabel2);
        
        contentPanel.add(messagePanel1, BorderLayout.CENTER);
        contentPanel.add(messagePanel2, BorderLayout.CENTER);
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
    }

    private void activateComponents() {


        yesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    for(CosmicInfo cosmicInfo : comsicInfoList){
				
                        		 if(cosmicInfo.openItem) {
                        			String MutationURL = DatabaseCommands_Mutations.getMutationURL(cosmicInfo.cosmicID);
                        		 	InternetCommands.searchCosmic(MutationURL);
                                    CosmicInfoFrame.this.dispose();
                        		 }
                        	}
                } catch (Exception e) {
                	HMVVDefectReportFrame.showHMVVDefectReportFrame(CosmicInfoFrame.this, e);
                }
            }
        });

        noButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    CosmicInfoFrame.this.dispose();

                } catch (Exception e) {
                	HMVVDefectReportFrame.showHMVVDefectReportFrame(CosmicInfoFrame.this, e);
                }

            }
        });
    }
}
