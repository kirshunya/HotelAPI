package com.example.HotelsAPI.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Table(name = "hotels")
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String brand;

    @Embedded
    private Address address;
    @Embedded
    private Contacts contacts;
    @Embedded
    private ArrivalTime arrivalTime;

    @ElementCollection
    @CollectionTable(name = "hotel_amenities", joinColumns = @JoinColumn(name = "hotel_id"))
    @Column(name = "amenity")
    private List<String> amenities;

    public boolean addToAmenities(List<String> amenitiesToAdd){
        boolean added = false;
        for (String amenity : amenitiesToAdd) {
            if (!amenities.contains(amenity)) {
                amenities.add(amenity);
                added = true;
            }
        }
        return added;
    }

}
