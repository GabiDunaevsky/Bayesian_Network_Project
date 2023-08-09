/**
 * The variable class that represents variable that holds Name and the Values of the variable.
 */

public class Var {
    private final String Name;
    private final String[] val;

    /**
     * A regular constructor.
     * @param Name - The name of the Variable.
     * @param val - The Evidence of the variable.
     */
    public Var(String Name ,String[] val){
        this.Name = Name;
        this.val = val;
    }

    /**
     * function that returning the Variable Name.
     * @return the variable name.
     */
    public String getName() {
        return Name;
    }

    /**
     * @return The variable values.
     */
    public String[] getVal() {
        return val;
    }

}
