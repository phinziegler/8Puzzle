import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.stream.Stream;

// COMMAND READER --- reads from a text file input in main. --- CONTAINS THE MAIN FUNCTION
public class CommandReader {

    // PRIVATE VARIABLES
    private String filePath;
    private LinkedList<String> commandList = new LinkedList<String>();

    public CommandReader(String filepath) {
        this.filePath = filepath;
        this.commandList = this.generateCommandList(this.readTxt());

    }

    // READ TXT --- turns the txt file at filepath into a string.
    private String readTxt() {
		StringBuilder stringBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(this.filePath), StandardCharsets.UTF_8)){
            stream.forEach(s -> stringBuilder.append(s).append("\n"));
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        String output = stringBuilder.toString();
		return output;
	}

    // GENERATE COMMAND LIST --- turns the input text into linked list where each element is a full line.
    private LinkedList<String> generateCommandList(String text) {
        LinkedList<String> list = new LinkedList<String>();
        Scanner scanner = new Scanner(text);    // reads the string.

        while(scanner.hasNext()) {              // add each line to list
            String line = scanner.nextLine();   
            list.addLast(line);
        }

        scanner.close();
        return list;
    }

    // GET COMMAND --- returns a string array where the first element is the command, and the second is the input.
    public String[] getCommand() {
        if (this.commandList.size() <= 0) {     // return blank when no commands are left.
            return new String[]{"",""};
        }

        String line = this.commandList.removeFirst();
        
        String command;
        String input;

        StringBuilder comBuilder = new StringBuilder();
        StringBuilder inBuilder = new StringBuilder();

        StringBuilder curr = comBuilder;        // the current stringbuilder to use.
        for(int i = 0; i < line.length(); i++) {    
            if(line.charAt(i) != ' ') {
                curr.append(line.charAt(i));
            }
            else {                              // after the space, you build the input string.
                curr = inBuilder;
            }
        }

        command = comBuilder.toString();
        input = inBuilder.toString();

        String[] output = {command, input};
        return output;
    }


    ///////////////////
    /// MAIN METHOD /// 
    ///////////////////



    public static void main(String[] args) {

        // EDIT VALUES BELOW //

        String filePath = "D:\\Documents\\Coding\\Java\\VSCode 8 Puzzle\\8 Puzzle\\commands.txt";   // filepath
        String heuristic = "h2";    // default heuristic
        int k = 10;                 // default value for k
        int maxNodes = 15000;       // default value for maxnodes

        // EDIT VALUES ABOVE //


        // Read commands and exectute them
        CommandReader reader = new CommandReader(filePath);
        NPuzzle puzzle = new NPuzzle(8);
        Graph pGraph;
        boolean loop = true;
        while (loop) {

            String[] line = reader.getCommand();
            String command = line[0];
            String input = line[1];

            switch (command) {
                case "//":  // ability to add comments in command doc (NOT inline with other commands)
                    break;
                case ".":   // ability to format spacing between commands
                    break;
                case "setState":
                    puzzle.setState(input);
                    break;
                case "printState":
                    puzzle.printState();
                    break;
                case "printIntState":   // visual output of current state
                    puzzle.printIntState();
                    break;
                case "move":
                    puzzle.move(input);
                    break;
                case "randomizeState":
                    puzzle.randomizeState(Integer.parseInt(input));
                    break;
                case "solve":
                    pGraph = new Graph(puzzle, maxNodes, heuristic, k);
                    pGraph.solve(input);
                    break;
                case "maxNodes":
                    maxNodes = (Integer.parseInt(input));
                    break;
                case "heuristic":   // change the heuristic used... accepts h0, h1, h2, h3
                    heuristic = input;
                    break;
                case "setK":    // change k value for beam search
                    k = Integer.parseInt(input);
                case "blank":   // adds a blank line in console output.
                    System.out.println("");
                    break;
                default:
                    loop = false;
                    break;
            }
        }

    }

}
