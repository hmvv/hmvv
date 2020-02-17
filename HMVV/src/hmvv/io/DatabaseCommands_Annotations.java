package hmvv.io;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import hmvv.model.Annotation;
import hmvv.model.Coordinate;
import hmvv.model.GeneAnnotation;

public class DatabaseCommands_Annotations {
	private static Connection databaseConnection = null;
	static void setConnection(Connection databaseConnection) {
		DatabaseCommands_Annotations.databaseConnection = databaseConnection;
	}
	
	static ArrayList<GeneAnnotation> getGeneAnnotationHistory(String gene) throws Exception{
		ArrayList<GeneAnnotation>  geneannotations = new ArrayList<GeneAnnotation>() ;
		PreparedStatement selectStatement = databaseConnection.prepareStatement("select geneAnnotationID, gene, curation, enteredBy, enterDate from geneAnnotation where gene = ? order by geneAnnotationID asc");
		selectStatement.setString(1, gene);
		ResultSet rs = selectStatement.executeQuery();
		while(rs.next()){
			geneannotations.add(new GeneAnnotation(rs.getInt("geneAnnotationID") , rs.getString("gene") , rs.getString("curation") , rs.getString("enteredBy") , rs.getTimestamp("enterDate")));
		}
		selectStatement.close();
		return geneannotations;
	}

	static String getVariantAnnotationDraft(Coordinate coordinate) throws Exception{
		String draft="";
		PreparedStatement selectStatement = databaseConnection.prepareStatement("select draft from variantAnnotationDraft where chr = ? and pos = ? and ref = ? and alt = ?");
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

	static void addGeneAnnotationCuration(GeneAnnotation geneAnnotation) throws Exception{
		String gene = geneAnnotation.gene;
		String curation = geneAnnotation.curation;
		String enteredBy = geneAnnotation.enteredBy;
		PreparedStatement pstEnterGeneAnnotation = databaseConnection.prepareStatement("insert into geneAnnotation (gene, curation, enteredBy, enterDate) values (?, ?, ?, ?)");
		pstEnterGeneAnnotation.setString(1, gene);
		pstEnterGeneAnnotation.setString(2, curation);
		pstEnterGeneAnnotation.setString(3, enteredBy);
		pstEnterGeneAnnotation.setTimestamp(4, new java.sql.Timestamp(geneAnnotation.enterDate.getTime()));
		pstEnterGeneAnnotation.executeUpdate();
		pstEnterGeneAnnotation.close();
	}

	static void addVariantAnnotationCuration(Annotation annotation) throws Exception{
		Coordinate coordinate = annotation.mutation.getCoordinate();
		String chr = coordinate.getChr();
		String pos = coordinate.getPos();
		String ref = coordinate.getRef();
		String alt = coordinate.getAlt();
		String classification = annotation.classification;
		String curation = annotation.curation;
		String somatic = annotation.somatic;
		String enteredBy = annotation.enteredBy;

		PreparedStatement pstEnterAnnotation = databaseConnection.prepareStatement("insert into variantAnnotation ( chr, pos, ref, alt, classification, curation, somatic, enteredBy, enterDate) "
				+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?)");
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

	static void addVariantAnnotationDraft(Coordinate coordinate, String draft) throws Exception{
		String chr = coordinate.getChr();
		String pos = coordinate.getPos();
		String ref = coordinate.getRef();
		String alt = coordinate.getAlt();

		PreparedStatement selectStatement = databaseConnection.prepareStatement("select draft from variantAnnotationDraft where chr = ? and pos = ? and ref = ? and alt = ?");
		selectStatement.setString(1, chr);
		selectStatement.setString(2, pos);
		selectStatement.setString(3, ref);
		selectStatement.setString(4, alt);
		ResultSet rsCheckSample = selectStatement.executeQuery();

		if(rsCheckSample.next()){
			PreparedStatement pstEnterAnnotation = databaseConnection.prepareStatement("update variantAnnotationDraft set draft=? where chr = ? and pos = ? and ref = ? and alt = ?");
			pstEnterAnnotation.setString(1, draft);
			pstEnterAnnotation.setString(2, chr);
			pstEnterAnnotation.setString(3, pos);
			pstEnterAnnotation.setString(4, ref);
			pstEnterAnnotation.setString(5, alt);
			pstEnterAnnotation.executeUpdate();
			pstEnterAnnotation.close(); }
		else {
			PreparedStatement pstEnterAnnotation = databaseConnection.prepareStatement("insert into variantAnnotationDraft ( chr, pos, ref, alt, draft) "
					+ "values (?, ?, ?, ?, ?)");
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
