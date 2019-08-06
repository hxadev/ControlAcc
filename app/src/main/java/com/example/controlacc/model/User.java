package com.example.controlacc.model;

/**
 * @author Alfonso Hernandez Xochipa
 */
public class User {

    private String userApp;
    private String passApp;


    public User() {
        ;
    }


    /**
     *
     * @param userApp
     * @param passApp
     */
    public User(String userApp,String passApp){
        this.userApp=userApp;
        this.passApp=passApp;


    }


    public String getUserApp() {
        return userApp;
    }

    public void setUserApp(String userApp) {
        this.userApp = userApp;
    }

    public String getPassApp() {
        return passApp;
    }

    public void setPassApp(String passApp) {
        this.passApp = passApp;
    }

    @Override
    public String toString() {
        return "User{" +
                "userApp='" + userApp + '\'' +
                ", passApp='" + passApp + '\'' +
                '}';
    }
}
