package com.saatco.murshadik.model.workersService;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Worker implements Serializable {

    @SerializedName("Id")
    private int id;

    @SerializedName("DateOfBirth")
    private String dateOfBirth;

    @SerializedName("Name")
    private String name;

    @SerializedName("Jobs")
    private ArrayList<Job> jobs;

    @SerializedName("Job")
    private String job;

    @SerializedName("Phone")
    private String phone;

    @SerializedName("Img")
    private String imgUrl;


    @SerializedName("Address")
    private String Address;

    @SerializedName("Isbusy")
    private boolean isBusy;

    @SerializedName("Experiences")
    private ArrayList<Experience> experiences;

    @SerializedName("ExpectedSalary")
    private int expectedSalary;


    @SerializedName("Nationalty")
    private String nationality;

    @SerializedName("NationaltyAr")
    private String nationalityAr;

    public int getId() {
        return id;
    }

    public String getDateOfBirth() {
        return dateOfBirth.split("[Tt]")[0];
    }

    public String getName() {
        return name;
    }

    public ArrayList<Job> getJobs() {
        return jobs;
    }

    public String getJob() {
        return job;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return Address;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public ArrayList<Experience> getExperiences() {
        return experiences;
    }

    public int getExpectedSalary() {
        return expectedSalary;
    }

    public String getNationality() {
        return nationality;
    }

    public String getNationalityAr() {
        return nationalityAr;
    }
}
