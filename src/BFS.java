import java.util.*;

public class BFS {

    public static Node root;

    /**
     * Given a problem instance, execute breadth first search on a tree that has:
     * <p>- the starting position of each individual as root
     * <p>- the ending position of each individual as terminating node
     * <p>- maximum depth of T
     * <p>- branching done by allowing only one move at a time
     * <p>- pruning1: each position that has already been visited cannot generate a new node
     * <p>- pruning2: each position that violates the distance constraint not generated
     * <p>-DEpruning: it is possible for a state to be reached earlier in terms of tree depth but the path taken to reach
     * it is not the shortest path in terms of turns taken to move and will be discovered later in the tree
     * => if such an instance occurs, the new shortest path parent gets plugged in as the new parent of that state and this change gets propagated downwards
     * <p></p>
     * @param instance instance of problem
     * @param move1D does 1 dimensional moves if true, otherwise everyone can move at the same time
     * @return first node in the tree that has the target destinations if found, else null.
     * <p></p>
     * @implNote 1D: O(n^(1+p)p^3). not 1D: O(n^(2+p)p^2) => 1D: O(n^(1+p)). not 1D: O(n^(2+p)) asymptotically.
     * The 1D moves reduce complexity by a factor of n.
     */
    public static Solution search(Instance instance, boolean move1D){

        long start = System.currentTimeMillis();

        Queue<Node> queue = new LinkedList<>();

        // Initialize root node as the starting position of individuals
        Node root;
        if(move1D){
            root = new Node1D(instance.starts, null, false,-1); // O(p) (O(1) in reality)
        }
        else{
            root = new Node(instance.starts, null, false); // O(1)
        }
        BFS.root = root;
        queue.add(root);

        // Pruning step: if a unique position (for each individual) has already been seen, there is no reason to explore it again at deeper depth
        // Impossible for it to result in a better solution because of BFS
        // => Removing it all together reduces the amount options dramatically as the tree expands
        Node[][] createdPositions = new Node[instance.graph.adjacencyMatrix.length][instance.graph.adjacencyMatrix.length];  // TODO p>2? pD array or p nested lists
        createdPositions[root.positions[0]][root.positions[1]] = root;

        while(!queue.isEmpty()){ // There are only n^p unique states => n^p loops of 1D O(np^3) or not 1D O(n^2p^2) => 1D: O(n^(1+p)p^3). not 1D: O(n^(2+p)p^2)
            Node cur = queue.remove();
            cur.visited = true;

            // If reached target destination for all => solution found.
            if(Arrays.equals(cur.positions, instance.ends)){ // O(1)
                return new Solution(cur, (System.currentTimeMillis() - start)/1000.0, move1D, instance.T);
            }

            // If turns taken is greater than T => node too deep => skip
            if( (move1D && ((Node1D)cur).turn>instance.T) || (!move1D && cur.depth>=instance.T)){ // O(1)
                continue;
            }

            if(move1D) {
                Create1DMoves((Node1D) cur, instance, queue, createdPositions); // O(np^3) and queue.size += np at most
            }else{
                CreateMultiDMoves(cur, instance, queue, createdPositions); // O(n^2p^2) and queue.size += n^2 at most
            }
        }
        return new Solution(null, (System.currentTimeMillis() - start)/1000.0, move1D, instance.T);
    } // 1D: O(n^(1+p)p^3). not 1D: O(n^(2+p)p^2)

    /**
     * Creates all possible states that can occur given current state if only one individual moves at a time, for each individual, with depth+1, and add to queue
     * @param cur node being expanded
     * @param instance problem instance
     * @param queue BFS queue
     * @param createdPositions positions that have already been created earlier in the tree => no need to explore at deeper depths.
     * @implNote O(np^3) => O(n) asymptotically. Worst case: generates np nodes.
     */
    private static void Create1DMoves(Node1D cur, Instance instance, Queue<Node> queue, Node[][] createdPositions){
        // Make who has the opportunity to move first change each sub-turn
        // => stops an individual from moving to its final destination individually and then the next one, etc...
        List<Integer> movePrio = new ArrayList<>();
        for (int i = 0; i < cur.moved.length; i++) {
            movePrio.add(cur.moved[i]? movePrio.size(): 0, i);
        }

        for (Integer individual : movePrio) { // p loops of O(np^2) => O(np^3)
            // If individual already moved and current state is not valid => Cannot go to next turn, someone else needs to resolve it.
            if(cur.moved[individual] && !isValid(cur.positions, instance.graph.distanceMatrix, instance.D, null)){ // O(p^2)
                continue;
            }
            // Generate all 1D moves that the individual can take and add to queue
            CreateIndividualMoves(cur, instance, queue, createdPositions, individual); // O(np^2)
        }
    } // O(np^3) => O(n)

