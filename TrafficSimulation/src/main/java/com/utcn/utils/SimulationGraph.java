package com.utcn.utils;

import java.util.*;


public class SimulationGraph {
    private Map<Integer, LinkedHashSet<Integer>> map = new HashMap<>();

    public void addEdge(Integer node1, Integer node2) {
        LinkedHashSet<Integer> adjacent = map.get(node1);
        if (adjacent == null) {
            adjacent = new LinkedHashSet<>();
            map.put(node1, adjacent);
        }
        adjacent.add(node2);
    }

    public void addTwoWayVertex(Integer node1, Integer node2) {
        addEdge(node1, node2);
        addEdge(node2, node1);
    }

    public boolean isConnected(Integer node1, Integer node2) {
        Set adjacent = map.get(node1);
        if (adjacent == null) {
            return false;
        }
        return adjacent.contains(node2);
    }

    public LinkedList<Integer> adjacentNodes(Integer last) {
        LinkedHashSet<Integer> adjacent = map.get(last);
        if (adjacent == null) {
            return new LinkedList<>();
        }
        return new LinkedList<>(adjacent);
    }
}
