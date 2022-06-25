package com.example.demo.src.hotel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetHotelFeedRes {

    private GetHotelInfoRes getHotelInfo;
    private List<String> hotelImg;
    private List<GetHotelRoomRes> getHotelRoomRes;
    private List<GetHotelAmenityRes> getHotelAmenityRes;
}
