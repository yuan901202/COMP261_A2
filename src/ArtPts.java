import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
*
* This is articulation class for display all articulation points on map
*
* @author Tianfu Yuan (Student ID: 300228072)
*
*/

public class ArtPts {
	private NodeSet nodes;
	private SegmentSet segs;
	private RoadSet roads;
	private Set<MapNode> pts;

	public ArtPts(NodeSet ns, SegmentSet ss, RoadSet rs) {
		nodes = ns;
		segs = ss;
		roads = rs;

		setSegs(ss);
		setRoads(rs);

		pts = new HashSet<MapNode>();

		for(Map.Entry<Integer, MapNode> temp : nodes.getNode().entrySet()) {
			temp.getValue().setPathDepth((int)Double.POSITIVE_INFINITY);
			temp.getValue().setReachBack(0);
		}
	}

	public Set<MapNode> findPt() {
		MapNode first = nodes.find(10);

		int subTrees = 0;

		for(MapNode neighbour : first.getNeighbourNode()) {
			if(neighbour.getPathDepth() == (int)Double.POSITIVE_INFINITY) {
				arriveArticulationPts(neighbour, 1, first);
				subTrees++;
			}
		}

		if(subTrees > 1) {
			pts.add(first);
		}

		return pts;
	}

	private int arriveArticulationPts(MapNode node, int depth, MapNode nodeFrom) {
		node.setPathDepth(depth);
		node.setReachBack(depth);

		for(MapNode neighbour : node.getNeighbourNode()) {

			if(!neighbour.equals(nodeFrom)) {

				if(neighbour.getPathDepth() < (int)Double.POSITIVE_INFINITY) {
					node.setReachBack(Math.min(neighbour.getPathDepth(), node.getReachBack()));
				}
				else{
					int childReach = arriveArticulationPts(neighbour, node.getPathDepth()+1, node);
					node.setReachBack(Math.min(childReach, node.getReachBack()));
					if(childReach >= node.getPathDepth()){
						pts.add(node);
					}
				}
			}
		}
		return node.getReachBack();
	}

	public SegmentSet getsegs() {
		return segs;
	}

	public void setSegs(SegmentSet segs) {
		this.segs = segs;
	}

	public RoadSet getRoads() {
		return roads;
	}

	public void setRoads(RoadSet roads) {
		this.roads = roads;
	}
}
