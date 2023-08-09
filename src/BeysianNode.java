import java.util.ArrayList;

/**
 * This class represents a single Node that holds Arraylist of children,Arraylist of parents, variable and cpt.
 */
public class BeysianNode {
    private final ArrayList<BeysianNode> children;
    private final ArrayList<BeysianNode> parents;
    private final Var v;
    private final Cpt table;

    /**
     * A default constructor.
     */
    public BeysianNode(){
        this.children = new ArrayList<>();
        this.parents = new ArrayList<>();
        this.v = null;
        this.table = null;
    }

    /**
     * A regular constructor.
     * @param children Arraylist of children.
     * @param parents Arraylist of parents.
     * @param v variable.
     * @param table Cpt.
     */
    public BeysianNode(ArrayList<BeysianNode> children,ArrayList<BeysianNode> parents,Var v,Cpt table){
        this.children = children;
        this.parents = parents;
        this.v = v;
        this.table = table;
    }

    /**
     * They Are all getters functions.
     */
    public ArrayList<BeysianNode> getChild() {
        return children;
    }
    public ArrayList<BeysianNode> getParent() {
        return parents;
    }
    public Cpt getTable() {
        return table;
    }
    public Var getV() {
        return v;
    }


}
