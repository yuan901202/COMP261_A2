import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;

/**
*
* This is A* search class for using A* search method find best route on map
*
* @author Tianfu Yuan (Student ID: 300228072)
*
*/

public class AStar{
	private NodeSet nodes;
	private SegmentSet segs;
	private RoadSet roads;
	private MapNode first, goal;

	//change the priority on the fringe
	private PriorityQueue<MapNode> fringe;
	private priorityComparator pc = new priorityComparator();

	public AStar(NodeSet ns, SegmentSet ss, RoadSet rs, MapNode firstNode, MapNode goalNode){
		nodes = ns;
		segs = ss;
		roads = rs;
		first = firstNode;
		goal = goalNode;

		//fringe = priority queue, ordered by total cost to goal
		fringe = new PriorityQueue<MapNode>(1, pc);

		for(Map.Entry<Integer, MapNode> temp : nodes.getNode().entrySet()){
			temp.getValue().setNodeVisited(false);
		}

		findRoute();
	}

	/**
	 * A* algorithm
	 * Very fast search
	 * Find best route on map
	 */
	private void findRoute(){
		MapNode tempNode, lastNode = null;
		fringe.offer(first);

		while(!fringe.isEmpty()){
			tempNode = fringe.poll();

			if(!tempNode.getNodeVisited()){
				tempNode.setNodeVisited(true);

				if(tempNode.equals(goal)){
					break;
				}

				lastNode = tempNode;

				for(MapSegment neighbourSeg : tempNode.getNeighbourOut()){
					MapNode nodeFrom = nodes.find(neighbourSeg.getNodeFrom());
					MapNode nodeTo = nodes.find(neighbourSeg.getNodeTo());

					if(nodeFrom.equals(lastNode)){
						tempNode = nodeTo;
					}
					else{
						if(!roads.getRoadById(neighbourSeg.getRoadId()).getOneway()){
							tempNode = nodeFrom;
						}
					}

					if(!tempNode.getNodeVisited()){
						tempNode.setPathFrom(lastNode);
						tempNode.setSegFrom(neighbourSeg);
						tempNode.setCost(lastNode.getCost() + neighbourSeg.getRoadLength());
						tempNode.setTotal(tempNode.getCost() + tempNode.getLocation().distance(goal.getLocation()));
						fringe.add(tempNode);
					}
				}
			}
		}
	}

	public SegmentSet getSegs(){
		return segs;
	}

	public void setSegs(SegmentSet edges){
		this.segs = edges;
	}

	private class priorityComparator implements Comparator<MapNode>{
		public int compare(MapNode i, MapNode ii){

			if(i.getTotal() > ii.getTotal()){
				return 1;
			}

			else if(i.getTotal() < ii.getTotal()){
				return -1;
			}

			return 0;
		}
	}
}