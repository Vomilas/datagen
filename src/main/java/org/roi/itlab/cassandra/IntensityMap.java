package org.roi.itlab.cassandra;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.geojson.*;
import org.roi.itlab.cassandra.person.Person;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

class IntensityMap {

    private static class Timetable implements Serializable {
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

        int getMaxIntensity() {
            return Arrays.stream(timetable).max().orElse(0);
        }

        String toCSV() {
            StringBuilder sb = new StringBuilder(500);
            for (int i = 0; i < SIZE; i++) {
                sb.append(timetable[i]).append('|');
            }
            return sb.toString();
        }
    }

    private Map<Edge, Timetable> mapForward;
    private Map<Edge, Timetable> mapBackward;

    public IntensityMap() {
        mapForward = new HashMap<>();
        mapBackward = new HashMap<>();
    }

    public IntensityMap(List<Person> drivers) {
        this();
        for (Person driver : drivers) {
            long startTime = driver.getWorkStart().toSecondOfDay() * 1000;
            long endTime = driver.getWorkEnd().toSecondOfDay() * 1000;
            put(startTime, driver.getToWork());
            put(endTime, driver.getToHome());
        }
    }

    public int getSize() {
        return mapForward.size() + mapBackward.size();
    }

    public void put(long starttime, Route route) {
        long time = starttime;
        Edge[] edges = route.getEdges();
        boolean[] directions = route.getDirections();
        for (int i = 0; i < edges.length; i++) {
            Edge e = edges[i];
            boolean direction = directions[i];
            if (direction) {
                mapForward.putIfAbsent(e, new Timetable());
                mapForward.get(e).intensify(time);
            } else {
                mapBackward.putIfAbsent(e, new Timetable());
                mapBackward.get(e).intensify(time);
            }
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

    public int getMaxIntensity() {
        int result = 0;
        for (Timetable timetable : mapForward.values()) {
            result = Math.max(result, timetable.getMaxIntensity());
        }
        for (Timetable timetable : mapBackward.values()) {
            result = Math.max(result, timetable.getMaxIntensity());
        }
        return result;
    }

    //translates the miliseconds from 1970-01-01 @ 00:00:00 to minutes from the beginning of the current day
    private static int getMinutes(long time) {
        return (int) (time / 1000 / 60 % (60 * 24));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int hour;
        int minute;
        for (Map.Entry<Edge, Timetable> entry : mapForward.entrySet()) {
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
    int getIntensity(Edge edge, long time, boolean direction) {
        if (direction) {
            if (mapForward.containsKey(edge)) {
                return mapForward.get(edge).getIntensity(time);
            }
        } else {
            if (mapBackward.containsKey(edge)) {
                return mapBackward.get(edge).getIntensity(time);
            }
        }
        return 0;
    }


    private static class forSerialize implements Serializable {
        private int edgeId;
        private Timetable forward;
        private Timetable backward;

        public forSerialize(int edgeId, Timetable forward, Timetable backward) {
            this.edgeId = edgeId;
            this.forward = forward;
            this.backward = backward;
        }
    }

    public void save(String filename) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filename, false);
             ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            Set<Edge> edges = new HashSet<>();
            edges.addAll(mapForward.keySet());
            edges.addAll(mapBackward.keySet());
            Timetable empty = new Timetable();
            for (Edge edge :
                    edges) {
                forSerialize temp = new forSerialize(edge.id, mapForward.getOrDefault(edge, empty), mapBackward.getOrDefault(edge, empty));
                oos.writeObject(temp);
            }
        }
    }

    public void load(String filename) throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(filename);
             ObjectInputStream ois = new ObjectInputStream(fis);) {
            int count = 0;
            while (fis.available() > 0) {
                count++;
                forSerialize temp = (forSerialize) ois.readObject();
                mapForward.put(Routing.getEdge(temp.edgeId), temp.forward);
                mapBackward.put(Routing.getEdge(temp.edgeId), temp.backward);
            }
        }
        System.out.printf("Loaded IntensityMap, %d edges, max intensity = %d\n", getSize(), getMaxIntensity());
    }

    //edgeID|intensity at 00:00|intensity at 00:05|...|intensity at 23:55
    public void writeToCSV(OutputStreamWriter writer) throws IOException {
        for (Map.Entry<Edge, Timetable> entry :
                mapForward.entrySet()) {
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
            mapForward.put(Routing.getEdge(Integer.parseInt(p[0])), new Timetable(timetable));
        };
        Files.lines(Paths.get(filename)).forEach(putEdge);
        System.out.printf("Loaded IntensityMap, %d edges, max intensity = %d\n", getSize(), getMaxIntensity());
    }

    void makeGeoJSON(File outputFile, long time) throws IOException {
        System.out.printf("Convert to %s\n", outputFile);
        FileOutputStream output = new FileOutputStream(outputFile, false);

        GeometryCollection[] geometries = new GeometryCollection[10]; // one collection for each level of load

        for (int i = 0; i < 10; ++i) {
            geometries[i] = new GeometryCollection();
        }

        int maxSum = getMaxIntensity();
        for (Map.Entry<Edge, Timetable> entry : mapForward.entrySet()) {

            Edge edge = entry.getKey();
            int load = entry.getValue().getIntensity(time);
            if (load == 0)
                continue;

            double sum = Math.log(load) * 10 / Math.log(maxSum);
            int index = Math.min((int) sum + 1, 10);

            //converting everything to library format
            double longtitudeStart = new BigDecimal(edge.getStart().getLon()).setScale(6, RoundingMode.FLOOR).doubleValue();
            double latitudeStart = new BigDecimal(edge.getStart().getLat()).setScale(6, RoundingMode.FLOOR).doubleValue();
            double longtitudeEnd = new BigDecimal(edge.getEnd().getLon()).setScale(6, RoundingMode.FLOOR).doubleValue();
            double latitudeEnd = new BigDecimal(edge.getEnd().getLat()).setScale(6, RoundingMode.FLOOR).doubleValue();

            geometries[index - 1].add(
                    new LineString(
                            new LngLatAlt(longtitudeStart, latitudeStart), new LngLatAlt(longtitudeEnd, latitudeEnd)
                    )
            );
        }


        FeatureCollection georoute = new FeatureCollection();
        for (int i = 1; i <= 10; ++i) {
            Feature loadGroup = new Feature();
            loadGroup.setProperty("load", i);
            loadGroup.setGeometry(geometries[i - 1]);
            georoute.add(loadGroup);
            System.out.printf("Group %d contains %d geometries\n", i, geometries[i - 1].getGeometries().size());
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(output, georoute);

        output.close();

    }
}
