package hmvv.model;

import hmvv.gui.GUICommonTools;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class HGMDDatabaseEntry {

    private String feature;
    private String value;


    public HGMDDatabaseEntry(String feature, String value){
        this.feature = feature;
        this.value = value;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
