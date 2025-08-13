package hmvv.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import hmvv.gui.mutationlist.tablemodels.*;
import hmvv.main.Configurations;
import hmvv.main.Configurations.MUTATION_SOMATIC_HISTORY;
import hmvv.main.Configurations.MUTATION_TIER;
import hmvv.model.*;

public class MutationReportGenerator{

	public static String generateShortReport(MutationCommon mutation, MUTATION_TIER tier_selected, MUTATION_SOMATIC_HISTORY choice_selected, boolean possibleGermline) throws Exception{
		StringBuilder report = new StringBuilder(500);
		String cDNA = mutation.getHGVSc();
		String codon = mutation.getHGVSp();
		String gene = mutation.getGene();

		DecimalFormat altFreqFormat = new DecimalFormat("#.#");
		double altFreq = Double.valueOf(altFreqFormat.format(mutation.getAltFreq()));

		String mutationText = String.format("%s:%s;%s     (%s%%)",gene, cDNA, codon, altFreq);
		report.append(mutationText + "\n");

		
		String[] codon_with_transcript_array = codon.split(":");
		String amino_acid = codon;
		if(codon_with_transcript_array.length == 2){
			amino_acid = codon_with_transcript_array[1];
		}
		if(tier_selected != null && tier_selected != MUTATION_TIER.BLANK){
			//String pubmedID = DatabaseCommands.getVariantAnnotationPubmedID(mutation.getCoordinate());
			String text = String.format("The %s %s variant is classified as a %s (PMID: 27993330).", gene, amino_acid, tier_selected.label);
			report.append(text);
		}

		if (choice_selected != null){
			report.append(String.format(" %s", choice_selected.label));
		}
		
		if(tier_selected == MUTATION_TIER.TIER_2){
			report.append(" The effect of this mutation on personalized therapeutic strategies for this patient is uncertain.");
		}else if(tier_selected == MUTATION_TIER.TIER_3){
			report.append(" The effect of this mutation on prognosis and personalized therapeutic strategies for this patient is uncertain.");
		}

		if (mutation.getOtherMutations().size() > 0){
			report.append(" This mutation was detected previously in a [bone marrow aspirate specimen] from this patient (BM case number; date)");
		}
		
		if(possibleGermline){
			report.append(" Given that the variant is present consistently at a frequency close to 50% in both specimens, the possibility of this variant being germline cannot be ruled out.");
		}

		return report.toString();
	}

	public static String generateShortReport(MutationList mutationList, MUTATION_TIER tier_selected) throws Exception{
		StringBuilder report = new StringBuilder(500);

		for(int i = 0; i < mutationList.getMutationCount(); i++) {
			MutationCommon mutation = mutationList.getMutation(i);
			if (!mutation.isReported()) {
				continue;
			}
			String thisReport = generateShortReport(mutation, tier_selected, null, false);
			report.append(thisReport + "\n");
		}
		return report.toString();
	}

	public static String generateLongReport(MutationList mutationList) throws Exception{
		String report = "";
		if (mutationList.getMutation_type() == Configurations.MUTATION_TYPE.SOMATIC){
			report =  generateLongReportSomatic(mutationList);
		} else if (mutationList.getMutation_type() == Configurations.MUTATION_TYPE.GERMLINE){
			report =  generateLongReportGermline(mutationList);
		}
		return report;
	}

	public static String generateLongReportSomatic(MutationList mutationList) throws Exception{
		StringBuilder report = new StringBuilder(500);
		for(int i = 0; i < mutationList.getMutationCount(); i++){
			MutationSomatic mutation = (MutationSomatic)mutationList.getMutation(i);
			if(!mutation.isReported()){
				continue;
			}

			String name = mutation.getLastName() + ", " + mutation.getFirstName();
			String cDNA = mutation.getHGVSc();
			String codon = mutation.getHGVSp();
			String gene = mutation.getGene();
			String mutationText = gene + ":" + cDNA + ";" + codon;
			String dbSNP = mutation.getDbSNPID();
			Coordinate coordinate = mutation.getCoordinate();
			String orderNumber = mutation.getOrderNumber();
			VariantPredictionClass variantPredictionClass = mutation.getVariantPredictionClass();
			String cosmicIDs = mutation.cosmicIDsToString();
			int occurrence = mutation.getOccurrence();

			report.append("Name: " + name + "\n");
			report.append("OrderNumber: " + orderNumber + "\n");
			report.append("Mutation Info: " + mutationText + "\n");
			report.append("Coordinate: " + coordinate.getCoordinateAsString() + "\n");
			report.append("VariantPredictionClass: " + variantPredictionClass + "\n");
			report.append("dbSNP ID: " + dbSNP + "\n");
			report.append("Cosmic IDs: " + cosmicIDs + "\n");
			report.append("Occurence: " + occurrence + "\n");

			Annotation annotation = mutation.getLatestAnnotation();
			if(annotation != null) {
				String somatic = annotation.somatic;
				String classification = annotation.classification;
				String curation = annotation.curation;
				report.append("Somatic: " + somatic + "\n");
				report.append("Classification: " + classification + "\n");
				report.append("Curation Note: " + curation + "\n" + "\n");
			}

			report.append("\n");
		}
		report.append("Database Information: \n");

		ArrayList<Database>  databases = SSHConnection.getDatabaseInformation();
		for (Database d: databases){
			report.append( d.getName() +"(" + d.getVersion() + ")" + "\n");
		}

		return report.toString();
	}

