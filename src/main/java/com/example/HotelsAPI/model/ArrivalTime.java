package com.example.HotelsAPI.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class ArrivalTime {
    private String checkIn;
    private String checkOut;
}
