package org.roi.itlab.cassandra;

import org.apache.commons.math3.random.RandomGenerator;
import org.roi.itlab.cassandra.person.Person;
import org.roi.itlab.cassandra.random_attributes.NormalGenerator;

import java.time.LocalTime;

/**
 * @author Anna Striganova
 */
public class AccidentRate {

    private IntensityMap intensityMap;
    private NormalGenerator normalGenerator;
    private final static double averageAccidentProbability = 1.0 / 100_000_000;
    private final static double onewwayFactor = 0.75;
    private RandomGenerator rng;

    public AccidentRate(IntensityMap intensityMap, NormalGenerator normalGenerator, RandomGenerator rng) {
        this.intensityMap = intensityMap;
        this.rng = rng;
        this.normalGenerator = normalGenerator;
    }

    public int getAccidents(Person person, int days) {
        double normalizedDistance = getNormalizedDistance(person.getToWork(), person.getWorkStart()) + getNormalizedDistance(person.getToHome(), person.getWorkEnd());
        double probability = averageAccidentProbability * normalizedDistance * person.getRushFactor() / person.getSkill();
        int accidents = 0;
        for (int i = 0; i < days; i++) {
            accidents += rng.nextDouble() < probability ? 1 : 0;
        }
        return accidents;
    }

    private double getNormalizedDistance(Route route, LocalTime startTime) {
        long time =  startTime.toSecondOfDay() * 1000;
        double result = 0;
        for (Edge e : route.getEdges()) {
            result += e.getDistance() * normalGenerator.getRandomDouble(intensityMap.getIntensity(e, time)) * (e.isBackward() ? 1.0 : onewwayFactor);
            time += e.getTime();
        }
        return result;
    }
}
