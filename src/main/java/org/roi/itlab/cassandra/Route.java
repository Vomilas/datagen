package org.roi.itlab.cassandra;

public class Route {
    private final Edge[] edges;
    //directions[i] true for forward direction(from baseNode to AdjNode) on Edge[i]
    private final boolean[] directions;
    private final int size;


    public Route(Edge[] edges, boolean[] directions) {
        this.edges = edges;
        this.size = edges.length;
        this.directions = directions;
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

    public String edgeIds() {
        StringBuilder sb = new StringBuilder();
        for (Edge e :
                edges) {
            sb.append(e.id).append(';');
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    public boolean[] getDirections() {
        return directions;
    }
}
