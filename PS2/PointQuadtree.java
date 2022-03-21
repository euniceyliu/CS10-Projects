import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position,
 * with children at the subdivided quadrants.
 * @author You-Chi Liu, Kevine Twagizihirwe, Dartmouth CS 10, Winter 2022
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015.
 * @author CBK, Spring 2016, explicit rectangle.
 * @author CBK, Fall 2016, generic with Point2D interface.
 *
 */
public class PointQuadtree<E extends Point2D> {
	private E point;                            // the point anchoring this node
	private int x1, y1;                            // upper-left corner of the region
	private int x2, y2;                            // bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;    // children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	// Getters

	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 *
	 * @param quadrant 1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant == 1) return c1;
		if (quadrant == 2) return c2;
		if (quadrant == 3) return c3;
		if (quadrant == 4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 *
	 * @param quadrant 1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant == 1 && c1 != null) || (quadrant == 2 && c2 != null) || (quadrant == 3 && c3 != null) || (quadrant == 4 && c4 != null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {
		// TODO: YOUR CODE HERE

		// quadrant 1
		if (this.point.getX() < p2.getX() && this.point.getY() > p2.getY()) { //checks if a point is in quadrant 1
			if (this.hasChild(1)) {
				this.c1.insert(p2); // adds the point as another child to the root
			} else {
				// creates a child (new tree holding the point) if the root does not have a child
				this.c1 = new PointQuadtree<E>(p2, (int) this.point.getX(), this.y1, this.x2, (int) this.point.getY());

			}
		}

		// quadrant 2
		if (this.point.getX() > p2.getX() && this.point.getY() > p2.getY()) { //checks if a point is in quadrant 2
			if (this.hasChild(2)) {
				this.c2.insert(p2); // adds the point as another child to the root
			} else {
				// creates a child (new tree holding the point) if the root does not have a child
				this.c2 = new PointQuadtree<E>(p2, this.x1, this.y1, (int) this.point.getX(), (int) this.point.getY());
			}
		}

		// quadrant 3
		if (this.point.getX() > p2.getX() && this.point.getY() < p2.getY()) { //checks if a point is in quadrant 3
			if (this.hasChild(3)) {
				this.c3.insert(p2); // adds the point as another child to the root
			} else {
				// creates a child (new tree holding the point) if the root does not have a child
				this.c3 = new PointQuadtree<E>(p2, this.x1, (int) this.point.getY(), (int) this.point.getX(), this.y2);
			}
		}

		// quadrant 4
		if (this.point.getX() < p2.getX() && this.point.getY() < p2.getY()) { //checks if a point is in quadrant 24
			if (this.hasChild(4)) {
				this.c4.insert(p2); // adds the point as another child to the root
			} else {
				// creates a child (new tree holding the point) if the root does not have a child
				this.c4 = new PointQuadtree<E>(p2, (int) this.point.getX(), (int) this.point.getY(), this.x2, this.y2);
			}
		}

		// same location
		if (this.point.getX() == p2.getX() && this.point.getY() == p2.getY()) {
			this.point = p2;
		}
	}


	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		// TODO: YOUR CODE HERE
		int qSize = 0;

		if (hasChild(1)) {
			qSize += c1.size();
		}
		if (hasChild(2)) {
			qSize += c2.size();
		}

		if (hasChild(3)) {
			qSize += c3.size();
		}

		if (hasChild(4)) {
			qSize += c4.size();
		}
		// add all the children back and then the parent (add 1 if we have reached the end of the tree)
		return 1 + qSize;
	}



	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public List<E> allPoints() {
		// TODO: YOUR CODE HERE
		// create an empty list to store the points
		ArrayList<E> everyPoint = new ArrayList<E>();
		addPoints(everyPoint); // calls the helper method to add all the points
		return everyPoint;
	}

	/**
	 * Helper for allpoints, adding a point to the list
	 */
	public void addPoints(ArrayList<E> everyPoint) {
		// TODO: YOUR CODE HERE
		if (!hasChild(1) && !hasChild(2) && !hasChild(3) && !hasChild(4)) {
			everyPoint.add(point);
		} else {
			// recursively checks each node's children and keeps tract of them until it gets to the end of the tree
			if (hasChild(1)) c1.addPoints(everyPoint);
			if (hasChild(2)) c2.addPoints(everyPoint);
			if (hasChild(3)) c3.addPoints(everyPoint);
			if (hasChild(4)) c4.addPoints(everyPoint);
		}
	}


	/**
	 * Uses the quadtree to find all points within the circle
	 *
	 * @param cx circle center x
	 * @param cy circle center y
	 * @param cr circle radius
	 * @return the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		// TODO: YOUR CODE HERE
		// create an empty list to stores the points in circle
		ArrayList<E> pointsInCircle = new ArrayList<E>();
		addIsHit(pointsInCircle, cx, cy, cr); // use helper method to add the points in the circle
		return pointsInCircle;
	}
	/**
	 * Helper for findInCircle, adding a point to the list of the points in the circle
	 * @param isHitList list of point within the circle
	 * @param cx circle center x
	 * @param cy circle center y
	 * @param cr circle radius
	 * @return the points in the circle (and the qt's rectangle)
	 */
	public void addIsHit(List<E> isHitList, double cx, double cy, double cr) {
		if (Geometry.circleIntersectsRectangle(cx, cy, cr, x1, y1, x2, y2)) {
			if (Geometry.pointInCircle(point.getX(), point.getY(), cx, cy, cr)) {
				isHitList.add(point);
			}

			if (hasChild(1)) {
				c1.addIsHit(isHitList, cx, cy, cr);
			}
			if (hasChild(2)) {
				c2.addIsHit(isHitList, cx, cy, cr);
			}
			if (hasChild(3)) {
				c3.addIsHit(isHitList, cx, cy, cr);
			}
			if (hasChild(4)) {
				c4.addIsHit(isHitList, cx, cy, cr);
			}
		}
	}
}