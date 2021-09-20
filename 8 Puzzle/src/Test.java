
public class Test {

	public static void main(String[] args) {

		NPuzzle puzzle = new NPuzzle(8);
		puzzle.setState("b12 345 678");
		puzzle.printIntState();
		puzzle.printState();
		
		puzzle.randomizeState(5000);
		
		System.out.println();
		
		puzzle.printIntState();
		puzzle.printState();
		
	}

}
