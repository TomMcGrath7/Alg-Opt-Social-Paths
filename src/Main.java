import java.util.List;

/**
 * Notation: n = |V|, m = |E|, p = # of players <br>
 * State: array representing position of players <br>
 * Turn: a complete multidimensional movement <br>
 */
public class Main {

    public static void main(String[] args) {
        /*
        Directory with all .in and .out files
         */
        String directory = "src/testcases";

        /*
        To run one problem, 2 options:
        1. Set file value to problem you want to run
        2. Change input to the input string of the problem. However, showGivenSolution will not work properly.
         */
        String file = "grid10-3.in";
        String input = Helper.read(directory+"/"+file);

        BFSNode[] sol = run(input, true);
        showGivenSolution(sol[1], Helper.read(directory+"/"+file.split("\\.", 2)[0] + ".out"));

        /*
        To run all problems in a directory. Filters out only .in files to run BFS on.
        If showGivenSolution is true, the .out files need to be in the same directory
        */
        //runOnAllInDirectory(directory, true, true);
    }

    /**
     * Runs the BFS search given the input string of the problem
     * @param input input string of problem
     * @param verbose if true, prints: the solution found, the time taken and if it is correct
     * @return root node of BFS tree and solution node found (Is empty solution node if no solution found)
     * @implNote O(n^(1+p)) => O(n^3) for two players
     */
    public static BFSNode[] run(String input, boolean verbose) {

        long s = System.currentTimeMillis();

        // Convert input string to instance of problem
        Instance instance = new Instance(input); // O(n^3) SAD :(

        BFSNode[] solutionFound = BFS.search(instance); // O(n^3) for 2 players [For p players: O(n^(1+p))]

        if(verbose){

            Solution sol = new Solution(solutionFound[1]); // O(T)

            System.out.println(sol);
            System.out.println((System.currentTimeMillis()-s)/1000.0+" seconds");
            int created = solutionFound[0].size();
            System.out.println("Generated "+created+"/"+Math.pow(instance.graph.V, instance.p)+" nodes = "+(created/Math.pow(instance.graph.V, instance.p)));
            System.out.println("Correct? "+sol.correct(instance, false));
        }
        return solutionFound;
    }

    public static void runOnAllInDirectory(String directory, boolean verbose, boolean showGivenSolution) {
        List<String> files = Helper.listFilesUsingJavaIO(directory, "in");

        long all = System.currentTimeMillis();
        for (String file : files) {
            if(verbose){
                System.out.println(file);
            }
            BFSNode[] sol = run(Helper.read(directory+"/"+file), true);
            if(showGivenSolution) {
                showGivenSolution(sol[1], Helper.read(directory + "/" + file.split("\\.", 2)[0] + ".out"));
            }
        }

        System.out.println(((System.currentTimeMillis() - all)/1000.0)+" total seconds.");
    }
    public static void showGivenSolution(BFSNode solutionFound, String givenSol){
        Solution sol = new Solution(solutionFound);
        Solution givenSolObj = new Solution(givenSol);

        System.out.println("Given solution:");
        System.out.println(givenSol);
        System.out.println("Same length? "+(givenSolObj.K==sol.K));
        System.out.println();
    }
}