import java.util.*;

/**
 * A class for the Variable Elimination algorithm.
 */
public class VariableElimination {
    private final ArrayList<BeysianNode> nodes;
    private final BeysianNode query;
    private final String queryVal;
    private final HashMap<String,String> evidenceValues;
    private final String[] eliminationOrder;
    private final ArrayList<Cpt> cptsNeeded = new ArrayList<>();
    private final ArrayList<BeysianNode> nodesNeeded = new ArrayList<>();
    private final ArrayList<BeysianNode> given;
    private int numOfMult = 0;
    private int numOfAdd = 0;

    /**
     * A regular constructor.
     * @param nodes The beysian network
     * @param evidenceValues A hushMap representing the Evidence values.
     * @param query The query node.
     * @param given The evidence values.
     * @param eliminationOrder The elimination order.
     * @param queryVal The query vakue.
     */
    public VariableElimination(ArrayList<BeysianNode> nodes,HashMap<String,String> evidenceValues,BeysianNode query
    ,ArrayList<BeysianNode> given, String[] eliminationOrder,String queryVal){
        this.nodes = nodes;
        this.query = query;
        this.evidenceValues = evidenceValues;
        this.eliminationOrder = eliminationOrder;
        this.given = given;
        this.queryVal = queryVal;
    }

    /**
     * The run function to the algorithm.
     * @return the probability for the query given the evidence.
     */
    public double run(){
        double ans;
        checkForAncestors();
        for (Cpt cpt : this.cptsNeeded) {
            boolean containsAllEvidenceAndQuery = containsAllEvidence(cpt);
            if (cpt.getListVar().size() == this.given.size() + 1 && containsAllEvidenceAndQuery) {
                ans = checkingIfThereImmidiatAns(cpt);
                return ans;
            }
        }
        prepareFactors();
        ans = creatingCptsToJoin();
        return ans;
    }
    /**
     * Checking for the ancestors of the query or the or one of the evidence.
     */
    public void checkForAncestors(){
        for (BeysianNode node : this.nodes) {
            if (node.getV().getName().equals(this.query.getV().getName())) {
                addAncestors(node);
            } else {
                for (Map.Entry<String, String> entry : this.evidenceValues.entrySet()) {
                    if (entry.getKey().equals(node.getV().getName())) {
                        addAncestors(node);
                    }
                }
            }
        }
        removeAllInd();
    }

    /**
     * A function that adding all the ancestors nodes to the relevant nodes.
     * @param node The node to add.
     */
    private void addAncestors(BeysianNode node){
        Queue<BeysianNode> queue = new LinkedList<>();
        queue.add(node);
        while(!(queue.isEmpty())){
            BeysianNode tmp = queue.peek();
            queue.remove();
            boolean alreadyAdded = false;
            for (BeysianNode beysianNode : this.nodesNeeded) {
                if (tmp.getV().getName().equals(beysianNode.getV().getName())) {
                    alreadyAdded = true;
                    break;
                }
            }
            if(!alreadyAdded){
                this.nodesNeeded.add(tmp);
            }
            queue.addAll(tmp.getParent());
        }
    }

    /**
     * After finding the ancestors removing all the independent nodes.
     */
    public void removeAllInd()
    {
        for(int i = 0 ; i < this.nodesNeeded.size();i++){
            String nodeName = this.nodesNeeded.get(i).getV().getName();
            boolean isInd;
            boolean isEvidence = false;
            isInd = checkingForInd(this.nodesNeeded.get(i),this.query,this.given);
            for (BeysianNode beysianNode : this.given) {
                if (beysianNode.getV().getName().equals(nodeName)) {
                    isEvidence = true;
                    break;
                }
            }
            if(isInd && !(nodeName.equals(this.query.getV().getName())) && !isEvidence){
                System.out.println(nodeName);
                this.nodesNeeded.remove(nodesNeeded.get(i));
                i--;
            }
        }
        for (BeysianNode beysianNode : this.nodesNeeded) {
            Cpt cptTmp = new Cpt(beysianNode.getTable());
            this.cptsNeeded.add(cptTmp);
        }
    }

