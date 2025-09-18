import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 * @author Dylan Lawler and Rebecca Liu, Spring 2021, employed growing region algorithm that allowed for the discoloring
 * of regions of a desired color
 */
public class RegionFinder {
	private static final int maxColorDiff = 20;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50; 				// how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions;			// a region is a list of points
															// so the identified regions are in a list of lists of points

	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;		
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Finds regions of pixels similar to the trackColor and floodfills them with a random color.
	 */
	public void findRegions(Color targetColor) {
		// can only run if there's an image to loop over
		if(image != null){
			// pixel tracking
			// coinciding image for visited that will change the color of the visited pixel
			BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
			// list that holds the pixels that need to be visied
			ArrayList<Point> visit = new ArrayList<Point>();
			// initializes the regions list so it's not null the entire time
			regions = new ArrayList<ArrayList<Point>>();
			// loops through every pixel
			for(int x = 0; x < image.getWidth(); x++){
				for (int y = 0; y < image.getHeight(); y++){
					// only adds a pixel to a region if it hasn't been visited and its the same color as the target
					if (visited.getRGB(x, y) == 0 && colorMatch(targetColor, new Color (image.getRGB(x,y)))) {
						ArrayList<Point> newregion = new ArrayList<Point>();
						Point p = new Point(x,y);
						visit.add(p);
						visited.setRGB(x, y, 1);
						// loops through all the pixels that still need to be visited
						while (visit.size()>0){
							// gets the last point, makes it visited and adds it to the region
							Point p2 = visit.get(visit.size()-1);
							newregion.add(p2);
							visit.remove(p2);
							// loops over the 8 neighbors around the given pixel
							for (int y1 = Math.max(0, p2.y-1); y1 < Math.min(image.getHeight(),p2.y +2); y1++) {
								for (int x1 = Math.max(0, p2.x-1); x1 < Math.min(image.getWidth(), p2.x+2); x1++) {
									// the neighbor will only have to be added to the need to visit list
									// if it matches the target, hasn't yet been visited, and isn't the same pixel as the center
									if (colorMatch(targetColor, new Color(image.getRGB(x1,y1))) && visited.getRGB(x1, y1) == 0){
										visit.add(new Point(x1,y1));
										visited.setRGB(x1,y1,1);
									}
								}
							}
						}
						// only keeps the region if it's big enough
						if (newregion.size()>minRegion) {
							regions.add(newregion);
						}

					}
				}
			}
		}
//		System.out.println(regions);
	}

	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		// TODO: YOUR CODE HERE
		// finds the difference between the red, green, and blue componesnts of the two compared colors
		int bluediff = c1.getBlue() - c2.getBlue();
		int reddiff = c1.getRed() - c2.getRed();
		int greendiff = c1.getGreen() - c2.getGreen();
		// is only true if they're within the boundaries of the allowed color difference
		if ((-1) * maxColorDiff < bluediff && bluediff < maxColorDiff ){
			if ( (-1) * maxColorDiff < greendiff && greendiff < maxColorDiff ){
				return ((-1) *maxColorDiff < reddiff && reddiff < maxColorDiff );
			}
		}
		return false;
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		// TODO: YOUR CODE HERE
		// sets the first region as the largest
		ArrayList<Point> max = regions.get(0);
		// loops through the regions, compares and updates if the following region is larger
		for(ArrayList<Point> x : regions) {
			if(x.size() > max.size()){
				max = x;
			}
		}
		return max;
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		// TODO: YOUR CODE HERE
		// loops through the points in every region
		for(ArrayList<Point> regionlist : regions){
			int v = (int)(Math.random() * 16777216);
			for (Point x: regionlist) {
				// floodfills them with a random color
				recoloredImage.setRGB((int) x.getX(), (int) x.getY(), v);
			}

		}
	}
}
