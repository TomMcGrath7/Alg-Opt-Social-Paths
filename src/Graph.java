import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * -1 to every vertex value from input file because they start at 1
 * => !!! output will be offset by -1 !!!
 */
public class Graph {

    public int[][] adjacencyMatrix;

    public List<List<Integer>> adjacencyList;
    public int[][] distanceMatrix;
    public Graph(int numVerts, String[] edges){

        this.adjacencyMatrix = new int[numVerts][numVerts];

        for (String edge : edges) {
            String[] ends = edge.split(" ");
            int[] endsi = new int[]{ Integer.parseInt(ends[0]), Integer.parseInt(ends[1])};
            adjacencyMatrix[endsi[0]-1][endsi[1]-1] = 1;
            adjacencyMatrix[endsi[1]-1][endsi[0]-1] = 1;
        }
        distanceMatrix = Helper.distanceMatrix(adjacencyMatrix);

        adjacencyList = Helper.toAdjacencyList(adjacencyMatrix);
    }

    public Graph(int[][] newAjd) {
        adjacencyMatrix = newAjd;
        distanceMatrix = Helper.distanceMatrix(adjacencyMatrix);
    }

    public Graph removeNodesAround(int node, int distance){
        int[] distances = distanceMatrix[node];
        List<Integer> toRemove = new ArrayList<>();
        for (int i = 0; i < distances.length; i++) {
            if(distances[i]<=distance){
                toRemove.add(i);
            }
        }
        System.out.println(Arrays.toString(distances));
        System.out.println(toRemove);
        int[][] newAjd = adjacencyMatrix.clone();
        for (int i = 0; i < distances.length; i++) {
            for (Integer n : toRemove) {
                newAjd[i][n] = 0;
                newAjd[n][i] = 0;
            }
        }

        return new Graph(newAjd);
    }
    @Override
    public String toString() {

        return "Graph{" +
                "adjacencyMatrix=\n" + Helper.adjacencyMatrixString(adjacencyMatrix) +
                ", distanceMatrix=\n" + Helper.adjacencyMatrixString(distanceMatrix) +
                "\n, list=\n" + Helper.adjacencyListString(adjacencyList) +
                '}';
    }
}
