package hmvv.gui.sampleList;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import hmvv.io.DatabaseCommands;
import hmvv.io.MutationReportGenerator;
import hmvv.main.Configurations.MUTATION_SOMATIC_HISTORY;
import hmvv.main.Configurations.MUTATION_TIER;
import hmvv.main.Configurations.REPORT_TYPE;
import hmvv.model.MutationSomatic;
import hmvv.model.Sample;
import hmvv.model.SampleVariantAnnotation;

public class ReportVariantPanel extends JPanel implements Comparable<ReportVariantPanel>{
	private static final long serialVersionUID = 1L;
    

    private JTextArea sampleVariantAnnotationTextArea;

    @SuppressWarnings("unused")
    private Sample sample;
	private MutationSomatic mutation;
    private SampleVariantAnnotation mutationAnnotation;
    private String tab_title;

    public ReportVariantPanel(REPORT_TYPE report_type, Sample sample, MutationSomatic mutation)  throws Exception{
		super();
		this.sample = sample;
		this.mutation = mutation;
        if(mutation.getMutationAnnotation() != null){
            mutationAnnotation = mutation.getMutationAnnotation();
        }else if(mutation.getMutationAnnotation() == null){
            ArrayList<SampleVariantAnnotation> sampleVariantAnnotationHistory = DatabaseCommands.getSampleVariantAnnotationHistory(mutation);
            if(sampleVariantAnnotationHistory.size() > 0) {
                mutationAnnotation = sampleVariantAnnotationHistory.get(sampleVariantAnnotationHistory.size() - 1);
            }
        }
        if(mutationAnnotation == null){
            String defaultCuration = MutationReportGenerator.generateShortReport(mutation, MUTATION_TIER.BLANK, MUTATION_SOMATIC_HISTORY.BLANK, false);
            mutationAnnotation = new SampleVariantAnnotation(mutation, MUTATION_TIER.BLANK, MUTATION_SOMATIC_HISTORY.BLANK, false, defaultCuration, "", null);
        }
		constructPanel();
	}

    private void constructPanel() throws Exception{
        sampleVariantAnnotationTextArea = new JTextArea(mutationAnnotation.curation);
        sampleVariantAnnotationTextArea.addMouseListener(new ContextMenuMouseListener());
        sampleVariantAnnotationTextArea.setLineWrap(true);
        sampleVariantAnnotationTextArea.setWrapStyleWord(true);
        sampleVariantAnnotationTextArea.setEnabled(false);
        JScrollPane variantTextScrollPane = new JScrollPane(sampleVariantAnnotationTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        tab_title = mutation.getHGVSp();
        if(tab_title.equals("")){
            tab_title = mutation.getHGVSc();
        }

        setLayout(new BorderLayout());
        add(variantTextScrollPane, BorderLayout.CENTER);
    }

    public String getSampleVariantAnnotationTextArea(){
        return sampleVariantAnnotationTextArea.getText();
    }

    public String getPanelTitle(){
        return tab_title;
    }

    @Override
    public int compareTo(ReportVariantPanel o) {
        if (o.mutationAnnotation.mutation_tier == null){
            return -1;
        }
        return mutationAnnotation.mutation_tier.compareTo(o.mutationAnnotation.mutation_tier);
    }

}
