package hmvv.io;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import hmvv.main.Configurations;

public class InternetCommands {
	private static void browseToURL(String url) throws IOException, URISyntaxException {
		if(Desktop.isDesktopSupported()){
			Desktop desktop = Desktop.getDesktop();
			desktop.browse(new URI(url));
		}else{
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("xdg-open " + url);
		}
	}

	public static void searchGnomad(String term){
		try {
			browseToURL("http://gnomad.broadinstitute.org/variant/" + term );
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}


	public static void searchOncokb(String term){
		String[] terms = term.split("-");
		try {
			browseToURL("http://oncokb.org/#/gene/"+terms[0]+"/alteration/"+ terms[1] );
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	public static void searchGoogle(String term){
		try {
			browseToURL("https://www.google.com/#q=" + "human%20genome%20" + term );
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	public static void searchSNP(String ID){
		if(ID.contains(",")){
			String searchTerm = "";
			Integer n = 0;
			for (String oneID: ID.split(",")){
				if(n == 0){
					searchTerm += oneID;
				}else{
					searchTerm += String.format("%%20OR%%20%s", oneID);
				}
				n += 1;
			}
			try {
				browseToURL("http://www.ncbi.nlm.nih.gov/snp?term=" + searchTerm);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}
		else{
			try {
				browseToURL("http://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=" + ID);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}
	}

	public static void searchCosmic(ArrayList<String> cosmicIDs){
		for(String cosmicID : cosmicIDs){
			String cosmicIDNumber = cosmicID.replaceAll("^COSM", "");
			try {
				browseToURL("http://cancer.sanger.ac.uk/cosmic/mutation/overview?genome=" + Configurations.GENOME_VERSION + "&id=" + cosmicIDNumber);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}
	}

	public static void searchClinvar(String term){
		try {
			browseToURL("https://www.ncbi.nlm.nih.gov/clinvar/variation/" + term );
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

	}

	public static void searchCivic(String url){

		try {
			browseToURL(url);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	public static void searchPubmed(String ID){
		String[] IDs = ID.split("&");
		Integer n = 0;
		String search = "http://www.ncbi.nlm.nih.gov/pubmed/?term=";
		for(String oneID : IDs){
			if(n==0){
				search += oneID;
			}else{
				search += "+" + oneID;
			}
			n += 1;
		}
		try {
			browseToURL(search);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	public static void hmvvHome(){
		String search = "http://10.110.21.70/hmvv3";
		try {
			browseToURL(search);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
}
