package ca.jrvs.apps.trading.model.domain;

import java.sql.Date;

public class Trader implements Entity<Integer> {

    private Integer id;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String country;
    private String email;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer newId) {
        this.id = newId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
