package hmvv.gui.mutationlist;
import hmvv.gui.mutationlist.tables.CommonTable;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.model.MutationCommon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CosmicInfoFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private JButton yesButton;
    private JButton noButton;
    
    private CommonTable parent;
    private JScrollPane tableScrollPane;
    
    public CosmicInfoFrame( CommonTable parent, JScrollPane tableScrollPane) throws HeadlessException {
        this.parent = parent;
        this.tableScrollPane = tableScrollPane;
        
        createComponents();
        layoutComponents();
        activateComponents();

        pack();
        //Rectangle bounds = GUICommonTools.getBounds(parent2);
		//setSize((int)(bounds.width*.60), (int)(bounds.height*.60));
        setResizable(false);
        setLocationRelativeTo(parent);
        //setAlwaysOnTop(true);
        
    }
    
    private void createComponents(){

        yesButton = new JButton("Yes");
        yesButton.setEnabled(true);

        noButton = new JButton("No");
        getRootPane().setDefaultButton(noButton);

    }

    private void layoutComponents(){

        JPanel contentPanel = new JPanel();
        setContentPane(contentPanel);

        JPanel cosmicInfoPanel = new JPanel();
        cosmicInfoPanel.add(tableScrollPane);
        
        JPanel buttonPane = new JPanel();
        buttonPane.add(yesButton);
        buttonPane.add(noButton);

        contentPanel.add(cosmicInfoPanel);
        contentPanel.add(buttonPane, BorderLayout.SOUTH);
        
    }

    private void activateComponents() {
        yesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    //annotationTextArea.setText("");
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
