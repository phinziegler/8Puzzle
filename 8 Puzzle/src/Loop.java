import jdk.internal.util.xml.impl.Input;

/*
    This class reads input from a text file and performs commands based on this input.
*/
public class Loop {

    private static String filePath;

    public static void setFilePath(String filePath) {
        Loop.filePath = filePath;
    }

    public static String getCommand() {
    }

    public static String getModifier() {
    }

    public static void main(String[] args) {

        NPuzzle puzzle = new NPuzzle(8);

        Graph pGraph = new Graph();

        boolean loop = true;

        Loop.setFilePath("<filepath>");

        while (loop) { // this loop will capture input from the text file, and then use a switch
                       // statement to perform the commands.
            String command = Loop.getCommand(); // returns some string
            String modifier = Loop.getModifier();

            switch (command) {
                case "setState":
                    puzzle.setState(modifier);
                    break;
                case "printState":
                    puzzle.printState();
                    break;
                case "move":
                    puzzle.move(modifier);
                    break;
                case "randomizeState":
                    puzzle.randomizeState(Integer.parseInt(modifier));
                    break;
                case "solve":
                    pGraph.solve(modifier);
                    break;
                case "maxNodes":
                    pGraph.maxNodes(Integer.parseInt(modifier));
                    break;
                default:
                    System.out.println(new Exception("Invalid Command"));
                    break;
            }

        }
    }

}
