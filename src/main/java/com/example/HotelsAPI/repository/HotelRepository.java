package com.example.HotelsAPI.repository;

import com.example.HotelsAPI.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class HotelRepository extends JpaRepository<Hotel, Long> {
}
