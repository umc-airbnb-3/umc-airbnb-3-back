package com.example.demo.src.hotel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetHotelRoomRes {
    private int hotelId;
    private int bedCnt;
    private String roomImg;
}
