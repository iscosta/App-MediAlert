package com.br.medialert.model;

import java.io.Serializable;

public class Captura implements Serializable {

    private String timestamp;
    private int devCode;
    private int luz;

    public Captura() {
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getDevCode() {
        return devCode;
    }

    public void setDevCode(int devCode) {
        this.devCode = devCode;
    }

    public int getLuz() {
        return luz;
    }

    public void setLuz(int luz) {
        this.luz = luz;
    }


    @Override
    public String toString() {
        return   getTimestamp() + " - " + getDevCode() + " - " + getLuz();
    }


}
