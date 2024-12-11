package com.finzly.config_management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "application")
public class Application {


    @Id
    @Column(name = "app_name", nullable = false, unique = true, length = 255)
    private String appName;

    public String getAppName() {
        return appName;
    }


    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Application() {
    }

    public Application(String appName) {
        this.appName = appName;
    }

}