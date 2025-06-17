package com.example.HotelsAPI.service;

import com.example.HotelsAPI.dto.HotelCreateDto;
import com.example.HotelsAPI.dto.HotelShortDto;
import com.example.HotelsAPI.model.*;
import com.example.HotelsAPI.repository.HotelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HotelApiServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelService hotelService;

    @Test
    public void createHotel_shouldSaveHotelAndReturnDto() {
        // Arrange
        HotelCreateDto request = new HotelCreateDto();
        request.setName("Grand Hotel");
        request.setDescription("Luxury hotel");

        Address address = new Address();
        address.setCity("Paris");
        request.setAddress(address);

        Contacts contacts = new Contacts();
        contacts.setPhone("+123456789");
        request.setContacts(contacts);

        Hotel savedHotel = new Hotel();
        savedHotel.setId(1L);
        savedHotel.setName(request.getName());
        savedHotel.setAddress(address);
        savedHotel.setContacts(contacts);

        ArgumentCaptor<Hotel> hotelCaptor = ArgumentCaptor.forClass(Hotel.class);
        when(hotelRepository.save(hotelCaptor.capture())).thenReturn(savedHotel);

        // Act
        HotelShortDto result = hotelService.createHotel(request);

        // Assert
        verify(hotelRepository, times(1)).save(any(Hotel.class));

        Hotel capturedHotel = hotelCaptor.getValue();
        assertThat(capturedHotel.getName()).isEqualTo("Grand Hotel");
        assertThat(capturedHotel.getAddress().getCity()).isEqualTo("Paris");

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Grand Hotel");
        assertThat(result.getPhone()).isEqualTo("+123456789");
    }

    @Test
    public void findAllHotels_shouldReturnHotelList() {
        // Arrange
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Test Hotel");

        when(hotelRepository.findAll()).thenReturn(List.of(hotel));

        // Act
        List<HotelShortDto> result = hotelService.findAllHotels();

        // Assert
        verify(hotelRepository, times(1)).findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Test Hotel");
    }

    @Test
    public void findHotelById_shouldReturnHotelWhenExists() {
        // Arrange
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Test Hotel");
        hotel.setAmenities(List.of("Pool", "WiFi"));

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        // Act
        HotelCreateDto result = hotelService.findHotelById(1L);

        // Assert
        verify(hotelRepository, times(1)).findById(1L);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test Hotel");
        assertThat(result.getAmenities()).containsExactly("Pool", "WiFi");
    }

    @Test
    public void findHotelById_shouldThrowWhenHotelNotFound() {
        // Arrange
        when(hotelRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> hotelService.findHotelById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Hotel not found");

        verify(hotelRepository, times(1)).findById(999L);
    }

    @Test
    public void addAmenities_shouldAddNewAmenitiesToHotel() {
        // Arrange
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setAmenities(new ArrayList<>(List.of("WiFi")));

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(any())).thenReturn(hotel);

        // Act
        hotelService.addAmenities(1L, List.of("Pool", "Spa"));

        // Assert
        verify(hotelRepository, times(1)).findById(1L);
        verify(hotelRepository, times(1)).save(hotel);
        assertThat(hotel.getAmenities()).containsExactly("WiFi", "Pool", "Spa");
    }

    @Test
    public void getHistogram_shouldReturnBrandCounts() {
        // Arrange
        List<Object[]> brandCounts = List.of(
                new Object[]{"Hilton", 5L},
                new Object[]{"Marriott", 3L}
        );

        when(hotelRepository.countByBrand()).thenReturn(brandCounts);

        // Act
        Map<String, Long> result = hotelService.getHistogram("brand");

        // Assert
        verify(hotelRepository, times(1)).countByBrand();
        assertThat(result).hasSize(2);
        assertThat(result).containsEntry("Hilton", 5L);
        assertThat(result).containsEntry("Marriott", 3L);
    }

    @Test
    public void getHistogram_shouldThrowForInvalidParameter() {
        // Act & Assert
        assertThatThrownBy(() -> hotelService.getHistogram("invalid_param"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid parameter");
    }

    @Test
    public void searchHotels_shouldReturnFilteredResults() {
        // Arrange
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Beach Resort");

        Address address = new Address();
        address.setCity("Miami");
        hotel.setAddress(address); // Правильная установка адреса

        when(hotelRepository.searchHotels(null, null, "Miami", null, null))
                .thenReturn(List.of(hotel));

        // Act
        List<HotelShortDto> result = hotelService.searchHotels(null, null, "Miami", null, null);

        // Assert
        verify(hotelRepository, times(1))
                .searchHotels(null, null, "Miami", null, null);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Beach Resort");
        assertThat(result.get(0).getAddress()).contains("Miami"); // Проверка через метод getAddress()
    }

    @Test
    public void addToAmenities_shouldAddOnlyNewAmenities() {
        // Arrange
        Hotel hotel = new Hotel();
        hotel.setAmenities(new ArrayList<>(List.of("WiFi", "Pool")));

        // Act
        boolean result = hotel.addToAmenities(List.of("Pool", "Spa"));

        // Assert
        assertThat(result).isTrue();
        assertThat(hotel.getAmenities()).containsExactly("WiFi", "Pool", "Spa");
    }

    @Test
    public void addToAmenities_shouldReturnFalseWhenNoNewAmenities() {
        // Arrange
        Hotel hotel = new Hotel();
        hotel.setAmenities(new ArrayList<>(List.of("WiFi", "Pool")));

        // Act
        boolean result = hotel.addToAmenities(List.of("Pool"));

        // Assert
        assertThat(result).isFalse();
        assertThat(hotel.getAmenities()).containsExactly("WiFi", "Pool");
    }
}