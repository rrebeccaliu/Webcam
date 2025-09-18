import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Webcam-based drawing 
 * Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Spring 2015 (based on a webcam app from previous terms)
 * @author Rebecca Liu and Dylan Lawler, Spring 2021, allowed for interactive webcam coloration and paint mode
 */
public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting, 
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) {
		// TODO: YOUR CODE HERE
		// if webcam mode is called, then only the camera is displayed with no recoloration
		if (displayMode == 'w'){

			g.drawImage(image,0,0,null);

		}
		// if the user switches to recolor mode, the recolored image is displayed
		else if (displayMode == 'r'){
			g.drawImage(finder.getRecoloredImage(), 0, 0, null);

		}
		// if it's on paint mode, a white screen with blue paint will be displayed
		else if (displayMode == 'p'){
			g.drawImage(painting, 0, 0, null);

		}
	}

	/**
	 * Webcam method, here finding regions and updating the painting.
	 */
	@Override
	public void processImage() {
		// TODO: YOUR CODE HERE
		// only sets the image and color if target color exists
		if (targetColor != null){
			finder.setImage(image);
			finder.findRegions(targetColor);

			// recolors the image when recolor mode is activated
			if (displayMode == 'r'){
				finder.recolorImage();
			}
			// processes the painting by ensuring a largest region exists and setting the color of the brush path blue
			if (displayMode == 'p'){
				if(finder.largestRegion()!= null){
					ArrayList<Point> brush =  finder.largestRegion();
					for (Point p : brush){
						painting.setRGB(p.x, p.y, paintColor.getRGB());

					}
				}

			}

		}

	}




	/**
	 * Overrides the DrawingGUI method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		// TODO: YOUR CODE HERE
		// can only work if an image is assigned
		if (image != null) {
			//makes the target color the color of the pixel that the user clicks on
			int colornumber = image.getRGB(x,y);
			targetColor = new Color(colornumber);
		}
	}



	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			saveImage(painting, "pictures/painting.png", "png");
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}
