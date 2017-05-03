package org.roi.itlab.cassandra.person;

import org.mongodb.morphia.geo.Point;
import org.roi.itlab.cassandra.Poi;
import org.roi.itlab.cassandra.Route;

import java.time.LocalTime;
import java.util.*;

/**
 * Created by Vadim on 01.03.2017.
 */
public class Person {
    private int age;
    private LocalTime workStart;
    private LocalTime workDuration;
    private double experience;
    private LocalTime workEnd;
    private Point home;
    private Point work;
    private Route toWork;
    private Route toHome;
    private UUID id;
    private String firstName = "";
    private String lastName = "";

    private double skill;
    private double rushFactor;

    private double probability;
    private int accidents;
    private int previousAccidents;

    public Person()
    {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public LocalTime getWorkStart() {
        return workStart;
    }

    public LocalTime getWorkDuration() {
        return workDuration;
    }

    public double getExperience() {
        return experience;
    }

    public LocalTime getWorkEnd() {
        return workEnd;
    }

    public Point getHome() {
        return home;
    }

    public Point getWork() {
        return work;
    }

    public double getSkill() {
        return skill;
    }

    public double getRushFactor() {
        return rushFactor;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setWorkStart(LocalTime workStart) {
        this.workStart = workStart;
    }

    public void setWorkDuration(LocalTime workDuration) {
        this.workDuration = workDuration;
    }

    public void setExperience(double experience) {
        this.experience = experience;
    }

    public void setWorkEnd(LocalTime workEnd) {
        this.workEnd = workEnd;
    }

    public void setHome(Point home) {
        this.home = home;
    }

    public void setWork(Point work) {
        this.work = work;
    }

    public void setSkill(double skill) {
        this.skill = skill;
    }

    public void setRushFactor(double rushFactor) {
        this.rushFactor = rushFactor;
    }

    @Override
    public String toString() {
        return "Person{" +
                "age=" + age +
                ", workStart=" + workStart +
                ", workDuration=" + workDuration +
                ", experience=" + experience +
                ", workEnd=" + workEnd +
                ", home=" + home +
                ", work=" + work +
                ", id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", skill=" + skill +
                ", rushFactor=" + rushFactor +
                '}';
    }

    public Route getToWork() {
        return toWork;
    }

    public void setToWork(Route toWork) {
        this.toWork = toWork;
    }

    public Route getToHome() {
        return toHome;
    }

    public void setToHome(Route toHome) {
        this.toHome = toHome;
    }

    public int getAccidents() {
        return accidents;
    }

    public void setAccidents(int accidents) {
        this.accidents = accidents;
    }

    public int getPreviousAccidents() {
        return previousAccidents;
    }

    public void setPreviousAccidents(int previousAccidents) {
        this.previousAccidents = previousAccidents;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}
