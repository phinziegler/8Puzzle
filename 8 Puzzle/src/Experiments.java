public class Experiments {
    

    public static void main(String[] args) {

        String heuristic = "h2";    // default heuristic
        int k = 200;                // default value for k
        int maxNodes = 10000;       // default value for maxnodes

        NPuzzle puzzle = new NPuzzle(8);
        NPuzzle puzzle2 = new NPuzzle(8);
        Graph h1Graph;
        Graph h2Graph;
        Graph beamGraph;


        int h1Success = 0;
        int h2Success = 0;
        int beamSuccess = 0;
        for(int i = 0; i < 100; i++) {
            puzzle.setState("b12 345 678");
            puzzle.randomizeState(10000);
            
            h1Graph = new Graph(puzzle, maxNodes, "h1", k);
            h2Graph = new Graph(puzzle, maxNodes, "h2", k);
            beamGraph = new Graph(puzzle, maxNodes, heuristic, k);

            
            // if (h1Graph.solve("A-Star") ){
            //     h1Success++;
            // }
            // if (h2Graph.solve("A-Star") ){
            //     h2Success++;
            // }
            if (beamGraph.solve("beam") ){
                beamSuccess++;
            }



        }
        System.out.println("***h1 successes = " + h1Success);
        System.out.println("***h2 successes = " + h2Success);
        System.out.println("***beam successes = " + beamSuccess);





    }
}
