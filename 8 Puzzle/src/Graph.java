import java.util.Hashtable;
import java.util.LinkedList;
import java.util.PriorityQueue;

/*
    Graph that will have puzzle states as nodes. Can use A* and bream searches
        NOTE: each node should have its state represented by a string. Finding the goal state is easy this way,
        memory is saved.
*/
public class Graph {
    
    private class Node {    // holds a state, and a list of edges, also holds the heuristic cost
        private int hCost;  // heuristic cost for this node
        private LinkedList<Edge> edges = new LinkedList<Edge>();
        private String state;   // will also be used as the id.

        private Node(String state) {
            this.state = state;
        }
    }

    private class Edge {    // holds a cost, endNode, and startNode
        private int cost;  // actual cost for this edge
        private Node startNode;
        private Node endNode;

        private Edge(Node start, Node end) {
            this.startNode = start;
            this.endNode = end;
            this.cost = 1;
        }
    }

    public Graph(NPuzzle puzzle, int maxNodes, String heuristic) {
        this.puzzle = puzzle;
        this.maxNodes = maxNodes;
        this.heuristic = heuristic;

        this.rootNode = new Node(puzzle.getState());
    }

    private Node rootNode;
    private NPuzzle puzzle;
    private int maxNodes;
    private String heuristic;
    private String goalState = "b12 345 678";

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
    public String aStar() {
        PriorityQueue<String> frontier = new PriorityQueue<String>();   // could use hash table and prio queue together for better performance
        Hashtable<String, String> explored = new Hashtable<String, String>();  // list of explored states

        return aStarRecurse(frontier, explored, 0);
    }
    private String aStarRecurse(PriorityQueue<String> frontier, Hashtable<String,String> explored, int cost) {


        return "";
    }

    // Beam Search
    public void beam() {

    }

}
