/*
 * Author : Sinu John
 * www.sinujohn.wordpress.com
 */

package me.sinu.thulika.entity;


public class LetterBuffer {

	CharData letter;
	String previous;
	LanguageProcessor processor;
	CharData[] alter;
	
	public LetterBuffer(LanguageProcessor processor) {
		this.processor = processor;
	}
	
	public String[] put(String previous, CharData[] currentLetters) {
		CharData current = currentLetters[0];
		this.alter = currentLetters;
		String[] ret = null;
		if(letter != null) {
			ret = processor.process(this.previous, letter);
			if(ret==null) {
				this.previous = previous;
			} else {
				String last = ret[ret.length-1];
				last = ""+last.charAt(last.length()-1);
				this.previous = last;
			}			
		} else {
			this.previous = previous;
		}
		letter = current;
		return ret;
	}
	
	public CharData[] getSuggestions() {
		if(letter == null) {
			return null;
		}
		/*List<CharData> alter = letter.getAlternatives();
		if(alter==null) {
			alter = new ArrayList<CharData>(1);
		}
		ArrayList<CharData> ret = new ArrayList<CharData>(alter);
		ret.add(0, letter);
		return ret;*/
		return this.alter;
	}
	
	public boolean isEmpty() {
		if(letter ==null) {
			return true;
		} else {
			return false;
		}
	}
	
	public String[] replace(CharData alter) {
		String[] ret = null;
		if(letter==null) {
			return null;
		}
		
		ret = processor.process(previous, alter);
		letter = null;
		this.alter = null;
		return ret;
	}
	
	public String[] emptyBuffer() {
		String[] ret = null;
		if(letter==null) {
			return null;
		}
		
		ret = processor.process(previous, letter);
		letter = null;
		alter = null;
		return ret;
	}
}
