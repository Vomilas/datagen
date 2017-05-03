package org.roi.itlab.cassandra;

import org.apache.commons.math3.stat.Frequency;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roi.itlab.cassandra.person.Person;
import org.roi.itlab.cassandra.random_attributes.PersonGenerator;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Vadim on 26.02.2017.
 */
public class PersonGeneratorIT {
    List<Person> list = new ArrayList<>();
    int mean_mean = 0;

    Frequency workStartTime = new Frequency();
    Frequency age = new Frequency();
    List<Comparable<?>> mode;

    @Before
    public  void init() {
        PersonGenerator personGenerator = new PersonGenerator();

        for(int j = 0 ; j < 1000;++j) {
            list.add(personGenerator.getResult());
            age.addValue(list.get(j).getAge());
            mean_mean += list.get(j).getAge();
            workStartTime.addValue(list.get(j).getWorkStart());
        }
        mean_mean = mean_mean / list.size();
        mode = age.getMode();
    }

    @Test
    public void TestBuilder() throws Exception {
        Assert.assertEquals(mean_mean, 30, 15);
        Assert.assertEquals(age.getUniqueCount(), 72, 2);
        Assert.assertEquals(Integer.parseInt(mode.get(0).toString()), 35, 5);
        Assert.assertEquals(workStartTime.getUniqueCount(), 13);
    }
}
