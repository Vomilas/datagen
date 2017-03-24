package org.roi.itlab.cassandra.random_attributes;

/**
 * author Anush
 */
public class WorkDurationRandomGenerator extends RandomGenerator {

    static double[] x = {4.0,8.0,12.0};
    static double[] y  = {1.0,10.0,2.0};

    public WorkDurationRandomGenerator(org.apache.commons.math3.random.RandomGenerator rng) {
        super(rng, x, y);
    }
}
