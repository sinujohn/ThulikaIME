/*
 * Author : Sinu John
 * www.sinujohn.wordpress.com
 */

package me.sinu.thulika.lang;

import me.sinu.thulika.entity.Engine;
import me.sinu.thulika.lang.common.IndicLangProcessor;

public class MalayalamProcessor extends IndicLangProcessor {

	private final String engineName = "engines/malayalam";
	private final String fontName = "fonts/malayalam.ttf";
	
	public MalayalamProcessor(Engine engine) {
		super(engine);
	}

	@Override
	public String getEngineName() {
		return engineName;
	}

	@Override
	public String getFontName() {
		return fontName;
	}

}
