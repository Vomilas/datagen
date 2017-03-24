package org.roi.itlab.cassandra.random_attributes;


/**
 * Created by Anush
 */

public class AgeRandomGenerator extends RandomGenerator {

    static double[] x = {18.0,25.0,30.0,60.0,90.0};
    static double[] y  = {1.0,2.0,3.0,1.0, 0.1};

    public AgeRandomGenerator(org.apache.commons.math3.random.RandomGenerator rng) {
        super(rng, x,y);
    }

}