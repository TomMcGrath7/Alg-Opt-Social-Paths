import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Solution{

    // entry i of list is positions of each individual is at step i of solution
    public final List<int[]> states;
    public final List<List<Integer>> paths;
    public final int K;
    public final double time; // in seconds
    public final Node solNode;

    public Solution(Node node, double timeInSeconds, boolean move1D, int T){
        solNode = node;
        this.time = timeInSeconds;
        if(node==null){
            paths = new ArrayList<>();
            states = new ArrayList<>();
        }else {

            if(move1D) {
                states = new ArrayList<>();
                List<Node> nodePath = node.nodePath();
                states.add(nodePath.get(0).positions);
                for (int i = 1; i < nodePath.size()-1; i++) {
                    if(((Node1D)nodePath.get(i)).turn != ((Node1D)nodePath.get(i+1)).turn){
                        states.add(nodePath.get(i).positions);
                    }
                }
                states.add(nodePath.get(nodePath.size()-1).positions);

            }else{
                states = node.statePath();
            }

            paths = new ArrayList<>();
            for (int i = 0; i < states.get(0).length; i++) {
                paths.add(new ArrayList<>());
                for (int[] state : states) {
                    paths.get(i).add(state[i]);
                }
            }
        }
        K = states.size()==0? T+1 : states.size()-1;
    }

    public Solution(String sol){
        solNode = null;

        String[] ar = sol.split("\n");
        K = Integer.parseInt(ar[0]);
        if(ar.length==2){
            time = Double.parseDouble(ar[1].split(" ")[0].replace(",","."));
            states = new ArrayList<>();
            paths = new ArrayList<>();
        }
        else {
            time = Double.parseDouble(ar[3].split(" ")[0].replace(",","."));

            states = new ArrayList<>();
            paths = new ArrayList<>();

            paths.add(new ArrayList<>());
            paths.add(new ArrayList<>());

            String[] p1 = ar[1].trim().split(" ");
            String[] p2 = ar[2].trim().split(" ");

            for (int i = 0; i < p1.length; i++) {
                int[] s = new int[]{Integer.parseInt(p1[i]) - 1, Integer.parseInt(p2[i]) - 1};
                states.add(s);
                paths.get(0).add(s[0]);
                paths.get(1).add(s[1]);
            }
        }
    }

    public boolean correct(Instance instance, boolean verbose){
        if(states.size()==0 || paths.size()==0){
            return false;
        }
        // Start or End or Length violation
        if(!Arrays.equals(states.get(0), instance.starts) &&
                !Arrays.equals(states.get(states.size()-1), instance.ends) &&
                states.size()> instance.T){
            if(verbose) {
                System.out.println("Length violation");
            }
            return false;
        }
        // For every state:
        for (int i = 0; i < states.size()-1; i++) {
            int[] current = states.get(i);
            int[] next = states.get(i+1);

            for (int p1 = 0; p1 < current.length; p1++) {
                // Distance violation between pairs
                for (int p2 = p1+1; p2 < current.length; p2++) {
                    if(instance.graph.distanceMatrix[current[p1]][current[p2]]<=instance.D){

                        if(verbose) {
                            System.out.println("Distance violation:");
                            System.out.println("Position " + Arrays.toString(current));
                            System.out.println("Idx " + p1 + " vs " + p2);
                            System.out.println("Distance matrix\n" + Helper.matrix2DString(instance.graph.distanceMatrix));
                        }
                        return false;
                    }
                }
                // Transition violation
                if(current[p1]!=next[p1] && instance.graph.adjacencyMatrix[current[p1]][next[p1]]!=1){
                    if(verbose) {
                        System.out.println("Transition Violation:");
                        System.out.println("Position now " + Arrays.toString(current) + " Position next " + Arrays.toString(next));
                        System.out.println("Adjacency matrix\n" + Helper.matrix2DString(instance.graph.adjacencyMatrix));
                    }
                    return false;
                }
            }

        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(K);
        for (List<Integer> path : paths) {
            sb.append("\n");
            path.forEach(i -> sb.append(i+1).append(" "));
        }
        sb.append("\n").append(time).append(" seconds");
        return sb.toString();
    }
}