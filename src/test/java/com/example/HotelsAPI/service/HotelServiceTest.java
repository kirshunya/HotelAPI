package com.example.HotelsAPI.service;

import com.example.HotelsAPI.dto.HotelCreateDto;
import com.example.HotelsAPI.dto.HotelShortDto;
import com.example.HotelsAPI.model.*;
import com.example.HotelsAPI.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class HotelServiceTest {

    private HotelRepository hotelRepository;
    private HotelService hotelService;

    @BeforeEach
    void setUp() {
        hotelRepository = mock(HotelRepository.class);
        hotelService = new HotelService(hotelRepository);
    }

    @Test
    void createHotel_shouldSaveAndReturnShortDto() {
        HotelCreateDto dto = new HotelCreateDto();
        dto.setName("Test Hotel");
        dto.setDescription("Test Description");
        dto.setBrand("BrandX");

        Address address = new Address();
        address.setHouseNumber(10);
        address.setStreet("Main");
        address.setCity("City");
        address.setCountry("Country");
        address.setPostCode("12345");
        dto.setAddress(address);

        Contacts contacts = new Contacts();
        contacts.setPhone("123456789");
        contacts.setEmail("test@example.com");
        dto.setContacts(contacts);

        ArrivalTime arrivalTime = new ArrivalTime();
        arrivalTime.setCheckIn("14:00");
        arrivalTime.setCheckOut("12:00");
        dto.setArrivalTime(arrivalTime);

        Hotel savedHotel = new Hotel();
        savedHotel.setId(1L);
        savedHotel.setName(dto.getName());
        savedHotel.setDescription(dto.getDescription());
        savedHotel.setBrand(dto.getBrand());
        savedHotel.setAddress(address);
        savedHotel.setContacts(contacts);
        savedHotel.setArrivalTime(arrivalTime);

        when(hotelRepository.save(any(Hotel.class))).thenReturn(savedHotel);

        HotelShortDto result = hotelService.createHotel(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Hotel");
        assertThat(result.getAddress()).contains("City");
        assertThat(result.getPhone()).isEqualTo("123456789");
    }

    @Test
    void findAllHotels_shouldReturnList() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Hotel1");
        hotel.setDescription("Description");

        when(hotelRepository.findAll()).thenReturn(List.of(hotel));

        List<HotelShortDto> result = hotelService.findAllHotels();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Hotel1");
    }

    @Test
    void findHotelById_shouldReturnHotelCreateDto() {
        Hotel hotel = new Hotel();
        hotel.setId(2L);
        hotel.setName("Hotel2");
        hotel.setBrand("BrandY");
        hotel.setAddress(new Address());
        hotel.setContacts(new Contacts());
        hotel.setArrivalTime(new ArrivalTime());
        hotel.setAmenities(List.of("WiFi"));

        when(hotelRepository.findById(2L)).thenReturn(Optional.of(hotel));

        HotelCreateDto dto = hotelService.findHotelById(2L);

        assertThat(dto.getName()).isEqualTo("Hotel2");
        assertThat(dto.getAmenities()).contains("WiFi");
    }

    @Test
    void findHotelById_shouldThrowWhenNotFound() {
        when(hotelRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelService.findHotelById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Hotel not found");
    }

    @Test
    void addAmenities_shouldAddAndSave() {
        Hotel hotel = new Hotel();
        hotel.setId(3L);
        hotel.setAmenities(new ArrayList<>());

        when(hotelRepository.findById(3L)).thenReturn(Optional.of(hotel));

        hotelService.addAmenities(3L, List.of("Pool", "Gym"));

        assertThat(hotel.getAmenities()).containsExactly("Pool", "Gym");
        verify(hotelRepository).save(hotel);
    }

    @Test
    void getHistogram_brand_shouldReturnMap() {
        List<Object[]> raw = Arrays.asList(
                new Object[]{"BrandA", 2L},
                new Object[]{"BrandB", 3L}
        );
        when(hotelRepository.countByBrand()).thenReturn(raw);

        Map<String, Long> result = hotelService.getHistogram("brand");

        assertThat(result).containsEntry("BrandA", 2L);
    }

    @Test
    void getHistogram_city_shouldReturnMap() {
        List<Object[]> raw = List.<Object[]>of(new Object[]{"Minsk", 5L});
        when(hotelRepository.countByCity()).thenReturn(raw);

        Map<String, Long> result = hotelService.getHistogram("city");

        assertThat(result).containsEntry("Minsk", 5L);
    }

    @Test
    void getHistogram_country_shouldReturnMap() {
        List<Object[]> raw = List.<Object[]>of(new Object[]{"Belarus", 1L});
        when(hotelRepository.countByCountry()).thenReturn(raw);

        Map<String, Long> result = hotelService.getHistogram("country");

        assertThat(result).containsEntry("Belarus", 1L);
    }

    @Test
    void getHistogram_amenities_shouldReturnMap() {
        List<Object[]> raw = List.<Object[]>of(new Object[]{"WiFi", 10L});
        when(hotelRepository.countByAmenities()).thenReturn(raw);

        Map<String, Long> result = hotelService.getHistogram("amenities");

        assertThat(result).containsEntry("WiFi", 10L);
    }

    @Test
    void getHistogram_invalid_shouldThrowException() {
        assertThatThrownBy(() -> hotelService.getHistogram("unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid parameter");
    }

    @Test
    void searchHotels_shouldReturnShortDtos() {
        Hotel hotel = new Hotel();
        hotel.setId(5L);
        hotel.setName("Searchable Hotel");

        when(hotelRepository.searchHotels(any(), any(), any(), any(), any()))
                .thenReturn(List.of(hotel));

        List<HotelShortDto> result = hotelService.searchHotels("name", "brand", "city", "country", "amenity");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Searchable Hotel");
    }

    @Test
    void createHotel_shouldHandleNullFields() {
        HotelCreateDto dto = new HotelCreateDto();
        dto.setName("NullFieldsHotel");
        dto.setDescription("No address, contacts, or arrivalTime");

        Hotel saved = new Hotel();
        saved.setId(77L);
        saved.setName("NullFieldsHotel");
        saved.setDescription("No address, contacts, or arrivalTime");

        when(hotelRepository.save(any(Hotel.class))).thenReturn(saved);

        HotelShortDto result = hotelService.createHotel(dto);

        assertThat(result.getName()).isEqualTo("NullFieldsHotel");
        assertThat(result.getAddress()).isNull();
        assertThat(result.getPhone()).isNull();
    }
}
