/*
 * Author : Sinu John
 * www.sinujohn.wordpress.com
 */

package me.sinu.thulika.entity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Engine implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6328105763027753353L;
	private MultiSOM net;
	private CharData[] neuronMap;
	private int sampleDataWidth;
	private int sampleDataHeight;
	private String langId;
	
	public MultiSOM getNet() {
		return net;
	}
	public CharData[] getNeuronMap() {
		return neuronMap;
	}
	
	public void setNet(MultiSOM net) {
		this.net = net;
	}
	public void setNeuronMap(CharData[] neuronMap) {
		this.neuronMap = neuronMap;
	}
	public int getSampleDataWidth() {
		return sampleDataWidth;
	}
	public void setSampleDataWidth(int sampleDataWidth) {
		this.sampleDataWidth = sampleDataWidth;
	}
	public int getSampleDataHeight() {
		return sampleDataHeight;
	}
	public void setSampleDataHeight(int sampleDataHeight) {
		this.sampleDataHeight = sampleDataHeight;
	}
	public String getLangId() {
		return langId;
	}
	public void setLangId(String langId) {
		this.langId = langId;
	}
	public void save(String filename) throws IOException {
		FileOutputStream fout = new FileOutputStream(filename);
		ObjectOutputStream oout = new ObjectOutputStream(fout);
		oout.writeObject(this);
	}
	
	public void restore(InputStream fstream) throws Exception {
		ObjectInputStream oin = new ObjectInputStream(fstream);
		Object obj = oin.readObject();
		Engine en = (Engine) obj;
		net = en.getNet();
		neuronMap = en.getNeuronMap();
		sampleDataWidth = en.getSampleDataWidth();
		sampleDataHeight = en.getSampleDataHeight();
		langId = en.getLangId();
	}
	
}