    /**
     * While checking in the run function if we can get The answer immediately, this function is checking if all
     * the evidence and query are in the cpt.
     * @param cpt the Cpt that we are checking on.
     * @return if the query and evidence are there.
     */
    private boolean containsAllEvidence(Cpt cpt) {
        boolean contains = true;
        boolean containsQuery = false;
        for (int i = 0 ; i < cpt.getListVar().size();i++){
            if (cpt.getListVar().get(i).getName().equals(this.query.getV().getName())) {
                containsQuery = true;
                break;
            }
        }
        if (!containsQuery){
            contains = false;
        }
        for (BeysianNode beysianNode : this.given) {
            boolean isThere = false;
            for (int j = 0; j < cpt.getListVar().size(); j++) {
                if (cpt.getListVar().get(j).getName().equals(beysianNode.getV().getName())) {
                    isThere = true;
                    break;
                }
            }
            if (!isThere) {
                contains = false;
            }
        }
        return contains;
    }

    /**
     * A function that checking if the  Two nodes are independent(using beyseball algorithm).
     * @param firstNode The first node to check.
     * @param SecondNode The second NODE TO CHECK
     * @param given the given nodes(evidence).
     * @return boolean ans if they are independent.
     */
    public boolean checkingForInd(BeysianNode firstNode,BeysianNode SecondNode,ArrayList<BeysianNode> given){
        boolean ans ;
        BeysBallAlgo beyesB = new BeysBallAlgo(nodes,firstNode,SecondNode,given);
        ans = beyesB.DFS();
        return ans;
    }

    /**
     * If there is an immediate answer (we are checking it in the run function) this function
     * is returning the answer,
     * @param cpt The cpt that we are checking on.
     * @return the answer.
     */

    private double checkingIfThereImmidiatAns(Cpt cpt){
        double ans = -1;
        int[] findAns = new int[cpt.getvalVesInCpt().size()];
        int quearyLoc = -1;
        Arrays.fill(findAns, 1);
        for (int i = 0; i < cpt.getListVar().size();i++){
            if (cpt.getListVar().get(i).getName().equals(this.query.getV().getName())){
                quearyLoc = i;
            }
        }

        for (int i = 0; i < cpt.getListVar().size();i++){
            for (int j =0; j < cpt.getvalVesInCpt().size();j++){
                String[] values = cpt.getvalVesInCpt().get(j).split(",");
                if (i != quearyLoc){
                    if (!(values[i].equals(this.evidenceValues.get(cpt.getListVar().get(i).getName())))
                            && findAns[j] != -1){
                        findAns[j] = -1;
                    }
                }else{
                    if (!(values[i].equals(this.queryVal))){
                        findAns[j] = -1;
                    }
                }
            }
        }
        for (int i = 0; i < findAns.length;i++){
            if (findAns[i] == 1){
                ans = cpt.getProbaval().get(i);
            }
        }
        return ans;
    }

    /**
     * Preparing the final factors by using the algorithm rules.
     */
    public void prepareFactors() {
        System.out.println(this.cptsNeeded.size());
        for (Map.Entry<String, String> entry : evidenceValues.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            for (int i = 0 ; i < this.cptsNeeded.size();i++){
                for (int j = 0; j < this.cptsNeeded.get(i).getListVar().size();j++){
                    if(this.cptsNeeded.get(i).getListVar().get(j).getName().equals(key)){
                            Cpt tmp = creatAnewFactorInstead(this.cptsNeeded.get(i),value,j);
                            this.cptsNeeded.set(i,tmp);

                    }
                }
                if(this.cptsNeeded.get(i).getvalVesInCpt().size() == 1){
                    this.cptsNeeded.remove(this.cptsNeeded.get(i));
                    i--;
                }
            }
        }
        removingUnNeededCptsBeforeJoin();
    }

