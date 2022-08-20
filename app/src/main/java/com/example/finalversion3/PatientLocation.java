package com.example.finalversion3;

public class PatientLocation {
    public String Name;
    public int id;
    public String latitude;
    public String longitude;

    public PatientLocation(){}

    public PatientLocation(String Name,int id,String latitude,String longitude){
        this.Name = Name;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
