import java.util.Arrays;

/**
 * Extension of Node: Also keeps track of turns. A turn ends when:
 * - Everyone moved once
 * - Someone moved a second time
 */
public class Node1D extends Node{
    public boolean[] moved;
    public int mover;
    public int turn;
    public boolean complete;

    public Node1D(int[] positions, Node parent, boolean visited, int mover) { // O(p)
        super(positions, parent, visited); // O(1)

        this.mover = mover;
        if(parent!=null){
            Node1D p = (Node1D)parent; // O(1)
            initParent(p);
        }
        else{
            moved = new boolean[positions.length]; // O(p)
            turn = 0;
        }
    }

    public void initParent(Node1D p){
        turn = p.turn;
        moved = new boolean[p.moved.length]; // O(p)
        if(p.moved[mover]){
            turn += 1;
        }
        else{
            System.arraycopy(p.moved, 0, moved, 0, p.moved.length);// O(p)
        }
        moved[mover] = true;

        complete = true;
        for (boolean b : moved) { // p loops of O(1) => O(p)
            complete &= b; // O(1)
        }
    }

    @Override
    public void changeParent(Node parent) {
        super.changeParent(parent);

        int oldMover=mover;
        for (int i = 0; i < parent.positions.length; i++) {
            if(parent.positions[i]!=positions[i]){
                mover = i;
                break;
            }
        }

        initParent((Node1D) parent);
        if(mover==oldMover){
            return;
        }
        for (Node child : children) {
            child.changeParent(this);
        }
    }

    @Override
    public String toString() {
        return super.toString()+"|-t("+turn+")-p"+Arrays.toString(moved)+"("+mover+")";
    }
}
