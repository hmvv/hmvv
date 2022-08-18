package hmvv.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import hmvv.model.Assay;
import hmvv.model.Instrument;

public class DatabaseCommands_Assays {
	private static Connection databaseConnection = null;
	static void setConnection(Connection databaseConnection) {
		DatabaseCommands_Assays.databaseConnection = databaseConnection;
	}
	
	static ArrayList<Instrument> getAllInstruments() throws Exception{
		ArrayList<Instrument> instruments = new ArrayList<Instrument>();
		String getInstruments = "select distinct instrument from assayInstrument order by instrument";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(getInstruments);
		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){
			instruments.add(Instrument.getInstrument(rs.getString(1)));
		}
		preparedStatement.close();
		return instruments;
	}
	
	static ArrayList<Assay> getAllAssays() throws Exception{
		ArrayList<Assay> assays = new ArrayList<Assay>();
		String getAssay = "select distinct assay from assayInstrument order by assay";
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(getAssay);
		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){
			assays.add(Assay.getAssay(rs.getString(1)));
		}
		preparedStatement.close();
		return assays;
	}

	static ArrayList<Assay> getAssaysForInstrument(Instrument instrument) throws Exception{
		ArrayList<Assay> assays = new ArrayList<Assay>();
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(
			"select distinct assay from assayInstrument where instrument = ?"
		);
		preparedStatement.setString(1, instrument.instrumentName);
		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){
			assays.add(Assay.getAssay(rs.getString(1)));
		}
		preparedStatement.close();
		return assays;
	}

	static ArrayList<Instrument> getInstrumentsForAssay(Assay assay) throws Exception{
		ArrayList<Instrument> instruments = new ArrayList<Instrument>();
		PreparedStatement preparedStatement = databaseConnection.prepareStatement(
			"select distinct instrument from assayInstrument where assay = ?"
		);
		preparedStatement.setString(1, assay.assayName);
		ResultSet rs = preparedStatement.executeQuery();
		while(rs.next()){
			instruments.add(Instrument.getInstrument(rs.getString(1)));
		}
		preparedStatement.close();
		return instruments;
	}
}
