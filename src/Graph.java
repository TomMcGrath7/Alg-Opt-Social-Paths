import java.util.ArrayList;
import java.util.List;
/**
 * Graph with edges represented as:
 * - adjacency matrix <br>
 * - adjacency list <br>
 * - distance matrix representing the distance between each pair of vertices <br>
 * @implNote  vertex values go from 0 to N-1. Input file has vertex values from 1 to N !!! -1 to output vertex values !!!
 */
public class Graph {

    public  int V;
    public int[][] adjacencyMatrix;
    public List<List<Integer>> adjacencyList;
    public int[][] distanceMatrix;

    /**
     * Complexity O(n^3) due to distance matrix
     * @param vertNum number of vertices N
     * @param edges edges represented in string form (x y <- where x and y are vertex indices 1 to N)
     */
    public Graph(int vertNum, String[] edges){ // O(n^3)

        this.adjacencyMatrix = new int[vertNum][vertNum]; // O(n^2)
        for (String edge : edges) { // m loops of O(1) => O(m)
            String[] ends = edge.split(" ");
            int[] endsi = new int[]{ Integer.parseInt(ends[0]), Integer.parseInt(ends[1])};
            adjacencyMatrix[endsi[0]-1][endsi[1]-1] = 1;
            adjacencyMatrix[endsi[1]-1][endsi[0]-1] = 1;
        }

        adjacencyList = Graph.generateAdjacencyMatrix(adjacencyMatrix); // O(n^2)

        distanceMatrix = Graph.generateDistanceMatrix(adjacencyMatrix); // O(n^3)
        V = vertNum;
    }

    // TODO can be made faster probably (Johnson's Algorithm O(n^2 * log(n) + n * m) OR Djikstra over each pair)
    /**
     * Floyd Warshall Algorithm
     * Get the shortest distance between each pair of vertices
     * @param adjacencyMatrix adjacency matrix representation of graph
     * @return distance matrix with the shortest distance between each pair of vertices
     */
    public static int[][] generateDistanceMatrix(int[][] adjacencyMatrix){ // O(n^3)
        int[][] distanceMatrix = new int[adjacencyMatrix.length][adjacencyMatrix.length];
        for (int i = 0; i < distanceMatrix.length; i++) { // n loops of O(n) => O(n^2)
            for (int j = 0; j < distanceMatrix[i].length; j++) { // n loops of O(1) => O(n)
                if(i==j){
                    continue;
                }
                distanceMatrix[i][j] = adjacencyMatrix[i][j]==0? Integer.MAX_VALUE : adjacencyMatrix[i][j];
            }
        }
        for (int i = 0; i < distanceMatrix.length; i++) { // n loops of O(n^2) => O(n^3)
            for (int j = 0; j < distanceMatrix[i].length; j++) { // n loops of O(n) => O(n^2)
                distanceMatrix[i][j] = minSum(distanceMatrix[i], distanceMatrix[j]); // O(n)
                distanceMatrix[j][i] = distanceMatrix[i][j]; // O(1)
            }
        }
        return distanceMatrix;
    }

    public static List<List<Integer>> generateAdjacencyMatrix(int[][] adjMat){ // O(n^2)
        List<List<Integer>> adjL = new ArrayList<>();
        while(adjL.size()<=adjMat.length) adjL.add(new ArrayList<>()); // O(n)

        for (int i = 0; i < adjMat.length; i++) { // n loops of O(n) => O(n^2)
            for (int j = i; j < adjMat[i].length; j++) { // n loops of O(1) => O(n)
                if(adjMat[i][j]==1){
                    adjL.get(i).add(j);
                    adjL.get(j).add(i);
                }
            }
        }
        return adjL;
    }

    private static int minSum(int[] ar1, int[] ar2){ // O(n)
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < ar1.length; i++) { // n loops of O(1)
            if(ar1[i]+ar2[i]<0){
                continue;
            }
            min = Math.min(min, ar1[i]+ar2[i]);
        }
        return min;
    }

    @Override
    public String toString() {

        return "Graph{" +
                "adjacencyMatrix=\n" + Helper.matrix2DString(adjacencyMatrix) +
                ", distanceMatrix=\n" + Helper.matrix2DString(distanceMatrix) +
                "\n, list=\n" + Helper.adjacencyListString(adjacencyList) +
                '}';
    }
}
