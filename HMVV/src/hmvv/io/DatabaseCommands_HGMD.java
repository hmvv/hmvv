package hmvv.io;

import hmvv.model.MutationHGMD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DatabaseCommands_HGMD {
	private static Connection databaseConnection = null;
	static void setConnection(Connection databaseConnection) {
		DatabaseCommands_HGMD.databaseConnection = databaseConnection;
	}
	
	static ArrayList<MutationHGMD> getAllMutationForGene(String gene) throws Exception{
		ArrayList<MutationHGMD> mutations = new ArrayList<MutationHGMD>();

		PreparedStatement preparedStatement = databaseConnection.prepareStatement("select refseq,hgvsALL from db_hgmd_allmut where gene =?");
		preparedStatement.setString(1, gene);

		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){
			mutations.add(new MutationHGMD(rs.getString(1),rs.getString(2)));
		}
		preparedStatement.close();
		return mutations;
	}


}
