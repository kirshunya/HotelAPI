package com.example.HotelsAPI.model;

import jakarta.persistence.Basic;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable

public class Address {
    private String houseNumber;
    private String street;
    private String city;
    private String country;
    private String postCode;
}
