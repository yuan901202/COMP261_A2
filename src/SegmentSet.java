import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
*
* This is segment class for read segment file and store the segment data
*
* @author Tianfu Yuan (Student ID: 300228072)
*
*/

public class SegmentSet {
	private HashMap<Integer, MapSegment> segs;

	public SegmentSet(String dataFile, RoadSet roads, NodeSet nodes) {
		segs = new HashMap<Integer, MapSegment>();
		readData(dataFile);
		roadsAndNodes(roads, nodes);
	}

	@SuppressWarnings("resource")
	public void readData(String segmentFile) {
		try{
			FileReader fr = new FileReader(segmentFile);
			BufferedReader br = new BufferedReader(fr);
			boolean rl = (br.readLine() == null);

			String line;
			String[] attr;

			int id = 0;
			int roadId, roadFrom, roadTo;
			double roadLength;

			while(!rl) {
				line = br.readLine();

				if(line == null) {
					rl = true;
				}
				else{
					attr = line.split("\t");
					roadId = Integer.parseInt(attr[0]);
					roadLength = Double.parseDouble(attr[1]);
					roadFrom = Integer.parseInt(attr[2]);
					roadTo = Integer.parseInt(attr[3]);
					Location[] location = new Location[(attr.length - 4) / 2];

					for(int i = 4; i < attr.length; i += 2) {
						int j = (i - 4) / 2;
						location[j] = Location.newFromLatLon(Double.parseDouble(attr[i]), Double.parseDouble(attr[i + 1]));
					}

					MapSegment newSeg = new MapSegment(id++, roadId, roadLength, roadFrom, roadTo, location);
					segs.put(newSeg.getId(), newSeg);
				}
			}
		}
		catch(IOException e) {
			System.out.println("Cannot open roadSeg-roadID-length-nodeID-nodeID-coords.tab!" + e.getMessage());
		}
	}

	public void roadsAndNodes(RoadSet roads, NodeSet nodes) {
		for(Map.Entry<Integer, MapSegment> e : segs.entrySet()) {
			MapSegment seg = e.getValue();

			roads.getRoadById(seg.getRoadId()).addSeg(seg); // add segment to the related road

			MapNode segFrom = nodes.find(seg.getNodeFrom()); // add segment to the related node
			segFrom.addSeg(seg);

			MapNode segTo = nodes.find(seg.getNodeTo());
			segTo.addSeg(seg);

			segFrom.addNode(segTo);
			segTo.addNode(segFrom);

			segFrom.addSegOut(seg);
			segTo.addSegIn(seg);

			if(!roads.getRoadById(seg.getRoadId()).getOneway()) {
				segFrom.addSegIn(seg);
				segTo.addSegOut(seg);
			}
		}
	}

	public void draw(Graphics g, Location origin, double scale) {
		for(Map.Entry<Integer, MapSegment> s : segs.entrySet()) {
			s.getValue().drawRoad(g, origin, scale);
		}
	}

	public MapSegment getSegById(int id) {
		return segs.get(id);
	}
}