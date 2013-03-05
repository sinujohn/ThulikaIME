/*
 * Author : Sinu John
 * www.sinujohn.wordpress.com
 */

package me.sinu.thulika.lang;

import me.sinu.thulika.entity.Engine;
import me.sinu.thulika.lang.common.LatinLangProcessor;

public class NumberProcessor extends LatinLangProcessor{

	private final String engineName = "engines/numbers";
	public NumberProcessor(Engine engine) {
		super(engine);
	}

	@Override
	public String getEngineName() {
		return engineName;
	}

	@Override
	public String getFontName() {
		return null;
	}

}
