package hmvv.io;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;

import hmvv.main.Configurations;

public class InternetCommands {
	private static void browseToURL(String url) throws Exception{
		if(Desktop.isDesktopSupported()){
			Desktop desktop = Desktop.getDesktop();
			desktop.browse(new URI(url));
		}else{
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("xdg-open " + url);
		}
	}

	public static void searchGnomad(String term) throws Exception{
		browseToURL("http://gnomad.broadinstitute.org/variant/" + term );
	}


	public static void searchOncokb(String term) throws Exception{
		String[] terms = term.split("-");
		browseToURL("http://oncokb.org/#/gene/"+terms[0]+"/alteration/"+ terms[1] );
	}

	public static void searchGoogle(String term) throws Exception{
		browseToURL("https://www.google.com/#q=" + "human%20genome%20" + term );
	}

	public static void searchSNP(String ID) throws Exception{
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
			browseToURL("http://www.ncbi.nlm.nih.gov/snp?term=" + searchTerm);
		}
		else{
			browseToURL("http://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=" + ID);
		}
	}

	public static void searchCosmic(ArrayList<String> cosmicIDs) throws Exception{
		for(String cosmicID : cosmicIDs){
			String cosmicIDNumber = cosmicID.replaceAll("^COSM", "");
			browseToURL("http://cancer.sanger.ac.uk/cosmic/mutation/overview?genome=" + Configurations.GENOME_VERSION + "&id=" + cosmicIDNumber);
		}
	}

	public static void searchClinvar(String term) throws Exception{
		browseToURL("https://www.ncbi.nlm.nih.gov/clinvar/variation/" + term );

	}

	public static void searchCivic(String url) throws Exception{
		browseToURL(url);
	}

	public static void searchPubmed(String ID) throws Exception{
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
		browseToURL(search);
	}

	public static void hmvvHome() throws Exception{
		String search = "http://" + Configurations.SSH_SERVER_ADDRESS + "/hmvv3";
		browseToURL(search);
	}
	
	public static void hmvvBugsReport() throws Exception{
		String search = "http://" + Configurations.SSH_SERVER_ADDRESS + "/hmvv3/bugs.html";
		browseToURL(search);
	}
}
