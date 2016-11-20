package com.example.chenkui.myapplication;

import java.io.Serializable;

public class IndicatorTabItem implements Serializable {

    private String name;
    private float mesureWidth;
    private float startAngle;
    private float endAngle;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getMesureWidth() {
        return mesureWidth;
    }

    public void setMesureWidth(float mesureWidth) {
        this.mesureWidth = mesureWidth;
    }

    public float getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }

    public float getEndAngle() {
        return endAngle;
    }

    public void setEndAngle(float endAngle) {
        this.endAngle = endAngle;
    }
}
