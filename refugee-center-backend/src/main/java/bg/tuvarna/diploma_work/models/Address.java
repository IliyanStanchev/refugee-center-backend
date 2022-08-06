package bg.tuvarna.diploma_work.models;

import com.neovisionaries.i18n.CountryCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ADDRESSES")
public class Address implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "serial")
    private Long id;

    @Column(nullable = false, length = 16)
    private String countryIsoCode;

    @Column(nullable = false, length = 16)
    private String stateIsoCode;

    @Column(nullable = false, length = 64)
    private String cityName;

    @Column(nullable = false, length = 256)
    private String address;

    public Address() {

    }

    public String getStateIsoCode() {
        return stateIsoCode;
    }

    public void setStateIsoCode(String stateIsoCode) {
        this.stateIsoCode = stateIsoCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    public void setCountryIsoCode(String countryIsoCode) {
        this.countryIsoCode = countryIsoCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address1 = (Address) o;
        return Objects.equals(id, address1.id) && Objects.equals(countryIsoCode, address1.countryIsoCode) && Objects.equals(cityName, address1.cityName) && Objects.equals(address, address1.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, countryIsoCode, cityName, address);
    }

    @Override
    public String toString() {
        return "Address{" +
                "countryIsoCode='" + countryIsoCode + '\'' +
                ", stateIsoCode='" + stateIsoCode + '\'' +
                ", cityName='" + cityName + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    public String getAddressDetails() {

        CountryCode countryCode = CountryCode.getByCode(countryIsoCode);

        return countryCode.getName() + ", " + cityName + ", " + address;
    }
}
