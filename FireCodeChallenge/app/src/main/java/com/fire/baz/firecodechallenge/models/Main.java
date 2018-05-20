package com.fire.baz.firecodechallenge.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Main {

    @SerializedName("temp")
    @Expose
    private Double temp;

    @SerializedName("pressure")
    @Expose(serialize = false, deserialize = false)
    private Double pressure;

    @SerializedName("humidity")
    @Expose
    private Integer humidity;

    @SerializedName("temp_min")
    @Expose
    private Integer tempMin;

    @SerializedName("temp_max")
    @Expose
    private Integer tempMax;

    @SerializedName("sea_level")
    @Expose(serialize = false, deserialize = false)
    private Double seaLevel;

    @SerializedName("grnd_level")
    @Expose(serialize = false, deserialize = false)
    private Double grndLevel;


    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getTempMin() {
        return tempMin;
    }

    public void setTempMin(Integer tempMin) {
        this.tempMin = tempMin;
    }

    public Integer getTempMax() {
        return tempMax;
    }

    public void setTempMax(Integer tempMax) {
        this.tempMax = tempMax;
    }

    public Double getSeaLevel() {
        return seaLevel;
    }

    public void setSeaLevel(Double seaLevel) {
        this.seaLevel = seaLevel;
    }

    public Double getGrndLevel() {
        return grndLevel;
    }

    public void setGrndLevel(Double grndLevel) {
        this.grndLevel = grndLevel;
    }

}