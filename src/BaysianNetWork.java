import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A FUNCTION THAT BUILDS THE BAYESIAN NETWORK BY USING DOM LIBRARY.
 */

public class BaysianNetWork {
    private final ArrayList<BeysianNode> nodes;

    /**
     * A default constructor.
     */
    public BaysianNetWork(){
        this.nodes = new ArrayList<>();
    }

    /**
     * A regular constructor.
     */
    public BaysianNetWork(ArrayList<BeysianNode> nodes) {
        this.nodes = nodes;
    }

    /**
     * @return Array list of nods.
     */
    public ArrayList<BeysianNode> getNodes() {
        return nodes;
    }

    /**
     * A function that by getting a xml file is building the all network.
     * @param fileName The xmlfile.
     * @return The beysian network.
     */
    public BaysianNetWork buildNetWork(String fileName) {
        Document document = null;
        //Get Document Builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(fileName);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        //Normalize the XML Structure; It's just too important !!
        document.getDocumentElement().normalize();
        NodeList mList = document.getElementsByTagName("VARIABLE");
        NodeList sList = document.getElementsByTagName("DEFINITION");
        ArrayList<Var> listsOfVars;
        ArrayList<Cpt> listsOfCpts;
        ArrayList<BeysianNode> allNodes;
        listsOfVars = creatAllVar(mList);
        listsOfCpts = creatAllBeysianCpt(sList, listsOfVars);
        allNodes = creatAllNodes(sList, listsOfCpts, listsOfVars);
        return new BaysianNetWork(allNodes);
    }

