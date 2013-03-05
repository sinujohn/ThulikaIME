/*
 * Author : Sinu John
 * www.sinujohn.wordpress.com
 */

package me.sinu.thulika.entity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class ImageData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8117312467495671387L;
	int[] pixels;
	int width;
	int height;
	String letter;
	
	public ImageData(int[] pix, int w, int h, String letter) {
		width = w;
		height = h;
		this.letter = letter;
		pixels = new int[pix.length];
		for(int i=0; i<pix.length; i++) {
			pixels[i] = pix[i];
		}
	}
	
	@Override
	public String toString() {
		return letter;
	}
	
	public ImageData() {
		
	}
	
	public int[] getPixels() {
		return pixels;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public void setLetter(String letter) {
		this.letter = letter;
	}
	public String getLetter() {
		return letter;
	}
	
	public void save(String filename) throws IOException {
		FileOutputStream fout = new FileOutputStream(filename);
		ObjectOutputStream oout = new ObjectOutputStream(fout);
		//Log.i(TAG, "Filename is "+filename);
		oout.writeObject(this);
	}
	
	public void restore(String filename) throws Exception {
		FileInputStream fin = new FileInputStream(filename);
		ObjectInputStream oin = new ObjectInputStream(fin);
		Object obj = oin.readObject();
		ImageData image = (ImageData) obj;
		pixels = image.getPixels();
		width = image.getWidth();
		height = image.getHeight();
		letter = image.getLetter();
	}
}
