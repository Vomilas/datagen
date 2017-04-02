package org.roi.itlab.cassandra;

import com.graphhopper.json.geo.Point;
import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TrafficCalcTestIT {
    //test data
    private static final int N = 5;
    private static final double[][] coords = {{59.947553, 30.259824, 59.926765, 30.240469},
            {59.94693, 30.274029, 59.942309, 30.257936},
            {59.929087, 30.24682, 59.948607, 30.22047},
            {59.948693, 30.280123, 59.944222, 30.230598},
            {59.94203, 30.262828, 59.935107, 30.264115}};
    private static final long[] time = {1488186000000L, 1488186180000L, 1488186300000L, 1488186180000L, 1488186180000L};
    private List<Route> routes;
    private List<Long> startTimes;

    @Before
    public void init() {
        routes = new ArrayList<>(N);
        startTimes = new ArrayList<>(N);
        for (int i = 0; i < N; ++i) {
            Poi start = new Poi(Integer.toString(i), Integer.toString(i), 0, coords[i][0], coords[i][1]);
            Poi end = new Poi(Integer.toString(i), Integer.toString(i), 0, coords[i][2], coords[i][3]);
            Trip trip = new Trip(start, end, time[i]);
            routes.add(trip.getRoute());
            startTimes.add(trip.getStartTime());
        }
    }

    @Test
    public void TestTrafficIT() {
        IntensityMap intensityMap = new IntensityMap();
        intensityMap.put(startTimes, routes);
//        System.out.println("\n" + intensityMap);
        assertEquals(intensityMap.getIntensity(Routing.getEdge(12205),
                1488186250000L),
                3);
        assertEquals(intensityMap.getIntensity(Routing.getEdge(426664),
                1488186250000L),
                1);
        Assert.assertNotEquals(intensityMap.getIntensity(Routing.getEdge(426664),
                1488185900000L),
                1);
    }

    @Test
    public void sameRouteSameTime() {
        IntensityMap intensityMap = new IntensityMap();
        intensityMap.put(0, Routing.route(59.947553, 30.259824, 59.926765, 30.240469));
        intensityMap.put(0, Routing.route(59.947553, 30.259824, 59.926765, 30.240469));
        for (int intensity :
                intensityMap.getIntensity(0).values()) {
            assertEquals(intensity, 2);
        }
    }
}
