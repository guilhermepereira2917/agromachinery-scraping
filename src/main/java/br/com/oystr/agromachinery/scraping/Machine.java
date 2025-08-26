package br.com.oystr.agromachinery.scraping;

import java.math.BigDecimal;

public class Machine {
    private String model;
    private ContractType contractType;
    private String make;
    private Integer year;
    private Integer workedHours;
    private String city;
    private BigDecimal price;
    private String photo;
    private String url;

    public Machine(String model, ContractType contractType, String make, Integer year, Integer workedHours, String city, BigDecimal price, String photo, String url) {
        this.model = model;
        this.contractType = contractType;
        this.make = make;
        this.year = year;
        this.workedHours = workedHours;
        this.city = city;
        this.price = price;
        this.photo = photo;
        this.url = url;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getWorkedHours() {
        return workedHours;
    }

    public void setWorkedHours(Integer workedHours) {
        this.workedHours = workedHours;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Machine{" +
            "model='" + model + '\'' +
            ", contractType=" + contractType +
            ", make='" + make + '\'' +
            ", year=" + year +
            ", workedHours=" + workedHours +
            ", city='" + city + '\'' +
            ", price=" + price +
            ", photo='" + photo + '\'' +
            ", url='" + url + '\'' +
            '}';
    }
}
