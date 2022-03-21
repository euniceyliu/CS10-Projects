import java.awt.Color;
import java.awt.Graphics;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, updated Fall 2016
 */
public class Polyline implements Shape {
	// TODO: YOUR CODE HERE
	private int x1, y1, x2, y2;		// upper left and lower right
	private Color color;
	int nPoints;
    private int[] xPoints = new int[nPoints];
	private int[] yPoints = new int[nPoints];


	public Polyline (int x1, int y1, Color color) {
		this.xPoints = add(xPoints, x1);
		this.yPoints = add(yPoints, y1);
		this.color = color;
	}

	public Polyline (int x1, int y1, int x2, int y2, Color color) {
		this.xPoints = add(xPoints, x1);
		this.yPoints = add(yPoints, y1);
		this.xPoints = add(xPoints, x2);
		this.yPoints = add(yPoints, y2);
		this.color = color;
	}

	public static int[] add(int[] points, int point) {

		// create a new ArrayList
		List<Integer> pointsList = Arrays.stream(points).boxed().collect(Collectors.toList());
		pointsList.add(point);
		// Convert the Arraylist and return the array
		return pointsList.stream().mapToInt(Integer::valueOf).toArray();
	}

	@Override
	public void moveBy(int dx, int dy) {
        for (int i = 0; i < xPoints.length; i ++) {
            xPoints[i] +=dx;
            yPoints[i] +=dy;
        }
	}

	/**
	 * Update the end (second point) of the segment
	 */
	public void setEnd(int x2, int y2) {
		this.xPoints = add(xPoints, x2);
		this.yPoints = add(yPoints, y2);
	}


	@Override
	public Color getColor() {
		return this.color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public boolean contains(int x, int y) {
		for (int i = 0; i < xPoints.length - 1; i ++ ) {
			if (Segment.pointToSegmentDistance(x, y, xPoints[i], yPoints[i], xPoints[i+1], yPoints[i+1]) <= 3) {
					return true;
				}
		}
		return false;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
        g.drawPolyline(xPoints, yPoints, xPoints.length);
	}

	@Override
	public String toString() {
		return "freehand "+x1+" "+y1+" "+x2+" "+y2+" "+color.getRGB();
	}
}
