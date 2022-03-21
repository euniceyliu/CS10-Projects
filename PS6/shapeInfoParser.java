import java.awt.*;


/**
 * shapeInfoParser class parses the message to get the shape type, the point to start drawing and ending from, and the color
 * @author Kevine Twagizihirwe, You-Chi Liu, 22W, CS10
 */

public class shapeInfoParser {
    public String shape;
    public Color colorRGB;
    public Point p1;
    public Point p2;

    public shapeInfoParser(String action) {
        String[] tokens = action.split(" ");
        // shape type
        this.shape = tokens[0];
        // point to start drawing from
        this.p1 = new Point(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
        // point to end drawing from
        this.p2 = new Point(Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]));
        // color of the shape
        this.colorRGB = new Color(Integer.parseInt(tokens[5]));
    }

    public String getShape(){
        return shape;
    }
    public Point getP1(){
        return this.p1;
    }
    public Point getP2(){
        return this.p2;
    }
    public Color getColor(){
        return this.colorRGB;
    }

}