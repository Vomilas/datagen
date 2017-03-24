package org.roi.itlab.cassandra.random_attributes;


import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.util.Pair;
import org.roi.itlab.cassandra.Poi;
import org.roi.itlab.cassandra.person.Person;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static com.graphhopper.util.Parameters.Algorithms.RoundTrip.SEED;

/**
 * Created by Vadim on 02.03.2017.
 */
public class PersonGenerator {
    public static final int SEED = 1;

    private RandomGenerator ageGenerator;
    private RandomGenerator workDurationGenerator;
    private RandomGenerator workStartGenerator;
    private LocationGenerator homeGenerator;
    private LocationGenerator workGenerator;
    private NormalGenerator skillGenerator;
    private NormalGenerator rushGenerator;
    private NormalGenerator experienceGenerator;

    public PersonGenerator() {
        this(new MersenneTwister(SEED));
    }

    public PersonGenerator(org.apache.commons.math3.random.RandomGenerator rng) {
        ageGenerator = new AgeRandomGenerator(rng);
        workStartGenerator = new WorkStartRandomGenerator(rng);
        workDurationGenerator = new WorkDurationRandomGenerator(rng);
        skillGenerator = new SkillNormalGenerator(rng);
        experienceGenerator = new ExperienceNormalGenerator(rng);
        rushGenerator = new RushFactorNormalGenerator(rng);

        try {
            homeGenerator = new HomeLocationGenerator(rng);
            workGenerator = new WorkLocationGenerator(rng);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Person getResult() {
        Person person = new Person();
        person.setId(UUID.randomUUID());

        person.setAge(ageGenerator.getRandomInt());
        person.setWorkStart(LocalTime.of(workStartGenerator.getRandomInt(),0));
        person.setWorkDuration(LocalTime.of(workDurationGenerator.getRandomInt(),0));
        person.setWorkEnd(LocalTime.of((person.getWorkStart().getHour() + person.getWorkDuration().getHour())% 24,0));
        person.setExperience((int) experienceGenerator.getRandomDouble(person.getAge()));
        person.setSkill(skillGenerator.getRandomDouble(person.getExperience()));
        person.setRushFactor(rushGenerator.getRandomDouble(person.getAge()));
        if (homeGenerator != null && workGenerator != null) {
            person.setHome(homeGenerator.sample());
            person.setWork(workGenerator.sample());
        }

        return person;
    }
}
