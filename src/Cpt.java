import java.util.ArrayList;
import java.util.List;

/**
 * A class that representing Cpt(table).
 */
public class Cpt implements Comparable<Cpt> {
    private final ArrayList<Double> probaval;
    private ArrayList <Var> listVar = new ArrayList<>();
    private final ArrayList<String> valuesInCpt = new ArrayList<>();

    /**
     * A regular constructor.
     * @param probaval Array list of the probabilities for each case of values in the Cpt.
     * @param list The list of vars of the Cpt.
     */
    public Cpt(ArrayList<Double> probaval , ArrayList<Var> list){
        this.listVar= list;
        this.probaval = probaval;
    }

    /**
     * A copy constructor.
     * @param c The table that we are coping from.
     */
    public Cpt(Cpt c){
        this.probaval = new ArrayList<>();
        this.probaval.addAll(c.probaval);
        this.valuesInCpt.addAll(c.valuesInCpt);
        for (int i = 0; i < c.listVar.size(); i++){
            this.listVar.add(c.listVar.get(i));// to check
            for (int j = 0 ; j < c.listVar.get(i).getVal().length;j++){
                this.listVar.get(i).getVal()[j] = c.listVar.get(i).getVal()[j];
            }
        }
    }

    /**
     *
     * @return The list of vars.
     */
    public ArrayList<Var> getListVar() {
        return listVar;
    }

    /**
     *
     * @return the list of probabilities.
     */
    public ArrayList<Double> getProbaval() {
        return probaval;
    }

    /**
     *
     * @return The option of values in the Cpt.
     */
    public ArrayList<String> getvalVesInCpt(){
        return this.valuesInCpt;
    }

    /**
     * This function is creating all the options of the values in the cpt and adding it to the valuesInCpt
     * list field;
     */
    public void creatValForCpt(){
        int size = 1;
        for (Var var : this.listVar) {
            size *= var.getVal().length;
        }
        Var lastVar = this.listVar.get(this.listVar.size()-1);
        String[] values = lastVar.getVal();
        int getValInd = 0;
        for (int i = 0 ; i < size;i++){
            valuesInCpt.add(i, values[getValInd]);
            getValInd = (getValInd + 1)%values.length;
        }
        if(listVar.size() > 1){
            int lastVarInd = (this.listVar.size()-2);
            while(lastVarInd >= 0){
                String StrFirsLine =this.valuesInCpt.get(0);
                lastVar = this.listVar.get(lastVarInd);
                values = lastVar.getVal();
                getValInd = 0;
                for(int i = 0; i < size; i++){
                    if(this.valuesInCpt.get(i).equals(StrFirsLine)){
                        this.valuesInCpt.set(i,values[getValInd] + "," + this.valuesInCpt.get(i));
                        getValInd  = (getValInd + 1)%values.length;
                    }else{
                        int IndBrfore = (getValInd -1) % values.length;
                        if(IndBrfore < 0){
                            IndBrfore+=values.length;}
                        this.valuesInCpt.set(i,values[IndBrfore] + "," + this.valuesInCpt.get(i));
                    }
                }
                lastVarInd --;
            }
        }
    }

    /**
     * A regular toString function.
     * @return printing the Cpt.
     */
    public String toString(){
        String s="";
        System.out.println("CPT: ");
        System.out.println("probArray :" + this.probaval);
        System.out.print("cptVars: " + "[");
        for (Var var : this.listVar) {
            System.out.print(var.getName() + ",");
        }
        System.out.print("]\n");
        for(int i = 0 ; i < this.probaval.size();i++){
            System.out.println(this.valuesInCpt.get(i) + "     " + this.probaval.get(i) + "\n");
        }
        return s;
    }
    private int getASCIIVarsSum(List<Var> vars){
        int sum = 0;
        for(Var var : vars){
            int varASCIIValue = 0;
            for(char charInName : var.getName().toCharArray())
            {
                varASCIIValue += charInName;
            }
            sum += varASCIIValue;
        }

        return sum;
    }

    /**
     * A comper to function(the class implement from comparator) to compar two cpts , firs opion by there size,
     *second option by there variable ASCII value.
     * @param o A cpt to compar to.
     * @return 1 if this is bigger -1 if o is bigger an 0 if they are equals.
     */
    @Override
    public int compareTo(Cpt o) {
        int aTableSize = this.getListVar().size() * this.getvalVesInCpt().size();
        int bTableSize = o.getListVar().size() * o.getvalVesInCpt().size();

        if (aTableSize < bTableSize) return -1;
        else if (aTableSize > bTableSize) return 1;
        else {
            return getASCIIVarsSum(this.getListVar()) - getASCIIVarsSum(o.getListVar());

        }
    }
}
