package org.roi.itlab.cassandra.random_attributes;

import org.apache.commons.math3.random.MersenneTwister;

/**
 * author Anush
 */
public class WorkStartRandomGenerator extends RandomGenerator {

    static double[] x = {7.0,9.0,12.0,19.0};
    static double[] y  = {3.0,10.0,1.0,0.1};

    public WorkStartRandomGenerator(org.apache.commons.math3.random.RandomGenerator rng) {
        super(rng, x, y);
    }
}
