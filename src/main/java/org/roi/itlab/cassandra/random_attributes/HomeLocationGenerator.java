package org.roi.itlab.cassandra.random_attributes;

import org.apache.commons.math3.random.*;
import org.apache.commons.math3.util.Pair;
import org.mongodb.morphia.geo.GeoJson;
import org.roi.itlab.cassandra.Location;
import org.mongodb.morphia.geo.Point;
import org.roi.itlab.cassandra.PoiLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vadim on 19.03.2017.
 */
public class HomeLocationGenerator extends LocationGenerator {
    private static final String testPois = "./src/test/resources/org/roi/payg/saint-petersburg_russia.csv";

    public static final int HOME_TYPE = 20;
    public static final double RADIUS = 200.0;


    public HomeLocationGenerator(org.apache.commons.math3.random.RandomGenerator rng) throws IOException {
        super(rng, PoiLoader.loadFromCsv(testPois), new Pair<Integer, Double>(HOME_TYPE, RADIUS));
    }
}
