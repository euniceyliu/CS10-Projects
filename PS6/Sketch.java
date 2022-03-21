import java.util.HashMap;
import java.util.Map;

public class Sketch {
    int i = 0;
    public Map<Integer,Shape> shapeMap;

    public Sketch(){
        // shapeMap stores the list of different shapes
        this.shapeMap = new HashMap<>();
    }
    public void addShape (Shape shape){
        shapeMap.put(i,shape);
        i = i + 1;
    }
    public Integer getID (Shape shape) {
        for (Integer i : shapeMap.keySet()){
            if (shapeMap.get(i).equals(shape)) {
                return i;
            }
        }
        return null;
    }
    public Map<Integer,Shape> accessShapeMap (){
        return shapeMap;
    }
}