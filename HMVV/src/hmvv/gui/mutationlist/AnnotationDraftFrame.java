package hmvv.gui.mutationlist;

import hmvv.gui.GUICommonTools;
import hmvv.io.DatabaseCommands;
import hmvv.io.SSHConnection;
import hmvv.main.HMVVDefectReportFrame;
import hmvv.model.MutationCommon;
import hmvv.main.Configurations;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnnotationDraftFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextArea annotationTextArea;
    private JButton clearButton;
    private JButton saveButton;
    
    private MutationCommon mutation;
    private JFrame parent;
    public JButton draftButton;
    
    public AnnotationDraftFrame( JFrame parent, MutationCommon mutation, JButton draftButton) throws HeadlessException {
        this.parent = parent;
        this.mutation = mutation;
        this.draftButton = draftButton;
        
        createComponents();
        layoutComponents();
        activateComponents();

        pack();
        Rectangle bounds = GUICommonTools.getBounds(parent);
		setSize((int)(bounds.width*.70), (int)(bounds.height*.70));
        setResizable(false);
        setLocationRelativeTo(parent);
        setAlwaysOnTop(true);
        
    }
    
    private void createComponents(){
        annotationTextArea = new JTextArea();
        
        annotationTextArea.setWrapStyleWord(true);
        annotationTextArea.setLineWrap(true);
        try {
            Rectangle bounds = GUICommonTools.getBounds(parent);
            annotationTextArea.setSize((int)(bounds.width*.50), (int)(bounds.height*.60));
        	annotationTextArea.setText(DatabaseCommands.getVariantAnnotationDraft(mutation.getCoordinate(),mutation.getMutationType()));
            
        } catch (Exception e) {
        	HMVVDefectReportFrame.showHMVVDefectReportFrame(parent, e);
        }

        clearButton = new JButton("Clear");
        clearButton.setEnabled(true);

        saveButton = new JButton("Save");
        getRootPane().setDefaultButton(saveButton);

    }

    private void layoutComponents(){
        JPanel contentPanel = new JPanel();
        setContentPane(contentPanel);

        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPanel.setLayout(new BorderLayout());

        Dimension textAreaDimension = new Dimension(400,400);

        JPanel textAreaPanel = new JPanel();
        //Annotation
        JPanel annotationPanel = new JPanel();
        annotationPanel.setLayout(new BoxLayout(annotationPanel, BoxLayout.Y_AXIS));
        JScrollPane annotationScrollPane = new JScrollPane(annotationTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        annotationScrollPane.setPreferredSize(textAreaDimension);
        TitledBorder annotationBorder = BorderFactory.createTitledBorder("Variant Annotation Draft");
        annotationBorder.setTitleFont(GUICommonTools.TAHOMA_BOLD_14);
        annotationPanel.setBorder(annotationBorder);
        annotationPanel.add(annotationScrollPane);
        textAreaPanel.add(annotationPanel);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPane.add(clearButton);
        buttonPane.add(saveButton);

        contentPanel.add(textAreaPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPane, BorderLayout.SOUTH);
        
    }

    private void activateComponents() {
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    annotationTextArea.setText("");
                } catch (Exception e) {
                	HMVVDefectReportFrame.showHMVVDefectReportFrame(AnnotationDraftFrame.this, e);
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    DatabaseCommands.addVariantAnnotationDraft(mutation.getCoordinate(),annotationTextArea.getText(),mutation.getMutationType());
                    AnnotationDraftFrame.this.dispose();
                    AnnotationDraftFrame.this.draftButton.setEnabled(SSHConnection.isSuperUser(Configurations.USER_FUNCTION.ANNOTATE_DRAFT));
                    

                } catch (Exception e) {
                	HMVVDefectReportFrame.showHMVVDefectReportFrame(AnnotationDraftFrame.this, e);
                }

            }
        });
    }
}
