package hmvv.io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

import hmvv.main.Configurations;
import hmvv.model.*;

public class DatabaseCommands {

	public static void connect() throws Exception{
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://" + SSHConnection.getForwardingHost() +":" + SSHConnection.getForwardingPort() + "/";
		String[] credentials = Configurations.READ_WRITE_CREDENTIALS;
		
		try{
			Class.forName(driver);
			Connection databaseConnection = DriverManager.getConnection(url+Configurations.DATABASE_NAME+"?noAccessToProcedureBodies=true", credentials[0], credentials[1]);
			DatabaseCommands_Annotations.setConnection(databaseConnection);
			DatabaseCommands_Assays.setConnection(databaseConnection);
			DatabaseCommands_Mutations.setConnection(databaseConnection);
			DatabaseCommands_Pipelines.setConnection(databaseConnection);
			DatabaseCommands_QC.setConnection(databaseConnection);
			DatabaseCommands_Samples.setConnection(databaseConnection);
		}catch (Exception e){
			throw new Exception("mysql connection error: " + e.getMessage());
		}
	}
	
	/* ************************************************************************
	 * Assay Queries
	 *************************************************************************/
	public static ArrayList<String> getAllInstruments() throws Exception{
		return DatabaseCommands_Assays.getAllInstruments();
	}
	
	public static ArrayList<String> getAllAssays() throws Exception{
		return DatabaseCommands_Assays.getAllAssays();
	}

	public static ArrayList<String> getAssaysForInstrument(String instrument) throws Exception{
		return DatabaseCommands_Assays.getAssaysForInstrument(instrument);
	}

	public static void createAssay(String instrument, String assay) throws Exception{
		DatabaseCommands_Assays.createAssay(instrument, assay);
	}

	/* ************************************************************************
	 * Mutation Queries
	 *************************************************************************/
	public static ArrayList<Mutation> getBaseMutationsBySample(Sample sample) throws Exception{
		return DatabaseCommands_Mutations.getBaseMutationsBySample(sample);
	}

	public static ArrayList<Mutation> getExtraMutationsBySample(Sample sample) throws Exception{
		return DatabaseCommands_Mutations.getExtraMutationsBySample(sample);
	}
	
	public static ArrayList<String> getCosmicIDs(Mutation mutation) throws Exception{
		return DatabaseCommands_Mutations.getCosmicIDs(mutation);
	}

	public static String getCosmicInfo(String cosmicID) throws Exception{
		return DatabaseCommands_Mutations.getCosmicInfo(cosmicID);
	}

	public static void updateOncokbInfo(Mutation mutation) throws Exception{
		DatabaseCommands_Mutations.updateOncokbInfo(mutation);
	}

	public static void updatePmkbInfo(Mutation mutation) throws Exception{
		DatabaseCommands_Mutations.updatePmkbInfo(mutation);
	}

	public static void updateCivicInfo(Mutation mutation) throws Exception{
		DatabaseCommands_Mutations.updateCivicInfo(mutation);
	}
	
	public static int getOccurrenceCount(Mutation mutation) throws Exception{
		return DatabaseCommands_Mutations.getOccurrenceCount(mutation);
	}
	
	public static void updateReportedStatus(boolean setToReported, Integer sampleID, Coordinate coordinate) throws SQLException{
		DatabaseCommands_Mutations.updateReportedStatus(setToReported, sampleID, coordinate);
	}

	public static ArrayList<GermlineMutation> getGermlineMutationsBySample(Sample sample) throws Exception{
		return DatabaseCommands_Mutations.getGermlineMutationDataByID(sample);
	}

	public static void updateGermlineCardiacAtlasInfo(GermlineMutation mutation) throws Exception{
		DatabaseCommands_Mutations.updateGermlineCardiacAtlasInfo(mutation);
	}


	/* ************************************************************************
	 * Sample Queries
	 *************************************************************************/
	public static void insertDataIntoDatabase(Sample sample) throws Exception{
		DatabaseCommands_Samples.insertDataIntoDatabase(sample);
	}
	
	public static ArrayList<Sample> getAllSamples() throws Exception{
		return DatabaseCommands_Samples.getAllSamples();
	}
	
	public static ExomeTumorMutationBurden getSampleTumorMutationBurden(TMBSample sample)throws Exception{
        return DatabaseCommands_Samples.getSampleTumorMutationBurden(sample);
    }
	
	public static void updateSampleNote(int sampleID, String newNote) throws Exception{
		DatabaseCommands_Samples.updateSampleNote(sampleID, newNote);
	}
	
	public static void updateSample(Sample sample) throws Exception{
		DatabaseCommands_Samples.updateSample(sample);
	}
	
	public static void deleteSample(int sampleID) throws SQLException{
		DatabaseCommands_Samples.deleteSample(sampleID);
	}
	
	/* ************************************************************************
	 * Annotation Queries
	 *************************************************************************/
	public static ArrayList<GeneAnnotation> getGeneAnnotationHistory(String gene) throws Exception{
		return DatabaseCommands_Annotations.getGeneAnnotationHistory(gene);
	}
	
	public static String getVariantAnnotationDraft(Coordinate coordinate) throws Exception{
		return DatabaseCommands_Annotations.getVariantAnnotationDraft(coordinate);
	}
	
	public static void addGeneAnnotationCuration(GeneAnnotation geneAnnotation) throws Exception{
		DatabaseCommands_Annotations.addGeneAnnotationCuration(geneAnnotation);
	}
	
	public static void addVariantAnnotationCuration(Annotation annotation) throws Exception{
		DatabaseCommands_Annotations.addVariantAnnotationCuration(annotation);
	}
	
	public static void addVariantAnnotationDraft(Coordinate coordinate, String draft) throws Exception{
		DatabaseCommands_Annotations.addVariantAnnotationDraft(coordinate, draft);
	}
	
	/* ************************************************************************
	 * Monitor Pipelines Queries
	 *************************************************************************/
	public static ArrayList<Pipeline> getAllPipelines() throws Exception{
		return DatabaseCommands_Pipelines.getAllPipelines();
	}
	
	public static ArrayList<PipelineStatus> getPipelineDetail(int queueID) throws Exception{
		return DatabaseCommands_Pipelines.getPipelineDetail(queueID);
	}
	
	public static float getPipelineTimeEstimate(Pipeline pipeline) throws Exception {
		return DatabaseCommands_Pipelines.getPipelineTimeEstimate(pipeline);
	}
	
	/* ************************************************************************
	 * Amplicon and QC Plot queries
	 *************************************************************************/
	public static AmpliconCount getAmpliconCount(int sampleID) throws Exception{
		return DatabaseCommands_QC.getAmpliconCount(sampleID);
	}
	
	public static ArrayList<Amplicon> getFailedAmplicon(int sampleID) throws Exception{
		return DatabaseCommands_QC.getFailedAmplicon(sampleID);
	}
	
	public static TreeMap<String, GeneQCDataElementTrend> getAmpliconQCData(String assay) throws Exception{
		return DatabaseCommands_QC.getAmpliconQCData(assay);
	}
	
	public static TreeMap<String, GeneQCDataElementTrend> getSampleQCData(String assay) throws Exception{
		return DatabaseCommands_QC.getSampleQCData(assay);
	}
}
