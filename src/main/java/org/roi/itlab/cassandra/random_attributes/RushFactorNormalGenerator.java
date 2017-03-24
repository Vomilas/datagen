package org.roi.itlab.cassandra.random_attributes;

/**
 * Created by Vadim on 20.03.2017.
 */

public class RushFactorNormalGenerator extends NormalGenerator {

    static double[] x = {18, 25,  35, 60, 90};
    static double[] y  = {3.0,1.5,1.0,0.8,0.7};
    static double[] z  = {0.5,0.3,0.2,0.2,0.1};

    public RushFactorNormalGenerator(org.apache.commons.math3.random.RandomGenerator rng) {
        super(rng, x, y, z);
    }
}
