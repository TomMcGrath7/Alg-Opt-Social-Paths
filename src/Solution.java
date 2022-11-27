import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Solution{

    // entry i of list is positions of each player is at step i of solution
    public final List<int[]> states;
    public final List<List<Integer>> paths;
    public final int K;

    /**
     * @param node node to compress and turn into a solution
     * @implNote O(T)
     */
    public Solution(BFSNode node){
        K = node.turn;
        states = new ArrayList<>();
        paths = new ArrayList<>();

        if(node.state!=null) {
            addState(node.state);
            while (node.parent != null) { // Worst case: T loops => O(Tp) => asymptotically O(T)
                if (node.parent.turn != node.turn) {
                    addState(node.parent.state);
                }
                node = node.parent; // O(p)
            }
            addState(node.state); // O(p)
        }

    }

    /**
     * @param s state to add to path
     * @implNote O(p)
     */
    private void addState(int[] s){
        states.add(0, s); // O(1) bc arraylist
        for (int i = 0; i < s.length; i++) { // O(p)
            if(paths.size()<=i){
                paths.add(new ArrayList<>());
            }
            paths.get(i).add(0, s[i]);
        }
    }

    public Solution(String sol){

        String[] ar = sol.split("\n");
        K = Integer.parseInt(ar[0]);
        if(ar.length==2){
            states = new ArrayList<>();
            paths = new ArrayList<>();
        }
        else {
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

    /**
     * Verifies the correctness of the solution given the problem instance: <br>
     * - Not too long <br>
     * - Starts and ends at correct place <br>
     * - No state has a distance violation <br>
     * - All transitions are possible <br>
     * @param instance problem instance
     * @param verbose if true and finds an error, prints what the error is. Else prints nothing
     * @return true if correct else false
     */
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
        return sb.toString();
    }
}