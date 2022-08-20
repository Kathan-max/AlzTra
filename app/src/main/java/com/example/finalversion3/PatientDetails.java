package com.example.finalversion3;


public class PatientDetails {
    public int id;
    public int Age;
    public String Name;
    public String host_email;
    public String latitude;
    public String longitude;
    public int num_b;


    public PatientDetails(){}

    public PatientDetails(int id, int age, String name, String host_email, String latitude, String longitude, int num_b){
        this.Age = age;
        this.id = id;
        this.Name = name;
        this.host_email = host_email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.num_b = num_b;
    }

//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public int getAge() {
//        return Age;
//    }
//
//    public void setAge(int Age) {
//        this.Age = Age;
//    }
//
//    public String getName() {
//        return Name;
//    }
//
//    public void setName(String name) {
//        Name = name;
//    }
//
//    public String getHost_email() {
//        return host_email;
//    }
//
//    public void setHost_email(String host_email) {
//        this.host_email = host_email;
//    }
//
//    public String getLatitude() {
//        return latitude;
//    }
//
//    public void setLatitude(String latitude) {
//        this.latitude = latitude;
//    }
//
//    public String getLongitude() {
//        return longitude;
//    }
//
//    public void setLongitude(String longitude) {
//        this.longitude = longitude;
//    }


}
