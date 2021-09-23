
public class NPuzzle {

	
	private class Pointer {
		private Pointer(int r, int c) {
			this.setCoords(r, c);
		}
		
		private void setCoords(int r, int c) {
			this.row = r;
			this.col = c;
		}
		
		private int row;
		private int col;
	}

	public NPuzzle(int n) {
		int square = n + 1;
		this.width = (int)Math.sqrt(square);
		this.state = new Integer[this.width][this.width];
	}

	private int width;
	private Integer[][] state;
	private Pointer bPoint;	// [r][c] of the blank slot

	// set state
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

	// String Representation of State
	public void printState() {
		for(int r = 0; r < this.width; r++) {

			for(int c = 0; c < this.width; c++) {
				if(this.state[r][c] == null) {
					System.out.print("b");
				}
				else {
					System.out.print(this.state[r][c]);	
				}
			}
			System.out.print(" ");
		}
		System.out.print("\n");
	}

	// Visual Representation of State
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

	// move
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

	// swap
	private boolean swap(Pointer blank, Pointer num) {
		if(num.row >= 0 && num.row < this.width && num.col >= 0 && num.col < this.width) {
			this.state[this.bPoint.row][this.bPoint.col] = this.state[num.row][num.col];
			this.state[num.row][num.col] = null;
			
			this.bPoint.setCoords(num.row, num.col);
			return true;
			
		}
		return false;
	}
	
	// randomizeState
	public void randomizeState(int n) {
		
		String[] moves = new String[]{"up","down","left","right"};
		
		for(int i = 0; i < n; i++) {
			String randMove = moves[(int)(Math.random() * moves.length)];
			
			if(this.move(randMove)) {
				//System.out.println(randMove);
			}
			else {
				//i--;	// tried a move that couldnt be done, try a new move.
			}
		}
	}

	public int heuristic1() {	// number of correctly or incorrectly placed tiles.
		return 0;
	}

	public int heuristic2() {	// manhattan distance
		return 0;
	}
}


