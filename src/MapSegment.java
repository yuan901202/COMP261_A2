import java.awt.Graphics;
import java.awt.Point;

/**
*
* This is segment class for read segment file
*
* @author Tianfu Yuan (Student ID: 300228072)
*
*/

public class MapSegment {

	private int id;
    private int roadId;
    private int nodeFrom; //nodeID1
    private int nodeTo; //nodeID2
    private double length;
    private Location[] location;

    public MapSegment(int id,int roadId, double length, int nodeFrom, int nodeTo, Location[] location) {
        this.id = id;
        this.roadId = roadId;
        this.length = length;
        this.nodeFrom = nodeFrom;
        this.nodeTo = nodeTo;

        if(location != null) {
            this.location = location;
        }
    }

    public void drawRoad(Graphics g, Location origin, double scale) {
        if(location.length >= 2) {
            Location start, end;
            Point pt1, pt2;

            for (int i = 0; i < location.length - 1; i++) {
                start = location[i];
                end = location[i + 1];

                pt1 = start.asPoint(origin, scale);
                pt2 = end.asPoint(origin, scale);

                g.drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getRoadId() {
        return roadId;
    }

    public double getRoadLength() {
    	return length;
    }

    public int getNodeFrom() {
        return nodeFrom;
    }

    public int getNodeTo() {
        return nodeTo;
    }
}
