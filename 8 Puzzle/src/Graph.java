import java.util.LinkedList;

/*
    Graph that will have puzzle states as nodes. Can use A* and bream searches
*/
public class Graph {
    
    private class Node {    // holds a state, and a list of edges, also holds the heuristic cost
        private int hCost;
        private LinkedList<Edge> edges = new LinkedList<Edge>();
    }

    private class Edge {    // holds a cost, endNode, and startNode
        private int gCost;
        private Node startNode;
        private Node endNode;
    }

    private NPuzzle puzzle = new NPuzzle(8);    // not ideal, refactor this into a constructor.
    private int maxNodes;

    public void maxNodes(int n) {
        this.maxNodes = n;
    }

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

    public void aStar() {

    }

    public void beam() {
        
    }

}
