package hmvv.io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

import java.sql.PreparedStatement;

import hmvv.main.Configurations;
import hmvv.model.*;

public class DatabaseCommands {

	private static Connection databaseConnection = null;
	static void setConnection(Connection databaseConnection) {
		DatabaseCommands.databaseConnection = databaseConnection;
	}

	public static void connect() throws Exception{
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://"+SSHConnection.getForwardingHost()+"/";
		String[] credentials = Configurations.READ_WRITE_CREDENTIALS;

		try{
			Class.forName(driver);
			Connection databaseConnection = DriverManager.getConnection(url+Configurations.DATABASE_NAME+"?noAccessToProcedureBodies=true", credentials[0], credentials[1]);
			DatabaseCommands.setConnection(databaseConnection);
			DatabaseCommands_Annotations.setConnection(databaseConnection);
			DatabaseCommands_Assays.setConnection(databaseConnection);
			DatabaseCommands_Mutations.setConnection(databaseConnection);
			DatabaseCommands_Pipelines.setConnection(databaseConnection);
			DatabaseCommands_QC.setConnection(databaseConnection);
			DatabaseCommands_Samples.setConnection(databaseConnection);
			DatabaseCommands_HGMD.setConnection(databaseConnection);
		}catch (Exception e){
			throw new Exception("mysql connection error: " + e.getMessage());
		}
	}

	public static  boolean checkDbVersion() throws Exception{
		String env = Configurations.DATABASE_NAME;
		PreparedStatement preparedStatement = databaseConnection.prepareStatement("select version from admin.hmvv_versions where environment = ? " +
		" AND expirationDate IS null ");
		preparedStatement.setString(1, env);
		ResultSet rs = preparedStatement.executeQuery();
		String version = new String();

		while(rs.next()){
			version = rs.getString(1);
		}
		preparedStatement.close();

		return (!version.equals(Configurations.DATABASE_VERSION));
		
	}
	
	/* ************************************************************************
	 * Assay Queries
	 *************************************************************************/
	public static ArrayList<Instrument> getAllInstruments() throws Exception{
		return DatabaseCommands_Assays.getAllInstruments();
	}
	
	public static ArrayList<Assay> getAllAssays() throws Exception{
		return DatabaseCommands_Assays.getAllAssays();
	}

	public static ArrayList<Assay> getAssaysForInstrument(Instrument instrument) throws Exception{
		return DatabaseCommands_Assays.getAssaysForInstrument(instrument);
	}

	public static ArrayList<Instrument> getInstrumentsForAssay(Assay assay) throws Exception{
		return DatabaseCommands_Assays.getInstrumentsForAssay(assay);
	}

	/* ************************************************************************
	 * Mutation Queries
	 *************************************************************************/
	public static ArrayList<MutationSomatic> getBaseMutationsBySample(Sample sample) throws Exception{
		return DatabaseCommands_Mutations.getBaseMutationsBySample(sample);
	}

	public static ArrayList<MutationSomatic> getExtraMutationsBySample(Sample sample) throws Exception{
		return DatabaseCommands_Mutations.getExtraMutationsBySample(sample);
	}
	
	public static ArrayList<CosmicID> getLinkedCosmicIDs(MutationSomatic mutation) throws Exception{
		return DatabaseCommands_Mutations.getLinkedCosmicIDs(mutation);
	}

	public static void updateOncokbInfo(MutationSomatic mutation) throws Exception{
		DatabaseCommands_Mutations.updateOncokbInfo(mutation);
	}

	public static void updatePmkbInfo(MutationSomatic mutation) throws Exception{
		DatabaseCommands_Mutations.updatePmkbInfo(mutation);
	}

	public static void updateCivicInfo(MutationSomatic mutation) throws Exception{
		DatabaseCommands_Mutations.updateCivicInfo(mutation);
	}
	
	public static int getOccurrenceCount(MutationCommon mutation) throws Exception{
		return DatabaseCommands_Mutations.getOccurrenceCount(mutation);
	}
	
	public static ArrayList<repeatMutations> getrepeatMutations(MutationSomatic mutation) throws Exception{
		return DatabaseCommands_Mutations.getrepeatMutations(mutation);
	}

	public static void updateReportedStatus(boolean setToReported, Integer sampleID, Coordinate coordinate) throws SQLException{
		DatabaseCommands_Mutations.updateReportedStatus(setToReported, sampleID, coordinate);
	}

