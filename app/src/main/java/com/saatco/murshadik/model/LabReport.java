package com.saatco.murshadik.model;

import java.io.Serializable;

public class LabReport implements Serializable {

    public int id;
    public int lab_id;
    public int appointment_id;
    public int user_id;
    public int region_id;
    public String collected_date;
    public String reported_date;
    public String collected_by;
    public String reported_by;
    public String sample_no;
    public double soil_electric_conductivity;
    public double soil_ph;
    public double soil_calcium_carbonate;
    public double soil_potasium_ppm;
    public double soil_phosphorus_ppm;
    public double soil_sand;
    public double soil_salt;
    public double soil_clay;
    public String soil_texture_type;
    public String soil_notes;
    public String soil_recommendations;
    public double water_ph;
    public double water_electric_conductivity;
    public double water_carbonate;
    public double water_chloride;
    public double water_salts;
    public String water_recommendation;
    public String water_notes;
    public String reporttype;
    public String extra1;

    public String getReportType(){

        if(reporttype.equals("2")){
           return "التربة";
        }
        else if(reporttype.equals("3")){
            return "ماء";
        }
        return "كلا";
    }

}
