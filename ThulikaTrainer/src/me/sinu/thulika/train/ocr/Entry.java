package me.sinu.thulika.train.ocr;

public class Entry {

	/**
	 * Specifies the left boundary of the cropping rectangle.
	 */
	protected int downSampleLeft;

	/**
	 * Specifies the right boundary of the cropping rectangle.
	 */
	protected int downSampleRight;

	/**
	 * Specifies the top boundary of the cropping rectangle.
	 */
	protected int downSampleTop;

	/**
	 * Specifies the bottom boundary of the cropping rectangle.
	 */
	protected int downSampleBottom;

	private SampleData sampleData;
	private int[] pixels;
	private int width;
	private int height;
	private double ratioX;
	private double ratioY;
	private final int PIXEL_ON = -1;
	private final int PIXEL_OFF = 0;
	
	public void setSampleData(SampleData sampleData) {
		this.sampleData = sampleData;
	}
	public SampleData getSampleData() {
		return sampleData;
	}
	
	public Entry(int w, int h) {
		width = w;
		height = h;
	}
		
	public void downsample(final int[] pix) {
		pixels = pix;//new int[width*height];
		findBounds(width, height);
		this.ratioX = (double) (this.downSampleRight - this.downSampleLeft)
				/ (double) sampleData.getWidth();
		this.ratioY = (double) (this.downSampleBottom - this.downSampleTop)
				/ (double) sampleData.getHeight();
		
		for (int y = 0; y < sampleData.getHeight(); y++) {
			for (int x = 0; x < sampleData.getWidth(); x++) {
				if (downSampleRegion(x, y)) {
					sampleData.setData(x, y, true);
				} else {
					sampleData.setData(x, y, false);
				}
			}
		}
	}
	
	/**
	 * Called to downsample a quadrant of the image.
	 * 
	 * @param x
	 *            The x coordinate of the resulting downsample.
	 * @param y
	 *            The y coordinate of the resulting downsample.
	 * @return Returns true if there were ANY pixels in the specified quadrant.
	 */
	protected boolean downSampleRegion(final int x, final int y) {
		final int w = this.width;
		final int startX = (int) (this.downSampleLeft + (x * this.ratioX));
		final int startY = (int) (this.downSampleTop + (y * this.ratioY));
		final int endX = (int) (startX + this.ratioX);
		final int endY = (int) (startY + this.ratioY);

		for (int yy = startY; yy <= endY; yy++) {
			for (int xx = startX; xx <= endX; xx++) {
				final int loc = xx + (yy * w);

				if (this.pixels[loc] == PIXEL_ON) {
					return true;
				}
			}
		}

		return false;
	}

	
	/**
	 * This method is called to automatically crop the image so that whitespace
	 * is removed.
	 * 
	 * @param w
	 *            The width of the image.
	 * @param h
	 *            The height of the image
	 */
	protected void findBounds(final int w, final int h) {
		// top line
		for (int y = 0; y < h; y++) {
			if (!hLineClear(y)) {
				this.downSampleTop = y-1<0? y : y-1;
				break;
			}

		}
		// bottom line
		for (int y = h - 1; y >= 0; y--) {
			if (!hLineClear(y)) {
				this.downSampleBottom = y+1>=h? y : y+1;
				break;
			}
		}
		// left line
		for (int x = 0; x < w; x++) {
			if (!vLineClear(x)) {
				this.downSampleLeft = x-1<0? x : x-1;
				break;
			}
		}

		// right line
		for (int x = w - 1; x >= 0; x--) {
			if (!vLineClear(x)) {
				this.downSampleRight = x+1>=w? x : x+1;
				break;
			}
		}
	}

	/**
	 * This method is called internally to see if there are any pixels in the
	 * given scan line. This method is used to perform autocropping.
	 * 
	 * @param y
	 *            The horizontal line to scan.
	 * @return True if there were no pixels in this horizontal line.
	 */
	protected boolean hLineClear(final int y) {
		for (int i = 0; i < this.width; i++) {
			if (this.pixels[(y * this.width) + i] == PIXEL_ON) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This method is called to determine ....
	 * 
	 * @param x
	 *            The vertical line to scan.
	 * @return True if there are no pixels in the specified vertical line.
	 */
	protected boolean vLineClear(final int x) {
		final int w = this.width;
		final int h = this.height;
		for (int i = 0; i < h; i++) {
			if (this.pixels[(i * w) + x] == PIXEL_ON) {
				return false;
			}
		}
		return true;
	}

	public void clear() {
		this.sampleData.clear();
		downSampleBottom = downSampleLeft = downSampleRight = downSampleTop =0;
		pixels=null;
	}
}
