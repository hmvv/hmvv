package hmvv.model;

import java.util.HashMap;

public class Assay {
    public final String assayName;

    private static HashMap<String, Assay> all_assays = new HashMap<String, Assay>();

    public static Assay getAssay(String assayName){
        if(assayName == null) throw new IllegalArgumentException("Assay Name cannot be null");
        
        if(all_assays.get(assayName) == null){
            all_assays.put(assayName, new Assay(assayName));
        }
        return all_assays.get(assayName);
    }

    private Assay(String assayName){
        this.assayName = assayName;
    }

    public String toString(){
        return assayName;
    }

    public boolean equals(Object o){
        if (o instanceof Assay){
            return ((Assay)o).assayName.equals(assayName);
        }
        return false;
    }
}
