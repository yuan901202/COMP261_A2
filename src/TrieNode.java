import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
*
* This is trie class for trie nodes
*
* @author Tianfu Yuan (Student ID: 300228072)
*
*/

public class TrieNode {
    private boolean nodeMarked;
    private Set<Integer> ids;
    TreeMap<Character, TrieNode> nodeChildren;

    public TrieNode() {
        nodeMarked = false;
        ids = new HashSet<Integer>();
        nodeChildren = new TreeMap<Character, TrieNode>();
    }

    public void setMarked(boolean isMarked) {
        this.nodeMarked = isMarked;
    }

    public boolean getMarked() {
        return nodeMarked;
    }

    public void addID(int id) {
        ids.add(id);
    }

    public Set<Integer> getIDs() {
        return ids;
    }

    public TrieNode getChild(char childNode) {
        return nodeChildren.get((Character)childNode);
    }

    public TrieNode addChild(char child) {
        TrieNode newChild = new TrieNode();
        nodeChildren.put((Character)child, newChild);
        return newChild;
    }

    public List<String> getNames(String prefix) {
        List<String> returnStrings = new ArrayList<String>();

        if(this.nodeMarked) {
            returnStrings.add(prefix);
        }

        for(Map.Entry<Character, TrieNode> e : nodeChildren.entrySet()) {

            if(e != null) {
            	TrieNode node = e.getValue();

            	if(node != null) {
                    returnStrings.addAll(node.getNames(prefix + e.getKey().toString()));
                }
            }
        }
        return returnStrings;
    }
}