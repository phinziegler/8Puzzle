//import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

/*
    Graph that will have puzzle states as nodes. Can use A* and bream searches
        NOTE: each node should have its state represented by a string. Finding the goal state is easy this way,
        memory is saved.
*/
public class Graph {
    
    // Node Class
    private class Node implements Comparable<Node> {    // holds a state, and a list of edges, also holds the heuristic cost
        private int hCost;      // heuristic cost for this node
        private int gCost;      // total cost to get here from initial state.
        private int fCost;      // h + g
        private String state;   // will also be used as the id.
        private String move;    // the move taken to get to this state
        private Node parent;    // parent of node.
        private LinkedList<Edge> edges = new LinkedList<Edge>();

        private void calcHeuristic() {
            NPuzzle puzzle = new NPuzzle(8);
            puzzle.setState(this.state);

            this.hCost = puzzle.heuristic2();
            this.fCost = this.hCost + this.gCost;
        }
        
        private Node(String state, String move, int gCost, Node parent) {
            this.state = state;
            this.move = move;
            this.gCost = gCost;
            this.parent = parent;
        }

        @Override
        public int compareTo(Graph.Node o) {
            if(this.fCost < o.fCost) {
                return -1;
            }
            if(this.fCost > o.fCost) {
                return 1;
            }
            return 0;
        }

        @Override
        public boolean equals(Graph.Node o) {
            if(this.gCost == o.gCost && this.state.equals(o.state)) {
                return true;
            }
            return false;
        }
    }

    // Edge Class
    private class Edge {    // holds a cost, endNode, and startNode
        private Node startNode;
        private Node endNode;

        private Edge(Node start, Node end) {
            this.startNode = start;
            this.endNode = end;
        }
    }

    // Graph Constructor
    public Graph(NPuzzle puzzle, int maxNodes, String heuristic) {
        this.rootNode = new Node(puzzle.getState(), "", 0, null);
        this.rootNode.gCost = 0;
        this.rootNode.calcHeuristic();
        this.maxNodes = maxNodes;
        this.heuristic = heuristic;
    }

    private Node rootNode;
//  private NPuzzle puzzle;
    private int maxNodes;
    private String heuristic;
    private String goalState = "b12 345 678 ";

    // maxNodes --- set max nodes to travel
    public void maxNodes(int n) {
        this.maxNodes = n;
    }

    // add edge
    private void addEdge(Node n1, Node n2) {
        n1.edges.add(new Edge(n1,n2));
    }

    // for commandReader to call.
    public void solve(String alg) {
        switch(alg) {
            case "A-star":
                this.aStar();
                break;
            case "beam":
                this.beam();
                break;
            default:
                System.out.println(new Exception("Invalid modifier for 'solve' command"));
        }
    }

    // A-Star
    /*
        1. Add root to frontier, then loop...

        while(!frontier.isEmpty()) {
            currNode = frontier.poll;
            if currNode.isGoal
                return
            explored.add(currNode);

            forEach n in curr{
                if explored.contains(n)
                    continue
                cost = curr.gCost + 1;
                if n in Frontier and cost < g(n)
                    remove n from frontier. // new path is better anyway.
                if n in explored and cost < n.gCost()
                    remove n from explored
                if n not in frontier and n not in explored
                    frontier.add(n)
                    n.gCost = cost
                    n.calcHeuristic()
            }
        }

    */
    public void aStar() {
        PriorityQueue<Node> frontier = new PriorityQueue<Node>();   // could use hash table and prio queue together for better performance
        Hashtable<String, Node> explored = new Hashtable<String, Node>();  // list of explored STATES not nodes.

        frontier.add(this.rootNode);
        int nodesExplored = 0;
        System.out.println("Solving from state: " + this.rootNode.state);
        while(!frontier.isEmpty()) {
            Node curr = frontier.poll();
            if (curr.state.equals(this.goalState)) {
                System.out.println("Reached Goal after " + nodesExplored + " nodes explored.");
                this.printPath(curr);
                return;
            }
            explored.put(curr.state, curr);
            nodesExplored++;
            if(this.maxNodes > 0 && this.maxNodes < nodesExplored) {
                System.out.println("Max number of nodes searched (" + this.maxNodes + ").");
                return;
            }

            this.expand(curr);
            Iterator<Edge> iterator = curr.edges.iterator();
            while(iterator.hasNext()) {
                Node n = iterator.next().endNode;
                n.calcHeuristic();
                if(explored.contains(n)) {
                    System.out.println("explored contains n");
                    continue;
                }
                int cost = curr.gCost + 1;

                Node same = this.getNodeWithSameState(frontier, n);
                if(cost < n.gCost && frontier.contains(same)) {
                    //System.out.println("2");
                    frontier.remove(same);  // new path is better
                }
                if(explored.contains(n) && cost < n.gCost) { // EXPERIMENTAL
                    //System.out.println("3");
                    explored.remove(n.state);
                }
                if(!frontier.contains(same) && !explored.contains(n)) {
                    //System.out.println("4");
                    n.calcHeuristic();
                    frontier.add(n);
                    //n.gCost = cost; // huh?
                    
                }
            }
        }
        // failed
        System.out.println("Failed to find solution.");
        return;

    }

    // expand a node
    private void expand(Node curr) {
        String[] moveList = new String[]{"right", "down", "left", "up"};

        for(int i = 0; i < moveList.length; i++) {
            NPuzzle puzzle = new NPuzzle(8);
            puzzle.setState(curr.state);
            boolean valid = puzzle.move(moveList[i]);

            if(valid) { // if the move was legal.
                String newState = puzzle.getState();
                this.addEdge(curr, new Node(newState, moveList[i], curr.gCost + 1, curr));
            }
        }
    }

    // looks throught the frontier and returns a node with a state matching that of n.
    private Node getNodeWithSameState(PriorityQueue<Node> frontier, Node n) {
        Iterator<Node> iterator = frontier.iterator();
        while(iterator.hasNext()) {
            Node curr = iterator.next();
            if(curr.state.equals(n.state)) {
                return curr;
            }
        }
        return new Node("", "", 0, null); // impossible to exist in frontier.
    }

    // printpath
    private void printPath(Node end) {
        System.out.println("Solution Path:");
        Node curr = end;
        LinkedList<String> moveList = new LinkedList<String>();
        while(curr.parent != null) {
            moveList.addFirst(curr.move);
            curr = curr.parent;
        }
        String move;
        Iterator<String> iterator = moveList.iterator();
        while(iterator.hasNext()) {
            move = iterator.next();
            System.out.print(move);
            System.out.print(" ");
        }
    }

    // Beam Search
    public void beam() {

    }

}
