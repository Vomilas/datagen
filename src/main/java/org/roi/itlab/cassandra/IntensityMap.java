package org.roi.itlab.cassandra;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.round;

class IntensityMap {

    private class Timetable {
        static final int SIZE = 288;
        private int[] timetable;

        Timetable(int[] arr) {
            timetable = arr.clone();
        }

        Timetable() {
            timetable = new int[SIZE];
        }

        int getIntensity(long time) {
            return timetable[getMinutes(time) / 5];
        }

        int getIntensityByNumber(int n) {
            return timetable[n];
        }

        void intensify(long time) {
            ++timetable[getMinutes(time) / 5];
        }

        String toCSV() {
            StringBuilder sb = new StringBuilder(500);
            for (int i = 0; i < SIZE; i++) {
                sb.append(timetable[i]).append('|');
            }
            return sb.toString();
        }
    }

    private Map<Edge, Timetable> map;

    public IntensityMap() {
        map = new HashMap<>();
    }

    public IntensityMap(int capacity) {
        map = new HashMap<>(capacity);
    }

    public void put(long starttime, Route route) {
        long time = starttime;
        for (Edge e :
                route.getEdges()) {
            map.putIfAbsent(e, new Timetable());
            map.get(e).intensify(time);
            time += e.getTime();
        }
    }

    public void put(List<Long> timeList, List<Route> routes) {

        if (timeList.size() != routes.size()) {
            throw new IllegalArgumentException("Sizes of the lists must be equal!");
        }

        for (int i = 0; i < routes.size(); ++i) {
            this.put(timeList.get(i), routes.get(i));

        }
    }

    //translates the miliseconds from 1970-01-01 @ 00:00:00 to minutes from the beginning of the current day
    private int getMinutes(long time) {
        return (int) (time / 1000 / 60 % (60 * 24));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int hour;
        int minute;
        for (Map.Entry<Edge, Timetable> entry : map.entrySet()) {
            builder.append(entry.getKey().toString() + ":\n");
            for (int i = 0; i < Timetable.SIZE; ++i) {
                if (entry.getValue().getIntensityByNumber(i) != 0) {
                    hour = i * 5 / 60;
                    minute = i * 5 % 60;
                    builder.append(hour + ":" + minute + " " + entry.getValue().getIntensityByNumber(i) + "\n");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    //traffic intensity on one edge
    int getIntensity(Edge edge, long time) {
        if (map.containsKey(edge)) {
            return map.get(edge).getIntensity(time);
        }
        return 0;
    }

    //traffic intensity on road network
    Map<Edge, Integer> getIntensity(long time) {
        Map<Edge, Integer> temp = new HashMap<>();
        for (Edge edge :
                map.keySet()) {
            temp.put(edge, getIntensity(edge, time));
        }
        return temp;
    }

    //edgeID|intensity at 00:00|intensity at 00:05|...|intensity at 23:55
    public void writeToCSV(OutputStreamWriter writer) throws IOException {
        for (Map.Entry<Edge, Timetable> entry :
                map.entrySet()) {
            writer.write(Integer.toString(entry.getKey().id));
            writer.write('|');
            writer.write(entry.getValue().toCSV());
            writer.write('\n');
        }
        writer.close();
    }

    public void loadFromCSV(String filename) throws IOException {
        Consumer<String> putEdge = s -> {
            String[] p = s.split("\\|");
            int[] timetable = new int[p.length - 1];
            for (int i = 1; i < p.length; i++) {
                timetable[i - 1] = Integer.parseInt(p[i]);
            }
            map.put(Routing.getEdge(Integer.parseInt(p[0])), new Timetable(timetable));
        };
        Files.lines(Paths.get(filename)).forEach(putEdge);
    }

    void makeGeoJSON(File outputFile) throws IOException {
        FileOutputStream output = new FileOutputStream(outputFile, false);

        GeometryCollection[] geometries = new GeometryCollection[10]; // one collection for each level of load

        for (int i =0; i<10; ++i)
        {
            geometries[i] = new GeometryCollection();
        }

        double maxLoad = 0;

        HashMap <Edge, Double> averageLoads = new HashMap<Edge, Double>();

        //looking for maximum load
        for (HashMap.Entry pair : map.entrySet()) {

            int timetableIndex = 0;
            double averageLoad = 0;
            while (timetableIndex < Timetable.SIZE)
            {
                //getting load every 30 minutes and adding it to averageLoad
                averageLoad += ((Timetable) pair.getValue()).getIntensityByNumber(timetableIndex);
                timetableIndex += 6;
            }

            averageLoad /= (Timetable.SIZE / 6);  // actual average load
            averageLoads.put((Edge)pair.getKey(), averageLoad);
            if (averageLoad > maxLoad)
            {
                maxLoad = averageLoad;
            }

        }

        for (HashMap.Entry pair : averageLoads.entrySet()) {

            //finding suitable level of load to each edge
            int index = 1;
            for (double step = maxLoad/10; (step <= maxLoad) && ((double)pair.getValue() - step > 0.00001); step += maxLoad/10)
            {
                ++index;
                if (index > 10)
                {
                    System.out.println("Error!");
                }
            }

            //converting everything to library format
            double longtitudeStart = ((Edge)pair.getKey()).getStart().getLon();
            double latitudeStart = ((Edge)pair.getKey()).getStart().getLat();
            double longtitudeEnd = ((Edge)pair.getKey()).getEnd().getLon();
            double latitudeEnd = ((Edge)pair.getKey()).getEnd().getLat();


            geometries[index - 1].add(
                    new LineString(
                            new LngLatAlt(longtitudeStart, latitudeStart), new LngLatAlt(longtitudeEnd, latitudeEnd)
                    )
            );
        }

        FeatureCollection georoute = new FeatureCollection();
        for(int i=1; i<=10; ++i)
        {
            Feature loadGroup = new Feature();
            loadGroup.setProperty("load", i);
            loadGroup.setGeometry(geometries[i-1]);
            georoute.add(loadGroup);
        }

        ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(output, georoute);

        output.close();
    }
}
