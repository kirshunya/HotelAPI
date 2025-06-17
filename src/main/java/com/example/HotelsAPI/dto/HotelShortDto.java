package com.example.HotelsAPI.dto;

import lombok.Data;

@Data
public class HotelShortDto {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String phone;
}
