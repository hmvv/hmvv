package hmvv.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DatabaseCommands_Assays {
	private static Connection databaseConnection = null;
	static void setConnection(Connection databaseConnection) {
		DatabaseCommands_Assays.databaseConnection = databaseConnection;
	}
	
	static ArrayList<String> getAllInstruments() throws Exception{
		ArrayList<String> instruments = new ArrayList<String>();
		String getAssay = "select distinct instrumentName from instruments order by instrumentName";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(getAssay);
		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){
			instruments.add(rs.getString(1));
		}
		preparedStatement.close();
		return instruments;
	}
	
	static ArrayList<String> getAllAssays() throws Exception{
		ArrayList<String> assays = new ArrayList<String>();
		String getAssay = "select distinct assayName from assays order by assayName";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(getAssay);
		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){
			assays.add(rs.getString(1));
		}
		preparedStatement.close();
		return assays;
	}

	static ArrayList<String> getAssaysForInstrument(String instrument) throws Exception{
		ArrayList<String> assays = new ArrayList<String>();
		PreparedStatement preparedStatement = databaseConnection.prepareStatement("select assays.assayName from assays " +
				"join assayInstrument on assayInstrument.assayID = assays.assayID " +
				"join instruments on instruments.instrumentID = assayInstrument.instrumentID " +
				"where instruments.instrumentName = ?");
		preparedStatement.setString(1, instrument);
		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){
			assays.add(rs.getString(1));
		}
		preparedStatement.close();
		return assays;
	}
}
