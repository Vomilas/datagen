package org.roi.itlab.cassandra;

import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.stat.Frequency;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roi.itlab.cassandra.random_attributes.*;
/**
 * author Vadim
 * author Anush
 */
public class TestRA {
    int count =1000;
    int age_mean,workstart_mean,workduration_mean;
    Frequency age_freq,workstart_freq,workduration_freq;
    List<Comparable<?>> age_mode,workstart_mode,workduration_mode;

    @Before
    public  void init()
    {
        age_freq = new Frequency();
        workstart_freq = new Frequency();
        workduration_freq = new Frequency();
        MersenneTwister rng = new MersenneTwister(1);

        RandomGenerator ageRandomGenerator = new AgeRandomGenerator(rng);

        long mean = 0;
        int tmp;

        for(int j = 0 ; j < count;++j) {
            tmp = ageRandomGenerator.getRandomInt();
            mean += tmp;
           age_freq.addValue(tmp);
        }

        age_mean = (int)mean/count;
        age_mode = age_freq.getMode();


        RandomGenerator workStartRandomGenerator = new WorkStartRandomGenerator(rng);

        mean = 0;

        for(int j = 0 ; j < count;++j) {
            tmp = workStartRandomGenerator.getRandomInt();
            mean += tmp;
            workstart_freq.addValue(tmp);
        }

        workstart_mean =  (int)mean/count;
        workstart_mode = workstart_freq.getMode();



        RandomGenerator workDurationRandomGenerator = new WorkDurationRandomGenerator(rng);

        mean = 0;

        for(int j = 0 ; j < count;++j) {
            tmp = workDurationRandomGenerator.getRandomInt();
            mean += tmp;
            workduration_freq.addValue(tmp);
        }

        workduration_mean =  (int)mean/count;
        workduration_mode = workduration_freq.getMode();


    }

    @Test
    public void TestBuilder() throws Exception {
        Assert.assertEquals(age_mean, 35, 15);
        Assert.assertEquals(Integer.parseInt(age_mode.get(0).toString()), 35, 5);
        Assert.assertEquals(workstart_mean, 9, 1);
        Assert.assertEquals(Integer.parseInt(workstart_mode.get(0).toString()), 9, 2);
        Assert.assertEquals(workduration_mean, 8, 2);
        Assert.assertEquals(Integer.parseInt(workduration_mode.get(0).toString()), 9, 2);
    }
}
