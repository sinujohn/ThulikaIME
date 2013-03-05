package me.sinu.keycog.ocr;

import me.sinu.thulika.entity.ImageData;

public class ImageFile {

	private ImageData imageData;
	private String filename;
	public ImageData getImageData() {
		return imageData;
	}
	public void setImageData(ImageData imageData) {
		this.imageData = imageData;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public ImageFile(String filename, ImageData imageData) {
		this.filename = filename;
		this.imageData = imageData;
	}
	
	@Override
	public String toString() {
		return filename;
	}
}
