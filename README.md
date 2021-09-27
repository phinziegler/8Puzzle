# Programming Assignment 1
In this assignment, you will explore problem solving by search by wri8ng AI algorithms to solve the 8-puzzle. A descrip8on and discussions of this problem are in Chapter 3 of the textbook. The puzzle begins with scrambled 8les, and the goal is to move the 8les (or equivalently the blank “8le”) so that the blank 8le in the upper leI corner and the numbers are in order as shown. You can do the assignment in either Java, python, or julia.  If you would like to use a different language, you need to check with me (Prof. Lewicki) within a week of when the assignment is handed out.  The only constraint that we have to be able to run and test your assignment at least one of our computers using standard installa8ons and libraries.Your code will read text input for commands and write the solu8on as text output.



## Commands (Instructions from prof)
Your code should read sequences of commands from a text file that operate on the current
puzzle state. The text file should specified as an argument to your program. Output should be
printed to the terminal (stdout). You will implement the following commands:

## setState <state>
Set the puzzle state. The argument specifies the puzzle tile positions with a sequence of three
groups of three digits with the blank tile represented by the letter ‘b’. For example, ‘1b5’
specifies a row with 1 in the left tile, nothing in the middle tile and 5 in the right tile. The goal
state is "b12 345 678”.

## printState
Print the current puzzle state.

## move <direction>
Move the blank tile 'up', 'down', 'left', or 'right'.

## randomizeState <n>
Make n random moves from the goal state. Note that the goal state is not reachable from all
puzzle states, so this method of randomizing the puzzle ensures that a solution exists.

## solve A-star <heuristic>
Solve the puzzle from its current state using A-star search using heuristic equal to “h1” or “h2”
(see section 3.6, p. 102). Briefly, h1 is the number of misplaced tiles; h2 is the sum of the
distances of the tiles from their goal positions. You are free to try other heuristics, but be sure 
that they are admissible and describe them in your writeup. When the goal is found, your code
should print the number of tile moves needed to obtain the solution followed by the solution as
a sequences of moves (up, down, left, or right) from the starting state to the goal state.

## solve beam <k>
Solve the puzzle from its current state by adapting local beam search with k states. You will
need to define an evaluation function which you should describe in your writeup. It should
have a minimum of zero at the goal state. When the goal is found, print the number of tile
moves and solution as for A-star search.

## maxNodes <n>
Specifies the maximum number of nodes to be considered during a search. If this limit is
exceeded during search an error message should be printed.
