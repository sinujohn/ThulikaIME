/*
 * Developer : Sinu John
 * www.sinujohn.wordpress.com
 */

package me.sinu.thulika.entity;

public interface LanguageProcessor {
	
	/**
	 * 
	 * @param previous the String present to the left of cursor 
	 * @param current the current String to be input
	 * @return 
	 */
	public String[] process(String previous, CharData current);
}
