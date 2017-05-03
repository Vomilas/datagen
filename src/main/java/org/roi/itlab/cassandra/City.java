package org.roi.itlab.cassandra;

import com.graphhopper.util.DistanceCalcEarth;
import org.apache.commons.math3.random.RandomGenerator;
import org.roi.itlab.cassandra.person.Person;
import org.roi.itlab.cassandra.random_attributes.IntensityNormalGenerator;
import org.roi.itlab.cassandra.random_attributes.NormalGenerator;
import org.roi.itlab.cassandra.random_attributes.PersonGenerator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Mikhail Kuperman on 28.03.2017.
 */
public class City {

    private static final String INTENSITY_FILENAME = "./target/intensity_map";
    private static final String EDGES_FILENAME = "./target/edges_storage";
    private static final int PREVIOUS_YEARS = 5;
    private AccidentRate accidentRate;
    private List<Person> drivers;

    public City(int size, RandomGenerator rng) throws IOException, ClassNotFoundException {
        PersonGenerator personGenerator = new PersonGenerator(rng);
        drivers = IntStream.range(0, size).parallel().mapToObj(i -> personGenerator.getResult()).collect(Collectors.toList());

        IntensityMap intensityMap = new IntensityMap();
        Routing.loadEdgesStorage(EDGES_FILENAME);
        intensityMap.load(INTENSITY_FILENAME);

        accidentRate = new AccidentRate(intensityMap, rng);
    }

    public void simulate() {
        drivers.parallelStream().forEach(person -> {
            int previousAccidents = accidentRate.calculateAccidents(person, PREVIOUS_YEARS * 365);
            int accidents = accidentRate.calculateAccidents(person, 365);
            person.setAccidents(accidents);
            person.setPreviousAccidents(previousAccidents);
        });
    }

    public void save(String filename, int size) throws IOException {
        Path path = FileSystems.getDefault().getPath(filename);
        Files.deleteIfExists(path);
        Files.createFile(path);
        try (
            OutputStream out = Files.newOutputStream(path, StandardOpenOption.WRITE);
            OutputStreamWriter writer = new OutputStreamWriter(out, Charset.defaultCharset());) {
            writer.write("Age,Experience,Skill,RushFactor,WorkStart,WorkDuration,WorkEnd,HomeLat,HomeLng,WorkLat,WorkLng,Distance,RouteDistance,PreviousAccidents,Probability,Accidents" + '\n');
            DistanceCalcEarth earth = new DistanceCalcEarth();
            for (int i = 0; i < size; i++) {
                Person person = drivers.get(i);
                double distance = earth.calcDist(person.getHome().getLatitude(), person.getHome().getLongitude(), person.getWork().getLatitude(), person.getWork().getLongitude());
                writer.write(person.getAge() + "," + String.format(Locale.ROOT, "%.1f", person.getExperience()) + "," + String.format(Locale.ROOT, "%.3f", person.getSkill()) + "," + String.format(Locale.ROOT, "%.3f", person.getRushFactor()) + "," + person.getWorkStart().getHour() + "," + person.getWorkDuration().getHour() + "," + person.getWorkEnd().getHour() + "," + String.format(Locale.ROOT, "%.4f", person.getHome().getLatitude()) + "," + String.format(Locale.ROOT, "%.4f", person.getHome().getLongitude()) + "," + String.format(Locale.ROOT, "%.4f", person.getWork().getLatitude()) + "," + String.format(Locale.ROOT, "%.4f", person.getWork().getLongitude()) + "," + String.format(Locale.ROOT, "%.3f", distance) + "," + String.format(Locale.ROOT, "%.3f", person.getToWork().getDistance() + person.getToHome().getDistance()) + "," + person.getPreviousAccidents() + "," + String.format(Locale.ROOT, "%.6f", person.getProbability()) + "," + person.getAccidents() + '\n');
            }
        }
    }

    public void saveRoutes(String filename, int size) throws IOException {
        Path path = FileSystems.getDefault().getPath(filename);
        Files.deleteIfExists(path);
        Files.createFile(path);
        try (
                OutputStream out = Files.newOutputStream(path, StandardOpenOption.WRITE);
                OutputStreamWriter writer = new OutputStreamWriter(out, Charset.defaultCharset());) {
            for (int i = 0; i < size; i++) {
                Person person = drivers.get(i);
                writer.write(person.getToWork().edgeIds() + ',' + person.getToHome().edgeIds() + '\n');
            }
        }
    }
}
