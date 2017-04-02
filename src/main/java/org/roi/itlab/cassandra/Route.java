package org.roi.itlab.cassandra;

public class Route {
    private final Edge[] edges;
    private final int size;

    public Route(Edge[] edges) {
        this.edges = edges;
        this.size = edges.length;
    }

    public Edge[] getEdges() {
        return edges;
    }

    public int getSize() {
        return size;
    }

    public double getDistance() {
        double distance = 0;
        for (Edge edge : edges) {
            distance += edge.getDistance();
        }
        return distance;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(edges[i].toString() + '\n');
        }
        return sb.toString();
    }
}
