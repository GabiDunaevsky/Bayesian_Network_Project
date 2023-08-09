import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class to read and parse the outputFile.
 */
public class Parse {
    private final String file;

    /**
     * A regular constructor.
     * @param file The file to read from.
     */
    public Parse(String file){this.file = file;}

    /**
     * The parsing by readin line after line.
     * @throws IOException exception.
     */
        public void parsingFile() throws IOException {
            File file = new File(this.file);
            Scanner scan = new Scanner(file);
            String pattern = "P\\((.*)\\|(.*)\\)\\s(.*)";
            Pattern r = Pattern.compile(pattern);
            String xmlFileName = scan.nextLine();
            BaysianNetWork bNet = creatingTheBeysianNetWork(xmlFileName);
            while (scan.hasNext()) {
                String line = scan.nextLine();
                Matcher m = r.matcher(line);
                if (m.find()) {
                    ansToVariableElimination(m.group(1),m.group(2),m.group(3),bNet);
                }else{
                    ansToBeyesBall(line,bNet);
                }
            }
        }

    /**
     * Writing the answers to the lines that are requiring probabilities.
     * @param ans The probability answer to write.
     * @param add How many times we added.
     * @param mult How many times we multiplied.
     * @throws FileNotFoundException exception.
     */
    private void writeProbaAns(double ans , int add,int mult) throws FileNotFoundException {
        PrintStream printStream = new PrintStream(new FileOutputStream("output.txt",true));
        DecimalFormat df = new DecimalFormat("#.#####");
        df.format(ans);
        printStream.print(df.format(ans));
        printStream.print(","+add);
        printStream.println(","+mult);
        printStream.close();
    }

    /**
     * Getting the answer to the question lines by using the Variable elimination algorithm.
     * @param query The query name while reading from the line..
     * @param given The evidenc while reading from the line.
     * @param elimOrder The elimination order that we read from the line.
     * @param b The beysian net work
     * @throws FileNotFoundException exception.
     */
    private void ansToVariableElimination(String query, String given , String elimOrder ,BaysianNetWork b) throws FileNotFoundException {
        String[] eliminOrder = elimOrder.split("-");
        HashMap<String,String> evidenceAndVals = new HashMap<>();
        BeysianNode queryToElim = new BeysianNode();
        String[] dataAboutQuery = query.split("=");
        String queryName = dataAboutQuery[0];
        String queryVal = dataAboutQuery[1];
        for (int i = 0; i < b.getNodes().size();i++){
            if(b.getNodes().get(i).getV().getName().equals(queryName)){
                queryToElim = b.getNodes().get(i);
            }
        }
        ArrayList<String> givenNodes = new ArrayList<>();
        if(given.length() > 1){
            String[] separateGiven = given.split(",");
            for (String s : separateGiven) {
                String[] givenNames = s.split("=");
                givenNodes.add(givenNames[0]);
                evidenceAndVals.put(givenNames[0], givenNames[1]);
            }
        }
        ArrayList<BeysianNode> givenToElim = gettingGivenNodes(givenNodes,b.getNodes());
        VariableElimination elim = new VariableElimination(b.getNodes(),evidenceAndVals,queryToElim,givenToElim,eliminOrder,queryVal);
        double ans = elim.run();
        int add = elim.getNumOfAdd();
        int mult = elim.getNumToMult();
        writeProbaAns(ans,add,mult);
    }

    /**
     * A function that creats the beysian network.
     * @param xmlFileName the xmlFile.
     * @return The network.
     */
    private BaysianNetWork creatingTheBeysianNetWork(String xmlFileName){
            BaysianNetWork b = new BaysianNetWork();
            b = b.buildNetWork(xmlFileName);
            return b;
        }

    /**
     * Getting the ans to beyesball by using the beyesball algorithem
     * @param line The line that we read from the file.
     * @param b The netWork
     * @throws IOException exception.
     */
        public void ansToBeyesBall(String line,BaysianNetWork b) throws IOException {
            ArrayList<String> givenNodes = new ArrayList<>();
            String[] separateQueryandGiven  = line.split("\\|");
            BeysianNode[] queryBeysianNodes = gettingQueryNodes(separateQueryandGiven[0].split("-"),b.getNodes());
            if(separateQueryandGiven.length > 1){
                String[] separateGiven = separateQueryandGiven[1].split(",");
                for (String s : separateGiven) {
                    String[] givenNames = s.split("=");
                    givenNodes.add(givenNames[0]);
                }
            }
            ArrayList<BeysianNode> given = gettingGivenNodes(givenNodes,b.getNodes());
            BeysBallAlgo beyesB = new BeysBallAlgo(b.getNodes(),queryBeysianNodes[0],queryBeysianNodes[1],given);
            boolean isInd = beyesB.DFS();
            writeBeysBallAns(isInd);
        }

    /**
     * Returning the query nodes.
     * @param queryNods Getting the query nides.
     * @param allNodes The beysian nodes.
     * @return The nodes.
     */
        private BeysianNode[] gettingQueryNodes(String[] queryNods,ArrayList<BeysianNode> allNodes){
            BeysianNode[] queryBeysianNodes = new BeysianNode[2];
            String firstNode =queryNods[0];
            String secondNode = queryNods[1];
            for (BeysianNode allNode : allNodes) {
                if (allNode.getV().getName().equals(firstNode)) {
                    queryBeysianNodes[0] = allNode;
                } else if (allNode.getV().getName().equals(secondNode)) {
                    queryBeysianNodes[1] = allNode;
                }
            }
        return queryBeysianNodes;
    }

    /**
     * Getting the GivenNodes.
     * @param givenNodes Getting the given Nodes.
     * @param allNodes Getting all the nodes.
     * @return The Nodes.
     */
    private ArrayList<BeysianNode> gettingGivenNodes(ArrayList<String> givenNodes,ArrayList<BeysianNode> allNodes){
        ArrayList<BeysianNode> given = new ArrayList<>();
        for (String givenNode : givenNodes) {
            for (BeysianNode allNode : allNodes) {
                if (givenNode.equals(allNode.getV().getName())) {
                    given.add(allNode);
                }
            }
        }
        return given;
    }

    /**
     * Writting The answer of the algorithm.
     * @param isInd returning if the variables are independent.
     * @throws IOException exception.
     */
    private void writeBeysBallAns(boolean isInd) throws IOException {
        PrintStream printStream = new PrintStream(new FileOutputStream("output.txt",true));
        if(isInd){
            printStream.println("yes");
        }else{
            printStream.println("no");
        }
        printStream.close();
    }
}
