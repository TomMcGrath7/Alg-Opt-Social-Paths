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

    public BFSNode(BFSNode parent, int[] state, int mover){
        if(state==null){
            return;
        }
        this.parent = parent;
        this.children = new ArrayList<>();
        this.mover = mover;
        this.state = Arrays.copyOf(state, state.length);
        if(parent==null){
            this.depth = 0;
            this.turn = 1;
            moved = new boolean[state.length];
        }else{
            this.depth = parent.depth+1;   //TODO maybe add to handleTurn()?
            handleTurn();
        }
    }

    private void handleTurn(){
        if(parent.moved[mover]){
            moved = new boolean[parent.moved.length];
            turn = parent.turn+1;
        }else{
            this.moved = Arrays.copyOf(parent.moved, parent.moved.length);
            turn = parent.turn;
        }
        moved[mover] = true;
    }

    public void changeParent(BFSNode parent) {
        this.parent = parent;
        for (int i = 0; i < parent.state.length; i++) {
            if(parent.state[i]!=state[i]){
                mover = i;
                break;
            }
        }

        handleTurn();
        for (BFSNode child : children) {
            child.changeParent(this);
        }
    }

    public String deepString(){
        return parent==null? this.toString() : parent.deepString() +" => " + this;
    }

    @Override
    public String toString() {
        return "s "+Arrays.toString(state) +" m "+Arrays.toString(moved)+"("+mover+")"+" t ("+turn+") d ("+depth+")";
    }

}
