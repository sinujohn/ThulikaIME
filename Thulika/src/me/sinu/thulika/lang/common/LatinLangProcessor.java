/*
 * Author : Sinu John
 * www.sinujohn.wordpress.com
 */

package me.sinu.thulika.lang.common;

import me.sinu.thulika.entity.CharData;
import me.sinu.thulika.entity.Engine;
import me.sinu.thulika.entity.LanguageProcessor;

public abstract class LatinLangProcessor implements LanguageProcessor{

	Engine engine;
	
	public LatinLangProcessor(Engine engine) {
		this.engine = engine;
	}
			
	public String getStack() {
		return "";
	}
	
	public String[] process(String previous, CharData current) {
		return new String[] {current.getSymbol()};
	}		
}


