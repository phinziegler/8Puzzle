
public class NPuzzle {

	// POINTER CLASS --- Npuzzle holds the location of its blank spot in a pointer.
	private class Pointer {
		private Pointer(int r, int c) {
			this.setCoords(r, c);
		}

		// Coords
		private int row;
		private int col;

		// setCoords
		private void setCoords(int r, int c) {
			this.row = r;
			this.col = c;
		}
	}

	// NPUZZLE CONSTRUCTOR --- Initially designed to handle n-puzzles, but it was too difficult. Input should always be 8 (for now)
	public NPuzzle(int n) {
		int square = n + 1;
		this.width = (int)Math.sqrt(square);
		this.state = new Integer[this.width][this.width];
	}

	// PRIVATE VARIABLES
	private int width;
	private Integer[][] state;
	private Pointer bPoint;	// [r][c] of the blank slot --- tracking this lets us do moves faster

	// SET STATE --- set state to input state
	public void setState(String state) {	// "b12 345 678"
		try {
			int spaces = 0;
			for(int i = 0; i < state.length(); i++) {
				char s = state.charAt(i);

				int c = (i - spaces) % this.width;	// column
				int r = (i - spaces) / this.width; 	// row

				switch(s) {
				case 'b':
					this.state[r][c] = null;
					this.bPoint = new Pointer(r,c);
					break;
				case ' ':
					spaces++;
					break;
				default: 
					this.state[r][c] = Integer.parseInt(String.valueOf(s));
					break;
				}
			}
		}
		catch(Exception e) {
			System.out.println(new Exception("Invalid argument for setState"));
		}
	}

	// GET STATE --- convert the Integer[][] into a string representing its state.
	public String getState() {
		StringBuilder sb = new StringBuilder();

		for(int r = 0; r < this.width; r++) {
			for(int c = 0; c < this.width; c++) {
				if(this.state[r][c] == null) {
					sb.append('b');
				}
				else {
					sb.append(this.state[r][c]);	
				}
			}
			sb.append(' ');
		}
		return sb.toString();
	}

	// PRINT STATE --- not really used.
	public void printState() {
		System.out.println(getState());
	}

	// PRINT INT STATE --- outputs to the console the 'matrix' of the puzzle.
	public void printIntState() {
		for(int r = 0; r < this.width; r++) {
			System.out.print("| ");
			for(int c = 0; c < this.width; c++) {
				if(this.state[r][c] == null) {
					System.out.print("  ");
				}
				else {
					System.out.print("" + state[r][c] + " ");
				}
			}

			System.out.println("|");
		}
	}

	// MOVE --- perform a move in input direction
	public boolean move(String direction) {

		int row = this.bPoint.row;
		int col = this.bPoint.col;
		Pointer newPos = new Pointer(row, col);

		switch(direction) {
		case "up":
			newPos.row--;
			break;

		case "down":
			newPos.row++;
			break;

		case "left":
			newPos.col--;
			break;

		case "right":
			newPos.col++;
			break;

		default: 
			System.out.println(new Exception("Invalid input for move"));
		}

		return this.swap(this.bPoint, newPos);
	}

	// SWAP --- helper method for move --- swaps a pointer with a number.
	private boolean swap(Pointer blank, Pointer num) {
		if(num.row >= 0 && num.row < this.width && num.col >= 0 && num.col < this.width) {
			this.state[this.bPoint.row][this.bPoint.col] = this.state[num.row][num.col];
			this.state[num.row][num.col] = null;
			
			this.bPoint.setCoords(num.row, num.col);
			return true;
			
		}
		return false;
	}
	
	// RANDOMIZE STATE --- performs n random moves. Does not count a move if it was an invalid move.
	public void randomizeState(int n) {
		
		String[] moves = new String[]{"up","down","left","right"};
		
		for(int i = 0; i < n; i++) {
			String randMove = moves[(int)(Math.random() * moves.length)];
			
			if(!this.move(randMove)) {
				i--;	// tried a move that couldnt be done, try a new move.
			}
		}
	}


	//////////////////////////////
	/// HEURISTICS CALCULATION /// 
	//////////////////////////////

	
	// H0 --- NO HEURISTIC --- returns 0 always.
	public int heuristic0() {
		return 0;
	}

	// H1 --- NUM MISPLACED TILES --- number of misplaced tiles.
	public int heuristic1() {
		int sum = 0;
		for(int r = 0; r < this.state.length; r++) {
			for(int c = 0; c < state[r].length; c++) {
				int i = (r*this.state.length) + c;
				if(this.state[r][c] == null) {
					if(r + c != 0)
						sum++;
				}
				else if(this.state[r][c] != i) {
					sum++;
				}
			}
		}
		return sum;
	}

	// H2 --- MANHATTAN DISTANCE --- sum of tile distance to their correct position
	public int heuristic2() {
		int sum = 0;
		for(int r = 0; r < this.state.length; r++) {
			for(int c = 0; c < this.state[r].length; c++) {
				sum += this.h2Dist(r,c);
			}
		}
		return sum;
	}
	// helper for H2
	private int h2Dist(int r, int c) {
		int val;
		if(this.state[r][c] == null) {
			val = 0;
		}
		else {
			val = this.state[r][c];
		}
		int goodRow = val / this.state.length; 
		int goodCol = val % this.state.length;
		int dist = Math.abs(r - goodRow) + Math.abs(c - goodCol);
		return dist;
	}

	// H3 --- MANHATTAN DISTANCE / 2 --- obsolete when move cost is considered to be > 1
	public int heuristic3() {
		return (this.heuristic2()/2);
	}
}


