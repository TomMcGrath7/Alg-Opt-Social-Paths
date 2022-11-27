import java.util.*;

public class BFS {

    public static BFSNode[] search(Instance instance){

        Queue<BFSNode> queue = new LinkedList<>();

        BFSNode root= new BFSNode(null, instance.starts,-1);
        queue.add(root);

        BFSNode[][] createdPositions = new BFSNode[instance.graph.V][instance.graph.V];  // TODO p>2? pD array or p nested lists
        createdPositions[root.state[0]][root.state[1]] = root;

        BFSNode best = new BFSNode(null, null, -1);
        best.turn = instance.T+1;

        while(!queue.isEmpty()){
            BFSNode toExpand = queue.remove();
            toExpand.visited = true;

            if(Arrays.equals(toExpand.state, instance.ends) && toExpand.turn<best.turn){
                best = toExpand;
            }

            if(toExpand.turn>instance.T){
                continue;
            }

            Create1DMoves(toExpand, instance, queue, createdPositions);
        }
        return new BFSNode[]{root, best};
    }

    private static void Create1DMoves(BFSNode toExpand, Instance instance, Queue<BFSNode> queue, BFSNode[][] createdPositions){
        List<Integer> movePrio = new ArrayList<>();
        for (int i = 0; i < toExpand.moved.length; i++) {
            movePrio.add(toExpand.moved[i]? movePrio.size(): 0, i);
        }
        for (Integer individual : movePrio) {
            if(toExpand.moved[individual] && !isValid(toExpand.state, instance.graph.distanceMatrix, instance.D, null)){
                continue;
            }
            CreateIndividualMoves(toExpand, instance, queue, createdPositions, individual);
        }
    }

    private static void CreateIndividualMoves(BFSNode toExpand, Instance instance, Queue<BFSNode> queue, BFSNode[][] createdPositions, int individual){
        List<Integer> options = instance.graph.adjacencyList.get(toExpand.state[individual]);
        for (Integer move : options) {
            int[] candidateState = Arrays.copyOf(toExpand.state, toExpand.state.length);
            candidateState[individual] = move;
            BFSNode candidate = new BFSNode(toExpand, candidateState, individual);

            BFSNode currentShortest = createdPositions[candidateState[0]][candidateState[1]];

            if(currentShortest!=null && candidate.turn< currentShortest.turn){
                currentShortest.changeParent(toExpand);
            }
            else if(createdPositions[candidateState[0]][candidateState[1]]==null && isValid(candidateState, instance.graph.distanceMatrix, instance.D, candidate.moved)){
                queue.add(candidate);
                createdPositions[candidateState[0]][candidateState[1]] = candidate;
                toExpand.children.add(candidate);
            }
        }
    }

    public static boolean isValid(int[] positions, int[][] distanceMatrix, int D, boolean[] moved){
        for (int i = 0; i < positions.length; i++) {
            for (int j = i+1; j < positions.length; j++) {
                int dist = moved==null? D: moved[i] && moved[j]? D : D-1;
                if(distanceMatrix[positions[i]][positions[j]]<=dist){
                    return false;
                }
            }
        }
        return true;
    }
}
