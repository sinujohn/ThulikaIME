package me.sinu.keycog.trainer;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import me.sinu.keycog.ocr.Entry;
import me.sinu.keycog.ocr.ImageFile;
import me.sinu.keycog.ocr.SampleData;
import me.sinu.thulika.entity.CharData;
import me.sinu.thulika.entity.Engine;
import me.sinu.thulika.entity.ImageData;
import me.sinu.thulika.entity.MultiSOM;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.som.SOM;
import org.encog.neural.som.training.clustercopy.SOMClusterCopyTraining;

public class Trainer {

	private static int DOWNSAMPLE_HEIGHT = 10;
	private static int DOWNSAMPLE_WIDTH = 10;
	private static final String LETTER_START="L_";
	private static final String TAG = "OCR";
	private Entry entry;
	private List<SampleData> letterList;
	private HashMap<CharData, HashMap<ImageFile, SampleData>> charSet;
	private CharData[] neuronMap;
	private String progress="";
	/**
	 * The neural network.
	 */
	private MultiSOM net;

	/*public Trainer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}*/	
	
	/*public void init() {
		letterFilepath = getContext().getExternalFilesDir(null).getPath();
	}*/
	
	private void createEntity(int w, int h) {
		SampleData sampleData = new SampleData("?", DOWNSAMPLE_WIDTH, DOWNSAMPLE_HEIGHT);
		entry = new Entry(w, h);
		entry.setSampleData(sampleData);
	}
	
	/*public HashMap<ImageData, SampleData> getImageMap() {
		return imageMap;
	}*/
	public HashMap<CharData,HashMap<ImageFile,SampleData>> getCharSet() {
		return charSet;
	}
	
	public boolean deleteFile(String filename) {
		for(CharData cData : charSet.keySet()) {
			HashMap<ImageFile, SampleData> map = charSet.get(cData);
			for(ImageFile iFile : map.keySet()) {
				if(iFile.getFilename().equals(filename)) {
					SampleData sData = map.get(iFile);
					letterList.remove(sData);
					map.remove(iFile);
					if(map.isEmpty()) {
						charSet.remove(cData);
					}
					File file = new File(filename);
					return file.delete();
				}
			}
		}
		return false;
	}

	/*public void addLetterAction(String letter) {
		if (letter == null || letter.isEmpty()) {
			Log.i(TAG, "No letter");
			return;
		}
		ImageData iData = new ImageData(getImagePixels());
		try {
			String fname = newFilename(letterFilepath, letter);
			String path = letterFilepath.endsWith("/")? letterFilepath : letterFilepath+"/";
			iData.save(path + fname);
		} catch (IOException e) {
			Log.e(TAG, "Error in Saving file", e);
		}
		clearAction();
	}*/
	
	private String newFilename(String filepath) {
		File dir = new File(filepath);
		final String prefix = LETTER_START;
		File [] files = dir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.startsWith(prefix);
		    }
		}); 
		Arrays.sort(files);
		String newName;
		try {
			newName = files[files.length-1].getName();
			newName = newName.substring(newName.lastIndexOf("_")+1);
			newName = "" + (Integer.parseInt(newName)+1);
		} catch (Exception e) {
			newName = "1";
		}
		
		newName = prefix+"_"+newName;
		return newName;
	}
	
	private File[] getFiles(String filepath) {
		File dir = new File(filepath);
		final String prefix = LETTER_START;
		File [] files = dir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.startsWith(prefix);
		    }
		});
		return files;
	}
	
	public void loadLettersAction(String filename, int dWidth, int dHeight) {
		Trainer.DOWNSAMPLE_WIDTH = dWidth;
		Trainer.DOWNSAMPLE_HEIGHT = dHeight;
		letterList = new ArrayList<SampleData>();
		charSet = new HashMap<CharData, HashMap<ImageFile,SampleData>>();
		File[] files = getFiles(filename);
		String total = ""+files.length;
		System.out.println("Loading... please wait. Total number : "+ total);
		int i=1;
		for(File f:files) {
			ImageData iData = new ImageData();
			try {
				iData.restore(f.getPath());
				//String fname = f.getName();
				//String letter = iData.getLetter();//fname.split("_")[1];
				addLetterAction(f.getPath(), iData);
				progress = i + "/" + total;
				System.out.println("Done file "+ i++);
			} catch (Exception e) {
				System.out.println("Cannot Load " + f.getPath());
				e.printStackTrace();
			}
		}
		System.out.println("Loading finished. :) Yippieee");
	}
	
	private void addLetterAction(String filename, ImageData iData) {
		/*if (letter == null || letter.isEmpty()) {
			System.out.println("No letter");
			return;
		}*/
		createEntity(iData.getWidth(), iData.getHeight());
		this.entry.downsample(iData.getPixels());
		this.entry.getSampleData().setLetter(iData.getLetter());//(letter.charAt(0));
		SampleData sData = (SampleData) entry.getSampleData().clone();
		letterList.add(sData);
		
		ImageFile iFile = new ImageFile(filename, iData);
		CharData symData = new CharData(iData.getLetter());
		HashMap<ImageFile, SampleData> imageMap = charSet.get(symData);
		if(imageMap==null) {
			imageMap = new HashMap<ImageFile, SampleData>();
		}
		imageMap.put(iFile, sData);
		
		charSet.put(symData, imageMap);
	}
	
	/**
	 * Run method for the background training thread.
	 */
	private void trainSOM() {
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

			this.net = new MultiSOM(inputNeuron,outputNeuron);
			this.net.reset();

			SOMClusterCopyTraining train = new SOMClusterCopyTraining(this.net,trainingSet);
			
			train.iteration();

			showMessageBox("Training has completed." + trainingSet.getRecordCount());
			System.out.println("Training has completed.");

			//neuronMap = mapNeurons();
			neuronMap = makeNeuronMap();
		} catch (final Exception e) {
			showMessageBox("ERROR: "+e.getMessage());
			e.printStackTrace();
		}

	}
	
	public boolean trainAndSave(String filename, String langId) throws IOException {
		trainSOM();
		Engine en = new Engine();
		en.setNet(net);
		en.setNeuronMap(neuronMap);
		en.setLangId(langId);
		en.setSampleDataWidth(Trainer.DOWNSAMPLE_WIDTH);
		en.setSampleDataHeight(Trainer.DOWNSAMPLE_HEIGHT);
		en.save(filename);
		return true;
	}
	
	/**
	 * Called when the recognize button is pressed.
	 */
	/*public void recognizeAction() {
		if (this.net == null) {
			showMessageBox("I need to be trained first!");
			Log.e(TAG, "I need to be trained first!");
			return;
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
		if(neuronMap==null) {
			neuronMap = mapNeurons();
		}
		showMessageBox("  " + neuronMap[best] + "   (Neuron #"+ best + " fired)");
		clearAction();
	}*/

	/*private void clearAction() {
		this.entry.clear();
		super.clear();
	}*/

	/**
	 * Used to map neurons to actual letters.
	 * 
	 * @return The current mapping between neurons and letters as an array.
	 */
	String[] mapNeurons() {
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
	}
	
	private CharData[] makeNeuronMap() {
		String[] map = mapNeurons();
		List<CharData> retMap = new ArrayList<CharData>();
		for(String letter : map) {
			for(CharData cData : charSet.keySet()) {
				if(cData.toString().equals(letter)) {
					retMap.add(cData);
					break;
				}
			}
		}
		return retMap.toArray(new CharData[0]);
	}

	private void showMessageBox(String msg) {
		//new AlertDialog.Builder(this.getContext()).setMessage(msg).show();
		System.out.println(msg);
	}
	
}
