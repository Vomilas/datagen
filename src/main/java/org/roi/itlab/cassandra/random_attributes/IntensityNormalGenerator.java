package org.roi.itlab.cassandra.random_attributes;

/**
 * Created by mkuperman on 3/19/2017.
 */
public class IntensityNormalGenerator extends NormalGenerator {

    private final int maxIntensity;
    private final static double[] x = new double[]{0, 1, 2, 5, 10};
    private final static double[] y = new double[]{1, 1, 1.1, 2.0, 5};
    private final static double[] z = new double[]{0.01, 0.1, 0.1, 0.2, 0.2};

    public IntensityNormalGenerator(int maxIntensity, org.apache.commons.math3.random.RandomGenerator rng) {
        super(rng, x, y, z);
        this.maxIntensity = maxIntensity;
    }

    @Override
    public double getRandomDouble(double value) {
        double sum = Math.log(value) * 10 / Math.log(maxIntensity);
        int index = Math.min((int) sum + 1, 10);

        return super.getRandomDouble(index);
    }
}
