import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class Graph {

    // Node Class
    private class Node extends Object implements Comparable<Node> {     // graph node
        // Node Constructor
        private Node(String state, String move, int gCost, Node parent) {
            this.state = state;
            this.move = move;
            this.gCost = gCost;
            this.parent = parent;
        }

        private int hCost;      // heuristic cost for this node
        private int gCost;      // total cost to get here from initial state.
        private int fCost;      // h + g
        private String state;   // will also be used as the id.
        private String move;    // the move taken to get to this state
        private Node parent;    // parent of node.
        private LinkedList<Edge> edges = new LinkedList<Edge>();

        private void calcHeuristic(String h) {
            NPuzzle puzzle = new NPuzzle(8);
            puzzle.setState(this.state);

            if(h.equals("h1")) {
                this.hCost = puzzle.heuristic1();
            }
            else if(h.equals("h2")) {
                this.hCost = puzzle.heuristic2();
            }
            this.fCost = this.hCost + this.gCost;
        }
        

        @Override
        public int compareTo(Graph.Node o) { // used by priority queue.
            if (this.fCost < o.fCost) {
                return -1;
            }
            if (this.fCost > o.fCost) {
                return 1;
            }
            return 0;
        }

        // same state --- compare node states.
        public boolean sameState(Node n) {
            n = (Node) n;
            if (this.state.equals(n.state)) {
                return true;
            }
            return false;
        }
    }

    // Edge Class
    private class Edge { // holds a cost, endNode, and startNode
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
        this.rootNode.calcHeuristic(heuristic);
        this.maxNodes = maxNodes;
        this.heuristic = heuristic;
    }

    private Node rootNode;
    private int maxNodes;
    private String heuristic;
    private String goalState = "b12 345 678 ";

    // maxNodes --- set max nodes to travel
    public void maxNodes(int n) {
        this.maxNodes = n;
    }

    // addEdge
    private void addEdge(Node n1, Node n2) {
        n1.edges.add(new Edge(n1, n2));
    }

    // for commandReader to call.
    public void solve(String alg) {
        switch (alg) {
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
    private void aStar() {
        PriorityQueue<Node> frontier = new PriorityQueue<Node>();   // states to be explored.
        LinkedList<Node> explored = new LinkedList<Node>();         // already explored states.

        frontier.add(this.rootNode);
        int nodesExplored = 0;
        System.out.println("Solving from state " + this.rootNode.state + " with heuristic " + "\'" + this.heuristic + "\'.");
        while (!frontier.isEmpty()) {

            Node curr = frontier.poll();
            if (curr.state.equals(this.goalState)) {
                System.out.println("Reached Goal after " + nodesExplored + " nodes explored.");
                this.printPath(curr);
                return;
            }

            explored.add(curr);

            nodesExplored++;
            if (this.maxNodes > 0 && this.maxNodes < nodesExplored) {
                System.out.println("Max number of nodes searched (" + this.maxNodes + ").");
                return;
            }

            this.expand(curr);  // branch

            Iterator<Edge> iterator = curr.edges.iterator();    // for each node connected to curr.....
            while (iterator.hasNext()) {
                Node n = iterator.next().endNode;

                n.calcHeuristic(this.heuristic);

                int cost = curr.fCost + 1;

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

                // n was not in frontier, and was not previously explored. add it to frontier.
                if (!frontier.contains(same) && !explored.contains(eSame)) {
                    frontier.add(n);
                }
            }
        }
        // failed
        System.out.println("Failed to find solution.");
        return;

    }

    // expand a node -- Helper for A*
    private void expand(Node curr) {
        String[] moveList = new String[] { "right", "down", "left", "up" };

        for (int i = 0; i < moveList.length; i++) {
            NPuzzle puzzle = new NPuzzle(8);
            puzzle.setState(curr.state);
            boolean valid = puzzle.move(moveList[i]);

            if (valid) { // if the move was legal.
                String newState = puzzle.getState();
                this.addEdge(curr, new Node(newState, moveList[i], curr.gCost + 1, curr));
            }
        }
    }

    // looks through the frontier and returns a node with a state matching that of
    // n. --- helper for A*
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

    // printpath --- helper for A*
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

    // helper for A*
    private Node getEqualNode(LinkedList<Node> explored, Node n) {
        Iterator<Node> iterator = explored.iterator();
        while (iterator.hasNext()) {
            Node curr = iterator.next();
            if (curr.sameState(n)) {
                return curr;
            }
        }
        return new Node("", "", 0, null); // impossible to exist in frontier.
    }

    // Beam Search
    public void beam() {

    }
}
