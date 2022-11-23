import java.util.Arrays;

/** Represents an instance of the problem:
 * -graph: graph to traverse
 * -starts: starting positions
 * -ends: target positions
 * -D: min distance between individuals
 * -T: time limit
 * <p>
 * -1 to every vertex value from input file because they start at 1 and vertex value is used for indexing
 * => !!! output will be offset by -1 !!!
 */
public class Instance {
    public int D;
    public int T;
    public int[] starts;
    public int[] ends;
    public Graph graph;

    /**
     * @param input as defined in assignment:
     *              <p>line 1: N M T D - N = # of vertices, M = # edges, T = time limit, D = distance limit
     *              <p>line 2: s1 e1 s2 e2 - start and end for each player
     *              <p>other lines: x y - representing edge, where 0 < x, y <= N integer value representing vertices
     */
    public Instance(String input){ // O(n^3+m)

        String[] lines = input.split("\n"); // 2+m lines of limited length => O(m)

        String[] params = lines[0].split(" "); // |V|, |E|, T and D
        this.T = Integer.parseInt(params[2]);
        this.D = Integer.parseInt(params[3]);

        String[] players = lines[1].split(" "); // s1, e1, s2, e2
        this.starts = new int[players.length/2]; // O(p)
        this.ends = new int[starts.length]; // O(p)

        for (int i = 0; i < starts.length; i++) { // p loops of O(1) => O(p)
            starts[i] = Integer.parseInt(players[2*i]) - 1;
            ends[i] = Integer.parseInt(players[(2*i)+1]) - 1;
        }

        this.graph = new Graph(Integer.parseInt(params[0]), Arrays.copyOfRange(lines, 2, lines.length)); // O(m) copy + O(n^3) distance matrix => O(n^3+m)

    }

    @Override
    public String toString() {
        return "Game{" +
                "D=" + D +
                ", T=" + T +
                ", starts=" + Arrays.toString(starts) +
                ", ends=" + Arrays.toString(ends) +
                ", \ngraph=" + graph +
                '}';
    }
}
