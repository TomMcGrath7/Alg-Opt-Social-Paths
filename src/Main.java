import java.util.List;

public class Main {

    public static void main(String[] args) {
        String directory = "src/testcases";

        // first bool: prints solutions, 2nd bool: prints size of tree by depth
        //Solution s = run(directory+"/case1.in", new boolean[]{true, false}, true);

        runOnAll(directory, new boolean[]{true, false}, true);

        //System.out.println("Tree created:");
        //System.out.println(BFS.root.treeString());
    }

    public static Solution run(String path, boolean[] verbose, boolean move1D) {
        Instance instance = new Instance(Helper.read(path));

        Solution solutionFound = BFS.search(instance, verbose[1], move1D);

        if(verbose[0]) {
            System.out.println(BFS.root.size()+" nodes created");
            System.out.println("Solution found:");
            System.out.println(solutionFound);
            System.out.println("Correct? "+solutionFound.correct(instance));

            Solution sol = new Solution(Helper.read(path.split("\\.", 2)[0] + ".out"));
            System.out.println("Given solution:");
            System.out.println(sol);
        }
        return solutionFound;
    }

    public static void runOnAll(String directory, boolean []verbose, boolean move1D) {
        List<String> files = Helper.listFilesUsingJavaIO(directory, "in");

        long all = System.currentTimeMillis();
        for (String file : files) {
            System.out.println(file);
            run(directory+"/"+file, verbose, move1D);
        }

        System.out.println(((System.currentTimeMillis() - all)/1000.0)+" total seconds.");
    }
}