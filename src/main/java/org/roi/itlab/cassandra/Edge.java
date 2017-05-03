package org.roi.itlab.cassandra;


import com.graphhopper.json.geo.Point;
import com.graphhopper.util.PointList;

//Road segment
public class Edge {

    final int id;
    private final Point start;
    private final Point end;
    //including start and end points
    private PointList geometry;
    private double distance;
    // distance/average speed
    private int time;
    private double speed;
    private boolean oneWay;
    private int baseNodeId;

    public Edge(int id, Point start, Point end, PointList geometry, double distance, int time, double speed, boolean oneWay, int baseNodeId) {
        this.start = start;
        this.end = end;
        this.id = id;
        this.geometry = geometry;
        this.distance = distance;
        this.time = time;
        this.speed = speed;
        this.oneWay = oneWay;
        this.baseNodeId = baseNodeId;
    }

    public Edge(String input) {
        String[] p = input.split("\\|");
        this.id = Integer.parseInt(p[0]);
        this.time = Integer.parseInt(p[2]);
        this.distance = Double.parseDouble(p[3]);
        this.speed = Double.parseDouble(p[4]);
        this.oneWay = Boolean.parseBoolean(p[5]);
        this.baseNodeId = Integer.parseInt(p[6]);
        String[] points = p[1].substring(1).split("[, ()]+");

        this.geometry = new PointList();
        for (int i = 0; i < points.length; i += 2) {
            this.geometry.add(Double.parseDouble(points[i]), Double.parseDouble(points[i + 1]));
        }
        this.start = new Point(geometry.getLat(0), geometry.getLon(0));
        this.end = new Point(geometry.getLat(geometry.size() - 1), geometry.getLon(geometry.size() - 1));
    }


    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    public PointList getGeometry() {
        return geometry;
    }

    public double getDistance() {
        return distance;
    }

    public int getTime() {
        return time;
    }

    public double getSpeed() {
        return speed;
    }

    public boolean isOneWay() {
        return oneWay;
    }

    public void setOneWay(boolean oneWay) {
        this.oneWay = oneWay;
    }

    @Override
    public boolean equals(Object ob) {
        if (ob == null) {
            return false;
        }
        if (ob.getClass() != this.getClass()) {
            return false;
        }
        if (this.id == ((Edge) ob).id) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "edge id: " + id + " start: " + start.toString() + " end: " + end.toString();
    }

    public String write() {
        StringBuilder sb = new StringBuilder();
        sb.
                append(id).append('|').
                append(this.geometry.toString()).append('|').
                append(this.time).append('|').
                append(this.distance).append('|').
                append(this.speed).append('|').
                append(this.oneWay).append('|').
                append(baseNodeId);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    public int getBaseNodeId() {
        return baseNodeId;
    }
}
