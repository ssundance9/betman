package com.ssundance.betman.model;

public class Game {
    private String home;
    private String away;
    private String homeStarter;
    private String awayStarter;
    private Stat homeStat;
    private Stat awayStat;
    
    public String getHome() {
        return home;
    }
    public void setHome(String home) {
        this.home = home;
    }
    public String getAway() {
        return away;
    }
    public void setAway(String away) {
        this.away = away;
    }
    public String getHomeStarter() {
        return homeStarter;
    }
    public void setHomeStarter(String homeStarter) {
        this.homeStarter = homeStarter;
    }
    public String getAwayStarter() {
        return awayStarter;
    }
    public void setAwayStarter(String awayStarter) {
        this.awayStarter = awayStarter;
    }
    public Stat getHomeStat() {
        return homeStat;
    }
    public void setHomeStat(Stat homeStat) {
        this.homeStat = homeStat;
    }
    public Stat getAwayStat() {
        return awayStat;
    }
    public void setAwayStat(Stat awayStat) {
        this.awayStat = awayStat;
    }
}
