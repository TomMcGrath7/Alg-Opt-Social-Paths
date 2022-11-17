import Dijkstra.Dijkstra;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Game g = new Game("src/testcases/case1.in");

        System.out.println(g);

        long start = System.currentTimeMillis();

        Node result = BFS(g, true).get(0);
        if(result!=null){
            List<List<Integer>> paths = result.backStack(1);
            System.out.println("Steps: "+(paths.get(0).size()-1));
            for (List<Integer> path : paths) {
                System.out.println(path);
            }
            System.out.println(result);
        }

        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println(timeElapsed/1000.0);
    }

    public static List<Node> BFS(Game g, boolean first){

        Queue<Node> queue = new LinkedList<>();

        Node root = new Node(g.starts, null, true, 0);
        queue.add(root);

        List<Node> solutions = new ArrayList<>();
        int prev = root.depth;
        int[][] visited = new int[g.graph.adjacencyMatrix.length][g.graph.adjacencyMatrix.length];
        visited[root.positions[0]][root.positions[1]] =1;

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
            if(cur.depth>=g.T*2){
                continue;
            }

            // All positions that can be made from here:
            List<Integer> options1 = new ArrayList<>(g.graph.adjacencyList.get(cur.positions[0]));
            Create1DMoves(0, options1, cur, g, queue, visited);

            List<Integer> options2 = new ArrayList<>(g.graph.adjacencyList.get(cur.positions[1]));
            Create1DMoves(1, options2, cur, g, queue, visited);
        }
        return solutions;
    }

    private static void Create1DMoves(int player, List<Integer> options, Node cur, Game g, Queue<Node> queue, int[][] visited){
        for (Integer move : options) {
            int[] curAr = new int[cur.positions.length];
            System.arraycopy(cur.positions, 0, curAr, 0, curAr.length);

            curAr[player] = move;
            if(visited[curAr[0]][curAr[1]]==0 && isValid(curAr, g.graph.distanceMatrix, g.D-1)){
                queue.add(new Node(curAr, cur, true, cur.depth+1));
                visited[curAr[0]][curAr[1]] = 1;
            }
        }
    }

    public static boolean isValid(int[] positions, int[][] distanceMatrix, int D){
        for (int i = 0; i < positions.length; i++) {
            for (int j = i+1; j < positions.length; j++) {
                if(distanceMatrix[positions[i]][positions[j]]<=D){
                    return false;
                }
            }
        }
        return true;
    }
}