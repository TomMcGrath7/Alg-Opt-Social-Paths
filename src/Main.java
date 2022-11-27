import java.util.List;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {
        String directory = "src/testcases";

        //run(directory+"/grid10-9.in", 1);
        runOnAll(directory, true);
    }

    public static BFSNode[] run(String path, boolean verbose) { // O(n^3+m) + O(n^(1+p)p^3)

        long s = System.currentTimeMillis();

        Instance instance = new Instance(Helper.read(path)); // O(n^3+m)

        BFSNode[] solutionFound = BFS.search(instance); // 1D: O(n^(1+p)p^3). not 1D: O(n^(2+p)p^2)

        if(verbose){
            System.out.println(path);
            Solution sol = new Solution(solutionFound[1]);

            System.out.println(sol);
            System.out.println((System.currentTimeMillis()-s)/1000.0+" seconds");
            System.out.println("Correct? "+sol.correct(instance, false));

            String givenSol = Helper.read(path.split("\\.", 2)[0] + ".out");
            Solution givenSolObj = new Solution(givenSol);
            System.out.println(givenSol);
            System.out.println("Same length? "+(givenSolObj.K==sol.K));
            System.out.println();
        }
        return solutionFound;
    }

    public static void runOnAll(String directory, boolean verbose) {
        List<String> files = Helper.listFilesUsingJavaIO(directory, "in");

        long all = System.currentTimeMillis();
        for (String file : files) {
            run(directory+"/"+file, verbose);
        }

        System.out.println(((System.currentTimeMillis() - all)/1000.0)+" total seconds.");
    }
}