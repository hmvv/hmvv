package hmvv.main;

import java.awt.Color;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

import hmvv.model.Sample;

public class Configurations {

	public static void loadConfigurations(Component parent, InputStream configurationInputStream) throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(configurationInputStream));
		String line = null;
		while((line = br.readLine()) != null){
			if(line.equals("")){
				continue;
			}
			String[] split = line.split("=");
			if(split.length != 2){
				JOptionPane.showMessageDialog(parent, "Invalid configuration: " + line);
				continue;
			}
			String variableName = split[0].trim();
			String variableValue = split[1].trim();
			try{
				if(variableName.startsWith("READ_ONLY_CREDENTIALS")){
					String[] lineSplit = variableValue.split(",");
					READ_ONLY_CREDENTIALS = new String[2];
					READ_ONLY_CREDENTIALS[0] = lineSplit[0];
					READ_ONLY_CREDENTIALS[1] = lineSplit[1];
				}else if(variableName.startsWith("READ_WRITE_CREDENTIALS")){
					String[] lineSplit = variableValue.split(",");
					READ_WRITE_CREDENTIALS = new String[2];
					READ_WRITE_CREDENTIALS[0] = lineSplit[0];
					READ_WRITE_CREDENTIALS[1] = lineSplit[1];
				}else if(variableName.startsWith("DATABASE_NAME")){
					DATABASE_NAME = variableValue;
				}else if(variableName.startsWith("DATABASE_PORT")){
					DATABASE_PORT = Integer.parseInt(variableValue);
				}else if(variableName.startsWith("SUPER_USER_GROUP")){
					SUPER_USER_GROUP = variableValue;
				}else if(variableName.startsWith("SSH_SERVER_ADDRESS")){
					SSH_SERVER_ADDRESS = variableValue;
				}else if(variableName.startsWith("SSH_PORT")){
					SSH_PORT = Integer.parseInt(variableValue);
				}else if(variableName.startsWith("SSH_FORWARDING_HOST")){
					SSH_FORWARDING_HOST = variableValue;
				}else {
					JOptionPane.showMessageDialog(parent, "Unknown configuration: " + line);
				}
			}catch(Exception e){
				JOptionPane.showMessageDialog(parent, e.getClass().toString() + ". Could not assign configuration " + line);
			}
		}
		br.close();
		String message = "";
		if(	READ_ONLY_CREDENTIALS == null){
			message+="READ_ONLY_CREDENTIALS ";
		}
		if(READ_WRITE_CREDENTIALS == null){
			message+="READ_WRITE_CREDENTIALS ";
		}
		if(DATABASE_NAME == null){
			message+="DATABASE_NAME ";
		}
		if(DATABASE_PORT == null){
			message+="DATABASE_PORT ";
		}
		if(SUPER_USER_GROUP == null){
			message+="SUPER_USER_GROUP ";
		}
		if(SSH_SERVER_ADDRESS == null){
			message+="SSH_SERVER_ADDRESS ";
		}
		if(SSH_PORT == null){
			message+="SSH_PORT ";
		}
		if(SSH_FORWARDING_HOST == null){
			message+="SSH_FORWARDING_HOST ";
		}
		if(message.length() > 0){
			throw new Exception("The following were not present in the configuration file: " + message);
		}
	}
	
	public static String abbreviationtoLetter(String mutation){
		return mutation
			.replaceAll("Ala", "A")
			.replaceAll("Asx", "B")
			.replaceAll("Cys", "C")
			.replaceAll("Asp", "D")
			.replaceAll("Glu", "E")
			.replaceAll("Phe", "F")
			.replaceAll("Gly", "G")
			.replaceAll("His", "H")
			.replaceAll("Ile", "I")
			.replaceAll("Xle", "J")
			.replaceAll("Lys", "K")
			.replaceAll("Leu", "L")
			.replaceAll("Met", "M")
			.replaceAll("Asn", "N")
			.replaceAll("Hyp", "O")
			.replaceAll("Pro", "P")
			.replaceAll("Gln", "Q")
			.replaceAll("Arg", "R")
			.replaceAll("Ser", "S")
			.replaceAll("Thr", "T")
			.replaceAll("Glp", "U")
			.replaceAll("Val", "V")
			.replaceAll("Trp", "W")
			.replaceAll("Ter", "X")
			.replaceAll("Tyr", "Y")
			.replaceAll("Glx", "Z");
	}
	
	/*
	 * Database configurations
	 */
	public static String[] READ_ONLY_CREDENTIALS;
	public static String[] READ_WRITE_CREDENTIALS;
	public static String DATABASE_NAME;
	public static Integer DATABASE_PORT;
	
	/*
	 * User configurations
	 */
	/**
	 * The Linux group which defines super users.
	 */
	public static String SUPER_USER_GROUP;
	public static String GENOME_VERSION = "37";
	
	public static int MAX_ALLELE_FREQ_FILTER = 100;
	public static int MAX_GLOBAL_ALLELE_FREQ_FILTER = 100;
	public static int READ_DEPTH_FILTER = 100;
	public static int MAX_OCCURENCE_FILTER = 1000000;
	
	
	public static int ALLELE_FREQ_FILTER = 10;
	public static int HORIZON_ALLELE_FREQ_FILTER = 1;
	public static int getAlleleFrequencyFilter(Sample sample) {
		//TODO Is there a better way to do this?
		if(sample.getLastName().contains("Horizon")){
			return HORIZON_ALLELE_FREQ_FILTER;
		}else {
			return ALLELE_FREQ_FILTER;
		}
	}
	
	public static String DEFAULT_ASSAY = "heme";
	
	/*
	 * SSH server configurations
	 */
	public static String SSH_SERVER_ADDRESS;
	public static Integer SSH_PORT;
	public static String SSH_FORWARDING_HOST;
	
	public static String getEnvironment() {
		return DATABASE_NAME;
	}
	
	public static Color TEST_ENV_COLOR = Color.CYAN;
	public static boolean isTestEnvironment() {
		return !getEnvironment().equals("ngs_live");
	}
}
