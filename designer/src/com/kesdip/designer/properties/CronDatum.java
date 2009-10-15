package com.kesdip.designer.properties;

import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class CronDatum {
	
	private SortedSet<Integer> cronData = new TreeSet<Integer>();
	private Set<Integer> unmodifiableCronData = Collections.unmodifiableSet(cronData);
	
	public void reset() {
		cronData = null;
	}
	
	public void setCron(String expr) {
		cronData = new TreeSet<Integer>();
		unmodifiableCronData = Collections.unmodifiableSet(cronData);
		
		String[] tokens = expr.split(",");
		if (tokens.length == 1  && tokens[0].equals("*"))
			return;
		
		
		for (int i = 0 ; i < tokens.length ; i++) {
			cronData.add(Integer.parseInt(tokens[i]));
		}
	}
	
	public String getCron() {
		if (cronData == null || cronData.size() == 0)
			return "*";
		StringBuilder builder = new StringBuilder();
		for (int i : cronData) {
			builder.append(i).append(',');
		}
		return builder.substring(0, builder.length() - 1);
	}
	
	public void add(Integer i) {
		cronData.add(i);
	}
	
	public void remove(Integer i) {
		cronData.remove(i);
	}
	
	public Set<Integer> getCronData() {
		return unmodifiableCronData;
	}

}
