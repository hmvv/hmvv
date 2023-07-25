package hmvv.gui.mutationlist;


import java.util.ArrayList;

import hmvv.gui.GUICommonTools;
import hmvv.model.repeatMutations;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.*;

public class repeatMutationsFrame extends JDialog {

    private static final long serialVersionUID = 1L;
    private JScrollPane tableScrollPane;
    

    public repeatMutationsFrame(JDialog parent,  JTable table, ArrayList<repeatMutations> repeatMutations) throws Exception {
        super(parent, "Repeats", ModalityType.APPLICATION_MODAL);

        tableScrollPane = new JScrollPane(table);
		tableScrollPane.setPreferredSize(new Dimension(700,200));

        JPanel contentPanel = new JPanel();
        setContentPane(contentPanel);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JPanel tablePanel = new JPanel();
        tablePanel.add(tableScrollPane);
        contentPanel.add(tablePanel, BorderLayout.CENTER);

        pack();
        Rectangle bounds = GUICommonTools.getBounds(parent);
        setSize((int)(bounds.width*.70), (int)(bounds.height*.50));
        setResizable(true);
        setLocationRelativeTo(parent);

    }
}
