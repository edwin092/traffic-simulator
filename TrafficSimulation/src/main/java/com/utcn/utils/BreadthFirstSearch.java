package com.utcn.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BreadthFirstSearch {

    private static int k;
    private static Map<Integer, List<Integer>> solution;

    /**
     * @param graph
     * @param visited
     * @param end
     */
    private static void bfs(SimulationGraph graph, LinkedList<Integer> visited, Integer end) {
        LinkedList<Integer> nodes = graph.adjacentNodes(visited.getLast());
        // examine adjacent nodes
        for (Integer node : nodes) {
            if (visited.contains(node)) {
                continue;
            }
            if (node.equals(end)) {
                visited.add(node);
//                printPath(visited);
                k++;
                solution.put(k, (List<Integer>) visited.clone());
                visited.removeLast();
                break;
            }
        }
        // in breadth-first, recursion needs to come after visiting adjacent nodes
        for (Integer node : nodes) {
            if (visited.contains(node) || node.equals(end)) {
                continue;
            }
            visited.addLast(node);
            bfs(graph, visited, end);
            visited.removeLast();
        }
    }

    public static Map<Integer, List<Integer>> breadthFirst(SimulationGraph graph, LinkedList<Integer> visited, Integer end) {
        k = 0;
        solution = new HashMap<>();
        bfs(graph, visited, end);
        return solution;
    }


    private static void printPath(LinkedList<Integer> visited) {
        for (Integer node : visited) {
            System.out.print(node);
            System.out.print(" ");
        }
        System.out.println();
    }
}
