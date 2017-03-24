package org.roi.itlab.cassandra;

import com.graphhopper.util.DistanceCalc;
import com.graphhopper.util.DistanceCalcEarth;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.util.Pair;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mongodb.morphia.geo.Point;
import org.roi.itlab.cassandra.person.Person;
import org.roi.itlab.cassandra.random_attributes.LocationGenerator;
import org.roi.itlab.cassandra.random_attributes.PersonGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;

public class TrafficTestIT {
    private static final DistanceCalc DIST_EARTH = new DistanceCalcEarth();
    private static final String testPois = "./src/test/resources/org/roi/payg/saint-petersburg_russia.csv";
    private static final String officePois = "./src/test/resources/org/roi/payg/saint-petersburg_russia_office.csv";
    private static final String target = "./target/intensity.txt";
    private static final String target2 = "./target/drivers.txt";
    private static final String IntensityMapSaveFile = "./target/traffic/intensity_map";
    private static final String EdgesStorageSaveFile = "./target/traffic/edges_storage";
    private static final String EdgesStrorageLoadFile = "./src/test/resources/edges_storage";
    private static final String IntenstityMapLoadFile = "./src/test/resources/intensity_map";

    private static final int ROUTES_COUNT = 1_000;
    static List<Route> routesToWork = new ArrayList<>(ROUTES_COUNT);
    static List<Route> routesFromWork = new ArrayList<>(ROUTES_COUNT);
    static List<Person> drivers;

    @BeforeClass
    public static void init() throws IOException {
        List<Poi> pois = PoiLoader.loadFromCsv(testPois);
        List<Poi> offices = PoiLoader.loadFromCsv(officePois);
        List<Point> homelocations = new LocationGenerator(new MersenneTwister(1), pois, new Pair<Integer, Double>(20, 200.0)).sample(ROUTES_COUNT);
        List<Point> worklocations = new LocationGenerator(new MersenneTwister(1), offices, new Pair<Integer, Double>(1, 200.0)).sample(ROUTES_COUNT);

        //generating locations and routes;
        int routingFailedCounter = 0;
        for (Point home :
                homelocations) {
            Predicate<Point> notTooFarFromHome = work -> DIST_EARTH.calcDist(home.getLatitude(), home.getLongitude(),
                    work.getLatitude(), work.getLongitude()) < 15_000;
            Predicate<Point> notTooCloseToHome = work -> DIST_EARTH.calcDist(home.getLatitude(), home.getLongitude(),
                    work.getLatitude(), work.getLongitude()) > 2_000;
            Point work = worklocations.stream().
                    filter(notTooFarFromHome).
                    filter(notTooCloseToHome).
                    findAny().orElse(home);

            try {
                Route routeToWork = Routing.route(home.getLatitude(), home.getLongitude(), work.getLatitude(), work.getLongitude());
                Route routeFromWork = Routing.route(work.getLatitude(), work.getLongitude(), home.getLatitude(), home.getLongitude());
                routesToWork.add(routeToWork);
                routesFromWork.add(routeFromWork);
            } catch (IllegalStateException e) {
                routingFailedCounter++;
            }
        }

        //generating drivers
        PersonGenerator personGenerator = new PersonGenerator();
        drivers = new ArrayList<>(routesFromWork.size());
        for (int i = 0; i < routesFromWork.size(); i++) {
            drivers.add(personGenerator.getResult());
        }
        System.out.println(routingFailedCounter);
    }

