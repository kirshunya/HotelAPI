package com.example.HotelsAPI.controller;

import com.example.HotelsAPI.dto.HotelCreateDto;
import com.example.HotelsAPI.dto.HotelShortDto;
import com.example.HotelsAPI.service.HotelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Hotels", description = "API для работы с отелями")
@RestController
@AllArgsConstructor
@RequestMapping("/property-view")
public class HotelController {
    private final HotelService hotelService;

    @GetMapping("/hotels")
    public List<HotelShortDto> getAllHotels() {
        return hotelService.findAllHotels();
    }

    @GetMapping("/hotels/{id}")
    public HotelCreateDto getHotelById(@PathVariable Long id) {
        return hotelService.findHotelById(id);
    }

    @GetMapping("/histogram/{param}")
    public Map<String, Long> getHistogram(@PathVariable String param) {
        return hotelService.getHistogram(param);
    }

    @GetMapping("/search")
    public ResponseEntity<List<HotelShortDto>> searchHotels(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String amenities
    ) {
        return ResponseEntity.ok(hotelService.searchHotels(name, brand, city, country, amenities));
    }


    @PostMapping("/hotels")
    public HotelShortDto createHotel(@RequestBody HotelCreateDto hotel) {
        return hotelService.createHotel(hotel);
    }

    @PostMapping("/hotels/{id}/amenities")
    public void addAmenity(@PathVariable Long id, @RequestBody List<String> amenities) {
        hotelService.addAmenities(id, amenities);
    }
}
