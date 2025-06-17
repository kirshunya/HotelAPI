package com.example.HotelsAPI.model;

import jakarta.persistence.Embeddable;
import lombok.Data;


@Data
@Embeddable

public class Address {
    private int houseNumber;
    private String street;
    private String city;
    private String country;
    private String postCode;
}
