import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.stream.Stream;

// This class reads input from a text file and performs commands based on this input.
public class CommandReader {

    private String filePath;
    private LinkedList<String> commandList = new LinkedList<String>();

    public CommandReader(String filepath) {
        this.filePath = filepath;
        this.commandList = this.generateCommandList(this.readTxt());

    }

    // readTxt --- turns the txt file at filepath into a string.
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

    // generateCommandList --- turns the input text into linked list where each element is a full line.
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

    // getCommand --- returns a string array where the first element is the command, and the second is the input.
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

    ////////////
    /// MAIN /// 
    ////////////

    // read through the commands in text file, and perform them.
    public static void main(String[] args) {

        NPuzzle puzzle = new NPuzzle(8);
        CommandReader reader = new CommandReader("D:\\commands.txt");
        int maxNodes = 200;
        Graph pGraph;
        String heuristic = "h1";

        boolean loop = true;
        while (loop) {              //use a switch statement to perform the commands.
            String[] line = reader.getCommand();
            String command = line[0];
            String input = line[1];

            switch (command) {
                case "setState":
                    puzzle.setState(input);
                    break;
                case "printState":
                    puzzle.printState();
                    break;
                case "printIntState":
                    puzzle.printIntState();
                    break;
                case "move":
                    puzzle.move(input);
                    break;
                case "randomizeState":
                    puzzle.randomizeState(Integer.parseInt(input));
                    break;
                case "solve":
                    pGraph = new Graph(puzzle, maxNodes, heuristic);
                    pGraph.solve(input);
                    break;
                case "maxNodes":
                    maxNodes = (Integer.parseInt(input));
                    break;
                case "heuristic":
                    heuristic = input;
                    break;
                case "blank":
                    System.out.println("");
                    break;
                default:
                    loop = false;
                    break;
            }
        }

    }

}
