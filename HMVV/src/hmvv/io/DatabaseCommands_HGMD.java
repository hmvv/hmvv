package hmvv.io;

import hmvv.model.MutationGermline;
import hmvv.model.MutationGermlineHGMD;
import hmvv.model.MutationGermlineHGMDGeneLevel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DatabaseCommands_HGMD {
	private static Connection databaseConnection = null;
	static void setConnection(Connection databaseConnection) {
		DatabaseCommands_HGMD.databaseConnection = databaseConnection;
	}
	
	static ArrayList<MutationGermlineHGMDGeneLevel> getMutationSummaryForGene(String gene) throws Exception{
		ArrayList<MutationGermlineHGMDGeneLevel> mutations = new ArrayList<MutationGermlineHGMDGeneLevel>();

		PreparedStatement preparedStatement = databaseConnection.prepareStatement("" +
				"select " +
				"( select count(*) from db_hgmd_mutation  where gene=? ) as MissenseNonsense, " +
				"( select count(*) from db_hgmd_splice  where gene=? ) as Splice , " +
				"(select count(*) from db_hgmd_prom  where gene=? ) as Regulatory, " +
				"( select count(*) from db_hgmd_deletion  where gene=? ) as Smalldeletions, " +
				"(select count(*) from db_hgmd_insertion  where gene=? ) as Smallinsertions, " +
				"(select count(*) from db_hgmd_indel where gene=?) as Smallindels, " +
				"(select count(*) from db_hgmd_grosdel where gene=? ) as Grossdeletions, " +
				"(select count(*) from db_hgmd_grosins where gene=? ) Grossinsertions, " +
				"(select count(*) from db_hgmd_complex where gene=? ) as ComplexRearrangments , " +
				"(select count(*) from db_hgmd_amplet where gene=? ) as RepeatVariations;");
		preparedStatement.setString(1, gene);
		preparedStatement.setString(2, gene);
		preparedStatement.setString(3, gene);
		preparedStatement.setString(4, gene);
		preparedStatement.setString(5, gene);
		preparedStatement.setString(6, gene);
		preparedStatement.setString(7, gene);
		preparedStatement.setString(8, gene);
		preparedStatement.setString(9, gene);
		preparedStatement.setString(10, gene);

		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){
			mutations.add(new MutationGermlineHGMDGeneLevel("Missense Nonsense",rs.getInt("MissenseNonsense")));
			mutations.add(new MutationGermlineHGMDGeneLevel("Splice",rs.getInt("Splice")));
			mutations.add(new MutationGermlineHGMDGeneLevel("Regulatory",rs.getInt("Regulatory")));
			mutations.add(new MutationGermlineHGMDGeneLevel("Small deletions",rs.getInt("Smalldeletions")));
			mutations.add(new MutationGermlineHGMDGeneLevel("Small insertions",rs.getInt("Smallinsertions")));
			mutations.add(new MutationGermlineHGMDGeneLevel("Small indels",rs.getInt("Smallindels")));
			mutations.add(new MutationGermlineHGMDGeneLevel("Gross deletions",rs.getInt("Grossdeletions")));
			mutations.add(new MutationGermlineHGMDGeneLevel("Gross insertions",rs.getInt("Grossinsertions")));
			mutations.add(new MutationGermlineHGMDGeneLevel("Complex rearrangments",rs.getInt("ComplexRearrangments")));
			mutations.add(new MutationGermlineHGMDGeneLevel("Repeat variations",rs.getInt("RepeatVariations")));
		}
		preparedStatement.close();
		return mutations;
	}

	static ArrayList<MutationGermlineHGMD> getMutationsByTable(MutationGermline mutation, String table) throws Exception{
		ArrayList<MutationGermlineHGMD> mutations = new ArrayList<MutationGermlineHGMD>();

		PreparedStatement preparedStatement = databaseConnection.prepareStatement("" +
				"select base, amino, disease," +
				" tag, pmid, year " +
//				"author, year,fullname,vol,page " +
				"from "+ table +" where gene=? ;");
		preparedStatement.setString(1, mutation.getGene());


		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){

			MutationGermlineHGMD hgmd_mutation = new MutationGermlineHGMD(mutation.getMutationGermlineHGMD().getId());
			hgmd_mutation.setVariant(rs.getString("base"));
			hgmd_mutation.setAAchange(rs.getString("amino"));
			hgmd_mutation.setDisease(rs.getString("disease"));
			hgmd_mutation.setCategory(rs.getString("tag"));

			mutations.add(hgmd_mutation);
}
		preparedStatement.close();
		return mutations;
	}
}



