import java.util.Arrays;

public class Node1D extends Node{

    public int mover;
    public boolean[] moved;
    public int turn;
    public Node1D(int[] positions, Node parent, boolean visited, int mover) {
        super(positions, parent, visited);
        this.mover = mover;
        if(parent!=null){
            Node1D p = (Node1D)parent;
            turn = p.turn;
            moved = new boolean[p.moved.length];
            if(p.moved[mover]){
                turn += 1;
            }
            else{
                System.arraycopy(p.moved, 0, moved, 0, p.moved.length);
            }
            moved[mover] = true;

            boolean done = true;
            for (boolean b : moved) {
                done &= b;
            }
            if(done){
                moved = new boolean[moved.length];
                turn += 1;
            }
        }
        else{
            moved = new boolean[positions.length];
            turn = 0;
        }
    }

    @Override
    public String toString() {
        return parent+" d|"+depth+"|-t("+turn+")-p"+Arrays.toString(positions)+"-m("+mover+")-"+Arrays.toString(moved);
    }
}
