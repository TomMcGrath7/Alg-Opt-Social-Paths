import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner; // Import the Scanner class to read text files


public class Helper {

    public static String read(String filename){
        StringBuilder sb = new StringBuilder();
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                sb.append(data).append("\n");
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String adjacencyMatrixString(int[][] mat){
        int max = 0;
        for (int[] objects : mat) {
            for (int object : objects) {
                max = Math.max(max, Integer.toString(object).length());
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int[] objects : mat) {
            for (int object : objects) {
                int size = Integer.toString(object).length();
                sb.append(object);
                for (int i = 0; i < max-size; i++) {
                    sb.append(" ");
                }
                sb.append("|");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static String adjacencyListString(List<List<Integer>> a){
        StringBuilder sb = new StringBuilder();
        sb.append(0).append(": ").append(a.get(0));
        for (int i = 1; i < a.size(); i++) {
            sb.append("\n").append(i).append(": ").append(a.get(i));
        }
        return sb.toString();
    }

    public static int[][] distanceMatrix(int[][] adjacencyMatrix){
        int[][] distanceMatrix = new int[adjacencyMatrix.length][adjacencyMatrix.length];
        for (int i = 0; i < distanceMatrix.length; i++) {
            for (int j = 0; j < distanceMatrix[i].length; j++) {
                if(i==j){
                    continue;
                }
                distanceMatrix[i][j] = adjacencyMatrix[i][j]==0? Integer.MAX_VALUE : adjacencyMatrix[i][j];
            }
        }
        for (int i = 0; i < distanceMatrix.length; i++) {

            for (int j = 0; j < distanceMatrix[i].length; j++) {
                distanceMatrix[i][j] = minSum(distanceMatrix[i], distanceMatrix[j]);
                distanceMatrix[j][i] = distanceMatrix[i][j];
            }
        }
        return distanceMatrix;
    }

    public static List<List<Integer>> toAdjacencyList(int[][] adjMat){
        List<List<Integer>> adjL = new ArrayList<>();
        while(adjL.size()<=adjMat.length) adjL.add(new ArrayList<>());

        for (int i = 0; i < adjMat.length; i++) {
            for (int j = i; j < adjMat[i].length; j++) {
                if(adjMat[i][j]==1){
                    adjL.get(i).add(j);
                    adjL.get(j).add(i);
                }
            }
        }
        return adjL;
    }

    private static int minSum(int[] ar1, int[] ar2){
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < ar1.length; i++) {
            if(ar1[i]+ar2[i]<0){
                continue;
            }
            min = Math.min(min, ar1[i]+ar2[i]);
        }
        return min;
    }

}
