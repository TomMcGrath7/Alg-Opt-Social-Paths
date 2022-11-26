import java.util.*;

public class TEST {

    public static void main(String[] args) {

        String directory = "src/testcases";
        List<String> files = Helper.listFilesUsingJavaIO(directory, "in");

        //run(Helper.read(directory+"/grid10-3-randomized.in"));

        long all = System.currentTimeMillis();

        for (String file : files) {
            System.out.println(file);
            String input = Helper.read(directory+"/"+file);
            run(input);
        }
        System.out.println((System.currentTimeMillis()-all)/1000.0);

    }

    public static void run(String input){
        long s = System.currentTimeMillis();

        Instance instance = new Instance(input); // O(n^3+m)

        BFSNode[] nodes = search(instance);
        Solution solution = new Solution(nodes[1], instance.T);

        System.out.println("SOLUTION:");
        System.out.println(solution);
        System.out.println(((System.currentTimeMillis()-s)/1000.0)+" seconds");
        System.out.println("Correct? " +solution.correct(instance, true));
        double tot = Math.pow(instance.graph.V, instance.p);
        int size = nodes[0].size();
        System.out.println("NODES CREATED: "+size+"/"+tot+" = "+(size/tot));
    }

    /**
     * BFS with memoization of the shortest path to each EXACT STATE
     * <p> The distinction between states and exact states are important due to the distance relaxation rule.
     * @param instance problem instance
     * @return array containing root node first and then solution node. Solution node is null if no solution found.
     */
    public static BFSNode[] search(Instance instance){

        // Initialize tree
        BFSNode root = new BFSNode(null, instance.starts, -1);
        Queue<BFSNode> queue = new PriorityQueue<>();
        queue.add(root);

        BFSNode[][] visited = new BFSNode[(int) Math.pow(instance.graph.V, instance.p)][(int) (Math.pow(instance.p, 2)-1)];
        for (int i = 0; i < visited[0].length; i++) {
            visited[Helper.to1D(root.state, instance.graph.V)][i] = root;
        }
        BFSNode best = null;
        // Search through tree, expanding as needed
        while(!queue.isEmpty()){
            BFSNode toExpand = queue.poll();
            if(toExpand.turn>instance.T || (best!=null && toExpand.turn>=best.turn)){ // If too long, not needed. If longer than current best solution, not needed.
                continue;
            }
            else if(Arrays.equals(toExpand.state, instance.ends)){ // If final state reached, solution
                System.out.println(toExpand.deepString());
                best = toExpand;
            }

            expandNode(toExpand, queue, instance, visited);
        }
        return new BFSNode[]{root, best};
    }

    static int wqe =0;
    /**
     * Expand node:
     * - 1D for each player
     * - A player can move again (already moved this turn) ONLY IF the current state is ABSOLUTELY VALID (D respect by every pair)
     * @param toExpand state to expand
     * @param queue BFS queue
     * @param instance problem instance
     * @param visited states already visited
     */
    public static void expandNode(BFSNode toExpand, Queue<BFSNode> queue, Instance instance, BFSNode[][] visited){
        for (int i = 0; i < instance.p; i++) { // p loops

            if(toExpand.moved[i] && !isValid(toExpand.state, instance, null)){
                continue;
            }
            expandNodeForPlayer(toExpand, queue, instance, visited, i);
        }
    }

    /**
     * Create all possible children of a state given only one player can move and reduce the total options:
     * <p>- If an EXACT STATE (positions + who moved to reach this position) has already been created => don't create it again. Works fine for a normal tree
     * (if multidimensional moves are allowed for example) but can create issues with 1D moves.
     *<p></p>
     * The pruning step explained above works based on the fact that in normal BFS, the only thing that matters is depth.
     * But in this case, multiple 1D moves (depth) could be compacted into a single turn (multiple moves).
     * So we need to keep track of the shortest path to each EXACT STATE which is done by the "visisted" matrix.
     * If a shorter path is found after an EXACT STATE has already been visited -> cut the new shortest path in as the parent of the already created and expanded node.
     * Avoids exploring the same EXACT STATES again.

     * @param toExpand state to expand
     * @param queue BFS queue
     * @param instance problem instance
     * @param visited states already visited
     * @param player player index that gets to move
     */
    public static void expandNodeForPlayer(BFSNode toExpand, Queue<BFSNode> queue, Instance instance, BFSNode[][] visited, int player){
        List<Integer> transitions = instance.graph.adjacencyList.get(toExpand.state[player]);
        for (Integer transition : transitions) { // WORST CASE: n loops
            int[] state = toExpand.state.clone();
            state[player] = transition;
            BFSNode possibleNode = new BFSNode(toExpand, state, player);

            int idx = Helper.to1D(possibleNode.state, instance.graph.V);
            int dim = Helper.boolToInt(possibleNode.moved);

            for (int i = 0; i < visited[0].length; i++) {
                if(visited[idx][i]!=null && visited[idx][i].turn<possibleNode.turn){
                    visited[idx][dim] = visited[idx][i];
                }
            }
            if(visited[idx][dim]!=null && possibleNode.isBetterThan(visited[idx][dim])){
                toExpand.children.add(visited[idx][dim]);
                visited[idx][dim].changeParent(toExpand);

            }
            else if(visited[idx][dim]==null && isValid(possibleNode.state, instance, possibleNode.moved)){
                queue.add(possibleNode);
                toExpand.children.add(possibleNode);
                visited[idx][dim] = possibleNode;
            }
        }
    }

