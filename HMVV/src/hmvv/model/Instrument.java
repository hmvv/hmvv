package hmvv.model;

import java.util.HashMap;

public class Instrument {
    public final String instrumentName;

    private static HashMap<String, Instrument> all_instruments = new HashMap<String, Instrument>();

    public static Instrument getInstrument(String instrumentName){
        if(instrumentName == null) throw new IllegalArgumentException("Instrument Name cannot be null");

        if(all_instruments.get(instrumentName) == null){
            all_instruments.put(instrumentName, new Instrument(instrumentName));
        }
        return all_instruments.get(instrumentName);
    }

    private Instrument(String instrumentName){
        this.instrumentName = instrumentName;
    }

    public String toString(){
        return instrumentName;
    }

    public boolean equals(Object o){
        if (o instanceof Instrument){
            return ((Instrument)o).instrumentName.equals(instrumentName);
        }
        return false;
    }
}
