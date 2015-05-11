import java.util.ArrayList;
import java.util.List;

/**
*
* This is trie class for searching method
*
* @author Tianfu Yuan (Student ID: 300228072)
*
*/

public class Trie {
    private TrieNode rootNode;

    public Trie() {
    	rootNode = new TrieNode();
    }

    public void addChild(String string, int id) {
        TrieNode node = rootNode;
        TrieNode childNode;

        for(int i = 0; i < string.length(); i++) {
            char child = string.charAt(i);
            childNode = node.getChild(child);

            if(childNode == null) {
                node = node.addChild(child);
            }
            else {
                node = childNode;
            }
        }

        node.addID(id);

        if(node.getMarked() == false) {
            node.setMarked(true);
        }
    }

    public TrieNode getChild(String string) {
        TrieNode node = rootNode;

        for(int i = 0; i < string.length(); i++) {
            char child = string.charAt(i);
            node = node.getChild(child);

            if(node == null) {
                return null;
            }
        }
        return node;
    }

    public List<String> getNames(String query) {
        TrieNode node = getChild(query);

        if(node != null) {
        	return node.getNames(query);
        }
        else {
            return new ArrayList<String>();
        }
    }
}