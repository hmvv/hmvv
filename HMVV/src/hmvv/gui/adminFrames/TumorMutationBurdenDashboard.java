package hmvv.gui.adminFrames;

import hmvv.gui.GUICommonTools;
import hmvv.gui.sampleList.SampleListFrame;
import hmvv.io.SSHConnection;
import hmvv.model.Sample;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TumorMutationBurdenDashboard extends JDialog {

    private static final long serialVersionUID = 1L;

    private SampleListFrame parent;

    private ArrayList<Sample> samples;

    private JTabbedPane tabbedPane;

    private int image_width;
    private int image_height;


    public TumorMutationBurdenDashboard(SampleListFrame parent,ArrayList<Sample> samples) throws Exception {
        super (parent,"TumorMutationBurden Dashboard");
        this.parent = parent;
        this.samples = samples;

        Rectangle bounds = GUICommonTools.getBounds(parent);
        setSize((int)(bounds.width*.6), (int)(bounds.height*.6));

        this.image_height = (int)(bounds.height*.5);
        this.image_width = (int)(bounds.width*.5);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        createComponents();
        layoutComponents();
        setLocationRelativeTo(parent);

    }

    private void createComponents(){

    }

    private void layoutComponents() throws Exception {

        JPanel coloPanel = new JPanel();
        coloPanel.add(new JLabel(uploadColoPanelImage()));

        JPanel tempPanel = new JPanel();

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.addTab("COLO829-Trend", null, coloPanel, null);
        tabbedPane.addTab("TEMP", null, tempPanel, null);


        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(tabbedPane);

        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPane);
    }

    private ImageIcon uploadColoPanelImage() throws Exception {
        BufferedImage img = ImageIO.read(SSHConnection.copyFileONLocal("tmb_control"));
        Image dimg = img.getScaledInstance(this.image_width, this.image_height, Image.SCALE_SMOOTH);
        return new ImageIcon(dimg);
    }

}