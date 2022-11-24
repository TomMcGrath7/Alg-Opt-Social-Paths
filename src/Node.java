import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Represents a specific node in tree, a positions of individuals + path taken to get there (AKA parent).
 * Not just the positions because many paths can be taken to reach a single position.
 */
public class Node {

    List<Node> children;
    public int[] positions;
    public Node parent;
    public boolean visited;

    public int depth;
    public Node(int[] positions, Node parent, boolean visited){ // O(1)
        this.positions = positions;
        this.parent = parent;
        this.visited = visited;
        children = new ArrayList<>();
        if(this.parent!=null) {
            this.parent.children.add(this);
            this.depth = parent.depth+1;
        }else{
            depth = 0;
        }
    }

    public void changeParent(Node parent){
        this.parent = parent;
        this.depth = parent.depth+1;
    }

    public List<int[]> statePath(){
        List<int[]> path = new ArrayList<>();
        if(parent!=null){
            path.addAll(parent.statePath());
        }
        path.add(positions);
        return path;
    }

    public int size(){
        int n = children.size();
        for (Node child : children) {
            n+= child.size();
        }
        return n;
    }

    public String treeString(){
        return treeString(0, 0);
    }

    private String treeString(int relativeDepth, int num){
        StringBuilder sb = new StringBuilder();
        sb.append("\t".repeat(relativeDepth)).append(num).append(".").append(Arrays.toString(positions));
        for (int i = 0; i < children.size(); i++) {
            sb.append("\n").append(children.get(i).treeString(relativeDepth+1, i));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return depth+"-"+Arrays.toString(positions);
    }

    public String toDeepString(){
        return parent==null? this.toString() : parent.toDeepString() + this.toString();
    }
}
