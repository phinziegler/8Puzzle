import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;


// GRAPH CLASS.
public class Graph {

    // NODE CLASS
    private class Node extends Object implements Comparable<Node> {

        // NODE CONSTRUCTOR
        private Node(String state, String move, int gCost, Node parent) {
            this.state = state;
            this.move = move;
            this.gCost = gCost;
            this.parent = parent;
        }

        // NODE VARIABLES.
        private int hCost;      // heuristic cost for this node
        private int gCost;      // total cost to get here from initial state.
        private int fCost;      // h + g
        private String state;   // nPuzzle state.
        private String move;    // the move taken to get to this state --- used to remember the path.
        private Node parent;    // parent of node.
        private LinkedList<Edge> edges = new LinkedList<Edge>();

        // CALCULATE HEURISTIC -- also assigns a value to fCost.
        private void calcHeuristic(String h) {
            NPuzzle puzzle = new NPuzzle(8);
            puzzle.setState(this.state);

            switch(h) {
                case "h0":
                    this.hCost = puzzle.heuristic0();
                    break;
                case "h1":
                    this.hCost = puzzle.heuristic1();
                    break;
                case "h2":
                    this.hCost = puzzle.heuristic2();
                    break;
                case "h3":
                    this.hCost = puzzle.heuristic3();
                    break;
                default:
                    throw new Error("Invalid heuristic input \'" + h + "\'.");
            }
            this.fCost = this.hCost + this.gCost;
        }

        // COMPARE TO --- used by priority queue to sort nodes.
        @Override
        public int compareTo(Graph.Node o) {
            if (this.fCost < o.fCost) {
                return -1;
            }
            if (this.fCost > o.fCost) {
                return 1;
            }
            return 0;
        }

        // same state --- check if a node has the same state as this one.
        public boolean sameState(Node n) {
            n = (Node) n;
            if (this.state.equals(n.state)) {
                return true;
            }
            return false;
        }
    }

    // EDGE CLASS
    private class Edge { // holds a cost, endNode, and startNode
        private Node endNode;

        private Edge(Node end) {
            this.endNode = end;
        }
    }

    // CONSTRUCTOR
    public Graph(NPuzzle puzzle, int maxNodes, String heuristic, int k) {
        this.rootNode = new Node(puzzle.getState(), "", 0, null);
        this.rootNode.gCost = 0;
        this.rootNode.calcHeuristic(heuristic);
        this.maxNodes = maxNodes;
        this.heuristic = heuristic;
        this.k = k;
    }

    // PRIVATE VARIABLES FOR GRAPH
    private Node rootNode;
    private int maxNodes;
    private String heuristic;
    private int k;
    private int moveCost = 2;
    private String goalState = "b12 345 678 ";

    // SET MAX NODES
    public void maxNodes(int n) {
        this.maxNodes = n;
    }

    // ADD EDGE
    private void addEdge(Node n1, Node n2) {
        n1.edges.add(new Edge(n2));
    }

    // CALLED BY COMMAND READER
    public void solve(String alg) {
        switch (alg) {
            case "A-star":
                this.aStar();
                break;
            case "beam":
                this.beam(this.k);
                break;
            default:
                System.out.println(new Exception("Invalid modifier for 'solve' command"));
        }
    }


    /////////////////////////
    /// SEARCH ALGORITHMS ///
    /////////////////////////
    

    // A-STAR SEARCH
    private void aStar() {
        PriorityQueue<Node> frontier = new PriorityQueue<Node>();   // states to be explored.
        LinkedList<Node> explored = new LinkedList<Node>();         // already explored states.

        frontier.add(this.rootNode);
        int nodesExplored = 0;
        System.out.println("A* search from state " + this.rootNode.state + "with heuristic " + "\'" + this.heuristic + "\'.");
        while (!frontier.isEmpty()) {
            Node curr = frontier.poll();

            // CHECK IF GOAL STATE HAS BEEN FOUND.
            if (curr.state.equals(this.goalState)) {
                System.out.println("Reached Goal after " + nodesExplored + " nodes explored.");
                this.printPath(curr);
                return;
            }

            explored.add(curr);

            // CHECK IF MAX NODES REACHED
            nodesExplored++;
            if (this.maxNodes > 0 && this.maxNodes < nodesExplored) {
                System.out.println("Error: Max number of nodes searched (" + this.maxNodes + ").");
                return;
            }

            this.expand(curr);  // branch --- creates 2,3, or 4 child nodes depending on the location of b.

            Iterator<Edge> iterator = curr.edges.iterator();    // for each node connected to curr.....
            while (iterator.hasNext()) {
                Node n = iterator.next().endNode;

                n.calcHeuristic(this.heuristic);

                int cost = curr.fCost + this.moveCost;

                // check for a copy of n in explored. If exists: If copy's path is longer than current path, remove copy from explored.
                Node eSame = this.getEqualNode(explored, n);
                if (explored.contains(eSame)) {
                    if (cost < eSame.fCost) {
                        explored.remove(eSame);
                    } else {
                        continue;
                    }
                }

                // check for a copy of n in the frontier. If exists, and its path cost is longer than the current path cost, remove the longer state.
                Node same = this.getNodeWithSameState(frontier, n);
                if (cost < same.fCost && frontier.contains(same)) {
                    frontier.remove(same); 
                }

                // ADD N TO FRONTIER
                if (!frontier.contains(same) && !explored.contains(eSame)) {
                    frontier.add(n);
                }
            }
        }
        // ALGORITHM FAILED TO FIND A SOLUTION
        System.out.println("Failed to find solution.");
        return;

    }

