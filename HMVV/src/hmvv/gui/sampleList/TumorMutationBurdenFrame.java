package hmvv.gui.sampleList;

import hmvv.gui.GUICommonTools;
import hmvv.io.DatabaseCommands;
import hmvv.model.ExomeTMB;
import hmvv.model.Sample;

import javax.swing.*;
import java.awt.*;

public class TumorMutationBurdenFrame extends JDialog {

    private static final long serialVersionUID = 1L;

    private JButton txtSample;
    private JButton txtTotalVariants;
    private JButton txtTMBScore;
    private JButton txtTMBGroup;

    private Sample sample;

    public TumorMutationBurdenFrame (SampleListFrame parent,Sample sample) throws Exception{
        super(parent, "Sample Amplicons");
        this.sample = sample;

        Rectangle bounds = GUICommonTools.getBounds(parent);
        setSize((int)(bounds.width*.5), (int)(bounds.height*.5));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);

        createComponents();
        layoutComponents();
        setLocationRelativeTo(parent);

        String title = "Tumor Mutation Burden Result - " + sample.getLastName() + "," + sample.getFirstName() +
                " (runID = " + sample.runID + ", sampleID = " + sample.sampleID + ")";
        setTitle(title);

        setValues();
    }

    private void createComponents(){

        txtSample = new JButton();
        txtSample.setFont(GUICommonTools.TAHOMA_BOLD_14);
        txtSample.setBackground(new Color(59, 89, 182));
        txtSample.setContentAreaFilled(false);

        txtTotalVariants = new JButton();
        txtTotalVariants.setFont(GUICommonTools.TAHOMA_BOLD_14);
        txtTotalVariants.setBackground(new Color(59, 89, 182));
        txtTotalVariants.setFocusPainted(false);

        txtTMBScore = new JButton();
        txtTMBScore.setFont(GUICommonTools.TAHOMA_BOLD_14);
        txtTMBScore.setBackground(new Color(59, 89, 182));
        txtTMBScore.setFocusPainted(false);

        txtTMBGroup = new JButton();
        txtTMBGroup.setFont(GUICommonTools.TAHOMA_BOLD_14);
        txtTMBGroup.setBackground(new Color(59, 89, 182));
        txtTMBGroup.setFocusPainted(false);
    }

    private void layoutComponents(){

        Container pane = getContentPane();

        JPanel mainPanel = new JPanel(new GridLayout(4,1));
        mainPanel.setPreferredSize(new Dimension(300, 225));

        JPanel samplePanel = new JPanel(new GridLayout(1,0));
        samplePanel.setPreferredSize(new Dimension(300, 75));
        samplePanel.setBorder(BorderFactory.createLineBorder(Color.black));
        samplePanel.add(txtSample);

        JPanel variantPanel = new JPanel();
        variantPanel.setPreferredSize(new Dimension(300, 50));
        variantPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        variantPanel.add(new RowPanel("Total Variants:",txtTotalVariants));

        JPanel scorePanel = new JPanel();
        scorePanel.setPreferredSize(new Dimension(300, 50));
        scorePanel.setBorder(BorderFactory.createLineBorder(Color.black));
        scorePanel.add(new RowPanel("TMB Score:",txtTMBScore));

        JPanel groupPanel = new JPanel();
        groupPanel .setPreferredSize(new Dimension(300, 50));
        groupPanel .setBorder(BorderFactory.createLineBorder(Color.black));
        groupPanel .add(new RowPanel("TMB Group:",txtTMBGroup));


        mainPanel.add(samplePanel);
        mainPanel.add(variantPanel);
        mainPanel.add(scorePanel);
        mainPanel.add(groupPanel);

        pane.add(mainPanel,BorderLayout.PAGE_START);

    }

    private void setValues() throws Exception {

        ExomeTMB exomeTMB = DatabaseCommands.getSampleTMB(sample);
        txtSample.setText(exomeTMB.getTMBPair());
        txtTotalVariants.setText(exomeTMB.getTMBTotalVariants());
        txtTMBScore.setText(exomeTMB.getTMBScore());
        txtTMBGroup.setText(exomeTMB.getTMBGroup());
    }

    private class RowPanel extends JPanel{
        private static final long serialVersionUID = 1L;
        private JLabel left;
        private Component right;

        RowPanel(String label, Component right){
            this.left = new JLabel(label);
            this.right = right;
            layoutComponents();
        }

        private void layoutComponents(){
            left.setFont(GUICommonTools.TAHOMA_BOLD_14);
            left.setPreferredSize(new Dimension(150, 25));
            right.setPreferredSize(new Dimension(150, 25));
            setLayout(new BorderLayout());
            add(left, BorderLayout.WEST);
            add(right, BorderLayout.CENTER);
        }
    }

}

