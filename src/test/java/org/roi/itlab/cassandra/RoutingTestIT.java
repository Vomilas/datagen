package org.roi.itlab.cassandra;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;


public class RoutingTestIT {
    private static final String testPois = "./src/test/resources/org/roi/payg/saint-petersburg_russia.csv";
    private static final int ROUTSCOUNT = 1000;
    private List<Poi> pois, starts, ends;
    private Random rng;
    private static final String target = "./target/edges_storage";

    @Before


    public void init() throws IOException {

        pois = PoiLoader.loadFromCsv(testPois);
        starts = new ArrayList<>(ROUTSCOUNT);
        ends = new ArrayList<>(ROUTSCOUNT);
        rng = new Random(42);


        for (int i = 0; i < ROUTSCOUNT; i++) {
            int temp = rng.nextInt(30000);
            Poi a = pois.get(temp);
            Poi b = pois.get(temp + rng.nextInt(50));
            starts.add(a);
            ends.add(b);
        }
    }

    @Test
    public void testRoute() {
        Route route = Routing.route(59.96226, 30.298873, 59.817727, 30.326528);
        System.out.println(route);
        Assert.assertEquals(route.getEdges()[0].getDistance(), 5.488, 0.0001);
    }

    @Test
    public void emptyRouteTest() {
        Route route = Routing.route(59.96226, 30.298873, 59.96226, 30.298873);
        Assert.assertEquals(route.getEdges().length, 0);
    }

    @Test
    public void EdgesStorageSavingLoading() throws IOException {
        Path path = FileSystems.getDefault().getPath(target);
        Files.deleteIfExists(path);
        Files.createFile(path);
        OutputStream out = Files.newOutputStream(path, StandardOpenOption.WRITE);
        OutputStreamWriter writer = new OutputStreamWriter(out, Charset.defaultCharset());
        for (int i = 0; i < ROUTSCOUNT; i++) {

            try {
                Routing.route(starts.get(i), ends.get(i));
            } catch (IllegalStateException e) {

            }
        }
        Routing.saveEdgesStorage(writer);
        int size = Routing.getEdgesStorage().size();
        Routing.loadEdgesStorage(target);
        int newsize = Routing.getEdgesStorage().size();
        Assert.assertEquals(size,newsize);
    }
}
