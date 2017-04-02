package org.roi.itlab.cassandra;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.roi.itlab.cassandra.person.Person;
import org.roi.itlab.cassandra.random_attributes.PersonGenerator;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertNotEquals;

/**
 * Created by Mikhail Kuperman on 28.03.2017.
 */
public class CityIT {

    private static final String DRIVERS_FILENAME_PREFIX = "./target/drivers";

    private static final int DRIVERS_COUNT = 5_000;
    private RandomGenerator rng = new MersenneTwister(2);

    @Test
    @Ignore
    public void citySimulationTest() throws IOException {
        City city = new City(DRIVERS_COUNT, rng);
        city.simulate();
        city.save(DRIVERS_FILENAME_PREFIX + "_" + DRIVERS_COUNT + ".csv");
    }
}
