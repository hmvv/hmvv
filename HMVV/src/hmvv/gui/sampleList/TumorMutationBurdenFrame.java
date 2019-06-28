package hmvv.gui.sampleList;

import hmvv.gui.GUICommonTools;
import hmvv.model.Sample;

import javax.swing.*;
import java.awt.*;

public class TumorMutationBurdenFrame extends JDialog {

    private static final long serialVersionUID = 1L;

    private JButton txtTotalVariants;
    private JButton txtTMBScore;
    private JButton txtTMBGroup;

    private Sample sample;

    public TumorMutationBurdenFrame (SampleListFrame parent,Sample sample) throws Exception{
        super(parent, "Sample Amplicons");
        this.sample = sample;

        Rectangle bounds = GUICommonTools.getBounds(parent);
        setSize((int)(bounds.width*.2), (int)(bounds.height*.21));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        createComponents();
        layoutComponents();
        setLocationRelativeTo(parent);

        String title = "Tumor Mutation Burden Result - " + sample.getLastName() + "," + sample.getFirstName() +
                " (runID = " + sample.runID + ", sampleID = " + sample.sampleID + ")";
        setTitle(title);

        setValues();
    }

    private void createComponents(){

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

        JPanel mainPanel = new JPanel(new GridLayout(3,1));
        mainPanel.setPreferredSize(new Dimension(200, 175));
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(200, 50));
        topPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        topPanel.add(new RowPanel("Total Variants:",txtTotalVariants));

        JPanel middlePanel = new JPanel();
        middlePanel .setPreferredSize(new Dimension(200, 50));
        middlePanel .setBorder(BorderFactory.createLineBorder(Color.black));
        middlePanel .add(new RowPanel("TMB Score:",txtTMBScore));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(200, 50));
        bottomPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        bottomPanel.add(new RowPanel("TMB Group:",txtTMBGroup));

        mainPanel.add(topPanel);
        mainPanel.add(middlePanel);
        mainPanel.add(bottomPanel);

        pane.add(mainPanel,BorderLayout.PAGE_START);

    }

    private void setValues(){
        txtTotalVariants.setText("250");
        txtTMBScore.setText("3.2");
        txtTMBGroup.setText("HIGH");
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