    /**
     * A function that the preparing function is using to creat the new Factor(table) by the rules of the algorithm
     * @param cpt The cpt to change.
     * @param value the value.
     * @param loc the location of the value.
     * @return The new Cpt.
     */
    private Cpt creatAnewFactorInstead(Cpt cpt,String value,int loc) {
        for(int i = 0 ; i < cpt.getvalVesInCpt().size();i++){
            String[] tmpCptVals = cpt.getvalVesInCpt().get(i).split(",");
            if(!(value.equals(tmpCptVals[loc]))){
                cpt.getvalVesInCpt().remove(i);
                cpt.getProbaval().remove(i);
                i--;
           }else{
                StringBuilder newValues = new StringBuilder();
                for(int j = 0 ; j < tmpCptVals.length;j++){
                    if( j != loc){
                        if (!newValues.toString().equals("")) {
                            newValues.append(",").append(tmpCptVals[j]);
                        }else{
                            newValues.append(tmpCptVals[j]);
                        }
                    }
                }
                cpt.getvalVesInCpt().set(i, newValues.toString());
            }
        }
        cpt.getListVar().remove(loc);
        return cpt;
    }

    /**
     * Removing the unneeded cpts after "cleaning" the unneeded cpts checking if there are more un needed.
     */

    public void removingUnNeededCptsBeforeJoin() {
        for (int i = 0; i < this.cptsNeeded.size(); i++) {
            boolean relevant = false;
            for (int j = 0; j < this.cptsNeeded.get(i).getListVar().size(); j++) {
                for (int k = 0; k < this.nodesNeeded.size(); k++) {
                    if (this.cptsNeeded.get(i).getListVar().get(j).getName().equals(this.nodesNeeded.get(k).getV().getName())) {
                        relevant = true;
                        break;
                    }
                }
                if (!relevant) {
                    this.cptsNeeded.remove(i);
                    i--;
                }
            }
        }
    }

    /**
     * Creatin the cpt to join and after that to eliminate and returning the final answer.
     * @return the final answer of the algorithm.
     */
    public double  creatingCptsToJoin(){
        double ans = -0.5;
        for (String s : this.eliminationOrder) {
            int[] removeIndexes = new int[this.cptsNeeded.size()];
            Arrays.fill(removeIndexes, 1);
            ArrayList<Cpt> cptsWithNodeToEliminate = new ArrayList<>();
            for (int j = 0; j < this.cptsNeeded.size(); j++) {
                for (int k = 0; k < this.cptsNeeded.get(j).getListVar().size(); k++) {
                    if (this.cptsNeeded.get(j).getListVar().get(k).getName().equals(s)) {
                        cptsWithNodeToEliminate.add(this.cptsNeeded.get(j));
                        removeIndexes[j] = 0;
                        this.cptsNeeded.remove(j);
                        j--;
                        break;
                    }
                }
            }
            Collections.sort(cptsWithNodeToEliminate);
            if (cptsWithNodeToEliminate.size() > 1) {
                join(cptsWithNodeToEliminate, s);
            } else if (cptsWithNodeToEliminate.size() == 1 && cptsWithNodeToEliminate.get(0).getListVar().size() > 1) {
                elimination(cptsWithNodeToEliminate.get(0), s);
            }
        }
        for (Cpt cpt : this.cptsNeeded) {
            System.out.println(cpt);
        }
        if (this.cptsNeeded.size() > 1){
            joinToQueris();
        }
        System.out.println(cptsNeeded.get(0));
            double totalSum = 0;
            for (int i = 0 ; i <this.cptsNeeded.get(0).getProbaval().size();i++){
                totalSum += this.cptsNeeded.get(0).getProbaval().get(i);
                this.numOfAdd++;
            }
            numOfAdd--;
            for (int j = 0 ; j < this.cptsNeeded.get(0).getvalVesInCpt().size();j++){
                if (this.cptsNeeded.get(0).getvalVesInCpt().get(j).equals(this.queryVal)){
                   ans =  this.cptsNeeded.get(0).getProbaval().get(j) /totalSum;
                }
            }
        return ans;
    }

