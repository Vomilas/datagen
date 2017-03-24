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

    public Edge(int id, Point start, Point end, PointList geometry, double distance, int time, double speed) {
        this.start = start;
        this.end = end;
        this.id = id;
        this.geometry = geometry;
        this.distance = distance;
        this.time = time;
        this.speed = speed;
    }

    public Edge(String input) {
        String[] p = input.split("\\|");
        this.id = Integer.parseInt(p[0]);
        this.time = Integer.parseInt(p[2]);
        this.distance = Double.parseDouble(p[3]);
        this.speed = Double.parseDouble(p[4]);
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
                append(this.speed);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}
