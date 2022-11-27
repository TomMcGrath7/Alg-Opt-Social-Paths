import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BFSNode {

    public int depth;
    public BFSNode parent;
    public List<BFSNode> children;

    // Position of players
    public int[] state;

    // Needed fort 1D
    public int turn;
    public boolean[] moved;
    public int mover;

    public boolean visited;

    /**
     * O(p)
     * @param parent parent of node
     * @param state player positions at node
     * @param mover player who moved to reach this state given parent state
     */
    public BFSNode(BFSNode parent, int[] state, int mover){
        if(state==null){
            return;
        }
        this.parent = parent;
        this.children = new ArrayList<>();
        this.mover = mover;
        this.state = Arrays.copyOf(state, state.length); // O(p)
        if(parent==null){
            this.depth = 0;
            this.turn = 1;
            moved = new boolean[state.length];
        }else{
            handleTurn();
        }
    }

    /**
     * Based on parent and who moved to reach this state, update the moved array and turn of node <br>
     * O(p)
     */
    private void handleTurn(){
        this.depth = parent.depth+1;
        if(parent.moved[mover]){
            moved = new boolean[parent.moved.length];
            turn = parent.turn+1;
        }else{
            this.moved = Arrays.copyOf(parent.moved, parent.moved.length); // O(p)
            turn = parent.turn;
        }
        moved[mover] = true;
    }

    /**
     * Update depth, turn and mover and moved array based on new parent
     * @param parent new parent
     * @implNote O(p)
     */
    public void changeParent(BFSNode parent) {
        this.parent = parent;
        for (int i = 0; i < parent.state.length; i++) { // O(p)
            if(parent.state[i]!=state[i]){
                mover = i;
                break;
            }
        }
        handleTurn(); // O(p)
    }

    public int size(){
        int s = 1;
        for (BFSNode child : children) {
            s+= child.size();
        }
        return s;
    }

    public String deepString(){
        return parent==null? this.toString() : parent.deepString() +" => " + this;
    }

    @Override
    public String toString() {
        return "s "+Arrays.toString(state) +" m "+Arrays.toString(moved)+"("+mover+")"+" t ("+turn+") d ("+depth+")";
    }

}
