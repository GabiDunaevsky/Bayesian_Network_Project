import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is the algorithem to find if two nodes are independent with given nodes values.
 */

public class BeysBallAlgo {
    private final ArrayList<BeysianNode> nodes;
    private final BeysianNode nodeForCheck1,nodeForCheck2;
    ArrayList<BeysianNode> given;
    private final HashMap<String,Boolean> checkedAsChilde= new HashMap<>();
    private final HashMap<String,Boolean> checkedAsParent= new HashMap<>();
    private boolean is_independent = true;

    /**
     * A regular constructor.
     * @param nodes Array list of nodes (The beysian network)
     * @param nodesForCheck1 The first node to check independent.
     * @param nodesForCheck2 The second node to check independent.
     * @param given An Arraylist of given nodes.
     */
    public BeysBallAlgo (ArrayList<BeysianNode> nodes ,BeysianNode nodesForCheck1,BeysianNode nodesForCheck2,ArrayList<BeysianNode> given){
        this.nodes = nodes;
        this.given = given;
        this.nodeForCheck1 = nodesForCheck1;
        this.nodeForCheck2 = nodesForCheck2;
        for(BeysianNode key:nodes ){
            this.checkedAsChilde.put(key.getV().getName(),false);
            this.checkedAsParent.put(key.getV().getName(),false);
        }

    }
    /**
     * A recursion function to Check nodes As children.
     * @param currNode node that we are starting check from.
     * @param nodeToGet node that we are trying to get to.
     */
    public void DFSCheckForKids(BeysianNode currNode, BeysianNode nodeToGet)
    {
        if(!this.is_independent) {
            return;
        }
        if(!(checkedAsChilde.containsValue(false))) {
            return;
        }
        if(currNode.getV().getName().equals(nodeToGet.getV().getName())){
            this.is_independent = false;
        }
        for (BeysianNode beysianNode : given) {
            if (currNode.getV().getName().equals(beysianNode.getV().getName())  && !checkedAsChilde.get(currNode.getV().getName())) {
                DFSCheckForParents(currNode, nodeToGet);
                return;
            }
        }
        for (BeysianNode child: currNode.getChild()) {
            checkedAsChilde.replace(currNode.getV().getName(),true);
            DFSCheckForKids(child, nodeToGet);
        }
    }
    /**
     * A recursion function to Check nodes As parents.
     * @param currNode node that we are starting to check from.
     * @param nodeToGet node that we are trying to get to.
     */
    public void DFSCheckForParents(BeysianNode currNode,BeysianNode nodeToGet)
    {
        if(!this.is_independent) return;
        if(!(checkedAsParent.values().contains(false))) {
            return;
        }
        if(currNode.getV().getName().equals(nodeToGet.getV().getName())){
            this.is_independent = false;
        }
        for (BeysianNode parent: currNode.getParent())
        {
            for (BeysianNode beysianNode : given) {
                if (parent.getV().getName().equals(beysianNode.getV().getName())) {
                    return;
                }
            }
            if(!checkedAsParent.get(currNode.getV().getName())){
                checkedAsChilde.replace(currNode.getV().getName(),true);
                DFSCheckForParents(parent, nodeToGet);
                DFSCheckForKids(parent, nodeToGet);
            }
        }
    }

    /**
     * The dfs method that we are checking the nodes with.
     * @return if the nodes Are independent.
     */
    boolean DFS()
    {
        for(int i = 0 ; i <given.size();i++){
            if(given.get(i).getV().getName().equals(nodeForCheck1.getV().getName()) || given.get(i).getV().getName().equals(nodeForCheck2.getV().getName())){
                return true;
            }
        }
        DFSCheckForKids(nodeForCheck1,nodeForCheck2);
        DFSCheckForParents(nodeForCheck1,nodeForCheck2);
        if(this.is_independent){
            return true;
        }
        return false;
    }
}
