package com.atharva.erp_telecom.entity;


import com.atharva.erp_telecom.constants.CustomerConstants;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long customerId;
    private String customerFirstName;
    private String customerLastName;
    private String customerClass;
    private String customerSubClass;
    private String country;
    private String countryCode;
    private String email;
    private long contactNumber;
    private String addressLine1;
    private String addressLine2;
    private String region;
    @CreatedDate
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedDate;


    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getCustomerClass() {
        return customerClass;
    }

    public void setCustomerClass(String customerClass) {
        this.customerClass = customerClass;
    }

    public String getCustomerSubClass() {
        return customerSubClass;
    }

    public void setCustomerSubClass(String customerSubClass) {
        this.customerSubClass = customerSubClass;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(long contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    // Added this getter to be appended to the response as fullname
    public String getFullName(){
        return Objects.equals(this.customerClass, CustomerConstants.CUSTOMER_ENTERPRISE) ?
                this.getCustomerFirstName() + " LLC" :
                this.getCustomerLastName() + ", " + this.getCustomerFirstName() ;
    }

}
