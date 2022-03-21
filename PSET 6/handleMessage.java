import java.awt.*;
/**
 * Handles message requests
 * @author You-Chi Liu, Kevine Twagizihirwe, 22W, CS10
 */

public class handleMessage {
    public String request;
    public Color colorRGB;
    public Integer id;
    public int dx;
    public int dy;
    public String shapeInfo;
    public String[] tokens;
    public String inputMessage;
    public Sketch sketch;

    public handleMessage(String inputMessage, Sketch sketch) {
        this.sketch = sketch;
        this.inputMessage = inputMessage;
        this.tokens = this.inputMessage.split(" ");
        this.request = tokens[0];

        switch (request) {
            case "add" -> {
                String[] addRequest = inputMessage.split("\\s", 2);
                this.shapeInfo = addRequest[1];
            }
            case "recolor" -> {
                this.id = Integer.valueOf(tokens[1]);
                this.colorRGB = new Color(Integer.parseInt(tokens[2]));
            }
            case "move" -> {
                this.id = Integer.parseInt(tokens[1]);
                this.colorRGB = new Color(Integer.parseInt(tokens[2]));
                this.dx = Integer.parseInt(tokens[3]);
                this.dy = Integer.parseInt(tokens[4]);
            }
            case "delete" -> this.id = Integer.parseInt(tokens[1]);
        }
    }

    public Integer getId() {
        return this.id;
    }

    public Color getColor() {
        return this.colorRGB;
    }

    public String getRequest() {
        return this.request;
    }

    public int getDx() {
        return this.dx;
    }

    public int getDy() {
        return this.dy;
    }

    public String getShapeInfo() {
        return this.shapeInfo;
    }


    /**
     * Handles add request
     */
    synchronized public void handleAdd() {
        Shape curr = null;
        shapeInfoParser shape = new shapeInfoParser(shapeInfo);
        switch (shape.getShape()) {
            case "ellipse" -> {
                curr = new Ellipse(shape.getP1().x, shape.getP1().y,
                        shape.getP2().x, shape.getP2().y, shape.getColor());
            }
            case "rectangle" -> {
                curr = new Rectangle(shape.getP1().x, shape.getP1().y,
                        shape.getP2().x, shape.getP2().y, shape.getColor());
            }
            case "segment" -> {
                curr = new Segment(shape.getP1().x, shape.getP1().y,
                        shape.getP2().x, shape.getP2().y, shape.getColor());
            }
            case "freehand" -> {
                curr = new Polyline(shape.getP1().x, shape.getP1().y,
                        shape.getP2().x, shape.getP2().y, shape.getColor());
            }
        }
        if (curr != null) {
            sketch.addShape(curr);

        }


    }

    /**
     * Handles move request
     */
    synchronized public void handleMove() {
        sketch.shapeMap.get(id).moveBy(dx,dy);
    }

    /**
     * Handles recolor request
     */
    synchronized public void handleRecolor() { sketch.shapeMap.get(id).setColor(colorRGB);}

    /**
     * Handles delete request
     */
    synchronized public void handleDelete() { sketch.shapeMap.remove(id);}

}