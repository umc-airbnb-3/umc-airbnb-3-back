package com.example.demo.src.hotel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetHotelAmenityRes {
    private int hotelId;
    private String img;
    private String introduction;
}
