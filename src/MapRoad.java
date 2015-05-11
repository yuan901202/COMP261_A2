import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;

/**
*
* This is road class for read road file
*
* @author Tianfu Yuan (Student ID: 300228072)
*
*/

public class MapRoad {

	private int id, type, speed, roadClass;
	private String label, city;
	private boolean oneWay, notForCar, notForPede, notForBicy;
    private Set<MapSegment> segs = new HashSet<MapSegment>();
    private double roadLength;

    public MapRoad(int id, int type, String label, String city, boolean oneWay, int speed, int roadClass, boolean notForCar, boolean notForPede, boolean notForBicy) {
        this.id = id;
        this.type = type;
        this.label = label;
        this.city = city;
        this.oneWay = oneWay;
        this.speed = speed;
        this.roadClass = roadClass;
        this.notForCar = notForCar;
        this.notForPede = notForPede;
        this.notForBicy = notForBicy;

        roadLength = 0;
    }

    public int getId() {
    	return id;
    }

    public int getType() {
    	return type;
    }

    public String getLabel() {
    	if (label == "-") {
    		return city;
    	}
    	else {
    		return label;
    	}
    }

    public String getCity() {
    	return city;
    }

    public boolean getOneway() {
    	return oneWay;
    }

    public int getSpeed() {
    	return speed;
    }

    public int getRoadClass() {
    	return roadClass;
    }

    public boolean isForCar() {
    	return notForCar;
    }

    public boolean isForPede() {
    	return notForPede;
    }

    public boolean isForBicy() {
    	return notForBicy;
    }

    public void addSeg(MapSegment seg) {
        segs.add(seg);
    }

    public Set<MapSegment> getSegs() {
        return segs;
    }

    public void setLength(double length) {
    	roadLength = length;
    }

    public double getLength(){
    	return roadLength;
    }

    public void draw(Graphics g, Location origin, double scale){
        for (MapSegment seg : segs){
            seg.drawRoad(g, origin, scale);
        }
    }
}