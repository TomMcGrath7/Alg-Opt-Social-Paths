import java.util.Arrays;

/**
 * -1 to every vertex value from input file because they start at 1
 * => !!! output will be offset by -1 !!!
 */
public class Game {
    public int D;
    public int T;
    public int[] starts;
    public int[] ends;
    public Graph graph;

    public Game(String file){

        String s = Helper.read(file);
        String[] lines = s.split("\n");

        String[] params = lines[0].split(" "); // |V|, |E|, T and D
        this.T = Integer.parseInt(params[2]);
        this.D = Integer.parseInt(params[3]);

        String[] players = lines[1].split(" "); // s1, e1, s2, e2
        this.starts = new int[players.length/2];
        this.ends = new int[starts.length];

        for (int i = 0; i < starts.length; i++) {
            starts[i] = Integer.parseInt(players[2*i]) - 1;
            ends[i] = Integer.parseInt(players[(2*i)+1]) - 1;
        }

        this.graph = new Graph(Integer.parseInt(params[0]), Arrays.copyOfRange(lines, 2, lines.length));

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