	public static String generateLongReportGermline(MutationList mutationList) throws Exception{
		StringBuilder report = new StringBuilder(500);
		for(int i = 0; i < mutationList.getMutationCount(); i++){
			MutationGermline mutation = (MutationGermline) mutationList.getMutation(i);
			if(!mutation.isReported()){
				continue;
			}

			String name = mutation.getLastName() + ", " + mutation.getFirstName();
			String cDNA = mutation.getHGVSc();
			String codon = mutation.getHGVSp();
			String gene = mutation.getGene();
			String mutationText = gene + ":" + cDNA + ";" + codon;
			Coordinate coordinate = mutation.getCoordinate();
			String orderNumber = mutation.getOrderNumber();
			VariantPredictionClass variantPredictionClass = mutation.getVariantPredictionClass();
			int occurrence = mutation.getOccurrence();

			report.append("Name: " + name + "\n");
			report.append("OrderNumber: " + orderNumber + "\n");
			report.append("Mutation Info: " + mutationText + "\n");
			report.append("Coordinate: " + coordinate.getCoordinateAsString() + "\n");
			report.append("VariantPredictionClass: " + variantPredictionClass + "\n");
			report.append("Occurence: " + occurrence + "\n");

			Annotation annotation = mutation.getLatestAnnotation();
			if(annotation != null) {
				String somatic = annotation.somatic;
				String classification = annotation.classification;
				String curation = annotation.curation;
				report.append("Origin: " + somatic + "\n");
				report.append("Classification: " + classification + "\n");
				report.append("Curation Note: " + curation + "\n" + "\n");
			}

			report.append("\n");
		}
		report.append("Database Information: \n");

		ArrayList<Database>  databases = SSHConnection.getDatabaseInformation();
		for (Database d: databases){
			report.append( d.getName() +"(" + d.getVersion() + ")" + "\n");
		}

		return report.toString();
	}


	public static void exportReportSomatic(File outputFile, BasicTableModel basicTable, ClinVarTableModel clinvarTable, CosmicTableModel coordinatesTable,
			G1000TableModel g1000Table) throws IOException{
		File fileName = outputFile;
		if(!outputFile.getName().endsWith(".txt")){
			fileName = new File(outputFile.toString() + ".txt");
		}
		BufferedWriter outFile = new BufferedWriter(new FileWriter(fileName));
		for (int col = 0; col < basicTable.getColumnCount(); col++){
			outFile.write(basicTable.getColumnName(col) + "\t"); 
		}
		for (int col = 5; col < coordinatesTable.getColumnCount(); col++){
			outFile.write(coordinatesTable.getColumnName(col) + "\t"); 
		}
		for (int col = 5; col < g1000Table.getColumnCount(); col++){
			outFile.write(g1000Table.getColumnName(col) + "\t"); 
		}
		for (int col = 5; col < clinvarTable.getColumnCount(); col++){
			outFile.write(clinvarTable.getColumnName(col) + "\t"); 
		}
		outFile.write("\r\n");
		for (int i = 0; i < basicTable.getRowCount(); i++) {
			for (int j = 0; j < basicTable.getColumnCount(); j++) {
				String output = "null";
				if(basicTable.getValueAt(i, j) != null){
					output = basicTable.getValueAt(i, j).toString();
				}
				outFile.write(output + "\t");
			}
			for (int j = 5; j < coordinatesTable.getColumnCount(); j++) {
				String output = "null";
				if(coordinatesTable.getValueAt(i, j) != null){
					output = coordinatesTable.getValueAt(i, j).toString();
				}
				outFile.write(output + "\t");
			}
			for (int j = 5; j < g1000Table.getColumnCount(); j++) {
				String output = "null";
				if(g1000Table.getValueAt(i, j) != null){
					output = g1000Table.getValueAt(i, j).toString();
				}
				outFile.write(output + "\t");
			}
			for (int j = 5; j < clinvarTable.getColumnCount(); j++) {
				String output = "null";
				if(clinvarTable.getValueAt(i, j) != null){
					output = clinvarTable.getValueAt(i, j).toString();
				}
				outFile.write(output + "\t");
			}
		}

		outFile.close();
	}

	public static void exportReportGermline(File outputFile, GermlineClinVarTableModel clinvarTable) throws IOException{
		File fileName = outputFile;
		if(!outputFile.getName().endsWith(".txt")){
			fileName = new File(outputFile.toString() + ".txt");
		}
		BufferedWriter outFile = new BufferedWriter(new FileWriter(fileName));


		for (int col = 5; col < clinvarTable.getColumnCount(); col++){
			outFile.write(clinvarTable.getColumnName(col) + "\t");
		}
		outFile.write("\r\n");

		outFile.close();
	}
}
