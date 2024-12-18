package hmvv.main;

import java.awt.Color;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;


import javax.swing.JOptionPane;

import hmvv.io.SSHConnection;
import hmvv.model.Sample;

public class Configurations {

	public static void loadLocalConfigurations(Component parent, InputStream configurationInputStream) throws Exception{
		BufferedReader br = new BufferedReader(new InputStreamReader(configurationInputStream));
		String line = null;
		while((line = br.readLine()) != null){
			if(line.equals("")){
				continue;
			}
			String[] variableConfig = parseConfigurationLine(line);
			String variableName = variableConfig[0];
			String variableValue = variableConfig[1];
			
			try{
				if(variableName.startsWith("SSH_SERVER_ADDRESS")){
					SSH_SERVER_ADDRESS = variableValue;
				}else if(variableName.startsWith("SSH_PORT")){
					SSH_PORT = Integer.parseInt(variableValue);
				}else if(variableName.startsWith("SSH_FORWARDING_HOST")){
					SSH_FORWARDING_HOST = variableValue;
				}else if(variableName.startsWith("DATABASE_PORT")){
					DATABASE_PORT = Integer.parseInt(variableValue);
				}else if(variableName.startsWith("DATABASE_NAME")){
					DATABASE_NAME = variableValue;
				}else if(variableName.startsWith("DATABASE_VERSION")){
						DATABASE_VERSION = variableValue;
				}else {
					JOptionPane.showMessageDialog(parent, "Unknown configuration: " + line);
				}
			}catch(Exception e){
				JOptionPane.showMessageDialog(parent, e.getClass().toString() + ". Could not assign configuration " + line);
			}
		}
		br.close();
		String message = "";
		if(SSH_SERVER_ADDRESS == null){
			message+="SSH_SERVER_ADDRESS ";
		}
		if(SSH_PORT == null){
			message+="SSH_PORT ";
		}
		if(SSH_FORWARDING_HOST == null){
			message+="SSH_FORWARDING_HOST ";
		}
		if(DATABASE_PORT == null){
			message+="DATABASE_PORT ";
		}
		if(DATABASE_NAME == null){
			message+="DATABASE_NAME ";
		}
		if(DATABASE_VERSION == null){
			message+="DATABASE_VERSION ";
		}
		if(message.length() > 0){
			throw new Exception("The following were not present in the configuration file: " + message);
		}
	}
	
	/**
	 * Dependent on SSHConnection already established
	 * @throws Exception
	 */
	public static void loadServerConfigurations() throws Exception{
		ArrayList<String> configurations = SSHConnection.readConfigurationFile();
		
		for(String line : configurations) {
			if(line.equals("")){
				continue;
			}
			String[] variableConfig = parseConfigurationLine(line);
			String variableName = variableConfig[0];
			String variableValue = variableConfig[1];
			
			try{
			    if(variableName.startsWith("READ_WRITE_CREDENTIALS")){
					String[] lineSplit = variableValue.split(",");
					READ_WRITE_CREDENTIALS = new String[2];
					READ_WRITE_CREDENTIALS[0] = lineSplit[0];
					READ_WRITE_CREDENTIALS[1] = lineSplit[1];
				}else if(variableName.startsWith("LIS_DRIVER")){
					LIS_DRIVER = variableValue;
				}else if(variableName.startsWith("LIS_CONNECTION_DRIVER")){
					LIS_CONNECTION_DRIVER = variableValue;
				}else if(variableName.startsWith("LIS_CONNECTION")){
					LIS_CONNECTION = variableValue;
				}else {
					throw new Exception ("Invalid configuration on server. Variable name is not valid.");
				}
			}catch(Exception e){
				throw new Exception ("Invalid configuration on server. " + e.getClass().toString());
			}
		}
		
		String message = "";
		if(READ_WRITE_CREDENTIALS == null){
			message+="READ_WRITE_CREDENTIALS ";
        }
        if(LIS_DRIVER == null){
			message+="LIS_DRIVER ";
		}
		if(LIS_CONNECTION_DRIVER == null){
			message+="LIS_CONNECTION_DRIVER ";
		}
		if(LIS_CONNECTION == null){
			message+="LIS_CONNECTION ";
		}
		if(message.length() > 0){
			throw new Exception("The following were not present in the configuration file: " + message);
		}
	}
	
