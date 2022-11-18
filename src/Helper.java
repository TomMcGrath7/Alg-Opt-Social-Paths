import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helper methods
 */
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
            System.out.println("File '" +filename+"' does not exist");
        }
        return sb.toString().trim();
    }

    public static String matrix2DString(int[][] mat){
        int max = 0;
        for (int[] objects : mat) {
            for (int object : objects) {
                max = Math.max(max, Integer.toString(object).length());
            }
        }
        StringBuilder sb = new StringBuilder();
        int counter = 1;
        for (int[] objects : mat) {
            sb.append(counter).append("-");
            counter++;
            for (int object : objects) {
                int size = Integer.toString(object).length();
                sb.append(object);
                sb.append(" ".repeat(Math.max(0, max - size)));
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

    public static List<String> listFilesUsingJavaIO(String dir, String extension) {
        return Stream.of(Objects.requireNonNull(new File(dir).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .filter(f -> f.endsWith(extension)).sorted().collect(Collectors.toList());
    }

}
