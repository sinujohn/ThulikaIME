/*
 * Author : Sinu John
 * www.sinujohn.wordpress.com
 */

package me.sinu.thulika.lang.common;

import java.util.Stack;

import me.sinu.thulika.entity.CharData;
import me.sinu.thulika.entity.Engine;
import me.sinu.thulika.entity.LanguageProcessor;

public abstract class IndicLangProcessor implements LanguageProcessor{

	Engine engine;
	Stack<String> stack;
	
	private static int LEFT_SYMBOL = -1;
	
	public IndicLangProcessor(Engine engine) {
		this.engine = engine;
		init();
	}
	
	private void init() {
		stack = new Stack<String>();
	}
	
	private String[] merge(String previous, CharData current) {
		if(previous==null || previous.isEmpty()) {
			return new String[] {current.toString()};
		}
		
		String result = current.getMergeRules().get(previous);
		if(result!=null) {
			return new String[] {"", result};
		} else {
			return new String[] {previous, current.toString()};
		}
	}
	
	public String getStack() {
		StringBuffer buf = new StringBuffer();
		for(int i=0; i<stack.size(); i++) {
			buf.append(stack.elementAt(i));
		}
		return buf.toString();
	}
	
	public String[] process(String previous, CharData current) {
		if(current.getAlign()==LEFT_SYMBOL) {
			String last;
			if(stack.isEmpty()) {
				last = null;
			} else {
				 last = stack.pop();
			}				
			String[] result = merge(last, current);
			for(String str : result) {
				stack.push(str);
			}
			return null;
		} else {
			StringBuffer buf = new StringBuffer();
			if(!stack.isEmpty()) {
				buf.append(current.toString());
				while(!stack.isEmpty()) {
					buf.append(stack.pop());
				}
				return new String[] {buf.toString()};
			} else {
				String[] result = merge(previous, current);
				return result;
			}			
		}
	}

}
