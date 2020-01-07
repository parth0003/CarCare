package com.androidapp.carcare.datamodels;

public class ServiceItem {


    public String getServiceName() {
        return ServiceName;
    }

    public String getServiceImageURL() {
        return ServiceImageURL;
    }

    public void setServiceName(String serviceName) {
        ServiceName = serviceName;
    }

    public void setServiceImageURL(String serviceImageURL) {
        ServiceImageURL = serviceImageURL;
    }

    String ServiceName;
    String ServiceImageURL;

    public String getServiceid() {
        return Serviceid;
    }

    public void setServiceid(String serviceid) {
        Serviceid = serviceid;
    }

    String Serviceid;

}
