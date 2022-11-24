import java.util.List;
import java.util.Objects;

/**
 * p = # of players
 * n = # of nodes
 * m = # of edges
 * <p>
 * Worst case:
 * - All nodes are connected => each node has a degree of n => n+1 options for each individual (asymptotically n)
 * - D = 0 => all connections are a valid step
 * <p>
 * terms in ONLY p can also be ignored asymptotically because it will (always?) be a lot smaller than n
 */
public class Main {

    /**
     *verbose int: 0=no print, 1=print results, 2=print results if path length different or solution found is wrong
     */
    public static void main(String[] args) {
        String directory = "src/testcases";

        run(directory+"/grid10-3-randomized.in", 1, true);
        //runOnAll(directory, 1, true);

        //System.out.println("Tree created:");
        //System.out.println(BFS.root.treeString());
    }

    public static Solution run(String path, int verbose, boolean move1D) { // O(n^3+m) + O(n^(1+p)p^3)
        Instance instance = new Instance(Helper.read(path)); // O(n^3+m)

        Solution solutionFound = BFS.search(instance, move1D); // 1D: O(n^(1+p)p^3). not 1D: O(n^(2+p)p^2)

        // Irrelevant, just for printing
        if(verbose!=0) {
            Solution sol = new Solution(Helper.read(path.split("\\.", 2)[0] + ".out"));
            if(verbose==1 || (verbose==2 && (sol.K!= solutionFound.K || sol.correct(instance, false)))){
                System.out.println(path.split("/",2)[1]);
                int created = BFS.root.size();
                int maxSize = (int) Math.pow(instance.graph.adjacencyMatrix.length,2);
                System.out.println(created+"/"+maxSize+"="+created/(double)maxSize+" nodes created");
                System.out.println("Solution found:");
                System.out.println(solutionFound);
                System.out.println("Correct? "+solutionFound.correct(instance, true));

                System.out.println("Given solution:");
                System.out.println(sol);
                System.out.println("Same? "+(sol.K==solutionFound.K));
            }
        }
        return solutionFound;
    }

    public static void runOnAll(String directory, int verbose, boolean move1D) {
        List<String> files = Helper.listFilesUsingJavaIO(directory, "in");

        long all = System.currentTimeMillis();
        for (String file : files) {
            run(directory+"/"+file, verbose, move1D);
        }

        System.out.println(((System.currentTimeMillis() - all)/1000.0)+" total seconds.");
    }
}