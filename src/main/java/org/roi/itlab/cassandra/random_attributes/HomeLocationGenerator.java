package org.roi.itlab.cassandra.random_attributes;

import org.apache.commons.math3.util.Pair;
import org.roi.itlab.cassandra.PoiLoader;

import java.io.IOException;

/**
 * Created by Vadim on 19.03.2017.
 */
public class HomeLocationGenerator extends LocationGenerator {
    private static final String testPois = "./src/test/resources/org/roi/payg/saint-petersburg_russia.csv";

    public static final Pair<Integer, Double> KINDERGARTEN = new Pair<>(20, 200.0);
    public static final Pair<Integer, Double> SCHOOL = new Pair<>(21, 300.0);


    public HomeLocationGenerator(org.apache.commons.math3.random.RandomGenerator rng) throws IOException {
        super(rng, PoiLoader.loadFromCsv(testPois), KINDERGARTEN, SCHOOL);
    }
}
