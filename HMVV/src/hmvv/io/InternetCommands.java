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
		browseToURL("https://www.google.com/#q=" + term );
	}

	public static void searchGoogleHGMD(String gene, String id) throws Exception{
		browseToURL("http://www.hgmd.cf.ac.uk/ac/gene.php?gene=" + gene + "&accession=" + id );


	}

	public static void searchGene(String term) throws Exception{
		browseToURL("https://www.ncbi.nlm.nih.gov/omim/?term=" + term );
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

	public static void searchCosmic(String cosmicID) throws Exception{
		String cosmicIDNumber = cosmicID.replaceAll("^COSM", "");
		browseToURL("http://cancer.sanger.ac.uk/cosmic/mutation/overview?genome=" + Configurations.GENOME_VERSION + "&id=" + cosmicIDNumber);
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
