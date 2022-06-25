package com.example.demo.src.hotel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetHotelInfoRes {
    private int hotelId;
    private String hotelTitle;
    private String location;
    private String ownerName;
    private int maxCapacity;
    private int bedroomCnt;
    private int bedCnt;
    private int bathroomCnt;
    private String checkIn;
    private String checkOut;
    private int hotelFee;
    private int extraFee;
    private int accomodationDay;
    private int hotelFeeSum;
    private int totalFee;
    private String hotelIntroduction;
}
