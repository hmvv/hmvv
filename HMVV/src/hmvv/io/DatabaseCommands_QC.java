package hmvv.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.TreeMap;

import hmvv.main.Configurations;
import hmvv.model.Amplicon;
import hmvv.model.Assay;
import hmvv.model.GeneQCDataElementTrend;
import hmvv.model.GeneTargetQC;
import hmvv.model.QCDataElement;
import hmvv.model.Sample;

public class DatabaseCommands_QC {
	private static Connection databaseConnection = null;
	static void setConnection(Connection databaseConnection) {
		DatabaseCommands_QC.databaseConnection = databaseConnection;
	}
	
	static ArrayList<Amplicon> getAmplicons(Sample sample) throws Exception{
		ArrayList<Amplicon> amplicons = new ArrayList<Amplicon>();
		PreparedStatement preparedStatement = databaseConnection.prepareStatement("select gene,ampliconName,chr,chr_start,chr_end,length,total_reads,average_depth,cumulative_depth,cov20x,cov100x,cov250x,cov500x,cov100xPercent,cov250xPercent "+
		" from sampleAmplicons where sampleID = ? ");
		preparedStatement.setInt(1, sample.sampleID);
		ResultSet resultSet = preparedStatement.executeQuery();
		while(resultSet.next()){
			
			Amplicon amplicon = new Amplicon(
				sample,
				resultSet.getString("gene"),
				resultSet.getString("ampliconName"),
				resultSet.getString("chr"),
				resultSet.getInt("chr_start"),
				resultSet.getInt("chr_end"),
				resultSet.getInt("length"),
				resultSet.getInt("total_reads"),
				resultSet.getInt("average_depth"),
				resultSet.getInt("cumulative_depth"),
				resultSet.getInt("cov20x"),
				resultSet.getInt("cov100x"),
				resultSet.getInt("cov250x"),
				resultSet.getInt("cov500x"),
				resultSet.getInt("cov100xPercent"),
				resultSet.getInt("cov250xPercent")
			);
			amplicons.add(amplicon);
		}
		preparedStatement.close();
		resultSet.close();

		return amplicons;
	}
	
	/**
	 *
	 * @param assay
	 * @return list of amplicons ordered by gene, ampliconName, then averageReadDepth
	 * @throws Exception
	 */
	static TreeMap<String, GeneQCDataElementTrend> getAmpliconQCData(Assay assay) throws Exception{
		String query = "select sampleAmplicons.sampleID, sampleAmplicons.gene, sampleAmplicons.ampliconName, sampleAmplicons.averageReadDepth from sampleAmplicons"
				+ " join samples on sampleAmplicons.sampleID = samples.sampleID"
				+ " join assays on assays.assayID = samples.assayID"
				+ " where samples.lastName like 'Horizon%' ";

		String geneFilter;
		if(assay.assayName.equals("heme")) {
			geneFilter = " and (sampleAmplicons.gene = 'BRAF' or sampleAmplicons.gene = 'KIT' or sampleAmplicons.gene = 'KRAS') and assay = 'heme'";
		}else if(assay.assayName.equals("gene50")) {
			geneFilter = " and (sampleAmplicons.gene like '%EGFR%' or sampleAmplicons.gene like '%KRAS%' or sampleAmplicons.gene like '%NRAS%') and assay = 'gene50'";
		}else if(assay.assayName.equals("neuro")) {
			//TODO the sampleAmplicons table currently does not properly store gene name
			//geneFilter = " and (sampleAmplicons.gene = 'EGFR' or sampleAmplicons.gene = 'IDH1' or sampleAmplicons.gene = 'KRAS' or sampleAmplicons.gene = 'NRAS') and assayName = 'neuro'";
			throw new Exception("Unsupported Assay: " + assay);
		}else {
			throw new Exception("Unsupported Assay: " + assay);
		}
		query += geneFilter;

		query += " order by sampleID, gene, ampliconName asc";

		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		ResultSet rs = preparedStatement.executeQuery();

		TreeMap<String, GeneQCDataElementTrend> geneAmpliconTrends = new TreeMap<String, GeneQCDataElementTrend>();

		while(rs.next()){
			String gene = rs.getString("gene");
			if(assay.assayName.equals("gene50")) {
				String[] splitGene = gene.split("_");
				if(splitGene.length == 3) {//the expected value
					gene = splitGene[1];
				}
			}
			QCDataElement amplicon = new QCDataElement(rs.getInt("sampleID"), gene, rs.getString("ampliconName"), rs.getInt("averageReadDepth"));
			GeneQCDataElementTrend geneAmpliconTrend = geneAmpliconTrends.get(amplicon.gene);
			if(geneAmpliconTrend == null) {
				geneAmpliconTrend = new GeneQCDataElementTrend(amplicon.gene);
				geneAmpliconTrends.put(amplicon.gene, geneAmpliconTrend);
			}

			geneAmpliconTrend.addDataElement(amplicon);
		}
		preparedStatement.close();

		return geneAmpliconTrends;
	}

