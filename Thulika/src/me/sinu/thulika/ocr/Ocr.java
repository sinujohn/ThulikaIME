/*
 * Author : Sinu John
 * www.sinujohn.wordpress.com
 */

package me.sinu.thulika.ocr;

import java.util.ArrayList;
import java.util.List;

import me.sinu.thulika.R;
import me.sinu.thulika.entity.CharData;
import me.sinu.thulika.entity.Engine;
import me.sinu.thulika.entity.LanguageProcessor;
import me.sinu.thulika.entity.LetterBuffer;
import me.sinu.thulika.entity.MultiSOM;
import me.sinu.thulika.view.SingleTouchEventView;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

import android.content.Context;
import android.graphics.Typeface;
import android.inputmethodservice.InputMethodService;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Ocr extends SingleTouchEventView{

	private static int DOWNSAMPLE_HEIGHT = 50;
	private static int DOWNSAMPLE_WIDTH = 50;
	private static final String TAG = "LOG";
	private static final int MAX_SLICE_LENGTH = 70;
	private Entry entry;
	private CharData[] neuronMap;
	
	private Engine engine;
	private LanguageProcessor langProc;
	private LetterBuffer lBuffer;
	private InputConnection inputConn;
	
	private TextView stackView;
	private LinearLayout suggestionsViewGroup;
	private InputMethodService imeService;
	private TextView sliceView;
	/**
	 * The neural network.
	 */
	private MultiSOM net;
	//private SOM net;
	
	private int lettersToDelete=0;

	public InputConnection getInputConn() {
		return inputConn;
	}
	public void setInputConn(InputConnection inputConn) {
		this.inputConn = inputConn;
	}
	public Engine getEngine() {
		return engine;
	}
	public void setImeService(InputMethodService imeService) {
		this.imeService = imeService;
	}
	
	public Ocr(Context context, AttributeSet attrs) {
		super(context, attrs);
		/*externalDir = "/mnt/sdcard/Android/data/me.sinu.thulika.train/files";
		engineDir = externalDir.endsWith("/")? externalDir : externalDir+"/";
		engineDir = engineDir + ENGINEDIR;
		File eDir = new File(engineDir);
		if(!eDir.isDirectory()) {
			eDir.mkdir();
		}
		if(!eDir.isDirectory()) {
			Log.e(TAG, "Directory doesnt exist : " + engineDir);
		}*/
	}	
	
	public void init() {
		SampleData sampleData = new SampleData("?", DOWNSAMPLE_WIDTH, DOWNSAMPLE_HEIGHT);
		entry = new Entry(this.getWidth(), this.getHeight());
		entry.setSampleData(sampleData);
	}
	
	public void loadEngine(LanguageProcessor langProc, LetterBuffer lBuffer, TextView stackView, LinearLayout suggestionsViewGroup, TextView sliceView) {
		this.langProc = langProc;
		this.lBuffer = lBuffer;
		this.stackView = stackView;
		this.suggestionsViewGroup = suggestionsViewGroup;
		this.sliceView = sliceView;
		Engine en = new Engine();
		try {
			en.restore(getContext().getAssets().open(langProc.getEngineName()));
			this.engine = en;
			this.net = en.getNet();
			this.neuronMap = en.getNeuronMap();
			Ocr.DOWNSAMPLE_WIDTH = en.getSampleDataWidth();
			Ocr.DOWNSAMPLE_HEIGHT = en.getSampleDataHeight();
			
			SampleData sampleData = new SampleData("?", DOWNSAMPLE_WIDTH, DOWNSAMPLE_HEIGHT);
			/*if(entry==null) {
				entry = new Entry(this.getWidth(), this.getHeight());
			}*/
			entry.setSampleData(sampleData);
			
			//init();
			Toast msg = Toast.makeText(getContext(),"Keyboard:" + langProc.getEngineName(), Toast.LENGTH_SHORT);
			msg.show();
		} catch (Exception e) {
			//Log.e(TAG, "Cannot Load " + langProc.getEngineName()/*files[0].getPath()*/, e);
		}
		
		cleanAllViews();
	}
	
	private void cleanAllViews() {
		lBuffer.emptyBuffer();
		suggestionsViewGroup.removeAllViews();
		stackView.setText("");
		showSliceText();
	}
	
	/**
	 * Called when the recognize button is pressed.
	 */
	public CharData[] recognizeAction(int count) {
		if (this.net == null) {
			//Log.e(TAG, "I need to be trained first!");
			return null;
		}
		if(count>this.engine.getNeuronMap().length) {
			count = this.engine.getNeuronMap().length;
		}
		if(entry==null) {
			init();
		}
		this.entry.downsample(getImagePixels());

		final MLData input = new BasicMLData(Ocr.DOWNSAMPLE_HEIGHT * Ocr.DOWNSAMPLE_WIDTH);
		int idx = 0;
		final SampleData ds = this.entry.getSampleData();
		for (int y = 0; y < ds.getHeight(); y++) {
			for (int x = 0; x < ds.getWidth(); x++) {
				input.setData(idx++, ds.getData(x, y) ? .5 : -.5);
			}
		}

		final int[] result = this.net.matches(input, count); 
		//CharData[] ret = new CharData[count];
		//for(int i=0; i<count; i++) {
				//ret[i] = neuronMap[result[i]];
		//}
		
		List<CharData> retList = new ArrayList<CharData>();
		for(int i=0; i<count; i++) {
			CharData newData = neuronMap[result[i]];
			if(!retList.contains(newData)) {
				retList.add(newData);
			}
		}
		CharData[] ret = retList.toArray(new CharData[]{});
		
		clearAction();
		//return neuronMap[best];
		return ret;
	}
	
	@Override
	protected void onTouchUp() {	
		if(super.isSmallPath()) {
			if(imeService!=null) {
				imeService.requestHideSelf(0);
				super.clear();
				hideView = true;
				return;
			}
		}
		
		CharData[] cData = this.recognizeAction(10);
		CharSequence pre = inputConn.getTextBeforeCursor(1, 0);
		String previous;
		try {
			previous = pre.toString();
			if(pre.length()==0) {
				previous = null;
			}
		} catch (Exception e) {
			previous = null;
		}
		String[] result = lBuffer.put(previous, cData);
		
		inputConn.deleteSurroundingText(lettersToDelete, 0);
		
		showCandidates();
		/*if(result==null) {
			return;
		}*/
		putText(previous, result);
		lettersToDelete = cData[0].getSymbol().length();
		inputConn.commitText(cData[0].getSymbol(), 1);
		showSliceText();
		//return true;
	}

	private void clearAction() {
		this.entry.clear();
		super.clear();
	}
	
	public void dumbProcess(CharData c) {
		String[] result;
		CharSequence pre = inputConn.getTextBeforeCursor(1, 0);
		String previous;
		try {
			previous = pre.toString();
			if(pre.length()==0) {
				previous = null;
			}
		} catch (Exception e) {
			previous = null;
		}
		
		if(!c.getSymbol().isEmpty()) {
			result = lBuffer.put(previous, new CharData[]{c});
			if(result!=null) {
				putText(previous, result);
				previous = "" + (char)result[result.length-1].codePointBefore(result[result.length-1].length());
			}
		}
		result = lBuffer.emptyBuffer();
		if(result!=null) {
			inputConn.deleteSurroundingText(lettersToDelete, 0);
			lettersToDelete = 0;
			putText(previous, result);
		}
		
		showSliceText();
		showCandidates();
	}
	
	private void showCandidates() {
		stackView.setText(langProc.getStack());
		suggestionsViewGroup.removeAllViews();
		if(!lBuffer.isEmpty()) {
			/*List<CharData>*/CharData[] suggestions = lBuffer.getSuggestions();
			if(suggestions!=null /*&& !suggestions.isEmpty()*/) {
				Context ctx = this.getContext();
				
				CharSequence pre = inputConn.getTextBeforeCursor(1, 0);
				String preStr;
				try {
					preStr = pre.toString();
					if(pre.length()==0) {
						preStr = null;
					}
				} catch (Exception e) {
					preStr = null;
				}
				final String previous = preStr;
				
				Typeface font = null;
				if(langProc.getFontName()!=null) {
					font = Typeface.createFromAsset(this.getContext().getAssets(), langProc.getFontName());
					stackView.setTypeface(font);
				} else {
					stackView.setTypeface(Typeface.SANS_SERIF);
				}
				
				for(final CharData suggestion : suggestions) {
					TextView suggestView = new TextView(ctx);
					suggestView.setMinimumHeight(30);
					suggestView.setMinimumWidth(70);
					suggestView.setTextAppearance(ctx, R.style.suggestText);
					if(suggestion == suggestions[0]) {
						suggestView.setPressed(true);
					}
					suggestView.setText(suggestion.getSymbol());
					suggestView.setGravity(Gravity.CENTER);
					suggestView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.line, 0);
					
					if(font!=null) {
						suggestView.setTypeface(font);
					}
					
					suggestView.setOnClickListener(new OnClickListener() {
		    			public void onClick(View v) {
		    				String[] result = lBuffer.replace(suggestion);
		    				
		    				/*int pos = inputBox.getSelectionStart();
							String previous;
							try {
								previous = "" + (char)inputBox.getText().toString().codePointBefore(pos);
							} catch (Exception e) {
								previous = null;
							}
		    				
		    				putText(pos, previous, result);*/
		    				
		    				inputConn.deleteSurroundingText(lettersToDelete, 0);
		    				lettersToDelete = 0;
		    				putText(previous, result);
		    				
		    				showSliceText();
		    				showCandidates();
		    			}
		    		});
					suggestionsViewGroup.addView(suggestView);
				}
			}
		}
		//suggestionsViewGroup.invalidate();
		((HorizontalScrollView)suggestionsViewGroup.getParent()).scrollTo(0, 0);	
	}
	
	private void putText(String previous, String[] result) {
		if(result==null) {
			return;
		}
		
		String finStr="";
		for(int i=0; i< result.length; i++) {
			finStr =finStr + result[i];
		}
		if(result.length>1 && previous!=null) {
			//inputBox.getText().replace(pos-1, pos, finStr);
			inputConn.deleteSurroundingText(1, 0);
			inputConn.commitText(finStr, 1);
		} else {
			//inputBox.getText().insert(pos, finStr);
			inputConn.commitText(finStr, 1);
		}
		
	}
	
	public void showSliceText() {
		CharSequence str = inputConn.getTextBeforeCursor(MAX_SLICE_LENGTH, 0);
		if(str==null) str = "";
		else {
			String s = str.toString();
			s = s.substring(s.lastIndexOf("\n")+1);
			str = s;
		}
		sliceView.setText(str);
		if(langProc.getFontName()==null) {
			sliceView.setTypeface(Typeface.SANS_SERIF);
		} else {
			sliceView.setTypeface(Typeface.createFromAsset(this.getContext().getAssets(), langProc.getFontName()));
		}
	}
}