    @Test
    public void saveDrivers() throws IOException {
        Path path = FileSystems.getDefault().getPath(target2);
        Files.deleteIfExists(path);
        Files.createFile(path);
        OutputStream out = Files.newOutputStream(path, StandardOpenOption.WRITE);
        OutputStreamWriter writer = new OutputStreamWriter(out, Charset.defaultCharset());

        writer.write("Age,Experience,Skill,RushFactor,WorkStart,WorkDuration,WorkEnd,HomeLat,HomeLng,WorkLat,WorkLng" + '\n');
        for (Person person : drivers) {
            writer.write(person.getAge() + "," + person.getExperience() + "," + String.format("%.3f", person.getSkill()) + "," + String.format("%.3f", person.getRushFactor()) + "," + person.getWorkStart() + "," + person.getWorkDuration() + "," + person.getWorkEnd() + "," + String.format("%.4f", person.getHome().getLatitude()) + "," + String.format("%.4f", person.getHome().getLongitude()) + "," + String.format("%.4f", person.getWork().getLatitude()) + "," + String.format("%.4f", person.getWork().getLongitude()) + '\n');
        }
        writer.write("_\n");
        writer.close();
    }

    @Test
    public void writeIntensityDistribution() throws IOException {
        //filling intensity map
        IntensityMap traffic = new IntensityMap();
        for (int i = 0; i < routesFromWork.size(); i++) {
            long startTime = drivers.get(i).getWorkStart().toSecondOfDay() * 1000;
            long endTime = drivers.get(i).getWorkEnd().toSecondOfDay() * 1000;
            traffic.put(startTime, routesToWork.get(i));
            traffic.put(endTime, routesFromWork.get(i));
        }

        Path path = FileSystems.getDefault().getPath(target);
        Files.deleteIfExists(path);
        Files.createFile(path);
        OutputStream out = Files.newOutputStream(path, StandardOpenOption.WRITE);
        OutputStreamWriter writer = new OutputStreamWriter(out, Charset.defaultCharset());

        // lines of:{intensity} {length in meters of road segments with this intensity}
        // for every 5 minutes separated with "_\n"
        long time = 0;
        for (int i = 0; i < 288; i++) {
            Map<Integer, Double> intensityDistance = new HashMap<>();
            Map<Edge, Integer> intensity = traffic.getIntensity(time);
            time += 300000L;
            for (Map.Entry<Edge, Integer> entry :
                    intensity.entrySet()) {
                intensityDistance.putIfAbsent(entry.getValue(), 0.0);
                intensityDistance.computeIfPresent(entry.getValue(), (key, sum) -> sum + entry.getKey().getDistance());
            }
            System.out.println(time);
            System.out.println(intensityDistance);
            for (Map.Entry<Integer, Double> e :
                    intensityDistance.entrySet()) {
                writer.write(e.getKey() + " " + e.getValue() + '\n');
            }
            writer.write("_\n");
        }
        writer.close();
    }


    public void IntensityMapSaving() throws IOException {
        IntensityMap traffic = new IntensityMap();
        for (int i = 0; i < routesFromWork.size(); i++) {
            long startTime = drivers.get(i).getWorkStart().toSecondOfDay() * 1000;
            long endTime = drivers.get(i).getWorkEnd().toSecondOfDay() * 1000;
            traffic.put(startTime, routesToWork.get(i));
            traffic.put(endTime, routesFromWork.get(i));
        }

        Path path = FileSystems.getDefault().getPath(IntensityMapSaveFile);
        Files.deleteIfExists(path);
        Files.createFile(path);
        OutputStream out = Files.newOutputStream(path, StandardOpenOption.WRITE);
        OutputStreamWriter writer = new OutputStreamWriter(out, Charset.defaultCharset());

        Path path2 = FileSystems.getDefault().getPath(EdgesStorageSaveFile);
        Files.deleteIfExists(path2);
        Files.createFile(path2);
        OutputStream out2 = Files.newOutputStream(path2, StandardOpenOption.WRITE);
        OutputStreamWriter writer2 = new OutputStreamWriter(out2, Charset.defaultCharset());

        Routing.saveEdgesStorage(writer2);
        traffic.writeToCSV(writer);

    }

    @Test
    public void IntensityMapLoading() throws IOException {
        IntensityMap loadedtraffic = new IntensityMap();

        Routing.loadEdgesStorage(EdgesStrorageLoadFile);
        loadedtraffic.loadFromCSV(IntenstityMapLoadFile);
    }

}
