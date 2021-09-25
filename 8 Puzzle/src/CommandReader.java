import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.stream.Stream;

/*
    This class reads input from a text file and performs commands based on this input.
*/
public class CommandReader {

    private String filePath;
    private LinkedList<String> commandList = new LinkedList<String>();

    public CommandReader(String filepath) {
        this.filePath = filepath;
        this.commandList = this.generateCommandList(this.readTxt());

    }

    // read line by line from filepath
    private String readTxt() {
		StringBuilder stringBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(this.filePath), StandardCharsets.UTF_8)){
            stream.forEach(s -> stringBuilder.append(s).append("\n"));
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        String output = stringBuilder.toString();
        System.out.println(output);

		return output;
	}

    public LinkedList<String> generateCommandList(String text) {
        LinkedList<String> list = new LinkedList<String>();
        Scanner scanner = new Scanner(text);

        while(scanner.hasNext()) {
            String line = scanner.nextLine();
            this.commandList.addLast(line);
            System.out.println("added line \"" + line + "\" to list of commands");
        }

        scanner.close();
        return list;
    }

    public String[] getCommand() {

        String line = this.commandList.removeFirst();
        System.out.println("got command " + line);

        String command;
        String input;

        StringBuilder comBuilder = new StringBuilder();
        StringBuilder inBuilder = new StringBuilder();

        StringBuilder curr = comBuilder;

        for(int i = 0; i < line.length(); i++) {
            if(line.charAt(i) != ' ') {
                curr.append(line.charAt(i));
            }
            else {
                curr = inBuilder;
            }
        }

        command = comBuilder.toString();
        input = inBuilder.toString();

        System.out.println("command: " + command + "\nmodifier: " + input);

        String[] output = {command, input};

        return output;
    }

    public static void main(String[] args) {

        NPuzzle puzzle = new NPuzzle(8);
        Graph pGraph = new Graph();
        CommandReader reader = new CommandReader("D:\\commands.txt");
        boolean loop = true;
        int i = 0;
        while (loop) { // this loop will capture input from the text file, and then use a switch
                       // statement to perform the commands.
            i++;
            if(i > 3) {
                return;
            }

            String[] line = reader.getCommand();
            String command = line[0];
            String modifier = line[1];

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
        //endWHile
    }

}
