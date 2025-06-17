package com.example.HotelsAPI.service;

import com.example.HotelsAPI.dto.HotelCreateDto;
import com.example.HotelsAPI.dto.HotelShortDto;
import com.example.HotelsAPI.model.Address;
import com.example.HotelsAPI.model.ArrivalTime;
import com.example.HotelsAPI.model.Contacts;
import com.example.HotelsAPI.model.Hotel;
import com.example.HotelsAPI.repository.HotelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HotelService {
    private final HotelRepository hotelRepository;

    public HotelShortDto createHotel(HotelCreateDto request) {
        Hotel hotel = new Hotel();
        hotel.setName(request.getName());
        hotel.setDescription(request.getDescription());
        hotel.setBrand(request.getBrand());

        if (request.getAddress() != null) {
            Address address = new Address();
            address.setHouseNumber(request.getAddress().getHouseNumber());
            address.setStreet(request.getAddress().getStreet());
            address.setCity(request.getAddress().getCity());
            address.setCountry(request.getAddress().getCountry());
            address.setPostCode(request.getAddress().getPostCode());
            hotel.setAddress(address);
        }

        if (request.getContacts() != null) {
            Contacts contacts = new Contacts();
            contacts.setPhone(request.getContacts().getPhone());
            contacts.setEmail(request.getContacts().getEmail());
            hotel.setContacts(contacts);
        }

        if (request.getArrivalTime() != null) {
            ArrivalTime arrivalTime = new ArrivalTime();
            arrivalTime.setCheckIn(request.getArrivalTime().getCheckIn());
            arrivalTime.setCheckOut(request.getArrivalTime().getCheckOut());
            hotel.setArrivalTime(arrivalTime);
        }

        Hotel savedHotel = hotelRepository.save(hotel);
        return mapToShortResponse(savedHotel);
    }

    public List<HotelShortDto> findAllHotels() {
        return mapToShortResponse(hotelRepository.findAll());
    }

    public HotelCreateDto findHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        return mapToFullResponse(hotel);
    }

    public void addAmenities(Long hotelId, List<String> amenities) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        hotel.addToAmenities(amenities);
        hotelRepository.save(hotel);
    }

    private List<HotelShortDto> mapToShortResponse(List<Hotel> hotels) {
        return hotels.stream()
                .map(this::mapToShortResponse)
                .collect(Collectors.toList());
    }

    private HotelShortDto mapToShortResponse(Hotel hotel) {
        HotelShortDto response = new HotelShortDto();
        response.setId(hotel.getId());
        response.setName(hotel.getName());
        response.setDescription(hotel.getDescription());

        if (hotel.getAddress() != null) {
            String addressStr = String.format("%d %s, %s, %s, %s",
                    hotel.getAddress().getHouseNumber(),
                    hotel.getAddress().getStreet(),
                    hotel.getAddress().getCity(),
                    hotel.getAddress().getPostCode(),
                    hotel.getAddress().getCountry());
            response.setAddress(addressStr);
        }

        if (hotel.getContacts() != null) {
            response.setPhone(hotel.getContacts().getPhone());
        }

        return response;
    }

    private HotelCreateDto mapToFullResponse(Hotel hotel) {
        HotelCreateDto response = new HotelCreateDto();
        response.setId(hotel.getId());
        response.setName(hotel.getName());
        response.setDescription(hotel.getDescription());
        response.setBrand(hotel.getBrand());
        response.setAddress(hotel.getAddress());
        response.setContacts(hotel.getContacts());
        response.setArrivalTime(hotel.getArrivalTime());
        response.setAmenities(hotel.getAmenities());
        return response;
    }

    public Map<String, Long> getHistogram(String param) {
        switch (param.toLowerCase()) {
            case "brand":
                return convertResult(hotelRepository.countByBrand());
            case "city":
                return convertResult(hotelRepository.countByCity());
            case "country":
                return convertResult(hotelRepository.countByCountry());
            case "amenities":
                return convertResult(hotelRepository.countByAmenities());
            default:
                throw new IllegalArgumentException("Invalid parameter: " + param);
        }
    }

    private Map<String, Long> convertResult(List<Object[]> result) {
        return result.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
    }

    public List<HotelShortDto> searchHotels(String name, String brand, String city, String country, String amenity) {
        List<Hotel> hotels = hotelRepository.searchHotels(name, brand, city, country, amenity);
        return mapToShortResponse(hotels);
    }

}