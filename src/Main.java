import Dijkstra.Dijkstra;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        Game g = new Game("src/testcases/grid10-0.in");
        Node result = BFS(g, true).get(0);
        if(result!=null){
            List<List<Integer>> paths = result.backStack(1);
            System.out.println("Steps: "+(paths.get(0).size()-1));
            for (List<Integer> path : paths) {
                System.out.println(path);
            }
        }
    }

    public static List<Node> BFS(Game g, boolean first){

        Queue<Node> queue = new LinkedList<>();

        Node root = new Node(g.starts, null, true, 0);
        queue.add(root);

        List<Node> solutions = new ArrayList<>();
        int prev = root.depth;

        while(!queue.isEmpty()){
            Node cur = queue.remove();
            if(cur.depth!=prev){
                prev = cur.depth;
                System.out.println(cur.depth+": "+queue.size());
            }
            // Solution found.
            if(Arrays.equals(cur.positions, g.ends)){
                solutions.add(cur);
                if(first){
                    return solutions;
                }
                continue;
            }

            // Node out of range. Skip.
            if(cur.depth>=g.T){
                continue;
            }

            // All positions that can be made from here:
            List<Integer> options1 = new ArrayList<>(g.graph.adjacencyList.get(cur.positions[0]));
            options1.add(0, cur.positions[0]);
            List<Integer> options2 = new ArrayList<>(g.graph.adjacencyList.get(cur.positions[1]));
            options2.add(0, cur.positions[1]);

            for (Integer move1 : options1) {
                for (Integer move2 : options2) {
                    int[] curAr = new int[cur.positions.length];
                    System.arraycopy(cur.positions, 0, curAr, 0, curAr.length);

                    curAr[0] = move1;
                    curAr[1] = move2;
                    if(isValid(g, curAr)){
                        queue.add(new Node(curAr, cur, true, cur.depth+1));
                    }
                }
            }
        }
        return solutions;
    }

    public static boolean isValid(Game g, int[] positions){
        for (int i = 0; i < positions.length; i++) {
            for (int j = i+1; j < positions.length; j++) {
                if(g.graph.distanceMatrix[positions[i]][positions[j]]<=g.D){
                    return false;
                }
            }
        }
        return true;
    }
}