package org.roi.itlab.cassandra;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.FeatureCollection;
import org.geojson.GeoJsonObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.roi.itlab.cassandra.person.Person;
import org.roi.itlab.cassandra.random_attributes.PersonGenerator;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

public class TrafficTestIT {
    private static final String INTENSITY_FILENAME = "./target/intensity_map";
    private static final String EDGES_FILENAME = "./target/edges_storage";
    private static final String GEO_JSON_FILENAME_PREFIX = "./target/";

    private static final int DRIVERS_COUNT = 100_000;

    @Before
    public void intensityMapCreate() throws IOException {
        PersonGenerator personGenerator = new PersonGenerator();
        List<Person> drivers = IntStream.range(0, DRIVERS_COUNT).parallel().mapToObj(i -> personGenerator.getResult()).collect(Collectors.toList());
        IntensityMap traffic = new IntensityMap(drivers);

        System.out.println("Max intensity: " + traffic.getMaxIntensity());

        Path path2 = FileSystems.getDefault().getPath(EDGES_FILENAME);
        OutputStream out2 = Files.newOutputStream(path2);
        OutputStreamWriter writer2 = new OutputStreamWriter(out2, Charset.defaultCharset());

        Routing.saveEdgesStorage(writer2);
        traffic.save(INTENSITY_FILENAME);
    }

    @Ignore
    @Test
    public void IntensityMapConvert() throws IOException, ClassNotFoundException {
        IntensityMap traffic = new IntensityMap();
        Routing.loadEdgesStorage(EDGES_FILENAME);
        traffic.load(INTENSITY_FILENAME);

        makeGeoJSON(traffic, GEO_JSON_FILENAME_PREFIX + "map", 7, 0);
        makeGeoJSON(traffic, GEO_JSON_FILENAME_PREFIX + "map", 9, 0);
        makeGeoJSON(traffic, GEO_JSON_FILENAME_PREFIX + "map", 11, 0);
        makeGeoJSON(traffic, GEO_JSON_FILENAME_PREFIX + "map", 18, 0);
        makeGeoJSON(traffic, GEO_JSON_FILENAME_PREFIX + "map", 23, 0);
    }

    private void makeGeoJSON(IntensityMap traffic, String filenamePrefix, int hour, int minute) throws IOException {
        File outputFile = Paths.get(filenamePrefix + "_" + hour + "_" + minute + ".geojson").toFile();
        traffic.makeGeoJSON(outputFile, LocalTime.of(hour, minute).toSecondOfDay() * 1000);
    }

    @Test
    public void IntensityMapNewLoading() throws IOException, ClassNotFoundException {
        IntensityMap traffic = new IntensityMap();
        Routing.loadEdgesStorage(EDGES_FILENAME);
        traffic.load(INTENSITY_FILENAME);
        makeGeoJSON(traffic, GEO_JSON_FILENAME_PREFIX + "test", 9, 0);
        GeoJsonObject object = new ObjectMapper().readValue(new FileInputStream(GEO_JSON_FILENAME_PREFIX + "test_9_0.geojson"), GeoJsonObject.class);
        assertTrue(object instanceof FeatureCollection);
    }
}
