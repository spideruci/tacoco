package org.spideruci.tacoco.analysis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Implements a classical property bag to store any objects associated with results of an
 * {@link org.spideruci.tacoco.analysis.AbstractAnalyzer AbstractAnalyzer}, particularly to be used
 * in the 
 * {@link org.spideruci.tacoco.analysis.AbstractAnalyzer#printAnalysisSummary() printAnalysisSummary} method.
 * <br>
 * This class permits results with heterogeneous data to be stored in 
 * this results object.
 * @author vpalepu
 *
 */
public class AnalysisResults implements Iterable<Entry<String, Object>> {
	
	private HashMap<String, Object> resultTable; // TODO change this data structure to a list or something.
	
	public <T> T get(String name) {
		Object object = resultTable.get(name);
		try {
			@SuppressWarnings("unchecked")
			T typedObject = (T) object.getClass();
			return typedObject;
		} catch(ClassCastException e) {
			return null;
		}
	}
	
	public <T> void put(String name, T object) {
		resultTable.put(name, object);
	}
	
	public <T> boolean has(String name) {
		T object = this.get(name);
		return object != null;  
	}

	@Override
	public Iterator<Entry<String, Object>> iterator() {
		return resultTable.entrySet().iterator();
	}

}
