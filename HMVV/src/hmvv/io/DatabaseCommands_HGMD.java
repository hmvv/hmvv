package hmvv.io;

import hmvv.model.HGMDDatabaseEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DatabaseCommands_HGMD {
	private static Connection databaseConnection = null;
	static void setConnection(Connection databaseConnection) {
		DatabaseCommands_HGMD.databaseConnection = databaseConnection;
	}
	
	static ArrayList<HGMDDatabaseEntry> getAllMutationForGene(String gene) throws Exception{
		ArrayList<HGMDDatabaseEntry> mutations = new ArrayList<HGMDDatabaseEntry>();

		PreparedStatement preparedStatement = databaseConnection.prepareStatement("select refseq,hgvsALL from db_hgmd_allmut where gene =?");
		preparedStatement.setString(1, gene);

		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){
			mutations.add(new HGMDDatabaseEntry(rs.getString(1),rs.getString(2)));
		}
		preparedStatement.close();
		return mutations;
	}


}
