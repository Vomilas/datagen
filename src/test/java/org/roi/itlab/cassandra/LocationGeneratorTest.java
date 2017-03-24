package org.roi.itlab.cassandra;

import com.graphhopper.util.DistanceCalcEarth;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.mongodb.morphia.geo.GeoJson;
import org.mongodb.morphia.geo.Point;
import org.roi.itlab.cassandra.random_attributes.LocationGenerator;

import java.util.Arrays;

/**
 * Created by mkuperman on 3/18/2017.
 */
public class LocationGeneratorTest {

    @Test
    public void generate() {
        DistanceCalcEarth earth = new DistanceCalcEarth();
        RandomGenerator rng = new MersenneTwister(1);
        Location big = new Location(rng, GeoJson.point(60, 40), 500, 2);
        Location small = new Location(rng, GeoJson.point(61, 41), 100, 1);
        LocationGenerator generator = new LocationGenerator(rng, Arrays.asList(new Location[] {big, small}));
        int countBig = 0;
        int countSmall = 0;
        for (int i = 0; i < 100; i++) {
            Point point = generator.sample();
            double distanceBig = earth.calcDist(point.getLatitude(), point.getLongitude(), big.getCenter().getLatitude(), big.getCenter().getLongitude());
            double distanceSmall = earth.calcDist(point.getLatitude(), point.getLongitude(), small.getCenter().getLatitude(), small.getCenter().getLongitude());
            Assert.assertTrue(distanceBig < 500 || distanceSmall < 100);
            if (distanceBig < 500)
                countBig++;
            else if (distanceSmall < 100)
                countSmall++;
        }
        Assert.assertTrue(countBig > countSmall);
    }
}