    /**
     * The join function that joining the Factors with the variable taht we need to eliminate.
     * @param cptsWithNodeToEliminate An Arraylist with node to eliminate.
     * @param varNameToEliminate The name of the var that we want to eliminate.
     */
    public void  join(ArrayList<Cpt> cptsWithNodeToEliminate,String varNameToEliminate){
        while (cptsWithNodeToEliminate.size() > 1){
            ArrayList<Double> proba = new ArrayList<>();
            ArrayList<Var> newVars = new ArrayList<>() ;
            addingVarsToNewCpt(newVars,cptsWithNodeToEliminate.get(0).getListVar(),varNameToEliminate);
            addingVarsToNewCpt(newVars,cptsWithNodeToEliminate.get(1).getListVar(),varNameToEliminate);
            for (int i = 0; i < cptsWithNodeToEliminate.get(0).getListVar().size();i++){
                if(cptsWithNodeToEliminate.get(0).getListVar().get(i).getName().equals(varNameToEliminate)){
                    newVars.add(cptsWithNodeToEliminate.get(0).getListVar().get(i));
                }
            }
            Cpt tmp = new Cpt(proba,newVars);
            tmp.creatValForCpt();
            for (int j = 0 ; j < tmp.getvalVesInCpt().size();j++){
                HashMap<String,String> valuesOfVars = new HashMap<>();
                String[] valuesNewFactor = tmp.getvalVesInCpt().get(j).split(",");
                for(int k = 0 ; k < tmp.getListVar().size();k++){
                    valuesOfVars.put(tmp.getListVar().get(k).getName(),valuesNewFactor[k]);
                }
                double firstProba = findingTheCorrectValueToMultiplie(cptsWithNodeToEliminate.get(0),valuesOfVars);
                double secondProba = findingTheCorrectValueToMultiplie(cptsWithNodeToEliminate.get(1),valuesOfVars);
                double commonProba = firstProba * secondProba;
                this.numOfMult++;
                tmp.getProbaval().add(commonProba);
            }
                cptsWithNodeToEliminate.remove(1);
                cptsWithNodeToEliminate.remove(0);
                cptsWithNodeToEliminate.add(tmp);
                Collections.sort(cptsWithNodeToEliminate);

        }
         elimination(cptsWithNodeToEliminate.get(0),varNameToEliminate);
    }

    /**
     * A helper function that the join is usin to find the correct value to multiply while using join.
     * @param cpt the cpt to search on.
     * @param valuesOfVars HashMap of of the variables.
     * @return the answer of the multiplies.
     */
    private double findingTheCorrectValueToMultiplie (Cpt cpt , HashMap<String,String> valuesOfVars){
        int[] toFindThePlace = new int[cpt.getvalVesInCpt().size()];
        Arrays.fill(toFindThePlace, 1);
        for (int h = 0 ; h < cpt.getListVar().size();h++){
            String varTmpName = cpt.getListVar().get(h).getName();
            for (int s = 0 ; s < cpt.getvalVesInCpt().size();s++ ){
                String[] findingRow = cpt.getvalVesInCpt().get(s).split(",");
                if(!(findingRow[h].equals(valuesOfVars.get(varTmpName))) && toFindThePlace[s] != -1){
                    toFindThePlace[s] = -1;
                }
            }
        }
        int loc = -1;
        for (int k = 0 ; k < toFindThePlace.length;k++){
            if (toFindThePlace[k] == 1){
                loc = k;
            }
        }
        double ans = cpt.getProbaval().get(loc);
        return ans;
    }

