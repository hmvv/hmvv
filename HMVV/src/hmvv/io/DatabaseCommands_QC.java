package hmvv.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.TreeMap;

import hmvv.main.Configurations;
import hmvv.model.Amplicon;
import hmvv.model.AmpliconCount;
import hmvv.model.Assay;
import hmvv.model.GeneQCDataElementTrend;
import hmvv.model.QCDataElement;

public class DatabaseCommands_QC {
	private static Connection databaseConnection = null;
	static void setConnection(Connection databaseConnection) {
		DatabaseCommands_QC.databaseConnection = databaseConnection;
	}
	
	static AmpliconCount getAmpliconCount(int sampleID) throws Exception{
		int total = -1;
		int failed = -1;

		//total
		PreparedStatement updateStatement = databaseConnection.prepareStatement("select count(*) as totalAmplicon from sampleAmplicons where sampleID = ?");
		updateStatement.setInt(1, sampleID);
		ResultSet getSampleResult = updateStatement.executeQuery();
		if(getSampleResult.next()){
			total = getSampleResult.getInt(1);
			if(getSampleResult.next()){
				throw new Exception("Error: more than one amplicon count result located for the sample");
			}
			updateStatement.close();
			getSampleResult.close();
		}

		//failed
		updateStatement = databaseConnection.prepareStatement("select count(*) as totalAmplicon from sampleAmplicons where sampleID = ?  and readDepth < ? ");
		updateStatement.setInt(1, sampleID);
		updateStatement.setInt(2, Configurations.READ_DEPTH_FILTER);

		getSampleResult = updateStatement.executeQuery();
		if(getSampleResult.next()){
			failed = getSampleResult.getInt(1);
			if(getSampleResult.next()){
				throw new Exception("Error: more than one amplicon count result located for the sample");
			}
			updateStatement.close();
			getSampleResult.close();
		}

		return new AmpliconCount(sampleID, total, failed);
	}

	static ArrayList<Amplicon> getFailedAmplicon(int sampleID) throws Exception{
		ArrayList<Amplicon> amplicons = new ArrayList<Amplicon>();
		PreparedStatement updateStatement = databaseConnection.prepareStatement("select ampliconName, gene, readDepth from sampleAmplicons where sampleID = ? and readDepth < 100");
		updateStatement.setString(1, ""+sampleID);
		ResultSet getSampleResult = updateStatement.executeQuery();
		while(getSampleResult.next()){
			Amplicon amplicon = new Amplicon(sampleID, getSampleResult.getString("gene"), getSampleResult.getString("ampliconName"), getSampleResult.getInt("readDepth"));
			amplicons.add(amplicon);
		}
		updateStatement.close();
		return amplicons;
	}
	
	/**
	 *
	 * @param assay
	 * @return list of amplicons ordered by gene, ampliconName, then readDepth
	 * @throws Exception
	 */
	static TreeMap<String, GeneQCDataElementTrend> getAmpliconQCData(Assay assay) throws Exception{
		String query = "select sampleAmplicons.sampleID, sampleAmplicons.gene, sampleAmplicons.ampliconName, sampleAmplicons.readDepth from sampleAmplicons"
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
			QCDataElement amplicon = new QCDataElement(rs.getInt("sampleID"), gene, rs.getString("ampliconName"), rs.getInt("readDepth"));
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
	
	/**
	 *
	 * @param assay
	 * @return list of amplicons ordered by gene, ampliconName, then readDepth
	 * @throws Exception
	 */
	static TreeMap<String, GeneQCDataElementTrend> getSampleQCData(Assay assay) throws Exception{
		String query = "select sampleVariants.sampleID, sampleVariants.gene, sampleVariants.HGVSc, sampleVariants.HGVSp, sampleVariants.altFreq, cosmicID"
				+ " from samples"
				+ " join sampleVariants on sampleVariants.sampleID = samples.sampleID"
				+ " join db_cosmic_grch37v86 on sampleVariants.chr = db_cosmic_grch37v86.chr and sampleVariants.pos = db_cosmic_grch37v86.pos and sampleVariants.ref = db_cosmic_grch37v86.ref and sampleVariants.alt = db_cosmic_grch37v86.alt "
				+ " where samples.lastName like 'Horizon%' "
				+ " and HGVSp IS NOT NULL";//have to do this because old data has null values

		String geneFilter;
		if(assay.assayName.equals("heme")) {
			geneFilter =
					//COSM1140132 and COSM532 have the same coordinates and track together
					//COSM1135366 and COSM521 have the same coordinates and track together
					"   and ( cosmicID = 'COSM476' or cosmicID = 'COSM1314' or cosmicID = 'COSM521' or cosmicID = 'COSM532')"
					+ " and samples.assay = 'heme' ";
		}else if(assay.assayName.equals("gene50")) {
			geneFilter =
					"   and ( cosmicID = 'COSM6252' or cosmicID = 'COSM532' or cosmicID = 'COSM580')"
							+ " and samples.assay = 'gene50' ";
		}else if(assay.assayName.equals("neuro")) {
			geneFilter =
					"   and ( cosmicID = 'COSM6252' or cosmicID = 'COSM97131' or cosmicID = 'COSM532' or cosmicID = 'COSM580')"
							+ " and samples.samples = 'neuro' ";
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
