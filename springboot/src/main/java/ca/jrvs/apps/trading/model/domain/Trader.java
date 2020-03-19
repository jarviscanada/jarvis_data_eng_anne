package ca.jrvs.apps.trading.model.domain;

public class Trader implements Entity<Integer> {

    private Integer id;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
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
