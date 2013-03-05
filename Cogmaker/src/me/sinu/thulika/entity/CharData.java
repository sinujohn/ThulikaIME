package me.sinu.thulika.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CharData implements Comparable<CharData>, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2994532794878830103L;
	private String symbol;
	private int align;
	private Map<String, String> mergeRules = new HashMap<String, String>(0);
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public int getAlign() {
		return align;
	}
	public void setAlign(int align) {
		this.align = align;
	}
	public Map<String, String> getMergeRules() {
		return mergeRules;
	}
	public void setMergeRules(Map<String, String> mergeRules) {
		this.mergeRules = mergeRules;
	}
	
	public void addToMergeRules(String pre, String result) {
		mergeRules.put(pre, result);
	}
	
	public CharData() {
		// TODO Auto-generated constructor stub
	}
	
	public CharData(String letter) {
		setSymbol(letter);
	}
	
	/**
	 * Merges current symbol with pre
	 * @param pre
	 * @return merged symbol OR null if no merge rule is found 
	 */
	public String merge(String pre) {
		return mergeRules.get(pre);
	}
	
	@Override
	public String toString() {
		return symbol;
	}
	@Override
	public int compareTo(CharData arg0) {
		return this.toString().compareTo(arg0.toString());
	}
	@Override
	public boolean equals(Object arg0) {
		return getSymbol().equals(arg0.toString());
	}
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
}
