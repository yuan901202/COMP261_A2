import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

/**
*
* This is node class for read road file
*
* @author Tianfu Yuan (Student ID: 300228072)
*
*/

public class MapNode {
	private int id;
	private double cost, total;
	private Location location;
	private Set<MapSegment> neighbourSeg, neighbourIn, neighbourOut;
	private Set<MapNode> neighbourNode;
	private boolean nodeVisited;
	private MapNode pathFrom;

	private MapSegment edgeFrom;
	private MapNode nodeFrom;
	private int pathDepth;
	private int reachBack;

	public static final int radius = 2;

	public MapNode(int id, Location location) {
		this.id = id;
		this.location = location;

		neighbourSeg = new HashSet<MapSegment>();
		neighbourIn = new HashSet<MapSegment>();
		neighbourOut = new HashSet<MapSegment>();

		neighbourNode = new HashSet<MapNode>();
		nodeVisited = false;
		pathFrom = null;
		pathDepth = (int)Double.POSITIVE_INFINITY;
		setNodeFrom(null);
	}

	public void drawNode(Graphics g, Location origin, double scale, Color color, int i) {
		Point pt = location.asPoint(origin, scale);
		g.setColor(color);
		g.fillOval(pt.x - i, pt.y - i, 2 * i, 2 * i);
		g.setColor(Color.black);
		g.drawOval(pt.x - i, pt.y - i, 2 * i - 1, 2 * i - 1);
	}

	public void drawNeighbourSeg(Graphics g, Location origin, double scale) {
		for(MapSegment e : neighbourSeg) {
			e.drawRoad(g, origin, scale);
		}
	}

	public void drawPath(MapNode nd, Graphics g, Location origin, double scale) {
		if(nd == null) {
			return;
		}

		Point pt1 = this.location.asPoint(origin, scale);
		Point pt2 = nd.location.asPoint(origin, scale);

		g.drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
	}

	public void addSeg(MapSegment s) {
		neighbourSeg.add(s);
	}

	public Set<MapSegment> getNeighbourSeg() {
		return neighbourSeg;
	}

	public void addSegIn(MapSegment s) {
		neighbourIn.add(s);
	}

	public Set<MapSegment> getNeighbourIn() {
		return neighbourIn;
	}

	public void addSegOut(MapSegment s) {
		neighbourOut.add(s);
	}

	public Set<MapSegment> getNeighbourOut() {
		return neighbourOut;
	}

	public void addNode(MapNode n) {
		neighbourNode.add(n);
	}

	public Set<MapNode> getNeighbourNode() {
		return neighbourNode;
	}

	public boolean on(Location currentLocation, double distance) {
		return location.distance(currentLocation) < distance;
	}

	public int getId() {
		return id;
	}

	public Location getLocation() {
		return location;
	}

	public double distanceTo(MapNode other) {
		return location.distance(other.location);
	}

	public boolean getNodeVisited() {
		return nodeVisited;
	}

	public void setNodeVisited(boolean nv) {
		nodeVisited = nv;
	}

	public MapNode getPathFrom() {
		return pathFrom;
	}

	public void setPathFrom(MapNode i) {
		pathFrom = i;
	}

	public void setCost(double c) {
		cost = c;
	}

	public double getCost() {
		return cost;
	}

	public void setTotal(double tc) {
		total = tc;
	}

	public double getTotal() {
		return total;
	}

	public void setSegFrom(MapSegment s) {
		edgeFrom = s;
	}

	public MapSegment getSegFrom() {
		return edgeFrom;
	}

	public void setPathDepth(int pd) {
		pathDepth = pd;
	}

	public int getPathDepth() {
		return pathDepth;
	}

	public void setReachBack(int rb) {
		reachBack = rb;
	}

	public int getReachBack() {
		return reachBack;
	}

	public MapNode getNodeFrom() {
		return nodeFrom;
	}

	public void setNodeFrom(MapNode nodeFrom) {
		this.nodeFrom = nodeFrom;
	}
}