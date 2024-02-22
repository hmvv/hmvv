package hmvv.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import hmvv.gui.mutationlist.tablemodels.*;
import hmvv.main.Configurations;
import hmvv.model.*;

public class MutationReportGenerator{

	public static String generateShortReport(MutationList mutationList) throws Exception{

		StringBuilder report = new StringBuilder(500);

		for(int i = 0; i < mutationList.getMutationCount(); i++) {
			MutationCommon mutation = mutationList.getMutation(i);
			if (!mutation.isReported()) {
				continue;
			}

			String cDNA = mutation.getHGVSc();
			String codon = mutation.getHGVSp();
			String gene = mutation.getGene();

			DecimalFormat altFreqFormat = new DecimalFormat("#.#");
			double altFreq = Double.valueOf(altFreqFormat.format(mutation.getAltFreq()));

			String mutationText = gene + ":" + cDNA + ";" + codon + "     (" + altFreq + "%)";
			report.append(mutationText + "\n");
		}

		report.append("\n");

		for(int i = 0; i < mutationList.getMutationCount(); i++) {
			MutationCommon mutation = mutationList.getMutation(i);
			if (!mutation.isReported()) {
				continue;
			}
			Annotation annotation = mutation.getLatestAnnotation();
			if(annotation != null) {
				String curation = annotation.curation;
				if(curation.length() > 0) {
					report.append(curation + "\n");
				}
			}

//			ArrayList<GeneAnnotation> geneAnnotationHistory = DatabaseCommands.getGeneAnnotationHistory(gene);
//			if(geneAnnotationHistory.size() > 0) {
//				GeneAnnotation geneAnnotation = geneAnnotationHistory.get(geneAnnotationHistory.size() - 1);
//				if(geneAnnotation.curation.length() > 0) {
//					report.append("Gene Note: " + geneAnnotation.curation + "\n");
//				}
//			}

			report.append("\n");
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
