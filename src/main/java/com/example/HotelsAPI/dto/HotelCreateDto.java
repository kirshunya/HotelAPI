package com.example.HotelsAPI.dto;

import com.example.HotelsAPI.model.Address;
import com.example.HotelsAPI.model.ArrivalTime;
import com.example.HotelsAPI.model.Contacts;
import jakarta.persistence.Embedded;
import lombok.Data;

import java.util.List;


@Data
public class HotelCreateDto {
    private Long id;
    private String name;
    private String description;
    private String brand;
    @Embedded
    private Address address;
    @Embedded
    private Contacts contacts;
    @Embedded
    private ArrivalTime  arrivalTime;
    private List<String> amenities;
}
