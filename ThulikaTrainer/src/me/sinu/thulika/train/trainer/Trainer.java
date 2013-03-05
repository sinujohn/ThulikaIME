/*
 * Author : Sinu John
 * www.sinujohn.wordpress.com
 */

package me.sinu.thulika.train.trainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

import me.sinu.thulika.entity.CharData;
import me.sinu.thulika.entity.Engine;
import me.sinu.thulika.entity.ImageData;
import me.sinu.thulika.entity.MultiSOM;
import me.sinu.thulika.train.ocr.Entry;
import me.sinu.thulika.train.ocr.SampleData;
import me.sinu.thulika.view.SingleTouchEventView;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Trainer extends SingleTouchEventView{

	private static int DOWNSAMPLE_HEIGHT = 10;
	private static int DOWNSAMPLE_WIDTH = 10;
	private static final String LETTER_START="L_";
	private static final String ENGINE_START="E_";
	private static final String TAG = "OCR";
	private final String LETTERDIR = "letters";
	private final String ENGINEDIR = "engines";
	public static final String FONT_NAME = "IndianFonts.ttf";
	//public static final String APPDIR = "ThulikaTrainer";
	private Entry entry;
	//private List<SampleData> letterList = new ArrayList<SampleData>();
	private CharData[] neuronMap = null;
	private String externalDir;
	private String letterDir;
	private String engineDir;
	private Engine engine;
	
	/**
	 * The neural network.
	 */
	private MultiSOM net;

	public Trainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		externalDir = getContext().getExternalFilesDir(null).getPath();
		//externalDir = (externalDir.endsWith("/")? externalDir : externalDir+"/") + APPDIR;
		letterDir = externalDir.endsWith("/")? externalDir : externalDir+"/";
		letterDir = letterDir + LETTERDIR;
		engineDir = externalDir.endsWith("/")? externalDir : externalDir+"/";
		engineDir = engineDir + ENGINEDIR;
		File lDir = new File(letterDir);
		if(!lDir.isDirectory()) {
			lDir.mkdir();
		}
		File eDir = new File(engineDir);
		if(!eDir.isDirectory()) {
			eDir.mkdir();
		}
		if(!lDir.isDirectory()) {
			Log.e(TAG, "Directory doesnt exist : " + letterDir);
		}
		if(!eDir.isDirectory()) {
			Log.e(TAG, "Directory doesnt exist : " + engineDir);
		}
	}	
	
	public void init() {
		//letterDir = getContext().getExternalFilesDir(null).getPath();
		SampleData sampleData = new SampleData("?", DOWNSAMPLE_WIDTH, DOWNSAMPLE_HEIGHT);
		entry = new Entry(this.getWidth(), this.getHeight());
		entry.setSampleData(sampleData);
	}

	public void addLetterAction(String letter) {
		if (isEmpty() || letter == null || letter.isEmpty()) {
			Toast msg = Toast.makeText(getContext(),"Please provide a letter", Toast.LENGTH_LONG);
			msg.show();
			Log.i(TAG, "No letter");
			return;
		}
		ImageData iData = new ImageData(getImagePixels(), this.getWidth(), this.getHeight(), letter);
		try {
			String fname = newFilename(letterDir);
			String path = letterDir.endsWith("/")? letterDir : letterDir+"/";
			iData.save(path + fname);
		} catch (IOException e) {
			Log.e(TAG, "Error in Saving file", e);
		}
		/*this.entry.downsample(getImagePixels());
		this.entry.getSampleData().setLetter(letter.charAt(0));
		letterList.add((SampleData) entry.getSampleData().clone());*/
		clearAction();
	}
	
	private String newFilename(String filepath) {
		File dir = new File(filepath);
		final String prefix = LETTER_START;
		File [] files = dir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.startsWith(prefix);
		    }
		}); 
		Integer filenos[] = new Integer[files.length];
		int i=0;
		for(File file : files) {
			String name = file.getName();
			int no;
			try {
				no = Integer.parseInt(name.substring(name.lastIndexOf("_")+1));
			} catch(Exception e) {
				no = 0;
			}			
			filenos[i++] = no;
		}
		Arrays.sort(filenos);
		/*Arrays.sort(files);
		String newName;
		try {
			newName = files[files.length-1].getName();
			newName = newName.substring(newName.lastIndexOf("_")+1);
			newName = "" + (Integer.parseInt(newName)+1);
		} catch (Exception e) {
			newName = "1";
		}
		
		newName = prefix+newName;*/
		String newName;
		try {
			newName = ""+(filenos[filenos.length-1]+1);
		} catch (Exception e) {
			newName = "1";
		}
		newName = prefix + newName;
		return newName;
	}
	
	private File[] getFiles(String filepath, final String prefix) {
		File dir = new File(filepath);
		//final String prefix = LETTER_START;
		File [] files = dir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.startsWith(prefix);
		    }
		});
		return files;
	}
	
	public void deleteLastLetterFile() {
		String fname = getLastLetterFile(letterDir);
		if(fname != null) {
			File file = new File(fname);
			if(file.delete()) {
				Toast msg = Toast.makeText(getContext(),"File " + file.getName() + " deleted", Toast.LENGTH_LONG);
				msg.show();
			} else {
				Toast msg = Toast.makeText(getContext(),"File " + file.getName() + " cannot be deleted", Toast.LENGTH_LONG);
				msg.show();
			}
		}
	}
	
	private String getLastLetterFile(String filepath) {
		File[] files = getFiles(filepath, LETTER_START);
		if(files.length==0) {
			return null;
		}
		Integer filenos[] = new Integer[files.length];
		int i=0;
		for(File file : files) {
			String name = file.getName();
			int no;
			try {
				no = Integer.parseInt(name.substring(name.lastIndexOf("_")+1));
			} catch(Exception e) {
				no = 0;
			}			
			filenos[i++] = no;
		}
		Arrays.sort(filenos);
		String fname = LETTER_START + (filenos[filenos.length-1]);
		return (filepath.endsWith("/")? filepath : filepath+"/") + fname;
	}
	
	/*public void loadLettersAction() {
		init();
		File[] files = getFiles(letterFilepath);
		Toast msg = Toast.makeText(getContext(),"Loading... please wait", Toast.LENGTH_LONG);
		msg.show();
		for(File f:files) {
			ImageData iData = new ImageData();
			try {
				iData.restore(f.getPath());
				String fname = f.getName();
				String letter = fname.split("_")[1];
				addLetterAction(letter, iData.getPixels());
			} catch (Exception e) {
				Log.e(TAG, "Cannot Load " + f.getPath(), e);
			}
		}
		showMessageBox("Loading finished. :) Yippieee");
	}
	
	public void addLetterAction(String letter, int[] pixels) {
		if (letter == null || letter.isEmpty()) {
			Log.i(TAG, "No letter");
			return;
		}
		this.entry.downsample(pixels);
		this.entry.getSampleData().setLetter(letter.charAt(0));
		letterList.add((SampleData) entry.getSampleData().clone());
	}*/
	
	public void loadEngine() {
		Engine en = new Engine();
		File[] files = getFiles(engineDir, ENGINE_START);
		Arrays.sort(files);
		try {
			en.restore(new FileInputStream(files[0].getPath()));
			this.engine = en;
			this.net = en.getNet();
			this.neuronMap = en.getNeuronMap();
			Trainer.DOWNSAMPLE_WIDTH = en.getSampleDataWidth();
			Trainer.DOWNSAMPLE_HEIGHT = en.getSampleDataHeight();
			init();
			Toast msg = Toast.makeText(getContext(),"Loaded Engine:" + files[0].getPath(), Toast.LENGTH_LONG);
			msg.show();
		} catch (Exception e) {
			Log.e(TAG, "Cannot Load " + files[0].getPath(), e);
		}
	}
	
	/**
	 * Run method for the background training thread.
	 */
	/*public void trainSOM() {
		try {
			final int inputNeuron = Trainer.DOWNSAMPLE_HEIGHT
					* Trainer.DOWNSAMPLE_WIDTH;
			final int outputNeuron = this.letterList.size();

			final MLDataSet trainingSet = new BasicMLDataSet();
			for (int t = 0; t < this.letterList.size(); t++) {
				final MLData item = new BasicMLData(inputNeuron);
				int idx = 0;
				final SampleData ds = this.letterList.get(t);
				for (int y = 0; y < ds.getHeight(); y++) {
					for (int x = 0; x < ds.getWidth(); x++) {
						item.setData(idx++, ds.getData(x, y) ? .5 : -.5);
					}
				}

				trainingSet.add(new BasicMLDataPair(item, null));
			}

			this.net = new SOM(inputNeuron,outputNeuron);
			this.net.reset();

			SOMClusterCopyTraining train = new SOMClusterCopyTraining(this.net,trainingSet);
			
			train.iteration();

			showMessageBox("Training has completed." + trainingSet.getRecordCount());
			Log.i(TAG, "Training has completed.");

			neuronMap = mapNeurons();

		} catch (final Exception e) {
			showMessageBox("ERROR: "+e.getMessage());
			Log.e(TAG, e.getMessage());
		}

	}*/
	
	public boolean isReadyToRecognize() {
		if (this.net == null) {
			return false;
		}
		return true;
	}
	
	public Engine getEngine() {
		return engine;
	}
	
	/**
	 * Called when the recognize button is pressed.
	 */
	public CharData[] recognizeAction() {
		if (this.net == null) {
			showMessageBox("I need to be trained first!");
			Log.e(TAG, "I need to be trained first!");
			return null;
		}
		this.entry.downsample(getImagePixels());

		final MLData input = new BasicMLData(Trainer.DOWNSAMPLE_HEIGHT * Trainer.DOWNSAMPLE_WIDTH);
		int idx = 0;
		final SampleData ds = this.entry.getSampleData();
		for (int y = 0; y < ds.getHeight(); y++) {
			for (int x = 0; x < ds.getWidth(); x++) {
				input.setData(idx++, ds.getData(x, y) ? .5 : -.5);
			}
		}

		final int best = this.net.classify(input);
		/*if(neuronMap==null) {
			neuronMap = mapNeurons();
		}*/
		showMessageBox(" " + neuronMap[best]);
		//showMessageBox("  " + neuronMap[best] + "   (Neuron #"+ best + " fired)");
		clearAction();
		return new CharData[]{neuronMap[best]};
	}

	public void clearAction() {
		this.entry.clear();
		super.clear();
	}

	/**
	 * Used to map neurons to actual letters.
	 * 
	 * @return The current mapping between neurons and letters as an array.
	 */
	/*String[] mapNeurons() {
		final String map[] = new String[this.letterList.size()];

		for (int i = 0; i < map.length; i++) {
			map[i] = "?";
		}
		for (int i = 0; i < this.letterList.size(); i++) {
			final MLData input = new BasicMLData(Trainer.DOWNSAMPLE_HEIGHT * Trainer.DOWNSAMPLE_WIDTH);
			int idx = 0;
			final SampleData ds = (SampleData) this.letterList.get(i);
			for (int y = 0; y < ds.getHeight(); y++) {
				for (int x = 0; x < ds.getWidth(); x++) {
					input.setData(idx++, ds.getData(x, y) ? .5 : -.5);
				}
			}

			final int best = this.net.classify(input);
			map[best] = ds.getLetter();
		}
		return map;
	}*/

	private void showMessageBox(String msg) {
		TextView content = new TextView(this.getContext());
        content.setText(msg);content.setTextSize(42);
        content.setTypeface(Typeface.createFromAsset(this.getContext().getAssets(), FONT_NAME));
        new AlertDialog.Builder(this.getContext()).setView(content).show();
		//new AlertDialog.Builder(this.getContext()).setMessage(msg).show();
	}
	
	class AlertPrompt {
		String letter;
		AlertPrompt(Context context) {
			final EditText input = new EditText(context);
			new AlertDialog.Builder(context)
		    .setTitle("Add Letter")
		    //.setMessage()
		    .setView(input)
		    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		            letter = input.getText().toString(); 
		            Log.i(TAG, "onClick Letter" + letter);
		        }
		    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		            // Do nothing.
		        }
		    }).show();
		}
		public String getLetter() {
			return letter;
		}
	}
	
}
