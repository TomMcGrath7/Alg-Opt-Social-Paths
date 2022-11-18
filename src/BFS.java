import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BFS {

    public static Node root;
    /**
     * @param instance problem instance
     * @param verbose printing
     * @return first/highest node in the tree that has the target destinations if found, else null.
     * AKA the fastest way to reach the target destinations given the limits
     * <p><p>
     * Given a problem instance, execute breadth first search on s tree that has:
     * <p>- the starting position of each individual as root
     * <p>- branching done by allowing only one move at a time
     * <p>- maximum depth of T
     * <p>- pruning1: each position that has already been visited cannot generate a new node
     * <p>- pruning2: each position that violates the distance constraint not generated
     */
    public static Solution search(Instance instance, boolean verbose, boolean move1D){

        long start = System.currentTimeMillis();

        Queue<Node> queue = new LinkedList<>();

        int numIndividuals = instance.starts.length;

        // Initialize root node as the starting position of individuals
        Node root;
        if(move1D){
            root = new Node1D(instance.starts, null, false,-1);
        }
        else{
            root = new Node(instance.starts, null, false);
        }
        BFS.root = root;
        queue.add(root);

        // To know when the next depth of the tree starts being explored
        int prev = root.depth;
        // Pruning step: if a unique position has already been seen, there is no reason to explore it again
        // Impossible for it to result in a better solution because of BFS
        // => Removing it all together reduces the amount options dramatically as the tree expands
        int[][] createdPositions = new int[instance.graph.adjacencyMatrix.length][instance.graph.adjacencyMatrix.length]; // TODO nD array for n individuals
        createdPositions[root.positions[0]][root.positions[1]] =1;
        // To alternate who moves (Otherwise first will move to its target, then the 2nd will, etc...)
        int currentMover = 0;

        while(!queue.isEmpty()){
            Node cur = queue.remove();
            cur.visited = true;

            // If reached target destination for all => solution found.
            if(Arrays.equals(cur.positions, instance.ends)){
                return new Solution(cur, (System.currentTimeMillis() - start)/1000.0, move1D);
            }

            // If depth greater than T => node too deep => skip
            // T * # of individuals: Moves are done in 1D but in reality everyone can move at once.
            // TODO think about this. Does this work?
            //  Because if in 2D move the players can only move one at a time anyways, then this would find a path
            //  that is 2x too long (?).
            // ^ Not important because BFS returns shorts path anyways so if shortest path is longer than T => no solution
            if(cur.depth>=instance.T*numIndividuals){
                continue;
            }

            // If next depth => next player gets the options to move first
            if(cur.depth!=prev){
                currentMover = (currentMover+1)%numIndividuals;
                prev = cur.depth;
                if(verbose){
                    System.out.println("Width at depth "+cur.depth+": "+queue.size());
                }
            }
            if(move1D) {
                // Make who has the opportunity to move first alternate => makes compacting the final chain easier
                int i = currentMover;
                do {
                    // If current position isn't valid, an individual who hasn't moved yet needs to resolve the issue
                    if(!isValid(cur.positions, instance.graph.distanceMatrix, instance.D, null) && ((Node1D)cur).moved[i]){
                        i = (i + 1) % numIndividuals;
                        continue;
                    }
                    // Generate all 1D moves that the individual can take and add to queue
                    List<Integer> options = instance.graph.adjacencyList.get(cur.positions[i]);
                    Create1DMoves(i, options, cur, instance, queue, createdPositions);
                    i = (i + 1) % numIndividuals;
                } while (i != currentMover);
            }else{
                CreateMultiDMoves(cur, instance, queue, createdPositions);
            }
        }
        return new Solution(null, (System.currentTimeMillis() - start)/1000.0, move1D);
    }

    /**
     * For every option of an individual, do it and add it to the queue with current depth+1
     * @param individual index that gets to move
     * @param options options of the given individual
     * @param cur node being expanded
     * @param instance problem instance
     * @param queue BFS queue
     * @param createdPositions positions that have already been created earlier in the tree => no need to explore at deeper depths.
     */
    private static void Create1DMoves(int individual, List<Integer> options, Node cur, Instance instance, Queue<Node> queue, int[][] createdPositions){

        for (Integer move : options) {
            int[] curAr = new int[cur.positions.length];
            System.arraycopy(cur.positions, 0, curAr, 0, curAr.length);

            curAr[individual] = move;

            if(createdPositions[curAr[0]][curAr[1]]==0 && isValid(curAr, instance.graph.distanceMatrix, instance.D, ((Node1D)cur).moved)){
                queue.add(new Node1D(curAr, cur, false, individual));
                createdPositions[curAr[0]][curAr[1]] = 1;
            }
        }
    }

    private static void CreateMultiDMoves(Node cur, Instance instance, Queue<Node> queue, int[][] createdPositions){
        List<Integer> options1 = instance.graph.adjacencyList.get(cur.positions[0]);
        options1.add(cur.positions[0]);
        List<Integer> options2 = instance.graph.adjacencyList.get(cur.positions[1]);
        options2.add(cur.positions[1]);

        for (Integer op1 : options1) {
            for (Integer op2 : options2) {
                int[] curAr = new int[cur.positions.length];
                curAr[0] = op1;
                curAr[1] = op2;
                if(createdPositions[curAr[0]][curAr[1]]==0 && isValid(curAr, instance.graph.distanceMatrix, instance.D, null)){
                    queue.add(new Node(curAr, cur, false));
                    createdPositions[curAr[0]][curAr[1]] = 1;
                }
            }
        }
    }

    /**
     * Checks if all pairs of individuals are D apart
     * @param positions current position of everyone
     * @param distanceMatrix # edges vertices between every pair of vertices
     * @param D min distance allowed
     * @return true if no one is too close, else true
     */
    public static boolean isValid(int[] positions, int[][] distanceMatrix, int D, boolean[] moved){
        for (int i = 0; i < positions.length; i++) {
            for (int j = i+1; j < positions.length; j++) {
                int dist = moved==null? D: moved[i]|| moved[j]? D : D-1;
                if(distanceMatrix[positions[i]][positions[j]]<=dist){
                    return false;
                }
            }
        }
        return true;
    }
}
