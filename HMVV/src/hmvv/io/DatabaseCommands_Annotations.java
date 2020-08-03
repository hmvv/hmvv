package hmvv.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import hmvv.main.Configurations;
import hmvv.model.Annotation;
import hmvv.model.Coordinate;
import hmvv.model.GeneAnnotation;

public class DatabaseCommands_Annotations {
	private static Connection databaseConnection = null;
	static void setConnection(Connection databaseConnection) {
		DatabaseCommands_Annotations.databaseConnection = databaseConnection;
	}
	
	static ArrayList<GeneAnnotation> getGeneAnnotationHistory(String gene, Configurations.MUTATION_TYPE mutation_type) throws Exception{
		ArrayList<GeneAnnotation>  geneannotations = new ArrayList<GeneAnnotation>() ;

		String tablename = "";
		if (mutation_type == Configurations.MUTATION_TYPE.SOMATIC) {
			tablename = "geneAnnotation";
		} else if (mutation_type == Configurations.MUTATION_TYPE.GERMLINE) {
			tablename = "germlineGeneAnnotation";
		}

		final String query = String.format("select geneAnnotationID, gene, curation, enteredBy, enterDate from %s where gene = ? order by geneAnnotationID asc",tablename);
		PreparedStatement selectStatement = databaseConnection.prepareStatement(query);
		selectStatement.setString(1, gene);
		ResultSet rs = selectStatement.executeQuery();
		while(rs.next()){
			geneannotations.add(new GeneAnnotation(rs.getInt("geneAnnotationID") , rs.getString("gene") , rs.getString("curation") , rs.getString("enteredBy") , rs.getTimestamp("enterDate")));
		}
		selectStatement.close();
		return geneannotations;
	}

	static String getVariantAnnotationDraft(Coordinate coordinate, Configurations.MUTATION_TYPE mutation_type) throws Exception{

		String tablename = "";
		if (mutation_type == Configurations.MUTATION_TYPE.SOMATIC) {
			tablename = "variantAnnotationDraft";
		} else if (mutation_type == Configurations.MUTATION_TYPE.GERMLINE) {
			tablename = "germlineVariantAnnotationDraft";
		}

		String draft="";
		final String query = String.format("select draft from %s where chr = ? and pos = ? and ref = ? and alt = ?",tablename);
		PreparedStatement selectStatement = databaseConnection.prepareStatement(query);
		selectStatement.setString(1, coordinate.getChr());
		selectStatement.setString(2, coordinate.getPos());
		selectStatement.setString(3, coordinate.getRef());
		selectStatement.setString(4, coordinate.getAlt());
		ResultSet rs = selectStatement.executeQuery();
		if(rs.next()){
			draft=rs.getString(1);
		}
		selectStatement.close();
		return draft;
	}

	static void addGeneAnnotationCuration(GeneAnnotation geneAnnotation, Configurations.MUTATION_TYPE mutation_type) throws Exception{
		String gene = geneAnnotation.gene;
		String curation = geneAnnotation.curation;
		String enteredBy = geneAnnotation.enteredBy;

		String tablename = "";
		if (mutation_type == Configurations.MUTATION_TYPE.SOMATIC) {
			tablename = "geneAnnotation";
		} else if (mutation_type == Configurations.MUTATION_TYPE.GERMLINE) {
			tablename = "germlineGeneAnnotation";
		}

		final String query = String.format("insert into %s (gene, curation, enteredBy, enterDate) values (?, ?, ?, ?)",tablename);
		PreparedStatement pstEnterGeneAnnotation = databaseConnection.prepareStatement(query);
		pstEnterGeneAnnotation.setString(1, gene);
		pstEnterGeneAnnotation.setString(2, curation);
		pstEnterGeneAnnotation.setString(3, enteredBy);
		pstEnterGeneAnnotation.setTimestamp(4, new java.sql.Timestamp(geneAnnotation.enterDate.getTime()));
		pstEnterGeneAnnotation.executeUpdate();
		pstEnterGeneAnnotation.close();
	}

