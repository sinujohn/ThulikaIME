/*
 * Author : Sinu John
 * www.sinujohn.wordpress.com
 */

package me.sinu.thulika.ime;

import java.lang.reflect.Constructor;

import me.sinu.thulika.R;
import me.sinu.thulika.entity.CharData;
import me.sinu.thulika.entity.Engine;
import me.sinu.thulika.entity.LanguageProcessor;
import me.sinu.thulika.entity.LetterBuffer;
import me.sinu.thulika.ocr.Ocr;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ThulikaIME extends InputMethodService{

	private static final String TAG = "LOG";
	private static final String PACKAGE_NAME = "me.sinu.thulika";
	private static final String SETTINGS_ACTIVITY = "me.sinu.thulika.ThulikaActivity";
	private static final String HELP_ACTIVITY = "me.sinu.thulika.HelpActivity";
	private Ocr ocr;
	//private Ocr numpad;
	private View rootView;
	private TextView stackView;
	private LinearLayout movableView;
	private TextView sliceView;
	
	private LanguageProcessor langProc;
	private LetterBuffer lBuffer;
	private LinearLayout suggestionsViewGroup;
	
	boolean menuVisible;
	private LinearLayout menuView;
	
	private final String stupidpenPrefs = "me.sinu.stupidpen";
	private final String touchupDelayKey = "me.sinu.stupidpen.touchupDelay";
	private final String defaultKeyboard = "me.sinu.stupidpen.defaultKeyboard";
	private final String DEF_KB_VAL = "me.sinu.thulika.lang.MalayalamProcessor";
	
	private int controlViewMovePointCount;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	@Override
	public void onInitializeInterface() {
		// TODO Auto-generated method stub
		super.onInitializeInterface();
	}
	
	private void showMenu() {
		menuView.setVisibility(View.VISIBLE);
		menuVisible = true;
	}
	
	private void hideMenu() {
		menuView.setVisibility(View.GONE);
		menuVisible = false;
	}
	
	private void populateMenu() {
		TextView[] items = new TextView[] {
								makeMenuItem("Help", null),
								makeMenuItem("Settings", null),
								makeMenuItem("Malayalam", "me.sinu.thulika.lang.MalayalamProcessor"),
								makeMenuItem("English (beta)", "me.sinu.thulika.lang.EnglishProcessor"),
								makeMenuItem("Numeric", "me.sinu.thulika.lang.NumberProcessor"),
								makeMenuItem("Symbols (beta)", "me.sinu.thulika.lang.SymbolProcessor")};
		
		for(TextView item : items) {
			menuView.addView(item);
		}
		
	}
	
	private TextView makeMenuItem(final String menuText, final String classname) {
		final SharedPreferences prefs = this.getSharedPreferences(stupidpenPrefs, Context.MODE_PRIVATE);
		TextView menuItem = new TextView(this);
		menuItem.setMinimumHeight(40);
		menuItem.setMinimumWidth(70);
		menuItem.setTextAppearance(this, R.style.menuText);
		menuItem.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.horizontal_line);
		menuItem.setText(menuText);
		menuItem.setGravity(Gravity.LEFT & Gravity.CENTER_VERTICAL);
		menuItem.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(menuText.equalsIgnoreCase("help")) {
					//show help
					Intent intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					intent.setClassName(PACKAGE_NAME, HELP_ACTIVITY);
					startActivity(intent);
				} else if(menuText.equalsIgnoreCase("settings")) {
					//call activity using intent
					Intent intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					intent.setClassName(PACKAGE_NAME, SETTINGS_ACTIVITY);
					startActivity(intent);
				} else {
					try {
						loadProcessor(classname);
						prefs.edit().putString(defaultKeyboard, classname).commit();
					} catch (Exception e) {
						//Log.e(TAG, e.getMessage(), e);
					}
				}
				
				hideMenu();
			}
		});
		return menuItem;
	}
	
	private void dotButtonAction() {
		CharData dot = new CharData(".");
		dot.setAlign(0);
		ocr.dumbProcess(dot);
	}
	
	private void spaceButtonAction() {
		CharData dot = new CharData(" ");
		dot.setAlign(0);
		ocr.dumbProcess(dot);
	}
	
	private void enterButtonAction() {
		CharData dot = new CharData("\n");
		dot.setAlign(0);
		ocr.dumbProcess(dot);
	}
	
	private void backspButtonAction() {
		CharData dot = new CharData("");
		dot.setAlign(0);
		ocr.dumbProcess(dot);
		getCurrentInputConnection().deleteSurroundingText(1, 0);
		ocr.showSliceText();
	}
	
	@Override
	public View onCreateInputView() {
		rootView = getLayoutInflater().inflate(R.layout.ime, null);
		ocr = (Ocr) rootView.findViewById(R.id.writePad);
		stackView = (TextView) rootView.findViewById(R.id.stackView);
		suggestionsViewGroup = (LinearLayout) rootView.findViewById(R.id.suggestionsViewGroup);
		sliceView = (TextView) rootView.findViewById(R.id.sliceView);
		movableView = (LinearLayout) rootView.findViewById(R.id.movableView);
		ocr.setImeService(this);
		
		View showMenuButton = rootView.findViewById(R.id.showMenuButton);
		menuView = (LinearLayout) rootView.findViewById(R.id.menuView);
		View dotButton = rootView.findViewById(R.id.dotButton);
		View spaceButton = rootView.findViewById(R.id.spaceButton);
		View enterButton = rootView.findViewById(R.id.enterButton);
		View backspButton = rootView.findViewById(R.id.backspButton);
		final View menuControlView = rootView.findViewById(R.id.controlsView);
		
		this.populateMenu();
		this.hideMenu();
		
		dotButton.setOnTouchListener(new View.OnTouchListener() {

		    private Handler mHandler;

		    @Override public boolean onTouch(View v, MotionEvent event) {
		        switch(event.getAction()) {
		        case MotionEvent.ACTION_DOWN:
		            if (mHandler != null) return true;
		            mHandler = new Handler();
		            mHandler.postDelayed(mAction, 500);
		            break;
		        case MotionEvent.ACTION_UP:
		            if (mHandler == null) return true;
		            mHandler.removeCallbacks(mAction);
		            dotButtonAction();
		            mHandler = null;
		            break;
		        }
		        return true;
		    }

		    Runnable mAction = new Runnable() {
		        @Override public void run() {
		            dotButtonAction();
		            mHandler.postDelayed(this, 200);
		        }
		    };

		});
		
		spaceButton.setOnTouchListener(new View.OnTouchListener() {

		    private Handler mHandler;

		    @Override public boolean onTouch(View v, MotionEvent event) {
		        switch(event.getAction()) {
		        case MotionEvent.ACTION_DOWN:
		            if (mHandler != null) return true;
		            mHandler = new Handler();
		            mHandler.postDelayed(mAction, 500);
		            break;
		        case MotionEvent.ACTION_UP:
		            if (mHandler == null) return true;
		            mHandler.removeCallbacks(mAction);
		            spaceButtonAction();
		            mHandler = null;
		            break;
		        }
		        return true;
		    }

		    Runnable mAction = new Runnable() {
		        @Override public void run() {
		        	spaceButtonAction();
		            mHandler.postDelayed(this, 200);
		        }
		    };

		});
		
		enterButton.setOnTouchListener(new View.OnTouchListener() {

		    private Handler mHandler;

		    @Override public boolean onTouch(View v, MotionEvent event) {
		        switch(event.getAction()) {
		        case MotionEvent.ACTION_DOWN:
		            if (mHandler != null) return true;
		            mHandler = new Handler();
		            mHandler.postDelayed(mAction, 500);
		            break;
		        case MotionEvent.ACTION_UP:
		            if (mHandler == null) return true;
		            mHandler.removeCallbacks(mAction);
		            enterButtonAction();
		            mHandler = null;
		            break;
		        }
		        return true;
		    }

		    Runnable mAction = new Runnable() {
		        @Override public void run() {
		        	enterButtonAction();
		            mHandler.postDelayed(this, 200);
		        }
		    };

		});
		
		backspButton.setOnTouchListener(new View.OnTouchListener() {

		    private Handler mHandler;

		    @Override public boolean onTouch(View v, MotionEvent event) {
		        switch(event.getAction()) {
		        case MotionEvent.ACTION_DOWN:
		            if (mHandler != null) return true;
		            mHandler = new Handler();
		            mHandler.postDelayed(mAction, 500);
		            break;
		        case MotionEvent.ACTION_UP:
		            if (mHandler == null) return true;
		            mHandler.removeCallbacks(mAction);
		            backspButtonAction();
		            mHandler = null;
		            break;
		        }
		        return true;
		    }

		    Runnable mAction = new Runnable() {
		        @Override public void run() {
		        	backspButtonAction();
		            mHandler.postDelayed(this, 100);
		        }
		    };

		});
		///////////////////////////////////
		
        showMenuButton.setOnTouchListener(new OnTouchListener() {
        	int _yDelta;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int Y = rootView.getHeight() - (int) event.getRawY();
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
		        case MotionEvent.ACTION_DOWN:
		        	//hideMenu();
		        	controlViewMovePointCount = 0;
		            FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) menuControlView.getLayoutParams();
		            _yDelta = Y - lParams.bottomMargin;
		            break;
		        case MotionEvent.ACTION_UP: 
		        	if(controlViewMovePointCount<=2) {
		        		if(menuVisible) {
		        			hideMenu();
		        		} else {
		        			showMenu();
		        		}
		        	}
		            break;
		        case MotionEvent.ACTION_POINTER_DOWN:
		            break;
		        case MotionEvent.ACTION_POINTER_UP:
		            break;
		        case MotionEvent.ACTION_MOVE:
		        	//hideMenu();
		        	
		        	controlViewMovePointCount++;
		            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) menuControlView.getLayoutParams();
		            layoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
		            layoutParams.bottomMargin = Y - _yDelta;
		            menuControlView.setLayoutParams(layoutParams);
		            break;
		    }
		    rootView.invalidate();
		    return true;
			}
		});
		////////////////////////////////////
		
		
		stackView.setOnTouchListener(new OnTouchListener() {
			int _yDelta;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int Y = (int) event.getRawY();
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
		        case MotionEvent.ACTION_DOWN:
		            FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) movableView.getLayoutParams();
		            _yDelta = Y - lParams.topMargin;
		            break;
		        case MotionEvent.ACTION_UP:
		            break;
		        case MotionEvent.ACTION_POINTER_DOWN:
		            break;
		        case MotionEvent.ACTION_POINTER_UP:
		            break;
		        case MotionEvent.ACTION_MOVE:
		            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) movableView.getLayoutParams();
		            layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
		            layoutParams.topMargin = Y - _yDelta;
		            movableView.setLayoutParams(layoutParams);
		            break;
		    }
		    rootView.invalidate();
		    return true;
			}
		});
		/*ocr.loadEngine();
		ocr.setInputConn(getCurrentInputConnection());*/
		return rootView;
	}
	
	/*@Override
	public void onStartInput(EditorInfo attribute, boolean restarting) {
		// TODO Auto-generated method stub
		super.onStartInput(attribute, restarting);
	}*/
	
	@Override
	public void onFinishInput() {
		super.onFinishInput();
	}
	
	@Override
	public void onStartInputView(EditorInfo info, boolean restarting) {
		super.onStartInputView(info, restarting);
		hideMenu();
		
		try {
			SharedPreferences prefs = this.getSharedPreferences(stupidpenPrefs, Context.MODE_PRIVATE);
			String cname = prefs.getString(defaultKeyboard, DEF_KB_VAL);
			loadProcessor(cname);
			setDelay();
		} catch (Exception e) {
			//Log.e(TAG, e.getMessage(),e);
		}
	}
	
	private void loadProcessor(String classname) throws Exception {
		Class<LanguageProcessor> c = (Class<LanguageProcessor>) Class.forName(classname);
		Constructor<LanguageProcessor> ct = c.getConstructor(Engine.class);
		
		langProc = ct.newInstance(ocr.getEngine());//new MalayalamProcessor(ocr.getEngine());
		lBuffer = new LetterBuffer(langProc);
		ocr.setInputConn(getCurrentInputConnection());
		ocr.loadEngine(langProc, lBuffer, stackView, suggestionsViewGroup, sliceView);
	}
	
	private void setDelay() {
		SharedPreferences prefs = getSharedPreferences(stupidpenPrefs, Context.MODE_PRIVATE);
		ocr.setDelay(prefs.getLong(touchupDelayKey, 100));
	}
	
	@Override
	public boolean onEvaluateFullscreenMode() {
		return true;
	}
	
	@Override
	public void setExtractViewShown(boolean shown) {
		super.setExtractViewShown(false);
	}	
	
	/*
	@Override
	public void onUpdateSelection(int oldSelStart, int oldSelEnd,
			int newSelStart, int newSelEnd, int candidatesStart,
			int candidatesEnd) {
		// TODO Auto-generated method stub
		super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
				candidatesStart, candidatesEnd);
	}
	
	@Override
	public void onDisplayCompletions(CompletionInfo[] completions) {
		// TODO Auto-generated method stub
		super.onDisplayCompletions(completions);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyUp(keyCode, event);
	}*/
	
}
