package com.shureck.requestapp;

import android.support.annotation.NonNull;

public class Req {
    String address;
    String port;
    String timeout;

    public Req(String address, String port, String timeout) {
        this.address = address;
        this.port = port;
        this.timeout = timeout;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getAddress() {
        return address;
    }

    public String getPort() {
        return port;
    }

    public String getTimeout() {
        return timeout;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }
}
