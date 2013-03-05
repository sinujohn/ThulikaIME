/*
 * Author : Sinu John
 * www.sinujohn.wordpress.com
 */

package me.sinu.thulika;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ThulikaActivity extends Activity {
	
	private static final String PACKAGE_NAME = "me.sinu.thulika";
	private static final String HELP_ACTIVITY = "me.sinu.thulika.HelpActivity";
	
	SeekBar delaySeeker;
	TextView delayView;
	View helpButton;
	View feedbackButton;
	private final String touchupDelayKey = "me.sinu.stupidpen.touchupDelay";
	private final String stupidpenPrefs = "me.sinu.stupidpen";
	private long delay;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    	
        final SharedPreferences prefs = getSharedPreferences(stupidpenPrefs, Context.MODE_PRIVATE);
		delay = prefs.getLong(touchupDelayKey, 250);
		
    	delaySeeker = (SeekBar) findViewById(R.id.delaySeeker);
    	delayView = (TextView) findViewById(R.id.delayView);
    	feedbackButton = findViewById(R.id.feedbackButton);
    	helpButton = findViewById(R.id.helpButton);
    	
    	delayView.setText(String.valueOf(delay));
    	delaySeeker.setProgress((int)delay/50);
    	delaySeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				delayView.setText(String.valueOf(progress*50));		
				prefs.edit().putLong(touchupDelayKey, progress*50).commit();
			}
		});
    	
    	feedbackButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"sjsuperapps@gmail.com"});
				i.putExtra(Intent.EXTRA_SUBJECT, "Thulika IME");
				i.putExtra(Intent.EXTRA_TEXT   , "Feedback\n");
				try {
				    startActivity(Intent.createChooser(i, "Send mail..."));
				} catch (android.content.ActivityNotFoundException ex) {
				    Toast.makeText(ThulikaActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
				}
			}
		});
    	
    	helpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				intent.setClassName(PACKAGE_NAME, HELP_ACTIVITY);
				startActivity(intent);
			}
		});
    }
}