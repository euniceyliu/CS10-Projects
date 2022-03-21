import java.awt.*;
import java.awt.image.*;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * PS-1, Dartmouth CS 10, 22W
 *
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 * @author You-Chi Liu and Kevine Twagizihirwe,
 */
public class RegionFinder {
	private static final int maxColorDiff = 20;             // how similar a pixel color must be to the target color
															// to belong to a region
	private static final int minRegion = 50;                // how many points in a region to be worth considering
	private static BufferedImage image;                     // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions;            // a region is a list of points so the identified regions
	                                                        // are in a list of lists of points

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
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) {
		// TODO: YOUR CODE HERE
		regions = new ArrayList<ArrayList<Point>>();
		// create an image that will be used to track visited points in the image
		BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		// looping over all pixels
		for (int y =0; y < image.getHeight(); y ++) {
			for (int x = 0; x < image.getWidth(); x++){
				Color currentColor = new Color(image.getRGB(x,y));
				Point currentPoint = new Point(x, y);
				ArrayList<Point> newRegion = new ArrayList<Point>();
				// contains a list of points that need to be visited
				ArrayList<Point> tovisit = new ArrayList<>();
				if (visited.getRGB(x, y) ==0){
					// start a new region if the current point matches the target color
					if (colorMatch(currentColor, targetColor)) {
						tovisit.add(currentPoint);
						newRegion.add(currentPoint);
					}
					visited.setRGB(x, y, 1); // marks the point as visited
				}
				// check if some pixels need to be visited and visit them
				while (tovisit.size() != 0){
					Point notVisited = tovisit.remove(tovisit.size() - 1);
					visited.setRGB(notVisited.x, notVisited.y, 1);
					// loop over all its neighbors
					for (int j = Math.max(0, notVisited.y - 1); j < Math.min(image.getHeight(), notVisited.y + 2); j++) {
						for (int i = Math.max(0, notVisited.x - 1); i < Math.min(image.getWidth(), notVisited.x + 2); i++) {
							Color neighborColor = new Color(image.getRGB(i,j));
							Point neighborPoint = new Point(i, j);
							// make sure it's not visited
							if (visited.getRGB(i, j) == 0) {
								// check if the neighbors of the current point are similar to the target color
								// if so, save the neighboring pixel to the region if they match the target color
								if (colorMatch(neighborColor, targetColor)) {
									tovisit.add(neighborPoint);
									newRegion.add(neighborPoint);
								}
								visited.setRGB(i, j, 1);
							}
							if (neighborColor.getRGB() != 0) {
								newRegion.add(neighborPoint);
							}
						}
					}
				}
				// keep adding the region if the size is greater than the threshold
				if (newRegion.size() > minRegion){
					regions.add(newRegion);
				}
			}
		}
	}

	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		// TODO: YOUR CODE HERE
		// check color similarity and return true if the absolute value of two colors is less than
		// or equal to a specified threshold of color difference
		return (Math.abs(c1.getRed() - c2.getRed()) <= maxColorDiff &&
				Math.abs(c1.getGreen() - c2.getGreen()) <= maxColorDiff &&
				Math.abs(c1.getBlue() - c2.getBlue()) <= maxColorDiff);
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		// TODO: YOUR CODE HERE
		// arraylist that will contain the largest region
		ArrayList<Point> maxRegion = new ArrayList<>();
		// checks all regions' size and returns the largest
		for(int i=0; i< regions.size() -1; i++) {
			if (regions.get(i).size() > maxRegion.size()){
				maxRegion = regions.get(i);
			}
		}
		return maxRegion;
	}

	/**
	 * Sets recoloredImage to be a copy of image, but with each region a uniform random color,
	 * so we can see where they are
	 */
	public void recolorImage() {
		// copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// TODO: YOUR CODE HERE
		// recolor all the regions in the image with random, distinct colors
		for (ArrayList<Point> region : regions) {
			Color randColor = new Color((int) (Math.random() * 16777216));
			for (Point pixel : region) {
				recoloredImage.setRGB(pixel.x, pixel.y, randColor.getRGB());
			}
		}

	}
}
