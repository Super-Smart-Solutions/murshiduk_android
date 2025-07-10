package com.saatco.murshadik.model.workersService;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class JobWithWorker implements Serializable {

    @SerializedName("Id")
    private int id;

    @SerializedName("JobId")
    private int jobId;

    @SerializedName("WorkerId")
    private int workerId;

    private String jobName;

    public int getId() {
        return id;
    }

    public int getJobId() {
        return jobId;
    }

    public int getWorkerId() {
        return workerId;
    }

    public JobWithWorker(int id){
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public JobWithWorker(int id, String jobName){
        this.id = id;
        this.jobName = jobName;
    }


    public void setJobName(String name) {
        this.jobName = name;
    }
}
