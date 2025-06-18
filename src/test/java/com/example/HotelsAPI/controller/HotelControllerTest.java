package com.example.HotelsAPI.controller;

import com.example.HotelsAPI.dto.HotelCreateDto;
import com.example.HotelsAPI.dto.HotelShortDto;
import com.example.HotelsAPI.service.HotelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HotelController.class)
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public HotelService hotelService() {
            return mock(HotelService.class);
        }
    }

    @Test
    void getAllHotels_shouldReturnList() throws Exception {
        HotelShortDto dto = new HotelShortDto();
        dto.setId(1L);
        dto.setName("Hotel1");

        when(hotelService.findAllHotels()).thenReturn(List.of(dto));

        mockMvc.perform(get("/property-view/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Hotel1"));
    }

    @Test
    void getHotelById_shouldReturnHotelCreateDto() throws Exception {
        HotelCreateDto dto = new HotelCreateDto();
        dto.setId(2L);
        dto.setName("Hotel2");

        when(hotelService.findHotelById(2L)).thenReturn(dto);

        mockMvc.perform(get("/property-view/hotels/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Hotel2"));
    }

    @Test
    void getHistogram_shouldReturnMap() throws Exception {
        Map<String, Long> map = new HashMap<>();
        map.put("BrandA", 5L);

        when(hotelService.getHistogram("brand")).thenReturn(map);

        mockMvc.perform(get("/property-view/histogram/brand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.BrandA").value(5));
    }

    @Test
    void searchHotels_shouldReturnResults() throws Exception {
        HotelShortDto dto = new HotelShortDto();
        dto.setId(3L);
        dto.setName("SearchHotel");

        when(hotelService.searchHotels("Hotel", "Brand", "City", "Country", "WiFi"))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/property-view/search")
                        .param("name", "Hotel")
                        .param("brand", "Brand")
                        .param("city", "City")
                        .param("country", "Country")
                        .param("amenities", "WiFi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[0].name").value("SearchHotel"));
    }

    @Test
    void createHotel_shouldReturnShortDto() throws Exception {
        HotelCreateDto createDto = new HotelCreateDto();
        createDto.setName("NewHotel");
        createDto.setDescription("A new test hotel");

        HotelShortDto responseDto = new HotelShortDto();
        responseDto.setId(4L);
        responseDto.setName("NewHotel");

        when(hotelService.createHotel(any(HotelCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/property-view/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4L))
                .andExpect(jsonPath("$.name").value("NewHotel"));
    }

    @Test
    void addAmenity_shouldReturnOk() throws Exception {
        List<String> amenities = List.of("WiFi", "Pool");

        doNothing().when(hotelService).addAmenities(eq(5L), anyList());

        mockMvc.perform(post("/property-view/hotels/5/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amenities)))
                .andExpect(status().isOk());

        verify(hotelService).addAmenities(5L, amenities);
    }
}
