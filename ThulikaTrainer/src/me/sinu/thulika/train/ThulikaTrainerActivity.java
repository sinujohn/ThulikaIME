package me.sinu.thulika.train;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import me.sinu.thulika.entity.CharData;
import me.sinu.thulika.entity.LanguageProcessor;
import me.sinu.thulika.entity.LetterBuffer;
import me.sinu.thulika.lang.IndicRecognizer;
import me.sinu.thulika.train.trainer.Trainer;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ThulikaTrainerActivity extends Activity {
	
	private static final String COMMA_STRING = "&COMMA;";
	//private static final String APPDIR = Trainer.APPDIR;
	private Trainer trainer;
	private LinearLayout letterPanel;
	private EditText inputBox;
	private final String LETTERSOURCE = "alpha.txt";
	private LanguageProcessor langProc;
	private LetterBuffer lBuffer;
	private LinearLayout suggestPanel;
	private TextView stackLabel;
	
	private String[] getLetters() throws Exception {
		String externalDir = getExternalFilesDir(null).getPath();
		/*externalDir = Environment.getExternalStorageDirectory().getPath();//getContext().getExternalFilesDir(null).getPath();
		externalDir = (externalDir.endsWith("/")? externalDir : externalDir+"/") + APPDIR;*/
		String letterSourceFile = externalDir.endsWith("/")? externalDir : externalDir+"/";
		letterSourceFile = letterSourceFile+LETTERSOURCE;
		FileInputStream fin = new FileInputStream(letterSourceFile);
		BufferedReader in = new BufferedReader(new InputStreamReader(fin, "UTF-16"));
		String str = in.readLine();
		return str.split(",");
	}
	
	/*private void showCandidates() {
		stackLabel.setText(langProc.getStack());
		suggestPanel.removeAllViews();
		if(!lBuffer.isEmpty()) {
			List<CharData> suggestions = lBuffer.getSuggestions();
			if(suggestions!=null && !suggestions.isEmpty()) {
				Context ctx = getApplicationContext();
				for(final CharData suggestion : suggestions) {
					TextView suggestView = new TextView(ctx);
					suggestView.setHeight(30);
					suggestView.setWidth(30);
					suggestView.setText(suggestion.getSymbol());
					suggestView.setOnClickListener(new OnClickListener() {
		    			public void onClick(View v) {
		    				String[] result = lBuffer.replace(suggestion);//lBuffer.replace(((TextView)v).getText().toString());
		    				
		    				int pos = inputBox.getSelectionStart();
							String previous;
							try {
								previous = "" + (char)inputBox.getText().toString().codePointBefore(pos);
							} catch (Exception e) {
								previous = null;
							}
		    				
		    				putText(pos, previous, result);
		    				showCandidates();
		    			}
		    		});
					suggestPanel.addView(suggestView);
				}
			}
		}
		suggestPanel.invalidate();
	}*/
	
	private void putText(int pos, String previous, String[] result) {
		if(result==null) {
			return;
		}
		String finStr="";
		for(int i=0; i< result.length; i++) {
			finStr =finStr + result[i];
		}
		if(result.length>1 && previous!=null) {
			inputBox.getText().replace(pos-1, pos, finStr);
		} else {
			inputBox.getText().insert(pos, finStr);
		}
	}

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        setContentView(R.layout.trainer);
        
        letterPanel = (LinearLayout) findViewById(R.id.letterPanel);
        trainer = (Trainer) findViewById(R.id.trainer1);
        inputBox = (EditText) findViewById(R.id.inputBox);
        suggestPanel = (LinearLayout) findViewById(R.id.sPanel);
        stackLabel = (TextView) findViewById(R.id.stackLabel);
        String[] letters;
		try {
			letters = getLetters();
		} catch (Exception e) {
			letters = getResources().getStringArray(R.array.letters);
		}
		
		Button recognizeB = (Button) findViewById(R.id.recognizeButton); //new Button(getApplicationContext());
		//recognizeB.setText("Recognize");
		recognizeB.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast msg = Toast.makeText(getBaseContext(),
						"You have clicked Recognize Button", Toast.LENGTH_LONG);
				if(trainer.isReadyToRecognize()) {
					if(langProc==null) {
						langProc = new IndicRecognizer(trainer.getEngine());
						lBuffer = new LetterBuffer(langProc);
					}
					CharData[] cData = trainer.recognizeAction();
					int pos = inputBox.getSelectionStart();
					String previous;
					try {
						previous = "" + (char)inputBox.getText().toString().codePointBefore(pos);
					} catch (Exception e) {
						previous = null;
					}
					String[] result = lBuffer.put(previous, cData); // langProc.process(previous, cData);
					//showCandidates(/*pos, previous*/);
					if(result == null) {
						msg.show();
						return;
					}
					
					putText(pos, previous, result);
				}
				msg.show();
			}

		});
		//letterPanel.addView(recognizeB);
		
		Button delLastButton = (Button) findViewById(R.id.delLastButton);
		delLastButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				trainer.deleteLastLetterFile();
			}
		});
		
		Button clearButton = (Button) findViewById(R.id.clearButton);
		clearButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				trainer.clearAction();
			}
		});
		
        for(int i=0; i<letters.length; i++) {
        	Context ctx = getApplicationContext();
        	final Button b = new Button(ctx);
        	b.setTypeface(Typeface.createFromAsset(getAssets(), Trainer.FONT_NAME));
        	//b.setTextSize(10);
        	if(letters[i].trim().equals(COMMA_STRING)) {
        		b.setText(",");
        	} else {
        		b.setText(letters[i].trim());
        	}
        	b.setMinWidth(80);
        	b.setOnClickListener(new OnClickListener() {
    			public void onClick(View v) {
    				Toast msg = Toast.makeText(getBaseContext(),
    						"You have clicked Add Button", Toast.LENGTH_LONG);
    				trainer.addLetterAction(b.getText().toString());
    				msg.show();
    			}
    		});
        	letterPanel.addView(b);
        }
        
        /*addB = (Button) findViewById(R.id.addButton);
        trainB = (Button) findViewById(R.id.trainButton);
        recognizeB = (Button) findViewById(R.id.recognizeButton);
        inputText = (EditText) findViewById(R.id.inputText);
		addB.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast msg = Toast.makeText(getBaseContext(),
						"You have clicked Add Button", Toast.LENGTH_LONG);
				ocr.addLetterAction(inputText.getText().toString());
				msg.show();
			}
		});
        
		trainB.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast msg = Toast.makeText(getBaseContext(),
						"You have clicked Train Button", Toast.LENGTH_LONG);
				ocr.trainSOM();
				msg.show();
			}
		});
		
		recognizeB.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast msg = Toast.makeText(getBaseContext(),
						"You have clicked Recognize Button", Toast.LENGTH_LONG);
				ocr.recognizeAction();
				msg.show();
			}
		});*/
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
    	super.onStart();
    	trainer.init();
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.trainmenu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
	        case R.id.load:
	            trainer.loadEngine();
	            return true;
            /*case R.id.train:
                ocr.trainSOM();
                return true;
            case R.id.recognize:
                ocr.recognizeAction();
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}