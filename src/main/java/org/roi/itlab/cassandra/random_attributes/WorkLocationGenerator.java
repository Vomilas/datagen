package org.roi.itlab.cassandra.random_attributes;

import org.apache.commons.math3.util.Pair;
import org.roi.itlab.cassandra.PoiLoader;

import java.io.IOException;

/**
 * Created by Vadim on 20.03.2017.
 */
public class WorkLocationGenerator extends LocationGenerator {
    private static final String officePois = "./src/test/resources/org/roi/payg/saint-petersburg_russia_office.csv";

    public static final int WORK_TYPE = 1;
    public static final double RADIUS = 200.0;


    public WorkLocationGenerator(org.apache.commons.math3.random.RandomGenerator rng) throws IOException {
        super(rng, PoiLoader.loadFromCsv(officePois), new Pair<Integer, Double>(WORK_TYPE, RADIUS));
    }
}
