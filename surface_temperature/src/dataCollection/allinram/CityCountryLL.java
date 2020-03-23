package dataCollection.allinram;

import java.io.Serializable;
import java.util.Objects;

public class CityCountryLL implements Serializable {
    private String city;
    private String country;
    private String longitude;
    private String latitude;

    public CityCountryLL(String city, String country, String longitude, String latitude) {
        this.city = city;
        this.country = country;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "CityCountryLL{" +
                "city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                '}';
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CityCountryLL that = (CityCountryLL) o;
        return city.equals(that.city) &&
                longitude.equals(that.longitude) &&
                latitude.equals(that.latitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, longitude, latitude);
    }

}