    /**
     * While creating the new Cpt( the multiplication of the tables) ading the correct vars to the new cpt.
     * @param newVars Arraylist of the new variables.
     * @param toAdd Arraylist of variables that we want to add.
     * @param varNameToEliminate The name of the variable that we want to eliminate.
     */
    private void addingVarsToNewCpt(ArrayList<Var> newVars,ArrayList<Var> toAdd,String varNameToEliminate){
        for (Var var : toAdd) {
            boolean varAlreadyThere = false;
            for (Var newVar : newVars) {
                if (var.getName().equals(newVar.getName())) {
                    varAlreadyThere = true;
                    break;
                }
            }
            if (!(var.getName().equals(varNameToEliminate)) && !varAlreadyThere) {
                newVars.add(var);
            }
        }
    }
    /**
     * A function in case that we are left  with a few factors that only has the query, this function is
     * joining them.
     */
    private void joinToQueris() {
        for (int i = 0 ; i < this.cptsNeeded.get(0).getvalVesInCpt().size(); i++){
            String value = this.cptsNeeded.get(0).getvalVesInCpt().get(i);
            double ans =  this.cptsNeeded.get(0).getProbaval().get(i);
            for (int j = 1; j < this.cptsNeeded.size(); j++){
                for (int k = 0 ; k < this.cptsNeeded.get(j).getvalVesInCpt().size();k++){
                    if (this.cptsNeeded.get(j).getvalVesInCpt().get(k).equals(value)){
                        ans *= this.cptsNeeded.get(j).getProbaval().get(k);
                        this.numOfMult++;
                    }
                }
            }
            this.cptsNeeded.get(0).getProbaval().set(i,ans);
        }
        int len = this.cptsNeeded.size()-1;
        while(this.cptsNeeded.size() > 1){
            this.cptsNeeded.remove(len);
            len--;
        }
    }
    /**
     * This function is eliminating the variable that we want to eliminate after using the join function on all the
     * C[rs that the function preforms.
     * @param cpt The cpt that we are making the elimination on.
     * @param nameVarToEliminate The name of the var to eliminate.
     */
    private void elimination(Cpt cpt,String nameVarToEliminate) {
        int loc = -1;
        ArrayList<Double> probavalAfterElimin = new ArrayList<>();
        ArrayList <Var> listVarAfterElimin = new ArrayList<>();
        for (int i = 0 ; i < cpt.getListVar().size();i++){
            if (cpt.getListVar().get(i).getName().equals(nameVarToEliminate)){
                loc = i;
            }else{
                listVarAfterElimin.add(cpt.getListVar().get(i));
            }
        }
        for (int j = 0 ; j < cpt.getvalVesInCpt().size(); j++){
            String[] tmp = cpt.getvalVesInCpt().get(j).split(",");
            double probaRes = cpt.getProbaval().get(j);
            for (int k = j+1 ; k < cpt.getvalVesInCpt().size();k++){
                boolean equals = true;
                String[] tmp2 = cpt.getvalVesInCpt().get(k).split(",");
                for(int m = 0 ; m < tmp2.length; m++){
                    if (m != loc && !(tmp2[m].equals(tmp[m]))) {
                        equals = false;
                        break;
                    }
                }
                if (equals){
                    probaRes += cpt.getProbaval().get(k);
                    numOfAdd++;
                    cpt.getvalVesInCpt().remove(k);
                    cpt.getProbaval().remove(k);
                    k--;
                }
            }
            probavalAfterElimin.add(probaRes);
        }
        Cpt c = new Cpt(probavalAfterElimin,listVarAfterElimin);
        if (c.getListVar().size() > 0){
            c.creatValForCpt();
            this.cptsNeeded.add(c);
        }
    }

    /**
     * Getting the numbers that we multiplied.
     */
    public int getNumToMult(){
        return this.numOfMult;
    }
    /**
     * Getting the numbers that we added.
     */
    public int getNumOfAdd(){
        return this.numOfAdd;
    }
}
