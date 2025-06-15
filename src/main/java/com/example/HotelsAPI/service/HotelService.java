package com.example.HotelsAPI.service;

import com.example.HotelsAPI.dto.HotelShortDTO;
import com.example.HotelsAPI.model.Hotel;
import com.example.HotelsAPI.repository.HotelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class HotelService {
    private final HotelRepository hotelRepository;

    //TODO: Create, CreateByAmenities ReadAll, ReadById, ReadByParam, ReadHistogramByParam

    public Hotel createHotel(HotelShortDTO dto) {
        Hotel hotel = Hotel.builder()
                .name(dto.getName())
                .description(dto.getDescription())

                .build();
    }
}
