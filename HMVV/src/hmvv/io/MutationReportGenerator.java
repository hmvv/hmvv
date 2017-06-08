package hmvv.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import hmvv.gui.mutationlist.tablemodels.BasicTableModel;
import hmvv.gui.mutationlist.tablemodels.ClinVarTableModel;
import hmvv.gui.mutationlist.tablemodels.CoordinatesTableModel;
import hmvv.gui.mutationlist.tablemodels.G1000TableModel;
import hmvv.gui.mutationlist.tablemodels.SampleTableModel;
import hmvv.model.Annotation;
import hmvv.model.Coordinate;

public class MutationReportGenerator{
	public static ArrayList<HashMap<String, String>> generateReport(BasicTableModel basicTable, ClinVarTableModel clinvarTable, CoordinatesTableModel coordinatesTable,
			G1000TableModel g1000Table, SampleTableModel sampleTable) throws Exception{
		ArrayList<HashMap<String, String>> report = new ArrayList<HashMap<String, String>>();

		for(int i = 0; i<basicTable.getRowCount(); i++){
			Boolean reported = Boolean.valueOf(basicTable.getValueAt(i, 0).toString());
			if(reported){
				HashMap<String, String> record = new HashMap<String, String>();
				String lastName = "";
				String firstName = "";
				if(sampleTable.getValueAt(i, 5) != null){
					lastName = sampleTable.getValueAt(i, 5).toString();
				}
				if(sampleTable.getValueAt(i, 6) != null){
					firstName = sampleTable.getValueAt(i, 6).toString();
				}
				String name = lastName + "," + firstName;
				String cDNA = String.valueOf(basicTable.getValueAt(i, 3));
				String codon = String.valueOf(basicTable.getValueAt(i, 4));
				String gene = String.valueOf(basicTable.getValueAt(i, 1));
				String mutation = gene + ":" + cDNA + ";" + codon;
				String dbSNP = String.valueOf(basicTable.getValueAt(i, 5));
				String chr = (coordinatesTable.getValueAt(i, 5)).toString();
				String pos = (coordinatesTable.getValueAt(i, 6)).toString();
				String ref = (coordinatesTable.getValueAt(i, 7)).toString();
				String alt = (coordinatesTable.getValueAt(i, 8)).toString();
				
				Coordinate coordinate = new Coordinate(chr, pos, ref, alt);
				Annotation annotation = DatabaseCommands.getAnnotation(coordinate);
				
				String orderNumber = "";
				if(sampleTable.getValueAt(i, 7) != null){
					orderNumber = sampleTable.getValueAt(i, 7).toString();
				}
				record.put("Name", name);
				record.put("OrderNumber", orderNumber);
				record.put("Mutation", mutation);
				record.put("Coordinate", coordinate.getCoordinateAsString());
				//record.put("Genotype", table1.getValueAt(i, 8).toString());
				record.put("dbSNP", dbSNP);
				record.put("Cosmic", String.valueOf(basicTable.getValueAt(i, 6)));
				record.put("Occurance", String.valueOf(basicTable.getValueAt(i, 12)));
				record.put("Somatic", annotation.getSomatic());
				record.put("Classification", annotation.getClassification());
				record.put("Curation", annotation.getCuration());
				
				report.add(record);
			}
		}

		return report;
	}

	public static void exportReport(File outputFile, BasicTableModel basicTable, ClinVarTableModel clinvarTable, CoordinatesTableModel coordinatesTable,
			G1000TableModel g1000Table, SampleTableModel sampleTable) throws IOException{
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
		for (int col = 5; col < sampleTable.getColumnCount(); col++){
			outFile.write(sampleTable.getColumnName(col) + "\t"); 
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
			for (int j = 5; j < sampleTable.getColumnCount(); j++) {
				String output = "null";
				if(sampleTable.getValueAt(i, j) != null){
					output = sampleTable.getValueAt(i, j).toString();
				}
				outFile.write(output + "\t");
			}
			outFile.write("\r\n");
		}

		outFile.close();
	}
}
