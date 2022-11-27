import java.util.*;

public class BFS {

    /**
     * Performs a BFS on the instance of the problem given: <br>
     * - A node represents a state <br>
     * - The root of the BFS' tree is the starting position of each player <br>
     * - Nodes get expanded as long as the path they represent is not too long AND it isn't the destination state <br>
     * - States that have already been created do not need to be created anymore, limiting size of the tree to n^p <br>
     * - States that do not respect the relaxed distance limit are not valid and are not created <br>
     * @param instance problem instance
     * @return root node and solution node in array
     * @implNote O(n^(1+p))
     */
    public static BFSNode[] search(Instance instance){

        // A PriorityQueue would be better logically as it would shuffle the entries with the lowest turn to the front to get expanded first
        // however this is almost twice as slow due to it having to iterate over each element and compare the turn of each node.
        Queue<BFSNode> queue = new LinkedList<>();

        // Set starting positions as root of tree
        BFSNode root= new BFSNode(null, instance.starts,-1);
        queue.add(root);

        // Array that keeps track of which nodes have been created
        BFSNode[][] createdPositions = new BFSNode[instance.graph.V][instance.graph.V];  // TODO p>2? 1D array with n^p entries. One for each permutation of positions
        createdPositions[root.state[0]][root.state[1]] = root;

        // Keep a node that tracks what the current shortest path is to the end
        // Initialized as an empty node with turn=T+1
        BFSNode best = new BFSNode(null, null, -1);
        best.turn = instance.T+1;

        while(!queue.isEmpty()){ // Worst case n^p loops => O(n^(1+p))

            BFSNode toExpand = queue.remove();
            toExpand.visited = true;

            // If node toExpand is too long, skip
            if(toExpand.turn>instance.T || toExpand.turn>best.turn){
                continue;
            }
            // If node toExpand is a represents the destination state AND is not too long, keep it as new best
            else if(Arrays.equals(toExpand.state, instance.ends)){
                best = toExpand;
                continue;
            }

            Create1DMoves(toExpand, instance, queue, createdPositions); // O(n)
        }
        return new BFSNode[]{root, best};
    }

    /**
     * Allows each player to make a move INDEPENDENTLY.
     * The players who haven't moved to reach the given node get to expand the node first. This ensures correctness and optimality.
     * @param toExpand node to expand
     * @param instance problem instance
     * @param queue BFS queue
     * @param createdPositions states that have already been created
     * @implNote O(np) => asymptotically O(n)
     */
    private static void Create1DMoves(BFSNode toExpand, Instance instance, Queue<BFSNode> queue, BFSNode[][] createdPositions){
        List<Integer> movePrio = new ArrayList<>();
        for (int i = 0; i < toExpand.moved.length; i++) { // p loops => O(p)
            movePrio.add(toExpand.moved[i]? movePrio.size(): 0, i);
        }

        for (Integer player : movePrio) { // p loops => O(np)

            // If a player moves again, this will end the turn and the state needs to respect the distance D. No relaxation.
            // If the current state does not respect the distance, the player cannot move again.
            if(toExpand.moved[player] && !isValid(toExpand.state, instance.graph.distanceMatrix, instance.D, null)){ // O(p^2)
                continue;
            }
            CreatePlayerMoves(toExpand, instance, queue, createdPositions, player); // O(n)
        }
    }

    /**
     * Create all children nodes of given node <br>
     * For each new candidate state, check if it has already been created: <br>
     * - If it has: verify that it is shorter than the newly created state. If it isn't cut in the newly created as new shortest <br>
     * - If it hasn't: verify that it respects the distance D (relaxed w.r.t who moved this turn) and add it to the queue <br>
     * @param toExpand node to expand
     * @param instance problem instance
     * @param queue BFS queue
     * @param createdPositions states that have already been created
     * @param player player that gets to move
     * @implNote O(n(p + p^2)) => asymptotically O(n)
     */
    private static void CreatePlayerMoves(BFSNode toExpand, Instance instance, Queue<BFSNode> queue, BFSNode[][] createdPositions, int player){

        List<Integer> options = instance.graph.adjacencyList.get(toExpand.state[player]); // O(1) because adjacency list
        for (Integer move : options) { // Worst case each vertex is adjacent to the player's position: n loops => O(n(p + p^2))

            int[] candidateState = Arrays.copyOf(toExpand.state, toExpand.state.length); // O(p)
            candidateState[player] = move;
            BFSNode candidate = new BFSNode(toExpand, candidateState, player); // O(p)

            BFSNode currentShortest = createdPositions[candidateState[0]][candidateState[1]];

            // If a node has already been created but the candidate is a shorter =>
            // Cut in the parent of the candidate as the parent of the previous best and propagate this change down to all children
            // This is done instead of replacing the by the candidate because if that is done, the children of candidate will be the same as the children of currentShortest.
            if(currentShortest!=null && candidate.turn< currentShortest.turn){
                //This always happen when both nodes have the same depth because the order of transitions matter.
                // The same transition but in different order will often result in a different amount of turns.
                currentShortest.changeParent(toExpand); // O(p)
            }
            else if(createdPositions[candidateState[0]][candidateState[1]]==null && isValid(candidateState, instance.graph.distanceMatrix, instance.D, candidate.moved)){ // O(p^2)
                queue.add(candidate);
                createdPositions[candidateState[0]][candidateState[1]] = candidate;
                toExpand.children.add(candidate);
            }
        }
    }

    /**
     * Goes over all pairs of players:
     * If a player in a pair hasn't moved yet, the distance can be relaxed to D-1 because that player can always move away this turn, resolving the distance conflict
     * @param positions player positions to check
     * @param distanceMatrix matrix with distance between each pair of vertices/positions
     * @param D distance to respect
     * @param moved array with who moved a given turn.
     * @return true if the relaxed distance with respect to who moved is respected
     * @implNote O(p^2)
     */
    public static boolean isValid(int[] positions, int[][] distanceMatrix, int D, boolean[] moved){
        for (int i = 0; i < positions.length; i++) { // O(p^2)
            for (int j = i+1; j < positions.length; j++) { // O(p)
                int dist = moved==null? D: !moved[i] || !moved[j]? D-1 : D;
                if(distanceMatrix[positions[i]][positions[j]]<=dist){
                    return false;
                }
            }
        }
        return true;
    }
}
