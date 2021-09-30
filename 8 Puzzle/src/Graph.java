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

        // calculate the heuristic using an input heuristic method
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
        private Node endNode;

        private Edge(Node end) {
            this.endNode = end;
        }
    }

    // Graph Constructor
    public Graph(NPuzzle puzzle, int maxNodes, String heuristic, int k) {
        this.rootNode = new Node(puzzle.getState(), "", 0, null);
        this.rootNode.gCost = 0;
        this.rootNode.calcHeuristic(heuristic);
        this.maxNodes = maxNodes;
        this.heuristic = heuristic;
        this.k = k;
    }

    private Node rootNode;
    private int maxNodes;
    private String heuristic;
    private int k;
    private int moveCost = 2;
    private String goalState = "b12 345 678 ";

    // maxNodes --- set max nodes to travel
    public void maxNodes(int n) {
        this.maxNodes = n;
    }

    // addEdge
    private void addEdge(Node n1, Node n2) {
        n1.edges.add(new Edge(n2));
    }

    // for commandReader to call.
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
    

    // A-Star
    private void aStar() {
        PriorityQueue<Node> frontier = new PriorityQueue<Node>();   // states to be explored.
        LinkedList<Node> explored = new LinkedList<Node>();         // already explored states.

        frontier.add(this.rootNode);
        int nodesExplored = 0;
        System.out.println("A* search from state " + this.rootNode.state + "with heuristic " + "\'" + this.heuristic + "\'.");
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

    // // looks through the frontier and returns a node with a state matching that of
    // // n. --- helper for A*
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

    // beam search is A* with a fixed frontier.
    // only add to the frontier if it is one of the best candidates

    /* PSEUDOCODE

        bestNodes.add(rootNode)  // best is a list of length k
        frontier = new list;

        while(!frontier.isEmpty())
            foreach node in frontier
                explored.add(node)

                if(node.state == goalState)
                    printpath()
                    return

                expand(node)
                foreach n in node
                    if !frontier.contains(n)
                        //frontier.add(n)
                        testNodes.add(n)    // a list of length k^b
                .
            .
            allNodes = bestNodes.concat(frontier)
            allNodes.sort()
            bestNodes = allNodes.subList(0, k)  // new list of best k nodes
        .

    */

    // TODO--- Implement an explored category. All nodes rejected by testNodes are bye bye.

    public void beam(int k) {
        System.out.println("Beam search with k = " + k + " from state " + this.rootNode.state + ": ");
        LinkedList<Node> bestNodes = new LinkedList<Node>();
        LinkedList<Node> explored = new LinkedList<Node>();
        LinkedList<Node> prevBest = new LinkedList<Node>();
        bestNodes.add(this.rootNode);
        int nodesSearched = 0;

        while(true) {
            PriorityQueue<Node> allNodes = new PriorityQueue<Node>();
            LinkedList<Node> testNodes = new LinkedList<Node>();
            testNodes.clear();


            // // DETECT WHEN STUCK
            // if(this.isSameList(bestNodes, prevBest)) {
            //     System.out.println("Error: All nodes stuck at local min.");
            //     return;
            // }
            // prevBest.clear();
            // prevBest = bestNodes;
            

            Iterator<Node> besterator = bestNodes.iterator();
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

                // EXPAND CURRENT NODE
                this.expand(curr);
                explored.add(curr);

                // ADD CURR TO TESTNODES (avoiding duplicates)
                // Node same1 = getNodeWithSameState(testNodes, curr);
                // if(!testNodes.contains(same1)) {
                //     testNodes.add(curr);
                // }

                // FOR EACH N in CURRENT NODE, ADD TO TESTNODES AND EVALUATE (avoiding duplicates)
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

            allNodes.clear();
            allNodes.addAll(testNodes);

            bestNodes.clear();

            int i=0;
            while(!allNodes.isEmpty() && i < k) {
                bestNodes.add(allNodes.poll());
                i++;
            }
        }
    }

    private void printList(LinkedList<Node> list) {
        Iterator<Node> it = list.iterator();
        while(it.hasNext()) {
            Node curr = it.next();
            System.out.print(curr.state + "F(n) = " + curr.fCost + " | ");
        }
        System.out.print("\n");
    }

    // the evaluation function for beam
    private void evaluate(Node n) {
        n.calcHeuristic("h2");          // use manhattan distance.
        n.fCost = n.fCost - n.gCost;    // we ignore the gCost assigned to the node from when it was expanded
    }

    // checks if two lists are the same.
    private boolean isSameList(LinkedList<Node> bestNodes, LinkedList<Node> prevBest) {
        //System.out.println();
        //this.printList(bestNodes);

        if(prevBest.size() <= k) {
            return false;
        }
        

        Iterator<Node> besterator = bestNodes.iterator();
        while(besterator.hasNext()) {       // if there is a state present in bestNodes that does not exist in prevBest, return false
            Node best = besterator.next();
            Node same = this.getNodeWithSameState(prevBest, best);  // same is a node that is equal to best, but present in prevBest (if it exists)

            if(!prevBest.contains(same)) {       // best is NOT in prevBest
                return false;       // not the same list
            }

            //System.out.println(best.state + " is in prev, " + same.state);
        }
        return true;
    }
    
}
