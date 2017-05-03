package org.roi.itlab.cassandra;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roi.itlab.cassandra.person.Person;
import org.roi.itlab.cassandra.random_attributes.PersonGenerator;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Аня on 22.03.2017.
 */
public class AccidentRateIT {

    private static final int ALL_DRIVERS_COUNT = 10_000;
    private static final int DRIVERS_COUNT = 1_000;
    private AccidentRate accidentRate;
    private List<Person> drivers;
    private RandomGenerator rng = new MersenneTwister(2);

    @Before
    public void setUp() throws IOException {
        PersonGenerator personGenerator = new PersonGenerator(rng);
        drivers = IntStream.range(0, ALL_DRIVERS_COUNT).parallel().mapToObj(i -> personGenerator.getResult()).collect(Collectors.toList());

        IntensityMap intensityMap = new IntensityMap(drivers);
        accidentRate = new AccidentRate(intensityMap, rng);
    }

    @Test
    public void accidentRateTest() {
        int accidents = drivers.subList(0, DRIVERS_COUNT).parallelStream().mapToInt(person -> accidentRate.calculateAccidents(person, 365)).sum();
        System.out.println("Accidents: " + accidents);
        Assert.assertEquals((double) accidents / DRIVERS_COUNT, 0.35, 0.2);
    }
}
