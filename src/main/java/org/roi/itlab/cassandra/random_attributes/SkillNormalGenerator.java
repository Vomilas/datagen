package org.roi.itlab.cassandra.random_attributes;

/**
 * Created by mkuperman on 3/19/2017.
 */
public class SkillNormalGenerator extends NormalGenerator {

    static double[] x = {0.0,5,10,20,40,60};
    static double[] y  = {0.5,1.0,1.4,1.5,1.2,1.1};
    static double[] z  = {0.1,0.15,0.2,0.2,0.1,0.1};

    public SkillNormalGenerator(org.apache.commons.math3.random.RandomGenerator rng) {
        super(rng, x, y, z);
    }
}