    /**
     * Check validity:
     * <p>- Check if each pair of players are at least D apart
     * <p>- Relaxed to D-1 if either players haven't moved yet
     * @param state state to check
     * @param instance problem instance
     * @param moved due to the possibility of multiple movements per turn, need to know who already moved
     * @return true if is valid given current state
     * @implNote o(2^p)
     */
    public static boolean isValid(int[] state, Instance instance, boolean[] moved){
        for (int i = 0; i < state.length; i++) {
            for (int j = i+1; j < state.length; j++) {
                // XOR
                int relaxedD = moved==null? instance.D : !moved[i] || !moved[j]? instance.D-1 : instance.D;
                if(instance.graph.distanceMatrix[state[i]][state[j]]<=relaxedD){
                    return false;
                }
            }
        }
        return true;
    }
}

// State of a node: position of players + who moved this turn
class BFSNode implements Comparable<BFSNode>{
    public BFSNode parent;
    public int depth;
    public int[] state;
    public boolean[] moved;
    public int turn;
    public int mover;
    public List<BFSNode> children;
    public BFSNode(BFSNode parent, int[] state, int mover){
        this.parent = parent;
        this.state = state;
        this.children = new ArrayList<>();
        this.mover = mover;
        if(parent==null){
            depth = 0;
            turn = 0;
            moved = new boolean[state.length];
        }
        else {
            update();
        }
    }

    public void update(){
        depth = parent.depth+1;
        if(parent.moved[mover]){
            moved = new boolean[parent.moved.length];
            turn = parent.turn+1;
        }else{
            this.moved = Arrays.copyOf(parent.moved, parent.moved.length);
            turn = parent.turn;
        }
        moved[mover] = true;
    }

    /**
     * Propagate the correct turn a node is at down to its children.
     * Needed to keep track of which solution is better than another
     * @param parent new updated parent
     * @implNote Worst case, need to propagate down from root to end => O(T)
     */
    public void changeParent(BFSNode parent){
        this.parent = parent;
        update();
        for (BFSNode child : children) {
            child.update();
        }
    }

    public int size(){

        int s = children.size();
        for (BFSNode child : children) {
            if(child.turn<turn){
                System.out.println(this);
                System.out.println(child);
                System.out.println();
            }
            s+= child.size();
        }
        return s;
    }

    /**
     * Checks if it's the exact same state:
     * - Same position for each player
     * - Same players moved the given turn
     * AND
     * if the new node has a lower turn value
     * @param node To check if better
     * @return if exact same state and node is faster path
     * @implNote Need to check every element of p sized arrays => O(p)
     */
    public boolean isBetterThan(BFSNode node){
        // First 2 should always be true
        return node!=null && Arrays.equals(state, node.state) && Arrays.equals(moved, node.moved) && turn<node.turn;
    }

    public List<BFSNode> getCompactedPath() {
        List<BFSNode> turns = new ArrayList<>();
        turns.add(this);
        turns.addAll(0,turns());
        return turns;
    }
    private List<BFSNode> turns() {
        List<BFSNode> turns = new ArrayList<>();
        if(parent==null){
            turns.add(this);
            return turns;
        }
        if(parent.turn!= turn){
            turns.add(0,parent);
        }
        turns.addAll(0, parent.turns());
        return turns;
    }

    public void printTree(int depth, int from, int to){
        if(depth>to){
            return;
        }
        if(depth>=from){
            System.out.println("\t".repeat(depth-from) + "d" + depth + "." + this);
        }
        for (BFSNode child : children) {
            child.printTree(depth+1, from, to);
        }
    }

    public String deepString(){
        return parent==null? toString() : parent.deepString()+" => "+ this;
    }

    @Override
    public String toString() {
        return "turn="+turn+" ("+depth+")"+", moved="+Arrays.toString(moved)+", state="+Arrays.toString(state);
    }

    @Override
    public int compareTo(BFSNode o) {
        return Integer.compare(turn, o.turn);
    }
}