	static ArrayList<GeneTargetQC> getGeneTargetQCData(Sample sample) throws Exception{
		ArrayList<GeneTargetQC> targets = new ArrayList<GeneTargetQC>();
		PreparedStatement preparedStatement = databaseConnection.prepareStatement("SELECT sampleID, Gene, SUM(Pass_Threshold) AS passing, COUNT(Pass_Threshold) AS total "+
		" FROM sampleAssayQC where sampleID = ? GROUP BY Gene order by Gene asc");
		preparedStatement.setInt(1, sample.sampleID);
		ResultSet resultSet = preparedStatement.executeQuery();
		while(resultSet.next()){
			GeneTargetQC target = new GeneTargetQC(
				sample,
				resultSet.getString("gene"),
				resultSet.getInt("passing"),
				resultSet.getInt("total")
			);
			targets.add(target);
		}
		preparedStatement.close();
		resultSet.close();

		return targets;
	}
	
	/**
	 *
	 * @param assay
	 * @return list of amplicons ordered by gene, ampliconName, then averageReadDepth
	 * @throws Exception
	 */
	static TreeMap<String, GeneQCDataElementTrend> getSampleQCData(Assay assay) throws Exception{
		String query = "select sampleVariants.sampleID, sampleVariants.gene, sampleVariants.HGVSc, sampleVariants.HGVSp, sampleVariants.altFreq, cosmicID"
				+ " from samples"
				+ " join sampleVariants on sampleVariants.sampleID = samples.sampleID"
				+ " join " + Configurations.COSMIC_TABLE + " on sampleVariants.chr = " + Configurations.COSMIC_TABLE + ".chr and sampleVariants.pos = " + Configurations.COSMIC_TABLE + ".pos and sampleVariants.ref = " + Configurations.COSMIC_TABLE + ".ref and sampleVariants.alt = " + Configurations.COSMIC_TABLE + ".alt "
				+ " where samples.lastName like 'Horizon%' "
				+ " and sampleVariants.HGVSp IS NOT NULL";//have to do this because old data has null values

		String geneFilter;
		if(assay.assayName.equals("heme")) {
			geneFilter =
					//COSM1140132 and COSM532 have the same coordinates and track together
					//COSM1135366 and COSM521 have the same coordinates and track together
					"   and ( cosmicID = 'COSM476' or cosmicID = 'COSM1314' or cosmicID = 'COSM521' or cosmicID = 'COSM532' "
					+ "or  cosmicID = 'COSV56056643' or cosmicID = 'COSV55386424' or cosmicID = 'COSV55497369' or cosmicID = 'COSV55497388')"
					+ " and samples.assay = 'heme' "
					+ "GROUP BY sampleVariants.sampleID,cosmicID";
		}else if(assay.assayName.equals("gene50")) {
			geneFilter =
					"   and ( cosmicID = 'COSM6252' or cosmicID = 'COSM532' or cosmicID = 'COSM580' "
					+ " or cosmicID = 'COSV51767289' or cosmicID = 'COSV55497388' or cosmicID = 'COSV54736310')"
							+ " and samples.assay = 'gene50' "
							+ "GROUP BY sampleVariants.sampleID,cosmicID";
		}else if(assay.assayName.equals("neuro")) {
			geneFilter =
					"   and ( cosmicID = 'COSM6252' or cosmicID = 'COSM97131' or cosmicID = 'COSM532' or cosmicID = 'COSM580' "
					+ " or cosmicID = 'COSV51767289' or cosmicID = 'COSV61616229' or cosmicID = 'COSV55497388' or cosmicID = 'COSV54736310')"
							+ " and samples.assay = 'neuro' "
							+ "GROUP BY sampleVariants.sampleID,cosmicID";
		}else {
			throw new Exception("Unsupported Assay: " + assay);
		}

		query += geneFilter;
		query += " order by sampleID, gene";

		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		ResultSet rs = preparedStatement.executeQuery();

		TreeMap<String, GeneQCDataElementTrend> geneVariantTrends = new TreeMap<String, GeneQCDataElementTrend>();

		while(rs.next()){
			String variant = rs.getString("HGVSp");
			variant = variant.replaceAll(".*:", "");
			variant = Configurations.abbreviationtoLetter(variant);
			variant += "(" + rs.getString("cosmicID") + ")";

			QCDataElement variantDataElement = new QCDataElement(rs.getInt("sampleID"), rs.getString("gene"), variant, Math.round(rs.getFloat("altFreq")));
			GeneQCDataElementTrend geneVariantTrend = geneVariantTrends.get(variantDataElement.gene);
			if(geneVariantTrend == null) {
				geneVariantTrend = new GeneQCDataElementTrend(variantDataElement.gene);
				geneVariantTrends.put(variantDataElement.gene, geneVariantTrend);
			}

			geneVariantTrend.addDataElement(variantDataElement);
		}
		preparedStatement.close();

		return geneVariantTrends;
	}
}
