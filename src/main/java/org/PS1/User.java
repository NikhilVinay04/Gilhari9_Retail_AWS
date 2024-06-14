package org.PS1;
//This class represents the JSON object inside entity
public class User {
    private String id;
    private String name;
    private boolean exempt;
    private double compensation;
    private long dob;
    //Constructor to initialize variable values
    public User(String id, String name,boolean exempt,double compensation,long dob) {
        this.id = id;
        this.name = name;
        this.exempt = exempt;
        this.compensation = compensation;
        this.dob = dob;
    }
    //Basic getters;
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public boolean isExempt() {
        return exempt;

    }
    public double getCompensation() {
        return compensation;
    }
    public long getDob() {
        return dob;
    }

}
