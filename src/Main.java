import java.util.List;

/**
 * p = # of players
 * n = # of nodes
 * m = # of edges
 * <p>
 * Worst case:
 * - All nodes are connected => each node has a degree of n => n+1 options for each individual (asymptotically n)
 * - D = 0 => all connections are a valid step
 * <p>
 * terms in p can also be ignored asymptotically because it will (always?) be a lot smaller than n
 */
public class Main {

    public static void main(String[] args) {
        String directory = "src/testcases";

        /*
        first int: 0=no print, 1=print results, 2=print results if different
        2nd int: 0=no print, 1=print width of tree for each depth
        */

        Solution s = run(directory+"/grid25-2.in", new int[]{1, 0}, true);

        //Solution sol = new Solution(Helper.read(directory+"/grid50-2.out"));
        //System.out.println(s.paths.get(0));
        //System.out.println(sol.paths.get(0));

        //runOnAll(directory, new int[]{1, 0}, true);

        //System.out.println("Tree created:");
        //System.out.println(BFS.root.treeString());
    }

    public static Solution run(String path, int[] verbose, boolean move1D) {
        Instance instance = new Instance(Helper.read(path)); // O(n^3+m)

        Solution solutionFound = BFS.search(instance, verbose[1]==1, move1D);

        if(verbose[0]!=0) {
            Solution sol = new Solution(Helper.read(path.split("\\.", 2)[0] + ".out"));
            if(verbose[0]==1 || (verbose[0]==2 && sol.K!= solutionFound.K)){
                System.out.println(path.split("/",2)[1]);
                System.out.println(BFS.root.size()+"/"+(instance.graph.adjacencyMatrix.length^2)+" nodes created");
                System.out.println("Solution found:");
                System.out.println(solutionFound);
                System.out.println("Correct? "+solutionFound.correct(instance));

                System.out.println("Given solution:");
                System.out.println(sol);
            }
        }
        return solutionFound;
    }

    public static void runOnAll(String directory, int[] verbose, boolean move1D) {
        List<String> files = Helper.listFilesUsingJavaIO(directory, "in");

        long all = System.currentTimeMillis();
        for (String file : files) {
            run(directory+"/"+file, verbose, move1D);
        }

        System.out.println(((System.currentTimeMillis() - all)/1000.0)+" total seconds.");
    }
}