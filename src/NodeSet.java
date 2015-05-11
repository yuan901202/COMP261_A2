import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
*
* This is node class for read node file and store the node data
*
* @author Tianfu Yuan (Student ID: 300228072)
*
*/

public class NodeSet {

    private HashMap<Integer, MapNode> mapNodes;
    QuadNode quadTree;

    private double westBoundary;
    private double eastBoundary;
    private double northBoundary;
    private double southBoundary;

    public NodeSet(String dataFile) {
        mapNodes = new HashMap<Integer, MapNode>();
        readData(dataFile);
        inputDataFromHashMapToQuadTree();
    }

    @SuppressWarnings("resource")
	private void readData(String nodeFile) {
        try{
            FileReader fr = new FileReader(nodeFile);
			BufferedReader br = new BufferedReader(fr);

            westBoundary = Double.POSITIVE_INFINITY;
            southBoundary = Double.POSITIVE_INFINITY;
            eastBoundary = Double.NEGATIVE_INFINITY;
            northBoundary = Double.NEGATIVE_INFINITY;

            boolean rl = false;
            String line, attr[];
            int id;
            double lat, lon;

            while(!rl) {
                line = br.readLine();
                if (line == null) {
                	rl = true;
                }
                else {
                    attr = line.split("\t");

                    id = Integer.parseInt(attr[0]);
                    lat = Double.parseDouble(attr[1]);
                    lon = Double.parseDouble(attr[2]);

                    Location location = Location.newFromLatLon(lat, lon);

                    //update the boundaries
                    if(westBoundary > location.x) {
                    	westBoundary = location.x;
                    }
                    if(southBoundary > location.y) {
                    	southBoundary = location.y;
                    }
                    if(eastBoundary < location.x) {
                    	eastBoundary = location.x;
                    }
                    if(northBoundary < location.y) {
                    	northBoundary = location.y;
                    }

                    mapNodes.put(id, new MapNode(id, location));
                }
            }
         }catch(IOException e) {
            System.out.println("Cannot open nodeID-lat-lon.tab!" + e.getMessage());
        }
    }

    private void inputDataFromHashMapToQuadTree() {
    	double [] bounds = getQuadBoundaries();

    	quadTree = new QuadNode(bounds[0], bounds[2], bounds[1], bounds[3]);

    	for(Map.Entry<Integer, MapNode> e : mapNodes.entrySet()){
    		quadTree.addNode(e.getValue());
    	}
    }

    public double[] getBoundaries() {
		final double margin = 5; //km
		return new double[]{(westBoundary - margin), (eastBoundary + margin), (southBoundary - margin), (northBoundary + margin)};
    }

    public double[] getQuadBoundaries() {
		final double margin = 10; //km
		return new double[]{(westBoundary - margin), (eastBoundary + margin), (southBoundary - margin), (northBoundary + margin)};
    }

    public MapNode find(Location location, double scale) {
		for(Map.Entry<Integer, MapNode> entry : mapNodes.entrySet()) {

			if(entry.getValue().on(location, (MapNode.radius*2 / scale))) {
		    	return entry.getValue();
		    }
		}
		return null;
    }

    public MapNode find(int id) {
    	return mapNodes.get(id);
    }

    public HashMap<Integer,MapNode> getNode() {
    	return mapNodes;
    }

    public MapNode findStopOver(MapNode first, MapNode goal) {
    	if(first == null || goal == null) {
    		return null;
    	}

    	MapNode mid = null;
    	double minDistance = Double.POSITIVE_INFINITY;

    	for(Map.Entry<Integer, MapNode> entry : mapNodes.entrySet()) {
            MapNode mapNode = entry.getValue();

            if(mapNode != first && mapNode != goal) {
            	double distance = mapNode.distanceTo(first) + mapNode.distanceTo(goal);

            	if (distance < minDistance) {
            		minDistance = distance;
            		mid = mapNode;
            	}
            }
    	}
    	return mid;
    }

    public Set<MapSegment> draw(Graphics g, Location origin, double scale, int width, int height, Color color) {
    	Location southWest = Location.newFromPoint(new Point(0, height), origin, scale);
    	Location northEast = Location.newFromPoint(new Point(width, 0), origin, scale);

    	Set<MapSegment> neighbourSegs = new HashSet<MapSegment>();
    	List<MapNode> nodes = quadTree.findNode(southWest.x, northEast.x, southWest.y, northEast.y);

    	//draw single intersection
    	for (MapNode mapNode : nodes){
    		mapNode.drawNode(g, origin, scale, color, MapNode.radius);
            neighbourSegs.addAll(mapNode.getNeighbourSeg());
		}
		return neighbourSegs;
    }
}