    /**
     * Creating all the variables of the network.
     * @param mList The node list.
     * @return Arraylist of variables.
     */
    private ArrayList<Var> creatAllVar(NodeList mList) {
        ArrayList<Var> listsOfVars = new ArrayList<>();
        for (int temp = 0; temp < mList.getLength(); temp++) {
            Node node = mList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                int length = eElement.getElementsByTagName("OUTCOME").getLength();
                String[] numOfValues = new String[length];
                String valueName = eElement.getElementsByTagName("NAME").item(0).getTextContent();
                for (int i = 0; i < length; i++) {
                    numOfValues[i] = eElement.getElementsByTagName("OUTCOME").item(i).getTextContent();
                }
                Var var = new Var(valueName, numOfValues);
                listsOfVars.add(var);
            }
        }
        return listsOfVars;
    }

    /**
     * Creating all the beysian cpts from the xml file.
     * @param sList getting all the nodes.
     * @param listsOfVars the list of vars that we already created the cpts needs list of vars.
     * @return a cpt Arraylist.
     */
    private ArrayList<Cpt> creatAllBeysianCpt(NodeList sList, ArrayList<Var> listsOfVars) {
        ArrayList<Cpt> listsOfCpt = new ArrayList<>();
        for (int temp = 0; temp < sList.getLength(); temp++) {
            ArrayList<Var> varsForCpt = new ArrayList<>();
            Node node = sList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                String varName = eElement.getElementsByTagName("FOR").item(0).getTextContent();
                ArrayList<String> parentsNames = new ArrayList<>();
                for (int i = 0; i < eElement.getElementsByTagName("GIVEN").getLength(); i++) {
                    parentsNames.add(eElement.getElementsByTagName("GIVEN").item(i).getTextContent());
                }
                for (String parentName : parentsNames) {
                    for (Var listsOfVar : listsOfVars) {
                        if (parentName.equals(listsOfVar.getName())) {
                            varsForCpt.add(listsOfVar);
                        }
                    }
                }
                for (Var listsOfVar : listsOfVars) {
                    if (varName.equals(listsOfVar.getName())) {
                        varsForCpt.add(listsOfVar);
                    }
                }

                ArrayList<Double> prob = new ArrayList<>();
                String probString = eElement.getElementsByTagName("TABLE").item(0).getTextContent();
                String[] probStringArr = probString.split(" ");
                for (int n = 0; n < probStringArr.length; n++) {

                    prob.add(n,Double.parseDouble(probStringArr[n]));
                }
                Cpt cpt = new Cpt(prob, varsForCpt);
                cpt.creatValForCpt();
                listsOfCpt.add(cpt);
            }
        }
        return listsOfCpt;
    }

    /**
     * Creating all the nodes to the beysian network.
     * @param sList the nodelist from the xml.
     * @param listsOfCpt all the cpts.
     * @param listsOfVars all the variables.
     * @return Arraylist of beysian nodes( the beysian network).
     */
    private ArrayList<BeysianNode> creatAllNodes(NodeList sList, ArrayList<Cpt> listsOfCpt, ArrayList<Var> listsOfVars) {
        ArrayList<BeysianNode> allNodes = new ArrayList<>();
        for (int temp = 0; temp < sList.getLength(); temp++) {
            Node node = sList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                String varName = eElement.getElementsByTagName("FOR").item(0).getTextContent();
                Var v = null;
                for (Var listsOfVar : listsOfVars) {
                    if (varName.equals(listsOfVar.getName())) {
                        v = listsOfVar;
                    }
                }
                Cpt cpt = null;
                for (Cpt value : listsOfCpt) {
                    if (varName.equals(value.getListVar().get(value.getListVar().size()-1).getName()) &&
                            value.getListVar().get(value.getListVar().size()-1).getName().equals(v.getName())) {
                        cpt = value;
                    }
                }
                BeysianNode bNode = new BeysianNode(new ArrayList<>(), new ArrayList<>(), v, cpt);
                allNodes.add(bNode);
            }
        }
        addParentsAndChildren(sList, allNodes);
        return allNodes;
    }

    /**
     * Adding the Array lists of the parents and the children for each node.
     * @param sList The nodes from the xml.
     * @param allNodes The "prepared nodes".
     */
    private void addParentsAndChildren(NodeList sList, ArrayList<BeysianNode> allNodes) {
        for (int temp = 0; temp < sList.getLength(); temp++) {
            Node node = sList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                String parentName = eElement.getElementsByTagName("FOR").item(0).getTextContent();
                BeysianNode tmp = new BeysianNode();
                for (BeysianNode allNode : allNodes) {
                    if (allNode.getV().getName().equals(parentName)) {
                        tmp = allNode;
                    }
                }
                for (int i = 0; i < eElement.getElementsByTagName("GIVEN").getLength(); i++) {
                    for (BeysianNode allNode : allNodes) {
                        if (allNode.getV().getName().equals(eElement.getElementsByTagName("GIVEN").item(i).getTextContent())) {
                            tmp.getParent().add(allNode);
                            allNode.getChild().add(tmp);
                        }
                    }
                }
            }
        }
    }

    /**
     * A regular toString that printing the network.
     * @return beysian network.
     */
    @Override
    public String toString() {
        System.out.println( "this is the size " + this.nodes.size() +"\n");
        for (BeysianNode node : this.nodes) {
            System.out.println("variable: " + node.getV().getName());
            System.out.println("val:" + Arrays.toString(node.getV().getVal()));
            System.out.print("children: " + "[");
            for (int j = 0; j < node.getChild().size(); j++) {
                System.out.print(node.getChild().get(j).getV().getName() + " ,");
            }
            System.out.print("]\n");
            System.out.print("parents: " + "[");
            for (int j = 0; j < node.getParent().size(); j++) {
                System.out.print(node.getParent().get(j).getV().getName() + ",");
            }
            System.out.print("]\n");
            System.out.print("cptVars: " + "[");
            for (int j = 0; j < node.getTable().getListVar().size(); j++) {
                System.out.print(node.getTable().getListVar().get(j).getName() + ",");
            }
            System.out.print("]\n");

            System.out.println("probval :" + node.getTable().getProbaval());
            Cpt tmp = node.getTable();
            tmp.creatValForCpt();
            System.out.println(tmp);
        }

        return "";
    }
}
