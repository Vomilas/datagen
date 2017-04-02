package org.roi.itlab.cassandra.random_attributes;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

/**
 * Created by mkuperman on 3/18/2017.
 */
public class NormalGenerator {
    private double max;
    private double min;

    private PolynomialSplineFunction meanPsf;
    private PolynomialSplineFunction devPsf;
    private org.apache.commons.math3.random.RandomGenerator rng;

    public NormalGenerator(org.apache.commons.math3.random.RandomGenerator rng){
        this.rng = rng;
    }

    public NormalGenerator(org.apache.commons.math3.random.RandomGenerator rng, double[] x, double[] y, double z[]) {
        this.rng = rng;
        LinearInterpolator li = new LinearInterpolator();
        setMeanPsf(li.interpolate(x,y));
        setDevPsf(li.interpolate(x,z));

        this.min = meanPsf.getKnots()[0];
        this.max = meanPsf.getKnots()[meanPsf.getKnots().length-1];
    }

    public void setMax(double max) {
        this.max = max;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getRandomDouble(double value) {
        double source;
        if(value < min)
            source = min;
        else if(value > max)
            source = max;
        else
            source = value;

        double mean = meanPsf.value(source);
        double dev = devPsf.value(source);
        RealDistribution dist = new NormalDistribution(rng, mean, dev);
        return dist.sample();
    }

    public PolynomialSplineFunction getMeanPsf() {
        return meanPsf;
    }

    public void setMeanPsf(PolynomialSplineFunction meanPsf) {
        this.meanPsf = meanPsf;
    }

    public PolynomialSplineFunction getDevPsf() {
        return devPsf;
    }

    public void setDevPsf(PolynomialSplineFunction devPsf) {
        this.devPsf = devPsf;
    }
}
