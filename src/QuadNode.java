import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
*
* This is quad-tree class for quad structure
*
* @author Tianfu Yuan (Student ID: 300228072)
*
*/

public class QuadNode {
	List<MapNode> mapNode;
	QuadNode NorthEast, SouthEast, SouthWest, NorthWest;

	private double minX, minY, maxX, maxY;
	private final int nodeCapacity = 4;

	public QuadNode(double minX, double minY, double maxX, double maxY) {
		mapNode = new ArrayList<MapNode>();

		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;

		NorthEast = null;
		SouthEast = null;
		SouthWest = null;
		NorthWest = null;
	}

	public boolean addNode(MapNode mapNodes) {
		if(!nodeQuad(mapNodes.getLocation())) {
			return false;
		}

		if(NorthEast == null) {
			if(mapNode.size() < nodeCapacity) {
				mapNode.add(mapNodes);
				return true;
			}
			else{
				center();
			}
		}

		if(NorthEast.addNode(mapNodes)) {
			return true;
		}
		if(SouthEast.addNode(mapNodes)) {
			return true;
		}
		if(SouthWest.addNode(mapNodes)) {
			return true;
		}
		if(NorthWest.addNode(mapNodes)) {
			return true;
		}

		return false;
	}

	private boolean nodeQuad(Location location) {
		return location.isInSquare(minX, maxX, minY, maxY);
	}

	private void center() {
		if(NorthEast != null) {
			return;
		}

		double centerX = (minX + maxX) / 2;
		double centerY = (minY + maxY) / 2;

		NorthEast = new QuadNode(centerX, centerY, maxX, maxY);
		SouthEast = new QuadNode(centerX, minY, maxX, centerY);
		SouthWest = new QuadNode(minX, minY, centerX, centerY);
		NorthWest = new QuadNode(minX, centerY, centerX, maxY);

		MapNode node;
		Location location;

		for(int i = 0; i < nodeCapacity; i++) {
			node = mapNode.get(i);
			location = node.getLocation();

			if(NorthEast.nodeQuad(location)) {
				NorthEast.addNode(node);
				continue;
			}

			if(SouthEast.nodeQuad(location)) {
				SouthEast.addNode(node);
				continue;
			}

			if(SouthWest.nodeQuad(location)) {
				SouthWest.addNode(node);
				continue;
			}

			if(NorthWest.nodeQuad(location)) {
				NorthWest.addNode(node);
				continue;
			}
		}

		mapNode = null;
	}

	private boolean isOverlapped(double left, double right, double bottom, double top) {
		if(left >= this.maxX || right < this.minX || bottom >= this.maxY || top < this.minY) {
			return false;
		}
		else{
			return true;
		}
	}

	public List<MapNode> returnIntersections() {
		List<MapNode> List = new ArrayList<MapNode>();

		if(this.NorthWest == null) {
			if(this.mapNode.size() != 0){
				for(MapNode i : mapNode){
					List.add(i);
				}
			}
		}
		else {
			List.addAll(NorthWest.returnIntersections());
			List.addAll(NorthEast.returnIntersections());
			List.addAll(SouthEast.returnIntersections());
			List.addAll(SouthWest.returnIntersections());
		}
		return List;
	}

	public List<MapNode> findNode(double left, double right, double bottom, double top) {
		if(!isOverlapped(left, right, bottom, top)) {
			return Collections.<MapNode>emptyList();
		}

		List<MapNode> node = new ArrayList<MapNode>();

		if(mapNode != null && mapNode.size() > 0) {
			for(MapNode i : mapNode) {
				if(i.getLocation().isInSquare(left, right, bottom, top)) {
					node.add(i);
				}
			}
		}
		else if(NorthEast != null) {
			node.addAll(NorthEast.findNode(left, right, bottom, top));
			node.addAll(SouthEast.findNode(left, right, bottom, top));
			node.addAll(SouthWest.findNode(left, right, bottom, top));
			node.addAll(NorthWest.findNode(left, right, bottom, top));
		}
		else {
			return Collections.<MapNode>emptyList();
		}

		return node;
	}
}