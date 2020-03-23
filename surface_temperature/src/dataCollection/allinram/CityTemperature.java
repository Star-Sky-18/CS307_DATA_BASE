package dataCollection.allinram;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class CityTemperature implements Serializable {
    private LocalDate date;
    private String cityName;
    private Double avgTemperature;
    private Double avgTemperatureUncertainty;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CityTemperature that = (CityTemperature) o;
        return date.equals(that.date) &&
                cityName.equals(that.cityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, cityName);
    }

    @Override
    public String toString() {
        return "CityTemperature{" +
                "date=" + date +
                ", cityName='" + cityName + '\'' +
                ", avgTemperature=" + avgTemperature +
                ", avgTemperatureUncertainty=" + avgTemperatureUncertainty +
                '}';
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Double getAvgTemperature() {
        return avgTemperature;
    }

    public void setAvgTemperature(Double avgTemperature) {
        this.avgTemperature = avgTemperature;
    }

    public Double getAvgTemperatureUncertainty() {
        return avgTemperatureUncertainty;
    }

    public void setAvgTemperatureUncertainty(Double avgTemperatureUncertainty) {
        this.avgTemperatureUncertainty = avgTemperatureUncertainty;
    }


    public CityTemperature(LocalDate date, String cityName, Double avgTemperature, Double avgTemperatureUncertainty) {
        this.date = date;
        this.cityName = cityName;
        this.avgTemperature = avgTemperature;
        this.avgTemperatureUncertainty = avgTemperatureUncertainty;
    }
}