	static void addVariantAnnotationCuration(Annotation annotation, Configurations.MUTATION_TYPE mutation_type) throws Exception{
		Coordinate coordinate = annotation.cordinate;
		String chr = coordinate.getChr();
		String pos = coordinate.getPos();
		String ref = coordinate.getRef();
		String alt = coordinate.getAlt();
		String classification = annotation.classification;
		String curation = annotation.curation;
		String somatic = annotation.somatic;
		String enteredBy = annotation.enteredBy;

		String tablename = "";
		if (mutation_type == Configurations.MUTATION_TYPE.SOMATIC) {
			tablename = "variantAnnotation";
		} else if (mutation_type == Configurations.MUTATION_TYPE.GERMLINE) {
			tablename = "germlineVariantAnnotation";
		}

		final String query = String.format("insert into %s ( chr, pos, ref, alt, classification, curation, somatic, enteredBy, enterDate) "
						+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?)",tablename);
		PreparedStatement pstEnterAnnotation = databaseConnection.prepareStatement(query);
		pstEnterAnnotation.setString(1, chr);
		pstEnterAnnotation.setString(2, pos);
		pstEnterAnnotation.setString(3, ref);
		pstEnterAnnotation.setString(4, alt);
		pstEnterAnnotation.setString(5, classification);
		pstEnterAnnotation.setString(6, curation);
		pstEnterAnnotation.setString(7, somatic);
		pstEnterAnnotation.setString(8, enteredBy);
		pstEnterAnnotation.setTimestamp(9, new java.sql.Timestamp(annotation.enterDate.getTime()));
		pstEnterAnnotation.executeUpdate();
		pstEnterAnnotation.close();
	}

	static void addVariantAnnotationDraft(Coordinate coordinate, String draft, Configurations.MUTATION_TYPE mutation_type) throws Exception{
		String chr = coordinate.getChr();
		String pos = coordinate.getPos();
		String ref = coordinate.getRef();
		String alt = coordinate.getAlt();

		String tablename = "";
		if (mutation_type == Configurations.MUTATION_TYPE.SOMATIC) {
			tablename = "variantAnnotationDraft";
		} else if (mutation_type == Configurations.MUTATION_TYPE.GERMLINE) {
			tablename = "germlineVariantAnnotationDraft";
		}

		final String query = String.format("select draft from %s where chr = ? and pos = ? and ref = ? and alt = ?",tablename);
		PreparedStatement selectStatement = databaseConnection.prepareStatement(query);
		selectStatement.setString(1, chr);
		selectStatement.setString(2, pos);
		selectStatement.setString(3, ref);
		selectStatement.setString(4, alt);
		ResultSet rsCheckSample = selectStatement.executeQuery();

		if(rsCheckSample.next()){
			final String update_query = String.format("update %s set draft=? where chr = ? and pos = ? and ref = ? and alt = ?",tablename);
			PreparedStatement pstEnterAnnotation = databaseConnection.prepareStatement(update_query);
			pstEnterAnnotation.setString(1, draft);
			pstEnterAnnotation.setString(2, chr);
			pstEnterAnnotation.setString(3, pos);
			pstEnterAnnotation.setString(4, ref);
			pstEnterAnnotation.setString(5, alt);
			pstEnterAnnotation.executeUpdate();
			pstEnterAnnotation.close(); }
		else {
			final String insert_query = String.format("insert into %s ( chr, pos, ref, alt, draft) "
							+ "values (?, ?, ?, ?, ?)",tablename);
			PreparedStatement pstEnterAnnotation = databaseConnection.prepareStatement(insert_query);
			pstEnterAnnotation.setString(1, chr);
			pstEnterAnnotation.setString(2, pos);
			pstEnterAnnotation.setString(3, ref);
			pstEnterAnnotation.setString(4, alt);
			pstEnterAnnotation.setString(5, draft);
			pstEnterAnnotation.executeUpdate();
			pstEnterAnnotation.close();
		}
	}
}