	private static String[] parseConfigurationLine(String line) throws Exception {
		String[] split = line.split("=");
		if(split.length != 2){
			throw new Exception ("Invalid configuration. Line is not correctly formatted.");
		}
		String variableName = split[0].trim();
		String variableValue = split[1].trim();
		return new String[] {variableName, variableValue};
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
	public static String[] READ_WRITE_CREDENTIALS;
	public static String DATABASE_NAME;
	public static Integer DATABASE_PORT;
	public static String DATABASE_VERSION;
	public static String BCL2FASTQ_DATABASE_NAME = "bcl_convert";
	public static String REFERENCE_DATABASE_NAME = "ngs_reference";
	
	//Reference database tables
	public static String CARDIAC_TABLE = REFERENCE_DATABASE_NAME + ".db_cardiac_72020";
	public static String COSMIC_TABLE = REFERENCE_DATABASE_NAME + ".db_cosmic_grch37v101";
	public static String COSMIC_CMC_TABLE = REFERENCE_DATABASE_NAME + ".db_cosmic_cmc_v101";
	public static String CIVIC_TABLE = REFERENCE_DATABASE_NAME + ".db_civic_42019";
	public static String CLINVAR_TABLE = REFERENCE_DATABASE_NAME + ".db_clinvar_20230514";
	public static String G1000_TABLE = REFERENCE_DATABASE_NAME + ".db_g1000_phase3v1";
	public static String GNOMAD_TABLE = REFERENCE_DATABASE_NAME + ".db_gnomad_r211";
	public static String GNOMAD_LF_TABLE = REFERENCE_DATABASE_NAME + ".db_gnomad_r211_lf";
	public static String ONCOKB_TABLE = REFERENCE_DATABASE_NAME + ".db_oncokb";
	public static String PMKB_TABLE = REFERENCE_DATABASE_NAME + ".db_pmkb_42019";

	//Annotation tables
	public static String SOMATIC_GENE_ANNOTATION_TABLE = REFERENCE_DATABASE_NAME + ".geneAnnotation";
	public static String SOMATIC_VARIANT_ANNOTATION_TABLE = REFERENCE_DATABASE_NAME + ".variantAnnotation";
	public static String SOMATIC_VARIANT_ANNOTATION_DRAFT_TABLE = REFERENCE_DATABASE_NAME + ".variantAnnotationDraft";
	public static String GERMLINE_GENE_ANNOTATION_TABLE = REFERENCE_DATABASE_NAME + ".germlineGeneAnnotation";
	public static String GERMLINE_VARIANT_ANNOTATION_TABLE = REFERENCE_DATABASE_NAME + ".germlineVariantAnnotation";
	public static String GERMLINE_VARIANT_ANNOTATION_DRAFT_TABLE = REFERENCE_DATABASE_NAME + ".germlineVariantAnnotationDraft";
	
	//LIS Connection
	public static String LIS_DRIVER;
	public static String LIS_CONNECTION_DRIVER;
	public static String LIS_CONNECTION;
			
	/*
	 * User configurations
	 */
	public enum USER_TYPE{
		TECHNOLOGIST,
		ROTATOR,
		FELLOW,
		PATHOLOGIST
	}
	
    public enum USER_FUNCTION{
        ENTER_SAMPLE{
        	public boolean isSuperUser(USER_TYPE userType) {
	    		return userType == USER_TYPE.TECHNOLOGIST || userType == USER_TYPE.PATHOLOGIST;
	    	}
        },
        
        EDIT_SAMPLE_LABR{
        	public boolean isSuperUser(USER_TYPE userType) {
	    		return userType == USER_TYPE.TECHNOLOGIST || userType == USER_TYPE.PATHOLOGIST;
	    	}
        },

		ANNOTATE_MAIN{
			public boolean isSuperUser(USER_TYPE userType) {
				return userType == USER_TYPE.FELLOW  || userType == USER_TYPE.PATHOLOGIST;
			}
		},

		ANNOTATE_DRAFT{
			public boolean isSuperUser(USER_TYPE userType) {
				return userType == USER_TYPE.FELLOW  || userType == USER_TYPE.PATHOLOGIST || userType == USER_TYPE.ROTATOR;
			}
		},

		RESTRICT_SAMPLE_ACCESS{
			public boolean isSuperUser(USER_TYPE userType) {
				return userType == USER_TYPE.ROTATOR;
			}
		};
    	
    	public abstract boolean isSuperUser(USER_TYPE userType);
    }


	public enum MUTATION_TYPE{
		SOMATIC,
		GERMLINE
	}
    
	/**
	 * The Linux group which defines super users.
	 */
	public static String GENOME_VERSION = "37";
	public static int RESTRICT_SAMPLE_DAYS = 60;
	public static int COVERAGE_PERCENTAGE_100X = 90;
	public static int COVERAGE_PERCENTAGE_250X = 90;
	public static int MAX_OCCURENCE_FILTER = 1000000;
	public static int MAX_ALLELE_FREQ_FILTER = 100;
	public static int MAX_GLOBAL_ALLELE_FREQ_FILTER = 100;
	public static int GERMLINE_READ_DEPTH_FILTER = 10;
	public static int GERMLINE_ALLELE_FREQ_FILTER = 15;
	public static int GERMLINE_GNOMAD_MAX_GLOBAL_ALLELE_FREQ_FILTER = 1;
	public static String MIN_VARIANT_CALLERS_COUNT = "2";
	public static String MAX_VARIANT_CALLERS_COUNT = "3";
	public static String OLDER_RUN_DATE = "2022-06-01"; 


	/**
	 * 
	 * Somatic Panel Allele Frequency Setups
	 * 
	 */
	public static double getDefaultAlleleFrequencyFilter(Sample sample) {
		if(sample.getLastName().contains("Horizon")){
			double HORIZON_ALLELE_FREQ_FILTER = 1;
			return HORIZON_ALLELE_FREQ_FILTER;

		}else if (sample.assay.assayName.equals("heme")){
			double HEME_ALLELE_FREQ_FILTER = 0;
			return HEME_ALLELE_FREQ_FILTER;

		}else if (sample.instrument.instrumentName.equals("proton")){
			double PROTON_ALLELE_FREQ_FILTER = 10;
			return PROTON_ALLELE_FREQ_FILTER;

		}else if (sample.instrument.instrumentName.equals("pgm")){
			double PGM_ALLELE_FREQ_FILTER = 10;
			return PGM_ALLELE_FREQ_FILTER;

		}
		double GLOBAL_ALLELE_FREQ_FILTER = 10;
		return GLOBAL_ALLELE_FREQ_FILTER;
	}

	public static String getqcMeasureDescription(Sample sample) {

		Timestamp runDate = sample.runDate; 

		if (sample.assay.assayName.equals("heme") && sample.instrument.instrumentName.equals("nextseq") && OLDER_RUN_DATE.compareTo(runDate.toString()) <= 0){
			return "250x Coverage < ";
		}
		return "100x Coverage < ";
	}





	public static int HISTORICAL_NEXTSEQ_HEME_READ_DEPTH_FILTER = 100;
	public static int getDefaultReadDepthFilter(Sample sample) {

		Timestamp runDate = sample.runDate; 

		if (sample.assay.assayName.equals("heme") && sample.instrument.instrumentName.equals("nextseq") && OLDER_RUN_DATE.compareTo(runDate.toString()) <= 0){
			int HEME_READ_DEPTH_FILTER = 250;
			return HEME_READ_DEPTH_FILTER;
		}
		int GLOBAL_READ_DEPTH_FILTER = 100;
		return GLOBAL_READ_DEPTH_FILTER;
	}
	

	public static String getPipelineFirstStep(String pipeline_instrument){
		if(pipeline_instrument.equals("proton")){
			return "queue";
		}
		else{
			return "trimming started";
		}
	}


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
	public static Color TABLE_SELECTION_COLOR = new Color(51,204,255);
	public static Color TABLE_SELECTION_FONT_COLOR = Color.black;
	public static Color TABLE_REPORTED_COLOR = new Color(51,255,102);
	
	public static Color TABLE_UNMATCHED_COSMIC_COLOR = Color.LIGHT_GRAY;
	public static Color TABLE_MATCHED_COSMIC_COLOR = Color.WHITE;

	public static boolean isTestEnvironment() {
		return !getEnvironment().equals("ngs_live");
	}
}
