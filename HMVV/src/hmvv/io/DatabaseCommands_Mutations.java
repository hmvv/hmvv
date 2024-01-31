package hmvv.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import hmvv.main.Configurations;
import hmvv.model.*;

public class DatabaseCommands_Mutations {

	private static Connection databaseConnection = null;
	static void setConnection(Connection databaseConnection) {
		DatabaseCommands_Mutations.databaseConnection = databaseConnection;
	}
	
	static ArrayList<MutationSomatic> getBaseMutationsBySample(Sample sample) throws Exception{
		ArrayList<MutationSomatic> mutations = getMutationDataByID(sample, false);
		addReportedMutationsByMRN(sample, mutations);		
		return mutations;
	}

	static ArrayList<MutationSomatic> getExtraMutationsBySample(Sample sample) throws Exception{
		ArrayList<MutationSomatic> mutations = getMutationDataByID(sample, true);
		addReportedMutationsByMRN(sample, mutations);		
		return mutations;
	}

	private static ArrayList<MutationSomatic> getMutationDataByID(Sample sample, boolean getFilteredData) throws Exception{
		String query = "WITH " 
				+ " OTHER_VARIANT_COUNT AS (SELECT chr, pos, ref, alt, COUNT(*) AS variantRepeatCount "
				+ " from sampleVariants "
				+ " inner join samples ON sampleVariants.sampleID = samples.sampleID "
				+ " where "
				+ " samples.runFolderName = ? " 
				+ " and samples.sampleID != ? " 
		 		+ " group by CHR, pos, ref, alt), "
				+ " all_annotations AS (SELECT annotationID,chr,pos,ref,alt, classification,curation, "
				+ " somatic, enteredBy, enterDate, "
				+ " ROW_NUMBER() over (PARTITION BY CHR,pos,ref,alt ORDER BY enterDate DESC) AS rownum "
				+ " FROM " + Configurations.SOMATIC_VARIANT_ANNOTATION_TABLE + " )"
		
		
				+ " select t2.sampleID, t2.reported, t2.gene, t2.exon, t2.chr, t2.pos, t2.ref, t2.alt,"
				+ " t2.impact,t2.type, t2.altFreq, t2.readDepth, t2.altReadDepth, t2.occurrenceCount, t2.VarScanVAF, t2.Mutect2VAF, t2.freebayesVAF, "
				+ " t2.consequence, t2.Sift, t2.PolyPhen,t2.HGVSc, t2.HGVSp, t2.dbSNPID, t2.COSMIC_pipeline, t2.COSMIC_VEP, t2.OncoKB, t2.pubmed,"
				+ " t1.lastName, t1.firstName, t1.orderNumber, t1.assay, t1.tumorSource, t1.tumorPercent,"
				+ " t4.altCount, t4.totalCount, t4.altGlobalFreq, t4.americanFreq, t4.eastAsianFreq,t4.southAsianFreq, t4.afrFreq, t4.eurFreq,"
				+ " t5.clinvarID, t5.cln_disease, t5.cln_significance, t5.cln_consequence,t5.cln_origin, occ.variantRepeatCount, "
				+ " t8.AF, t9.annotationID,t9.curation, t9.somatic,t9.classification, t9.enteredBy, t9.enterDate "
				+ " from sampleVariants as t2"
				+ " join samples as t1 on t2.sampleID = t1.sampleID "
				+ " left join " + Configurations.G1000_TABLE + " as t4"
				+ " on t2.chr = t4.chr and t2.pos = t4.pos and t2.ref = t4.ref and t2.alt = t4.alt"
				+ " left join " + Configurations.CLINVAR_TABLE + " as t5"
				+ " on t2.chr = t5.chr and t2.pos = t5.pos and t2.ref = t5.ref and t2.alt = t5.alt"
				+ " left join " + Configurations.GNOMAD_TABLE + " as t8 on t2.chr = t8.chr and t2.pos = t8.pos and t2.ref = t8.ref and t2.alt = t8.alt "
				+ " left join OTHER_VARIANT_COUNT AS occ ON t2.chr = occ.chr AND t2.pos = occ.pos AND t2.ref = occ.ref AND t2.alt = occ.alt"
				+ " LEFT JOIN (SELECT * FROM all_annotations WHERE rownum = 1) AS t9 ON t9.chr = t2.chr and t9.pos = t2.pos AND t9.ref = t2.ref AND t9.alt = t2.alt"
				+ " where t2.sampleID = ? ";
		//				+ " and t2.exon != '' ";//Filter the introns
		String where = " ( (t2.impact = 'HIGH' or t2.impact = 'MODERATE') and t2.altFreq >= " + Configurations.getDefaultAlleleFrequencyFilter(sample) + " and t2.readDepth >= " + Configurations.getDefaultReadDepthFilter(sample) + ")";
		if(getFilteredData) {
			where = " !" + where;
		}
		query = query + " and " + where ;
		
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, ""+sample.runFolder.runFolderName);
		preparedStatement.setString(2, ""+sample.sampleID);
		preparedStatement.setString(3, ""+sample.sampleID);
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<MutationSomatic> mutations = makeModel(rs);
		preparedStatement.close();
		return mutations;
	}


	public static ArrayList<MutationGermline> getGermlineMutationDataByID(Sample sample, boolean getFilteredData) throws Exception{
		String query = "select t2.sampleID, t2.reported, t2.gene, t2.exon, t2.chr, t2.pos, t2.ref, t2.alt,"
				+ " t2.impact,t2.type, t2.altFreq, t2.readDepth, t2.altReadDepth, t2.occurrenceCount, "
				+ " t2.consequence,t2.HGVSc, t2.HGVSp, t2.STRAND, t2.ALT_TRANSCRIPT_START, t2.ALT_TRANSCRIPT_END,t2.ALT_VARIANT_POSITION,"
				+ " t2.protein_id, t2.protein_type, t2.protein_feature, t2.protein_note, t2.protein_start, t2.protein_end ,"
				+ " t2.nextprot,t2.uniprot_id, t2.pfam, t2.scoop, t2.uniprot_variant,t2.expasy_id,"
				+ " t2.revel,t2.cadd_phred, t2.canonical, t2.sift,t2.polyphen,"
				+ " t2.phastCons100, t2.phyloP100, t2.phastCons20, t2.phyloP20, t2.GERP_RS,"
		        + "  t2.hgmd_ID, t2.hgmd_Variant, t2.hgmd_AAchange, t2.hgmd_Disease, t2.hgmd_Category, t2.hgmd_PMID,t2.hgmd_Citation,t2.hgmd_ExtraCitations,"
				+ " t1.lastName, t1.firstName, t1.orderNumber, t1.assay, "
				+ " t5.clinvarID, t5.cln_disease, t5.cln_significance, t5.cln_consequence,t5.cln_origin, "
				+ " t8.AF, t8.AF_afr, t8.AF_amr, t8.AF_asj, t8.AF_eas, t8.AF_fin, t8.AF_nfe, t8.AF_sas, t8.AF_oth, t8.AF_male, t8.AF_female "
				+ " from sampleVariantsGermline as t2"
				+ " join samples as t1 on t2.sampleID = t1.sampleID "
				+ " left join " + Configurations.CLINVAR_TABLE + " as t5"
				+ " on t2.chr = t5.chr and t2.pos = t5.pos and t2.ref = t5.ref and t2.alt = t5.alt"
				+ " left join " + Configurations.GNOMAD_LF_TABLE + " as t8 on t2.chr = t8.chr and t2.pos = t8.pos and t2.ref = t8.ref and t2.alt = t8.alt "
				+ " where t2.sampleID = ? ";
		String where = " ( t2.altFreq >= " + Configurations.GERMLINE_ALLELE_FREQ_FILTER +
				" and t2.readDepth >= " + Configurations.GERMLINE_READ_DEPTH_FILTER +
				" and t8.AF <= " + Configurations.GERMLINE_GNOMAD_MAX_GLOBAL_ALLELE_FREQ_FILTER +  ")";
		if(getFilteredData) {
			where = " !" + where;
		}
		query = query + " and " + where ;
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, ""+sample.sampleID);
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<MutationGermline> mutations = makeGermlineModel(rs);
		preparedStatement.close();
		return mutations;
	}
	/**
	 * Get all reported mutations from the MRN on the provided sample
	 * @param sample
	 * @return
	 * @throws Exception
	 */
	private static void addReportedMutationsByMRN(Sample sample, ArrayList<MutationSomatic> mutations) throws Exception {
		ArrayList<MutationSomatic> reportedMutations = new ArrayList<MutationSomatic>();
		for(Sample otherSample : sample.getLinkedPatientSamples()) {
			reportedMutations.addAll(getMutationDataByID(otherSample, false));
		}

		for(MutationSomatic mutation : mutations) {
			for(MutationSomatic other : reportedMutations) {
				if(mutation.equals(other)) {
					mutation.addOtherMutation(other);
				}
			}
		}
	}

	/**
	 * Acquires the cosmicID from the database. If it isn't found, an empty array is returned
	 */
	static ArrayList<CosmicIdentifier> getLinkedCosmicIDs(MutationSomatic mutation) throws Exception{
		Coordinate coordinate = mutation.getCoordinate();
		String query = "select cosmicID, gene, strand, genomic_ID, legacyID, CDS, AA, HGVSc, HGVSp, HGVSg, old_variant from " + Configurations.COSMIC_TABLE + " where chr = ? and pos = ? and ref = ? and alt = ?";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, coordinate.getChr());
		preparedStatement.setString(2, coordinate.getPos());
		preparedStatement.setString(3, coordinate.getRef());
		preparedStatement.setString(4, coordinate.getAlt());
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<CosmicIdentifier> cosmicIDs = new ArrayList<CosmicIdentifier>();

		while(rs.next()){
			CosmicIdentifier cosmicID = new CosmicIdentifier(
				getStringOrBlank(rs, "cosmicID"),
				coordinate,
				getStringOrBlank(rs, "gene"),
				// getStringOrBlank(rs, "strand"),
				// getStringOrBlank(rs, "genomic_ID"),
				// getStringOrBlank(rs, "legacyID"),
				// getStringOrBlank(rs, "CDS"),
				// getStringOrBlank(rs, "AA"),
				// getStringOrBlank(rs, "HGVSc"),
				// getStringOrBlank(rs, "HGVSp"),
				// getStringOrBlank(rs, "HGVSg"),
				// getStringOrBlank(rs, "old_variant"),
				"Linked"
			);
			cosmicIDs.add(cosmicID);
		}
		preparedStatement.close();
		return cosmicIDs;
	}




	static void updateOncokbInfo(MutationSomatic mutation) throws Exception{
		String ENST_id = mutation.getHGVSc().split("\\.")[0];
		String ENSP = "";
		String[] getHGVSpArray = mutation.getHGVSp().split("\\.");
		if(getHGVSpArray.length > 1) {
			ENSP = getHGVSpArray[2];
		}else {
			//TODO What to do here?
			return;
		}

		String query = "select Protein_Change,Protein_Change_LF,Oncogenicity, Mutation_Effect from " + Configurations.ONCOKB_TABLE + " where Isoform = ? and  Gene = ? and Protein_Change_LF = ? limit 1";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, ENST_id);
		preparedStatement.setString(2, mutation.getGene());
		preparedStatement.setString(3, ENSP);
		ResultSet rs = preparedStatement.executeQuery();
		if(rs.next()){
			mutation.setOnco_Protein_Change(getStringOrBlank(rs, "Protein_Change"));
			mutation.setOnco_Protein_Change_LF(getStringOrBlank(rs, "Protein_Change_LF"));
			mutation.setOncogenicity(getStringOrBlank(rs, "Oncogenicity"));
			mutation.setOnco_MutationEffect(getStringOrBlank(rs, "Mutation_Effect"));
		}
		preparedStatement.close();
	}

	static void updatePmkbInfo(MutationSomatic mutation) throws Exception{
		String ENSP = "";
		String[] getHGVSpArray = mutation.getHGVSp().split("\\.");
		if(getHGVSpArray.length > 1) {
			ENSP = Configurations.abbreviationtoLetter(getHGVSpArray[2]);
		}else {
			//TODO What to do here?
			return;
		}
		String query = "select tumor_type,tissue_type from " + Configurations.PMKB_TABLE + " where gene = ? and variant = ? limit 1";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, mutation.getGene());
		preparedStatement.setString(2, ENSP);
		ResultSet rs = preparedStatement.executeQuery();
		if(rs.next()){
			mutation.setPmkb_tumor_type(getStringOrBlank(rs, "tumor_type"));
			mutation.setPmkb_tissue_type(getStringOrBlank(rs, "tissue_type"));
		}
		preparedStatement.close();
	}

	static void updateCivicInfo(MutationSomatic mutation) throws Exception{
		String ENSP = "";
		String[] getHGVSpArray = mutation.getHGVSp().split("\\.");
		if(getHGVSpArray.length > 1) {
			ENSP = getHGVSpArray[2];
		}else {
			//TODO What to do here?
			return;
		}

		String query = "select variant_origin,variant_civic_url from " + Configurations.CIVIC_TABLE + " where gene = ? and variant_LF = ? limit 1 ";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, mutation.getGene());
		preparedStatement.setString(2, ENSP);
		ResultSet rs = preparedStatement.executeQuery();
		if(rs.next()){
			mutation.setCivic_variant_origin(getStringOrBlank(rs, "variant_origin"));
			mutation.setCivic_variant_url(getStringOrBlank(rs, "variant_civic_url"));
		}
		preparedStatement.close();
	}


	static void updateGermlineCardiacAtlasInfo(MutationGermline mutation) throws Exception{
		String[] getHGVScArray = mutation.getHGVSc().split("\\:");
		String cds_variant = getHGVScArray[1];

		String query = "select gene, cds_variant,protein_variant,variant_type from " + Configurations.CARDIAC_TABLE + " where gene = ? and cds_variant = ? limit 1";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, mutation.getGene());
		preparedStatement.setString(2, cds_variant);
		ResultSet rs = preparedStatement.executeQuery();
		if(rs.next()){
			mutation.setCardiacAtlasId(getStringOrBlank(rs, "gene"));
			mutation.setCds_variant(getStringOrBlank(rs, "cds_variant"));
			mutation.setProtein_variant(getStringOrBlank(rs, "protein_variant"));
			mutation.setVariant_type(getStringOrBlank(rs, "variant_type"));

		}
		preparedStatement.close();
	}

	/**
	 * Acquires the number of occurrences of this mutation from the database.
	 */
	static int getOccurrenceCount(MutationCommon mutation) throws Exception{
        Coordinate coordinate = mutation.getCoordinate();
		String query = "select count as occurrence "
					+ " from occurrenceCount"
					+ " where chr = ? and pos = ? and ref = ? and alt = ? and assay = ?";

		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, coordinate.getChr());
		preparedStatement.setString(2, coordinate.getPos());
		preparedStatement.setString(3, coordinate.getRef());
		preparedStatement.setString(4, coordinate.getAlt());
		preparedStatement.setString(5, mutation.getAssay().assayName);

		ResultSet rs = preparedStatement.executeQuery();
		if(rs.next()){
			String result = rs.getString(1);
			preparedStatement.close();
			//not checking for errors because query is count(*), so this should work
			return Integer.parseInt(result);
		}else{
			preparedStatement.close();
			return 0;
		}
	}
	static void updateReportedStatus(boolean setToReported, Integer sampleID, Coordinate coordinate) throws SQLException{
		String reported = (setToReported) ? "1" : "0";
		PreparedStatement updateStatement = databaseConnection.prepareStatement(
				"update sampleVariants set reported = ? where sampleID = ? and chr = ? and pos = ? and ref = ? and alt = ?");
		updateStatement.setString(1, reported);
		updateStatement.setString(2, sampleID.toString());
		updateStatement.setString(3, coordinate.getChr());
		updateStatement.setString(4, coordinate.getPos());
		updateStatement.setString(5, coordinate.getRef());
		updateStatement.setString(6, coordinate.getAlt());
		updateStatement.executeUpdate();
		updateStatement.close();
	}

	private static ArrayList<MutationSomatic> makeModel(ResultSet rs) throws Exception{
		ArrayList<MutationSomatic> mutations = new ArrayList<MutationSomatic>();

		while(rs.next()){
			MutationSomatic mutation = new MutationSomatic();

			//common
			boolean reported = Integer.parseInt(rs.getString("reported")) != 0;
			mutation.setReported(reported);
			mutation.setGene(getStringOrBlank(rs, "gene"));
			mutation.setExons(getStringOrBlank(rs, "exon"));
			mutation.setChr(getStringOrBlank(rs, "chr"));
			mutation.setPos(getStringOrBlank(rs, "pos"));
			mutation.setRef(getStringOrBlank(rs, "ref"));
			mutation.setAlt(getStringOrBlank(rs, "alt"));
			VariantPredictionClass variantPredictionClass = VariantPredictionClass.createPredictionClass(getStringOrBlank(rs, "impact"));
			mutation.setVariantPredictionClass(variantPredictionClass);
			mutation.setType(getStringOrBlank(rs, "type"));
			mutation.setAltFreq(getDoubleOrNull(rs, "altFreq"));
			mutation.setReadDP(getIntegerOrNull(rs, "readDepth"));
			mutation.setAltReadDP(getIntegerOrNull(rs, "altReadDepth"));
			mutation.setConsequence(getStringOrBlank(rs, "consequence"));
			mutation.setSift(getStringOrBlank(rs, "Sift"));
			mutation.setPolyPhen(getStringOrBlank(rs, "PolyPhen"));
			mutation.setHGVSc(rs.getString("HGVSc"));
			mutation.setHGVSp(rs.getString("HGVSp"));
			mutation.setDbSNPID(getStringOrBlank(rs, "dbSNPID"));
			mutation.setPubmed(getStringOrBlank(rs, "pubmed"));

			//Sample
			mutation.setLastName(getStringOrBlank(rs, "lastName"));
			mutation.setFirstName(getStringOrBlank(rs, "firstName"));
			mutation.setOrderNumber(getStringOrBlank(rs, "orderNumber"));
			mutation.setAssay(Assay.getAssay(getStringOrBlank(rs, "assay")));
			mutation.setSampleID(getIntegerOrNull(rs, "sampleID"));
			mutation.setTumorSource(getStringOrBlank(rs, "tumorSource"));
			mutation.setTumorPercent(getStringOrBlank(rs, "tumorPercent"));

			//G1000
			mutation.setAltCount(getIntegerOrNull(rs, "altCount"));
			mutation.setTotalCount(getIntegerOrNull(rs, "totalCount"));
			mutation.setAltGlobalFreq(getDoubleOrNull(rs, "altGlobalFreq"));
			mutation.setAmericanFreq(getDoubleOrNull(rs, "americanFreq"));
			mutation.setEastAsianFreq(getDoubleOrNull(rs, "eastAsianFreq"));
			mutation.setSouthAsianFreq(getDoubleOrNull(rs, "southAsianFreq"));
			mutation.setAfricanFreq(getDoubleOrNull(rs, "afrFreq"));
			mutation.setEurFreq(getDoubleOrNull(rs, "eurFreq"));

			//ClinVar
			mutation.setClinvarID(getStringOrBlank(rs, "clinvarID"));
			mutation.setClinicaldisease(getStringOrBlank(rs, "cln_disease"));
			mutation.setClinicalsignificance(getStringOrBlank(rs, "cln_significance"));
			mutation.setClinicalconsequence(getStringOrBlank(rs, "cln_consequence"));
			mutation.setClinicalorigin(getStringOrBlank(rs, "cln_origin"));
			
			//COSMIC
			ArrayList<CosmicIdentifier> pipelineCosmicIDList = parseCosmicIDsFromDelimiter(getStringOrBlank(rs, "COSMIC_pipeline"), "&", "Pipeline",mutation);
			mutation.addCosmicIDsPipeline(pipelineCosmicIDList);
			ArrayList<CosmicIdentifier> vepCosmicIDList = parseCosmicIDsFromDelimiter(getStringOrBlank(rs, "COSMIC_VEP"), "&", "VEP",mutation);
			vepCosmicIDList.removeAll(pipelineCosmicIDList);
			mutation.addVEPCosmicIDs(vepCosmicIDList);
					
			//temp holder fields - filled later separately
			mutation.addCosmicIDLoading();
			mutation.setOccurrence(getIntegerOrNull(rs, "occurrenceCount"));
			mutation.setVarScanVAF(getDoubleOrNull(rs, "VarScanVAF"));
			mutation.setMutect2VAF(getDoubleOrNull(rs, "Mutect2VAF"));
			mutation.setfreebayesVAF(getDoubleOrNull(rs, "freebayesVAF"));
			mutation.setvariantRepeatCount(getIntegerOrNull(rs, "variantRepeatCount"));
			

			//annotation history
			//Integer annotationID, Coordinate cordinate, String classification, String curation, String somatic, String enteredBy, Date enterDate
			
			if ( getIntegerOrNull(rs, "annotationID") != null){
				Annotation latestAnnotation = new Annotation(getIntegerOrNull(rs, "annotationID"), mutation.getCoordinate(), rs.getString("classification") ,rs.getString("curation"), 
															rs.getString("somatic"), rs.getString("enteredBy"),rs.getDate("enterDate") );
				mutation.setLatestAnnotation(latestAnnotation);
			};
			
			//gnomad
			Double gnomadAllFreq = getDoubleOrNull(rs, "AF");
			if(gnomadAllFreq != null) {
				mutation.setGnomad_allfreq(gnomadAllFreq);
			}

			mutations.add(mutation);
		}
		return mutations;
	}

	
    private static ArrayList<CosmicIdentifier> parseCosmicIDsFromDelimiter(String cosmicIDString, String separator, String source, MutationSomatic mutation) throws Exception{
		ArrayList<CosmicIdentifier> cosmicIDList = new ArrayList<CosmicIdentifier>();
        String[] cosmicIDs = cosmicIDString.split(separator);
        for(String cosmicID : cosmicIDs){
            if(cosmicID.equals("")){
                continue;
            }

			CosmicIdentifier cosmicIDObject = new CosmicIdentifier(cosmicID, mutation.getCoordinate(), mutation.getGene(), source);
            cosmicIDList.add(cosmicIDObject);
        }
		return cosmicIDList;
    }

	static ArrayList<CosmicID> getCosmicIDInfo(CosmicIdentifier cosmicID) throws Exception{
		String query = "select cosmicID, chr, pos, ref, alt, gene, strand, genomic_ID, legacyID, CDS, AA, HGVSc, HGVSp, HGVSg, old_variant from " + Configurations.COSMIC_TABLE + " where cosmicID = ? or legacyID = ?";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, cosmicID.cosmicID);
		preparedStatement.setString(2, cosmicID.cosmicID);
		ResultSet rs = preparedStatement.executeQuery();
		ArrayList<CosmicID> cosmicIDs = new ArrayList<CosmicID>();
		while(rs.next()){
			Coordinate coordinate = new Coordinate(
			getStringOrBlank(rs, "chr"),
			getStringOrBlank(rs, "pos"),
			getStringOrBlank(rs, "ref"),
			getStringOrBlank(rs, "alt")
			);


			CosmicID cosmicIDObj = new CosmicID(
				getStringOrBlank(rs, "cosmicID"),
				coordinate,
				getStringOrBlank(rs, "gene"),
				getStringOrBlank(rs, "strand"),
				getStringOrBlank(rs, "genomic_ID"),
				getStringOrBlank(rs, "legacyID"),
				getStringOrBlank(rs, "CDS"),
				getStringOrBlank(rs, "AA"),
				rs.getString( "HGVSc"),
				rs.getString("HGVSp"),
				getStringOrBlank(rs, "HGVSg"),
				getStringOrBlank(rs, "old_variant"),
				cosmicID.source
				
			);
			cosmicIDs.add(cosmicIDObj);
		}
		preparedStatement.close();
		return cosmicIDs;
	}

	static ArrayList<repeatMutations> getrepeatMutations(MutationSomatic mutation) throws Exception{
		String queryRunforlderName = "SELECT runFolderName FROM samples WHERE sampleID = ?";
		PreparedStatement preparedStatementRunfolderName = databaseConnection.prepareStatement(queryRunforlderName);
		preparedStatementRunfolderName.setString(1, mutation.getSampleID().toString());
		ResultSet rs = preparedStatementRunfolderName.executeQuery();

		String runFolderName = "";
		if(rs.next()){
			runFolderName = rs.getString(1);
			preparedStatementRunfolderName.close();
		}
		
		

		String queryRepeatMutations = "SELECT s.sampleID, s.sampleName, s.lastName, s.firstName, sv.altFreq, sv.readDepth, sv.altReadDepth" +
		" from sampleVariants sv " +
		" inner join samples s ON sv.sampleID = s.sampleID   " +
		" where   " +
		" s.runFolderName = ?  and s.sampleID != ?  and sv.CHR = ? and sv.pos = ? and sv.ref = ? and sv.alt = ?"; 

		PreparedStatement preparedStatementRepeatMutations = databaseConnection.prepareStatement(queryRepeatMutations);
		preparedStatementRepeatMutations.setString(1, runFolderName);
		preparedStatementRepeatMutations.setString(2, mutation.getSampleID().toString());
		preparedStatementRepeatMutations.setString(3, mutation.getChr().toString());
		preparedStatementRepeatMutations.setString(4, mutation.getPos().toString());
		preparedStatementRepeatMutations.setString(5, mutation.getRef().toString());
		preparedStatementRepeatMutations.setString(6, mutation.getAlt().toString());

		ResultSet rsRepeatMutations = preparedStatementRepeatMutations.executeQuery();
		

		ArrayList<repeatMutations> repeatMutations = new ArrayList<repeatMutations>();

		while(rsRepeatMutations.next()){
			repeatMutations repeatMutationsObj = new repeatMutations(
				rsRepeatMutations.getString("sampleID"),
				getStringOrBlank(rsRepeatMutations, "sampleName"),
				getStringOrBlank(rsRepeatMutations, "lastName"),
				getStringOrBlank(rsRepeatMutations, "firstName"),
				getDoubleOrNull(rsRepeatMutations,"altFreq"),
				rsRepeatMutations.getInt("readDepth"),
				rsRepeatMutations.getInt("altReadDepth"));

			repeatMutations.add(repeatMutationsObj);
		}
		preparedStatementRepeatMutations.close();

		return repeatMutations;

	}

	private static ArrayList<MutationGermline> makeGermlineModel(ResultSet rs) throws Exception{
		ArrayList<MutationGermline> mutations = new ArrayList<MutationGermline>();

		while(rs.next()){
			MutationGermline mutation = new MutationGermline();

			//common
			boolean reported = Integer.parseInt(rs.getString("reported")) != 0;
			mutation.setReported(reported);
			mutation.setGene(getStringOrBlank(rs, "gene"));
			mutation.setExons(getStringOrBlank(rs, "exon"));
			mutation.setChr(getStringOrBlank(rs, "chr"));
			mutation.setPos(getStringOrBlank(rs, "pos"));
			mutation.setRef(getStringOrBlank(rs, "ref"));
			mutation.setAlt(getStringOrBlank(rs, "alt"));
			VariantPredictionClass variantPredictionClass = VariantPredictionClass.createPredictionClass(getStringOrBlank(rs, "impact"));
			mutation.setVariantPredictionClass(variantPredictionClass);
			mutation.setType(getStringOrBlank(rs, "type"));
			mutation.setAltFreq(getDoubleOrNull(rs, "altFreq"));
			mutation.setReadDP(getIntegerOrNull(rs, "readDepth"));
			mutation.setAltReadDP(getIntegerOrNull(rs, "altReadDepth"));
			mutation.setConsequence(getStringOrBlank(rs, "consequence"));
			mutation.setHGVSc(getStringOrBlank(rs, "HGVSc"));
			mutation.setHGVSp(getStringOrBlank(rs, "HGVSp"));

			//transcript
			mutation.setTranscript_strand(getStringOrBlank(rs,"STRAND"));
			mutation.setAlt_transcript_start(getStringOrBlank(rs,"ALT_TRANSCRIPT_START"));
			mutation.setAlt_transcript_end(getStringOrBlank(rs,"ALT_TRANSCRIPT_END"));
			mutation.setAlt_transcript_position(getStringOrBlank(rs,"ALT_VARIANT_POSITION"));

			//protein
			mutation.setProtein_id(getStringOrBlank(rs,"protein_id"));
			mutation.setProtein_type(getStringOrBlank(rs,"protein_type"));
			mutation.setProtein_feature(getStringOrBlank(rs,"protein_feature"));
			mutation.setProtein_note(getStringOrBlank(rs,"protein_note"));
			mutation.setProtein_start(getDoubleOrNull(rs,"protein_start"));
			mutation.setProtein_end(getDoubleOrNull(rs,"protein_end"));
			mutation.setNextprot(getStringOrBlank(rs,"nextprot"));
			mutation.setUniprot_id(getStringOrBlank(rs,"uniprot_id"));
			mutation.setPfam(getStringOrBlank(rs,"pfam"));
			mutation.setScoop(getStringOrBlank(rs,"scoop"));
			mutation.setUniprot_variant(getStringOrBlank(rs,"uniprot_variant"));
			mutation.setExpasy_id(getStringOrBlank(rs,"expasy_id"));

			//prediction
			mutation.setRevel(getStringOrBlank(rs,"revel"));
			mutation.setCadd_phred(getStringOrBlank(rs,"cadd_phred"));
			mutation.setCanonical(getStringOrBlank(rs,"canonical"));
			mutation.setSift(getStringOrBlank(rs,"sift"));
			mutation.setPolyphen(getStringOrBlank(rs,"polyphen"));

			//conservation
			mutation.setPhastCons100(getStringOrBlank(rs,"phastCons100"));
			mutation.setPhyloP100(getStringOrBlank(rs,"phyloP100"));
			mutation.setPhastCons20(getStringOrBlank(rs,"phastCons20"));
			mutation.setPhyloP20(getStringOrBlank(rs,"phyloP20"));
			mutation.setGERP_RS(getStringOrBlank(rs,"GERP_RS"));

			//Sample
			mutation.setLastName(getStringOrBlank(rs, "lastName"));
			mutation.setFirstName(getStringOrBlank(rs, "firstName"));
			mutation.setOrderNumber(getStringOrBlank(rs, "orderNumber"));
			mutation.setAssay(Assay.getAssay(getStringOrBlank(rs, "assay")));
			mutation.setSampleID(getIntegerOrNull(rs, "sampleID"));


			//ClinVar
			mutation.setClinvarID(getStringOrBlank(rs, "clinvarID"));
			mutation.setClinicaldisease(getStringOrBlank(rs, "cln_disease"));
			mutation.setClinicalsignificance(getStringOrBlank(rs, "cln_significance"));
			mutation.setClinicalconsequence(getStringOrBlank(rs, "cln_consequence"));
			mutation.setClinicalorigin(getStringOrBlank(rs, "cln_origin"));

			//HGMD mutation
			MutationGermlineHGMD mutationGermlineHGMD = new MutationGermlineHGMD(getStringOrBlank(rs, "hgmd_ID"));
			mutation.setMutationGermlineHGMD(mutationGermlineHGMD);
			mutationGermlineHGMD.setVariant(getStringOrBlank(rs,"hgmd_Variant"));
			mutationGermlineHGMD.setAAchange(getStringOrBlank(rs,"hgmd_AAchange"));
			mutationGermlineHGMD.setDisease(getStringOrBlank(rs,"hgmd_Disease"));
			mutationGermlineHGMD.setCategory(getStringOrBlank(rs,"hgmd_Category"));
			mutationGermlineHGMD.setPmid(getStringOrBlank(rs,"hgmd_PMID"));
			mutationGermlineHGMD.setPmid_info(getStringOrBlank(rs,"hgmd_Citation"));
			mutationGermlineHGMD.setExtra_pmids(getStringOrBlank(rs,"hgmd_ExtraCitations"));


			//temp holder fields - filled later separately
			//mutation.setOccurrence(getIntegerOrNull(rs, "occurrence"));


			//annotation history
			//ArrayList<Annotation> annotationHistory = getVariantAnnotationHistory(mutation.getCoordinate(),Configurations.MUTATION_TYPE.GERMLINE);
			//mutation.setAnnotationHistory(annotationHistory);

			//gnomad
			Double gnomadAllFreq = getDoubleOrNull(rs, "AF");
			if(gnomadAllFreq != null) {
				mutation.setGnomad_allfreq(gnomadAllFreq);

				mutation.setGnomad_allfreq_afr(getDoubleOrNull(rs, "AF_afr"));
				mutation.setGnomad_allfreq_amr(getDoubleOrNull(rs, "AF_amr"));
				mutation.setGnomad_allfreq_asj(getDoubleOrNull(rs, "AF_asj"));
				mutation.setGnomad_allfreq_eas(getDoubleOrNull(rs, "AF_eas"));
				mutation.setGnomad_allfreq_fin(getDoubleOrNull(rs, "AF_fin"));
				mutation.setGnomad_allfreq_nfe(getDoubleOrNull(rs, "AF_nfe"));
				mutation.setGnomad_allfreq_sas(getDoubleOrNull(rs, "AF_sas"));
				mutation.setGnomad_allfreq_oth(getDoubleOrNull(rs, "AF_oth"));
				mutation.setGnomad_allfreq_male(getDoubleOrNull(rs, "AF_male"));
				mutation.setGnomad_allfreq_female(getDoubleOrNull(rs, "AF_female"));
			}
			mutations.add(mutation);
		}
		return mutations;
	}

	private static String getStringOrBlank(ResultSet rs, String columnLabel){
		try{
			if( rs.getString(columnLabel) == null) {
				return "";
			}
			return rs.getString(columnLabel).replaceAll("_"," ");
		}catch(Exception e){
			return "";
		}
	}

	private static Integer getIntegerOrNull(ResultSet rs, String columnLabel){
		try{
			String value = rs.getString(columnLabel);
			return Integer.parseInt(value);
		}catch(Exception e){
			return null;
		}
	}

	private static Double getDoubleOrNull(ResultSet rs, String columnLabel){
		try{
			String value = rs.getString(columnLabel);
			return Double.parseDouble(value);
		}catch(Exception e){
			return null;
		}
	}
	
	static ArrayList<Annotation> getVariantAnnotationHistory(Coordinate coordinate, Configurations.MUTATION_TYPE mutation_type) throws Exception{

		ArrayList<Annotation> annotations = new ArrayList<Annotation>() ;

		String tablename = "";
		if (mutation_type == Configurations.MUTATION_TYPE.SOMATIC) {
			tablename = Configurations.SOMATIC_VARIANT_ANNOTATION_TABLE;
		} else if (mutation_type == Configurations.MUTATION_TYPE.GERMLINE) {
			tablename = Configurations.GERMLINE_VARIANT_ANNOTATION_TABLE;
		}

		final String query = String.format("select annotationID, classification, curation, somatic, enteredBy, enterDate from %s where chr = ? and pos = ? and ref = ? and alt = ? order by annotationID asc",tablename);
		PreparedStatement selectStatement = databaseConnection.prepareStatement(query);
		selectStatement.setString(1, coordinate.getChr());
		selectStatement.setString(2, coordinate.getPos());
		selectStatement.setString(3, coordinate.getRef());
		selectStatement.setString(4, coordinate.getAlt());
		ResultSet rs = selectStatement.executeQuery();
		while(rs.next()){
			annotations.add(new Annotation(rs.getInt("annotationID"),coordinate, rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getTimestamp(6)));
		}
		selectStatement.close();
		return annotations;
	}


	public static String getMutationURL(CosmicID cosmicID) throws SQLException {


		ArrayList<String> urls = new ArrayList<String>();
		String cosmic_id = cosmicID.toString();
		cosmic_id = cosmic_id.split("\\(")[0];
		String query = "select MUTATION_URL from " + Configurations.COSMIC_CMC_TABLE + " where (LEGACY_MUTATION_ID = ? or GENOMIC_MUTATION_ID = ?) and ACCESSION_NUMBER = ? ";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
		preparedStatement.setString(1, cosmic_id);
		preparedStatement.setString(2, cosmic_id);
		preparedStatement.setString(3, cosmicID.HGVSc.split(":")[0]);
		ResultSet rs = preparedStatement.executeQuery();
		

		while(rs.next()){
			urls.add(rs.getString("MUTATION_URL"));
		}

		preparedStatement.close();

		if(urls.size() == 0){
			return "https://cancer.sanger.ac.uk/cosmic/search?q=" + cosmicID.cosmicID;
		}else{
			return urls.get(0);
		}
		
	}
}