    // EXPAND A NODE FROM ITS CURRENT STATE --- helper for searches. Also assigns a gCost.
    private void expand(Node curr) {
        curr.edges.clear();
        String[] moveList = new String[] { "right", "down", "left", "up" };

        for (int i = 0; i < moveList.length; i++) {
            NPuzzle puzzle = new NPuzzle(8);
            puzzle.setState(curr.state);
            boolean valid = puzzle.move(moveList[i]);

            if (valid) { // if the move was legal.
                //System.out.println("move " + moveList[i] + " at pos " + curr.state +"was valid");
                String newState = puzzle.getState();
                this.addEdge(curr, new Node(newState, moveList[i], curr.gCost + this.moveCost, curr));
            }
        }
    }

    // RETURNS A NODE THAT HAS THE SAME STATE AS THE INPUT NODE --- used to find out if a node equivalent to n is in the list.
    private Node getNodeWithSameState(LinkedList<Node> testNodes, Node n) {
        Iterator<Node> iterator = testNodes.iterator();
        while (iterator.hasNext()) {
            Node curr = iterator.next();
            if (curr.sameState(n)) {
                return curr;
            }
        }
        return new Node("", "", 0, null); // impossible to exist in frontier.
    }
    // SAME AS ABOVE -- just takes a priority queue as input.
    private Node getNodeWithSameState(PriorityQueue<Node> frontier, Node n) {
        Iterator<Node> iterator = frontier.iterator();
        while (iterator.hasNext()) {
            Node curr = iterator.next();
            if (curr.state.equals(n.state)) {
                return curr;
            }
        }
        return new Node("", "", 0, null); // impossible to exist in frontier.
    }

    // PRINT PATH OF A NODE --- helper for searches.
    private void printPath(Node end) {
        System.out.println("Solution Path:");
        Node curr = end;
        LinkedList<String> moveList = new LinkedList<String>();
        while (curr.parent != null) {
            moveList.addFirst(curr.move);
            curr = curr.parent;
        }
        String move;
        Iterator<String> iterator = moveList.iterator();
        while (iterator.hasNext()) {
            move = iterator.next();
            System.out.print(move);
            System.out.print(" ");
        }
        System.out.print("(" + moveList.size() + " moves)\n");
    }

    // HELPER FOR A*
    private Node getEqualNode(LinkedList<Node> explored, Node n) {
        Iterator<Node> iterator = explored.iterator();
        while (iterator.hasNext()) {
            Node curr = iterator.next();
            if (curr.sameState(n)) {
                return curr;
            }
        }
        return new Node("", "", 0, null); // impossible to exist in a frontier.
    }

    // LOCAL BEAM SEARCH
    public void beam(int k) {
        LinkedList<Node> bestNodes = new LinkedList<Node>();    // list of current k best
        LinkedList<Node> explored = new LinkedList<Node>();     // list of all explored nodes

        bestNodes.add(this.rootNode);
        int nodesSearched = 0;
        
        System.out.println("Beam search with k = " + k + " from state " + this.rootNode.state + ": ");

        while(true) {       // repeats until stuck at local min, found state, or maxnodes reached

            PriorityQueue<Node> sortNodes = new PriorityQueue<Node>();  // sorted list of testNodes
            LinkedList<Node> testNodes = new LinkedList<Node>();        // newly found nodes

            Iterator<Node> besterator = bestNodes.iterator();

            // FOR ALL BEST K STATES
            while(besterator.hasNext()) {
                Node curr = besterator.next();
                nodesSearched++;

                // DETECT IF GOAL REACHED
                if(curr.state.equals(this.goalState)) {
                    System.out.println("Goal Reached after " + nodesSearched + " nodes explored.");
                    this.printPath(curr);
                    return;
                }

                // DETECT IF MAX NODES REACHED
                if(this.maxNodes > 0 && this.maxNodes < nodesSearched) {
                    System.out.println("Error: Max number of nodes searched (" + this.maxNodes + ").");
                    return;
                }

                // EXPAND CURRENT NODE -- also add current node to explored
                this.expand(curr);
                explored.add(curr);

                // FOR EACH N in CURRENT NODE, ADD TO TESTNODES AND EVALUATE (avoiding duplicates and already explored states)
                Iterator<Edge> edgeit = curr.edges.iterator();
                while(edgeit.hasNext()) {
                    Node n = edgeit.next().endNode;
                    
                    Node same1 = getNodeWithSameState(testNodes, n);
                    Node same2 = getNodeWithSameState(explored, n);
                    if(!testNodes.contains(same1) && !explored.contains(same2)) {
                        this.evaluate(n);
                        testNodes.add(n); 
                    }
                }
            }

            // STUCK AT LOCAL MIN --- fail condition
            if(testNodes.size() == 0) {
                System.out.println("Error: all beams stuck at local min.");
                return;
            }

            sortNodes.addAll(testNodes);
            
            // ADD K NODES FROM SORTNODES TO BEST --- THIS PUTS THE K LOWEST VALUE NODES IN BEST.
            int i = 0;
            bestNodes.clear();
            while(!sortNodes.isEmpty() && i < k) {
                bestNodes.add(sortNodes.poll());
                i++;
            }
        }
    }

    // EVALUATION FUNCTION FOR BEAM
    private void evaluate(Node n) {
        n.calcHeuristic("h2");          // use manhattan distance.
        n.fCost = n.fCost - n.gCost;    // we ignore the gCost assigned to the node from when it was expanded
    }
    
}
