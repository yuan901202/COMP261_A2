import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
*
* This is road class for read road file and store the road data
*
* @author Tianfu Yuan (Student ID: 300228072)
*
*/

public class RoadSet implements AutoSuggestor<String> {
    private HashMap<Integer, MapRoad> roads;
    private Trie tries;

    public RoadSet(String dataFile) {
        roads = new HashMap<Integer, MapRoad>();
        tries = new Trie();
        openData(dataFile);
    }

    @SuppressWarnings("resource")
	private void openData(String roadFile) {
        try{
            FileReader fr = new FileReader(roadFile);
            BufferedReader br= new BufferedReader(fr);
            boolean rl = (br.readLine() == null);

            String line;
            String[] attr;

            int id, type, speed, roadClass;
            String label, city;
            boolean oneWay, notForCar, notForPede, notForBicy;

            MapRoad newRoad;

            while(!rl) {
                line = br.readLine();

                if(line == null) {
                    rl = true;
                }
                else{
                    attr = line.split("\t");

                    id = Integer.parseInt(attr[0]);
                    type = Integer.parseInt(attr[1]);
                    label = attr[2];
                    city = attr[3];
                    oneWay = Boolean.parseBoolean(attr[4]);
                    speed = Integer.parseInt(attr[5]);
                    roadClass = Integer.parseInt(attr[6]);
                    notForCar = Boolean.parseBoolean(attr[7]);
                    notForPede = Boolean.parseBoolean(attr[8]);
                    notForBicy = Boolean.parseBoolean(attr[9]);

                    tries.addChild(label, id);

                    //build a new road
                    newRoad = new MapRoad(id, type, label, city, oneWay, speed, roadClass, notForCar, notForPede, notForBicy);

                    if (roads.get(id) != null) {
                        System.out.println("===Duplicated roads.===");
                    }
                    roads.put(id, newRoad);
                }
            }
        }catch(IOException e) {
            System.out.println("Cannot open roadID-roadInfo.tab!" + e.getMessage());
        }
    }

    public MapRoad getRoadById(int id) {
        return roads.get(id);
    }

    public Set<MapRoad> getRoadByName(String name) {
        TrieNode node = tries.getChild(name);
        Set<MapRoad> returnRoads = Collections.<MapRoad>emptySet();

        if(node != null && node.getMarked() == true) {
            returnRoads = new HashSet<MapRoad>();

            for(int i : node.getIDs()) {
                returnRoads.add(this.getRoadById(i));
            }
        }
        return returnRoads;
    }

    public List<String> getSuggestions(String query) {
        if(query.isEmpty()) {
        	return Collections.<String>emptyList();
        }
        return tries.getNames(query);
    }
}