    /**
     * Creates all possible states that can occur given current state if only one individual moves at a time, with depth+1, and add to queue
     * @param cur node being expanded
     * @param instance problem instance
     * @param queue BFS queue
     * @param createdPositions positions that have already been created earlier in the tree => no need to explore at deeper depth
     * @param individual index that gets to move
     * @implNote O(np^2) => O(n) asymptotically. Worst case: generates n nodes
     */
    private static void CreateIndividualMoves(Node1D cur, Instance instance, Queue<Node> queue, Node[][] createdPositions, int individual){
        List<Integer> options = instance.graph.adjacencyList.get(cur.positions[individual]);
        for (Integer move : options) { // n loops of O(p^2)
            int[] curAr = new int[cur.positions.length]; // O(p)
            System.arraycopy(cur.positions, 0, curAr, 0, curAr.length); // O(p)

            curAr[individual] = move;

            Node1D curBest = (Node1D) createdPositions[curAr[0]][curAr[1]];
            Node1D n = new Node1D(curAr, cur, false, individual);
            if(curBest!=null && n.turn< curBest.turn){
                curBest.changeParent(cur);
                cur.children.remove(n);
            }
            else if(createdPositions[curAr[0]][curAr[1]]==null && isValid(curAr, instance.graph.distanceMatrix, instance.D, n.complete? new boolean[n.moved.length] : n.moved)){
                queue.add(n);
                createdPositions[curAr[0]][curAr[1]] = n;
            }else{
                cur.children.remove(n);
            }// O(p^2)
        } // O(np^2)
    } // O(np^2) = O(n)

    /**
     * Creates all possible states that can occur given current state, with depth+1, and add to queue
     * @param cur node being expanded
     * @param instance problem instance
     * @param queue BFS queue
     * @param createdPositions positions that have already been created earlier in the tree => no need to explore at deeper depths
     * @implNote O(n^2p^2) => O(n^2) asymptotically. Worst case: generates n^2 nodes.
     */
    private static void CreateMultiDMoves(Node cur, Instance instance, Queue<Node> queue, Node[][] createdPositions){
        // TODO p>2? options list for each individual
        List<Integer> options1 = instance.graph.adjacencyList.get(cur.positions[0]);
        options1.add(cur.positions[0]); // size = n+1
        List<Integer> options2 = instance.graph.adjacencyList.get(cur.positions[1]);
        options2.add(cur.positions[1]); // size = n+1

        for (Integer op1 : options1) { // n+1 loops of O(p^2) => O(n^2p^2)
            for (Integer op2 : options2) { // n+1 loops of O(1) => O(np^2)
                int[] curAr = new int[cur.positions.length];
                curAr[0] = op1;
                curAr[1] = op2;
                if(createdPositions[curAr[0]][curAr[1]]==null && isValid(curAr, instance.graph.distanceMatrix, instance.D, null)){ // O(p^2)
                    Node n = new Node(curAr, cur, false);
                    queue.add(n); // O(1)
                    createdPositions[curAr[0]][curAr[1]] = n;
                }
            }
        }
    } // O(n^2p^2) = O(n^2)

    /**
     * Checks if all pairs of individuals are D apart.
     * Allows an individual to move within the distance limit of another if the other hasn't moved yet
     * This is because if the other hasn't moved yet => he can move away from the ones that are too close in the same turn
     * @param positions current position of everyone
     * @param distanceMatrix # edges vertices between every pair of vertices
     * @param D min distance allowed
     * @return true if no one is too close, else true
     * @implNote O(p^2) => O(1) asymptotically
     */
    public static boolean isValid(int[] positions, int[][] distanceMatrix, int D, boolean[] moved){
        for (int i = 0; i < positions.length; i++) { // p loops of O(p) => O(p^2)
            for (int j = i+1; j < positions.length; j++) { // p loops of O(1) => O(p)
                // Allows an individual to move within the distance limit of another if one of the 2 hasn't moved yet
                // This is because if the other hasn't moved yet => he can move away from the ones that are too close in the same turn
                int dist = moved==null? D: moved[i] && moved[j]? D : D-1;
                if(distanceMatrix[positions[i]][positions[j]]<=dist){
                    return false;
                }
            }
        }
        return true;
    } // O(p^2) = O(1)
}
