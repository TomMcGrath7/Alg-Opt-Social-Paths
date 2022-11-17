import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Node {

    public int[] positions;
    public Node parent;
    public boolean visited;

    public int depth;
    public Node(int[] positions, Node parent, boolean visited, int depth){
        this.positions = positions;
        this.parent = parent;
        this.visited = visited;
        this.depth = depth;
    }

    public List<List<Integer>> backStack(int toAdd){
        if(parent==null){
            List<List<Integer>> l = new ArrayList<>();
            for (int i = 0; i < positions.length; i++) {
                l.add(new ArrayList<>());
                l.get(i).add(positions[i]+toAdd);
            }
            return l;
        }
        List<List<Integer>> l = parent.backStack(toAdd);
        for (int i = 0; i < positions.length; i++) {
            l.get(i).add(positions[i]+toAdd);
        }
        return l;
    }

    @Override
    public String toString() {
        return parent+" "+depth+"-"+Arrays.toString(positions);
    }
}