	public static ArrayList<MutationGermline> getBaseGermlineMutationsBySample(Sample sample) throws Exception{
		return DatabaseCommands_Mutations.getGermlineMutationDataByID(sample,false);
	}

	public static ArrayList<MutationGermline> getExtraGermlineMutationsBySample(Sample sample) throws Exception{
		return DatabaseCommands_Mutations.getGermlineMutationDataByID(sample,true);
	}

	public static void updateGermlineCardiacAtlasInfo(MutationGermline mutation) throws Exception{
		DatabaseCommands_Mutations.updateGermlineCardiacAtlasInfo(mutation);
	}


	/* ************************************************************************
	 * Sample Queries
	 *************************************************************************/
	public static void insertbclconvertIntoDatabase(Instrument instrument, RunFolder runFolder) throws Exception{
		DatabaseCommands_Samples.insertbclconvertIntoDatabase(instrument, runFolder);
	}

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
	public static ArrayList<GeneAnnotation> getGeneAnnotationHistory(String gene, Configurations.MUTATION_TYPE mutation_type) throws Exception{
		return DatabaseCommands_Annotations.getGeneAnnotationHistory(gene,mutation_type);
	}
	
	public static String getVariantAnnotationDraft(Coordinate coordinate, Configurations.MUTATION_TYPE mutation_type) throws Exception{
		return DatabaseCommands_Annotations.getVariantAnnotationDraft(coordinate,mutation_type);
	}
	
	public static void addGeneAnnotationCuration(GeneAnnotation geneAnnotation, Configurations.MUTATION_TYPE mutation_type) throws Exception{
		DatabaseCommands_Annotations.addGeneAnnotationCuration(geneAnnotation,mutation_type);
	}
	
	public static void addVariantAnnotationCuration(Annotation annotation, Configurations.MUTATION_TYPE mutation_type) throws Exception{
		DatabaseCommands_Annotations.addVariantAnnotationCuration(annotation, mutation_type);
	}
	
	public static void addVariantAnnotationDraft(Coordinate coordinate, String draft, Configurations.MUTATION_TYPE mutation_type) throws Exception{
		DatabaseCommands_Annotations.addVariantAnnotationDraft(coordinate, draft, mutation_type);
	}
	
	/* ************************************************************************
	 * Monitor Pipelines Queries
	 *************************************************************************/
	public static ArrayList<Pipeline> getAllPipelines() throws Exception{
		return DatabaseCommands_Pipelines.getAllPipelines();
	}
	
	public static ArrayList<PipelineStatus> getPipelineDetail(Pipeline pipeline) throws Exception{
		return DatabaseCommands_Pipelines.getPipelineDetail(pipeline);
	}

	public static float getPipelineTimeEstimate(Pipeline pipeline) throws Exception {
		return DatabaseCommands_Pipelines.getPipelineTimeEstimate(pipeline);
	}
	
	/* ************************************************************************
	 * Amplicon and QC Plot queries
	 *************************************************************************/
	public static ArrayList<Amplicon> getAmplicons(Sample sample) throws Exception{
		return DatabaseCommands_QC.getAmplicons(sample);
	}
	
	public static TreeMap<String, GeneQCDataElementTrend> getAmpliconQCData(Assay assay) throws Exception{
		return DatabaseCommands_QC.getAmpliconQCData(assay);
	}
	
	public static TreeMap<String, GeneQCDataElementTrend> getSampleQCData(Assay assay) throws Exception{
		return DatabaseCommands_QC.getSampleQCData(assay);
	}

	/* ************************************************************************
	 * HGMD Database queries
	 *************************************************************************/
	public static ArrayList<MutationGermlineHGMDGeneLevel> getMutationSummaryForGene(String gene) throws Exception{
		return DatabaseCommands_HGMD.getMutationSummaryForGene(gene);
	}

	public static ArrayList<MutationGermlineHGMD> getMutationsByTable(String gene, String table) throws Exception{
		return DatabaseCommands_HGMD.getMutationsByTable(gene,table);
	}

	public static ArrayList<MutationGermlineHGMD> getAllMutationsByGene(String gene) throws Exception{
		return DatabaseCommands_HGMD.getAllMutationsByGene(gene);
	}


}
