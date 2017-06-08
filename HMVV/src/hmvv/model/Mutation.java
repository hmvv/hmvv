package hmvv.model;

import java.util.HashMap;
import java.util.Set;

public class Mutation {
	private HashMap<String, Object> mutationData;
	
	public Mutation(){
		mutationData = new HashMap<String, Object>();
	}
	
	/**
	 * Assumes a chr, pos, ref, and alt key are set
	 * @return
	 */
	public Coordinate getCoordinate(){
		return new Coordinate(mutationData.get("chr").toString(),
				mutationData.get("pos").toString(),
				mutationData.get("ref").toString(),
				mutationData.get("alt").toString());
	}
	
	public Set<String> getKeys(){
		return mutationData.keySet();
	}
	
	public void addData(String key, Object value){
		mutationData.put(key, value);
	}
	
	public Object getValue(String key){
		return mutationData.get(key);
	}
}
