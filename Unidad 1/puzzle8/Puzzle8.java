package puzzle8;

public class Puzzle8 {
    public static void main(String[] args) throws Exception {
        /* Puedes cambiar estos estados por los que necesites probar */
        String initialState = "7621 3458";
        String goalState = "12345678 ";

        System.out.println("Iniciando busquedas no informadas...\n");

        /* BFS */
        SearchTree treeBFS = new SearchTree(initialState, goalState);
        treeBFS.breadthFirstSearch();

        /* DFS */
        SearchTree treeDFS = new SearchTree(initialState, goalState);
        treeDFS.depthFirstSearch();

        /* UCS */
        SearchTree treeUCS = new SearchTree(initialState, goalState);
        treeUCS.uniformCostSearch();

        /* IDS */
        SearchTree treeIDS = new SearchTree(initialState, goalState);
        treeIDS.iterativeDeepeningSearch();
    }
}