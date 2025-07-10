package com.saatco.murshadik.api.response;

import com.google.gson.annotations.SerializedName;
import com.saatco.murshadik.model.City;
import com.saatco.murshadik.model.Mobile;
import com.saatco.murshadik.model.Package;
import com.saatco.murshadik.model.Slider;

import java.util.ArrayList;

public class PackageResponse extends BaseResponse{

    @SerializedName("packages")
    private ArrayList<Package> packages;

    @SerializedName("sliders")
    private ArrayList<Slider> sliders;

    @SerializedName("mobile")
    private ArrayList<Mobile> mobiles;

    @SerializedName("cities")
    private ArrayList<City> cities;

    public ArrayList<Package> getPackages() {
        return packages;
    }

    public void setPackages(ArrayList<Package> packages) {
        this.packages = packages;
    }

    public ArrayList<Slider> getSliders() {
        return sliders;
    }

    public void setSliders(ArrayList<Slider> sliders) {
        this.sliders = sliders;
    }

    public ArrayList<Mobile> getMobiles() {
        return mobiles;
    }

    public void setMobiles(ArrayList<Mobile> mobiles) {
        this.mobiles = mobiles;
    }

    public ArrayList<City> getCities() {
        return cities;
    }

    public void setCities(ArrayList<City> cities) {
        this.cities = cities;
    }
}
