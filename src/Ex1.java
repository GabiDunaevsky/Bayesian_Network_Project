import java.io.IOException;

/**
 * this is a class just to run the main function.
 */
public class Ex1
{
    /**
     * The main function.
     * @param args - main function.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Parse p = new Parse("input.txt");
        p.parsingFile();
    }
}

