package hmvv.gui.sampleList;

import hmvv.gui.GUICommonTools;
import hmvv.model.Sample;

import javax.swing.*;import java.awt.*;

public class ExomeQCFrame extends JDialog {

    private static final long serialVersionUID = 1L;

    private JTextArea txtSequenceStats;

    private Sample sample;

    public ExomeQCFrame (SampleListFrame parent,Sample sample) throws Exception{
        super(parent, "Sample Amplicons");
        this.sample = sample;

        Rectangle bounds = GUICommonTools.getBounds(parent);
        setSize((int)(bounds.width*.3), (int)(bounds.height*.35));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        createComponents();
        layoutComponents();
        setLocationRelativeTo(parent);

        String title = "Exome Quality Control - " + sample.getLastName() + "," + sample.getFirstName() +
                " (runID = " + sample.runID + ", sampleID = " + sample.sampleID + ")";
        setTitle(title);

        setValues();
    }

    private void createComponents(){

        txtSequenceStats = new JTextArea();
        txtSequenceStats.setEnabled(false);
        txtSequenceStats.setDisabledTextColor(Color.BLACK);

    }

    private void layoutComponents(){

        Container pane = getContentPane();

        JPanel mainPanel = new JPanel(new GridLayout(3,1));
        mainPanel.setPreferredSize(new Dimension(400, 1000));
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        mainPanel.add(txtSequenceStats);

        pane.add(mainPanel,BorderLayout.PAGE_START);

    }

    private void setValues(){
        txtSequenceStats.setText("sample:\tExome29-N_S2\n" +
                "\t\n" +
                "Raw Reads:\tTotal:155669848\n" +
                "\t\n" +
                "Percent duplicates:\t13.2%\n" +
                "\t\n" +
                "Average Coverage:\t210X\n" +
                "\t\n" +
                "PCT_TARGET_BASES_2X\t97%\n" +
                "PCT_TARGET_BASES_10X\t96%\n" +
                "PCT_TARGET_BASES_20X\t94%\n" +
                "PCT_TARGET_BASES_30X\t93%\n" +
                "PCT_TARGET_BASES_40X\t90%\n" +
                "PCT_TARGET_BASES_50X\t88%\n" +
                "PCT_TARGET_BASES_100X\t73%");
    }
}

