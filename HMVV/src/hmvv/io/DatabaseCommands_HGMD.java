package hmvv.io;

import hmvv.model.MutationGermline;
import hmvv.model.MutationGermlineHGMD;
import hmvv.model.MutationGermlineHGMDGeneLevel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseCommands_HGMD {
	private static Connection databaseConnection = null;

	private static Map<String, String> tableNameDBMap = new HashMap<String, String>() {{
		put("missense nonsense", "mutation");
		put("splice", "splice");
		put("regulatory", "prom");
		put("small deletions", "deletion");
		put("small insertions", "insertion");
		put("small indels", "indel");
		put("gross deletions", "grosdel");
		put("gross insertions", "grosins");
		put("complex rearrangments", "complex");
		put("repeat variations", "amplet");
	}};


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

	static ArrayList<MutationGermlineHGMD> getMutationsByTable(MutationGermline mutation, String appTable) throws Exception{

		String table = "db_hgmd_"+ tableNameDBMap.get(appTable);

		ArrayList<MutationGermlineHGMD> mutations = new ArrayList<MutationGermlineHGMD>();

		PreparedStatement preparedStatement = databaseConnection.prepareStatement("" +
				" select db_hgmd_allmut.acc_num, db_hgmd_allmut.hgvs, db_hgmd_allmut.descr, db_hgmd_allmut.disease," +
				" db_hgmd_allmut.tag, db_hgmd_allmut.pmid, db_hgmd_allmut.year, " +
				" db_hgmd_allmut.author, db_hgmd_allmut.year,db_hgmd_allmut.fullname,db_hgmd_allmut.vol,db_hgmd_allmut.page " +
				" from "+ table +" join db_hgmd_allmut on "+table+".acc_num=db_hgmd_allmut.acc_num "+
				" where "+table+".gene=? ;");
		preparedStatement.setString(1, mutation.getGene());


		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){

			MutationGermlineHGMD hgmd_mutation = new MutationGermlineHGMD(rs.getString("acc_num"));

			hgmd_mutation.setMutation_type(appTable);
			hgmd_mutation.setVariant(rs.getString("hgvs"));
			hgmd_mutation.setAAchange(rs.getString("descr"));
			hgmd_mutation.setDisease(rs.getString("disease"));
			hgmd_mutation.setCategory(rs.getString("tag"));
			hgmd_mutation.setPmid(rs.getString("pmid"));
			hgmd_mutation.setPmid_info(rs.getString("author")+"("+
					rs.getString("year")+") "+
					rs.getString("fullname")+" "+
					rs.getString("vol")+"."+
					rs.getString("vol"));

			mutations.add(hgmd_mutation);
}
		preparedStatement.close();
		return mutations;
	}


}



