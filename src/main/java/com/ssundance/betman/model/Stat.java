package com.ssundance.betman.model;

public class Stat {
    private String name;
    private String r27;
    private String awayReliefEra;
    private String homeReliefEra;
    private String awayStarterEra;
    private String homeStarterEra;
    private double awayEra;
    private double homeEra;
    
    public Stat() {
    }
    
    public Stat(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getR27() {
        return r27;
    }
    public void setR27(String r27) {
        this.r27 = r27;
    }

    public String getAwayReliefEra() {
        return awayReliefEra;
    }

    public void setAwayReliefEra(String awayReliefEra) {
        this.awayReliefEra = awayReliefEra;
    }

    public String getHomeReliefEra() {
        return homeReliefEra;
    }

    public void setHomeReliefEra(String homeReliefEra) {
        this.homeReliefEra = homeReliefEra;
    }

    public String getAwayStarterEra() {
        return awayStarterEra;
    }

    public void setAwayStarterEra(String awayStarterEra) {
        this.awayStarterEra = awayStarterEra;
    }

    public String getHomeStarterEra() {
        return homeStarterEra;
    }

    public void setHomeStarterEra(String homeStarterEra) {
        this.homeStarterEra = homeStarterEra;
    }

    public double getAwayEra() {
        Double era = Double.parseDouble(this.awayStarterEra) * 0.66 + Double.parseDouble(this.awayReliefEra) * 0.33;
        return Math.round(era*100D)/100D;
    }

    public void setAwayEra(double awayEra) {
        this.awayEra = awayEra;
    }

    public double getHomeEra() {
        Double era = Double.parseDouble(this.homeStarterEra) * 0.66 + Double.parseDouble(this.homeReliefEra) * 0.33;
        return Math.round(era*100D)/100D;
    }

    public void setHomeEra(double homeEra) {
        this.homeEra = homeEra;
    }
    
    
}
