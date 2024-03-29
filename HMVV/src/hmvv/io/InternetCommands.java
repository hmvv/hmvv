package hmvv.io;

import java.awt.Desktop;
import java.net.URI;
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

	public static void searchPmkb(String term) throws Exception{
		String[] terms = term.split("-");
		browseToURL("https://pmkb.weill.cornell.edu/search?utf8=&search="+terms[0]+"+"+ terms[1] );
	}

	public static void searchGoogle(String term) throws Exception{
		browseToURL("https://www.google.com/search?q=" + term );
	}

	public static void searchGoogleHGMD(String gene, String id) throws Exception{
		browseToURL("http://www.hgmd.cf.ac.uk/ac/gene.php?gene=" + gene + "&accession=" + id );


	}

	public static void searchGene(String term) throws Exception{
		browseToURL("https://www.ncbi.nlm.nih.gov/omim/?term=" + term );
	}
	public static void searchSNP(String ID) throws Exception{
			for (String oneID: ID.split("&")){
			browseToURL("https://www.ncbi.nlm.nih.gov/snp/" + oneID);
		}
	}

	public static void searchCosmic(String url) throws Exception{
		browseToURL(url);
	}

	public static void downloadHMVV() throws Exception{
		browseToURL("http://10.110.21.19/hmvv3/download_application.html");
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

	public static void searchCardiacAtlas(String term) throws Exception{
		browseToURL("https://www.cardiodb.org/acgv/acgv_gene.php?gene=" + term );
	}

	public static void searchClinvarID(String term) throws Exception{
		browseToURL("https://www.ncbi.nlm.nih.gov/clinvar/variation/" + term +"/");
	}
	public static void searchNCBIProtein(String term) throws Exception{
		browseToURL("https://www.ncbi.nlm.nih.gov/protein/" + term+"/" );

	}
	public static void searchExpasyVariant(String term) throws Exception{
		browseToURL("https://web.expasy.org/variant_pages/" + term +".html");
	}

	public static void searchUniprotProtein(String term) throws Exception{

		if (term.contains("NP_")){
			browseToURL("https://www.ncbi.nlm.nih.gov/protein/" + term);
		} else{
			browseToURL("https://www.uniprot.org/uniprot/" + term);}

	}
}
