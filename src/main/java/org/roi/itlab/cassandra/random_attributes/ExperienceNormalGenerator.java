package org.roi.itlab.cassandra.random_attributes;

/**
 * Created by Vadim on 20.03.2017.
 */
public class ExperienceNormalGenerator extends NormalGenerator {

    static double[] x = new double[]{18, 25,  30, 60, 90};
    static double[] y = new double[]{1, 4, 8, 35, 43};
    static double[] z = new double[]{0.1, 0.2, 0.6, 5.0, 7.0};

    public ExperienceNormalGenerator(org.apache.commons.math3.random.RandomGenerator rng){
        super(rng, x, y, z);
    }
}
