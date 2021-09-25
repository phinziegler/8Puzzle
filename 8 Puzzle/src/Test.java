
public class Test {

	public static void main(String[] args) {

		NPuzzle puzzle = new NPuzzle(8);
		puzzle.setState("b12 345 678");
		
		puzzle.randomizeState(50);
		
		//puzzle.printIntState();
		puzzle.printState();

		puzzle.heuristic2();
		
	}
	
}
