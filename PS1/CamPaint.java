import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Webcam-based drawing 
 * PS-1, Dartmouth CS 10, 22W
 * 
 * @author Chris Bailey-Kellogg, Spring 2015 (based on a different webcam app from previous terms)
 * @author You-Chi Liu and Kevine Twagizihirwe, adds the draw, processImage, and handlemousepress methods
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
		if (displayMode=='w'){ 		 // live webcam (w)
			g.drawImage(image,0,0,null);
		}
		if (displayMode=='r'){       // recolored image (r)
			g.drawImage(finder.getRecoloredImage(),0,0,null);
		}
		else if (displayMode=='p'){  // painting (p)
			g.drawImage(painting,0,0,null);
		}
	}

	/**
	 * Webcam method, here finding regions and updating the painting.
	 */
	@Override
	public void processImage() {
		// TODO: YOUR CODE HERE
		if (targetColor!= null) {
			finder.setImage(image); // call the region finder and pass it an image
			finder.findRegions(targetColor); // give the region finder the color
			finder.recolorImage(); // call the method to recolor the image so that regions show up distinctly

			// check if the largest region is not empty, then use it as the paintbrush
			if (finder.largestRegion() != null) {
				for (Point p: finder.largestRegion()){
					painting.setRGB(p.x, p.y, paintColor.getRGB());
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
		// when there's an image, set the target color to where mouse was pressed
			if (image != null) {
				targetColor = new Color(image.getRGB(x, y));
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
