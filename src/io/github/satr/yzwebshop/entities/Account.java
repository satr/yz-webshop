package io.github.satr.yzwebshop.entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class Account {
    private int id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String passwordHash;
    private String passwordSalt;
    private String email;
    private Timestamp createdOn;
    private Timestamp updatedOn;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "FirstName")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Basic
    @Column(name = "LastName")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Basic
    @Column(name = "MiddleName")
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @Basic
    @Column(name = "PasswordHash")
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Basic
    @Column(name = "PasswordSalt")
    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    @Basic
    @Column(name = "Email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "CreatedOn")
    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    @Basic
    @Column(name = "UpdatedOn")
    public Timestamp getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Timestamp updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (id != account.id) return false;
        if (firstName != null ? !firstName.equals(account.firstName) : account.firstName != null) return false;
        if (lastName != null ? !lastName.equals(account.lastName) : account.lastName != null) return false;
        if (middleName != null ? !middleName.equals(account.middleName) : account.middleName != null) return false;
        if (passwordHash != null ? !passwordHash.equals(account.passwordHash) : account.passwordHash != null)
            return false;
        if (passwordSalt != null ? !passwordSalt.equals(account.passwordSalt) : account.passwordSalt != null)
            return false;
        if (email != null ? !email.equals(account.email) : account.email != null) return false;
        if (createdOn != null ? !createdOn.equals(account.createdOn) : account.createdOn != null) return false;
        if (updatedOn != null ? !updatedOn.equals(account.updatedOn) : account.updatedOn != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
        result = 31 * result + (passwordHash != null ? passwordHash.hashCode() : 0);
        result = 31 * result + (passwordSalt != null ? passwordSalt.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (createdOn != null ? createdOn.hashCode() : 0);
        result = 31 * result + (updatedOn != null ? updatedOn.hashCode() : 0);
        return result;
    }
}
