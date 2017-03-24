package org.roi.itlab.cassandra.random_attributes;

/**
 * Created by mkuperman on 3/19/2017.
 */
public class SkillNormalGenerator extends NormalGenerator {

    static double[] x = {0.0,5.0,10.0,20.0,90};
    static double[] y  = {0.5,0.9,1.2,1.3,1.0};
    static double[] z  = {0.1,0.2,0.3,0.3,0.2};

    public SkillNormalGenerator(org.apache.commons.math3.random.RandomGenerator rng) {
        super(rng, x, y, z);
    }
}
