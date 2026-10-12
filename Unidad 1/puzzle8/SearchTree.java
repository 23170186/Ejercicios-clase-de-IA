package puzzle8;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class SearchTree {
    Node root;
    String initialState;
    String goalState;

    public SearchTree(String initialState, String goalState) {
        this.initialState = initialState;
        this.goalState = goalState;
        this.root = new Node(initialState, null);
    }

    /* Metodo para imprimir los resultados y armar la tabla comparativa */
    private void printMetrics(String aName, Node goalNode, int contador, long startTime) {
        double totalSeconds = (System.nanoTime() - startTime) / 1_000_000_000.0;

        System.out.println("Resultados del algoritmo: " + aName);
        if (goalNode != null) {
            System.out.println("Meta: Alcanzada");
            System.out.println("Profundidad de la busqueda: " + goalNode.getDepth());
            System.out.println("Iteraciones (Nodos evaluados): " + contador);
            System.out.println("Tiempo Total: " + totalSeconds + " segundos");
        } else {
            System.out.println("Meta: No alcanzada");
        }
        System.out.println("--------------------------------------------------");
    }

    /* 1. Busqueda en anchura BFS */
    public void breadthFirstSearch() {
        long startTime = System.nanoTime();
        Set<String> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();

        queue.add(root);
        int Contador = 0;

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            visited.add(currentNode.getState());
            Contador++;

            if (currentNode.getState().equals(goalState)) {
                printMetrics("BFS (Anchura)", currentNode, Contador, startTime);
                return;
            }

            List<Node> children = NodeUtils.generateChildren(currentNode);
            for (Node child : children) {
                if (!visited.contains(child.getState())) {
                    queue.add(child);
                }
            }
        }
    }

    /* 2. Busqueda en profundidad DFS */
    public void depthFirstSearch() {
        long startTime = System.nanoTime();
        Set<String> visited = new HashSet<>();
        Stack<Node> stack = new Stack<>();

        stack.push(root);
        int iterations = 0;

        while (!stack.isEmpty()) {
            Node currentNode = stack.pop();
            iterations++;

            if (currentNode.getState().equals(goalState)) {
                printMetrics("DFS (Profundidad)", currentNode, iterations, startTime);
                return;
            }

            if (!visited.contains(currentNode.getState())) {
                visited.add(currentNode.getState());
                List<Node> children = NodeUtils.generateChildren(currentNode);
                for (Node child : children) {
                    if (!visited.contains(child.getState())) {
                        stack.push(child);
                    }
                }
            }
        }
    }

    /* 3. Costo uniforme UCS */
    public void uniformCostSearch() {
        long startTime = System.nanoTime();
        Set<String> visited = new HashSet<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(Node::getCost));

        pq.add(root);
        int Contador = 0;

        while (!pq.isEmpty()) {
            Node currentNode = pq.poll();
            Contador++;

            if (currentNode.getState().equals(goalState)) {
                printMetrics("UCS (Costo Uniforme)", currentNode, Contador, startTime);
                return;
            }

            if (!visited.contains(currentNode.getState())) {
                visited.add(currentNode.getState());
                List<Node> children = NodeUtils.generateChildren(currentNode);
                for (Node child : children) {
                    if (!visited.contains(child.getState())) {
                        child.setCost(currentNode.getCost() + 1);
                        pq.add(child);
                    }
                }
            }
        }
    }

    /* 4. Profundidad Iterativa IDS */
    public void iterativeDeepeningSearch() {
        long startTime = System.nanoTime();
        int contador = 0;

        for (int limit = 0; limit <= 50; limit++) {
            Stack<Node> stack = new Stack<>();
            stack.push(root);

            while (!stack.isEmpty()) {
                Node currentNode = stack.pop();
                contador++;

                if (currentNode.getState().equals(goalState)) {
                    printMetrics("IDS (Profundidad Iterativa)", currentNode, contador, startTime);
                    return;
                }

                if (currentNode.getDepth() < limit) {
                    List<Node> children = NodeUtils.generateChildren(currentNode);
                    for (Node child : children) {
                        stack.push(child);
                    }
                }
            }
        }
    }
}