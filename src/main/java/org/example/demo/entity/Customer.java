package org.example.demo.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends Person {

    private String loyaltyLevel;

    public Customer() {
    }

    public Customer(String name, String loyaltyLevel) {
        super(name);
        this.loyaltyLevel = loyaltyLevel;
    }

    public String getLoyaltyLevel() {
        return loyaltyLevel;
    }

    public void setLoyaltyLevel(String loyaltyLevel) {
        this.loyaltyLevel = loyaltyLevel;
    }
}
