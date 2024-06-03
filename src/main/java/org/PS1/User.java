package org.PS1;

public class User {
    private int id;
    private String name;
    private boolean exempt;
    private double compensation;
    private long dob;
    public User(int id, String name,boolean exempt,double compensation,long dob) {
        this.id = id;
        this.name = name;
        this.exempt = exempt;
        this.compensation = compensation;
        this.dob = dob;
    }
    public int getId() {
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
