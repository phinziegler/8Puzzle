
/*
    This class reads input from a text file and performs commands based on this input.
*/
public class Loop {

    private static String filePath;

    public static void setFilePath(String filePath) {
        Loop.filePath = filePath;
    }

    public static String getCommand() {
        return "";
    }

    public static String getModifier() {
        return "";
    }

    public static void main(String[] args) {

        NPuzzle puzzle = new NPuzzle(8);
        Graph pGraph = new Graph();
        Loop.setFilePath("<filepath>");
        
        boolean loop = true;
        while (loop) { // this loop will capture input from the text file, and then use a switch
                       // statement to perform the commands.
            String command = Loop.getCommand(); // returns some string
            String modifier = Loop.getModifier();

            switch (command) {
                case "setState":
                    System.out.println("Found command 'setState('" + modifier + "')");
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
