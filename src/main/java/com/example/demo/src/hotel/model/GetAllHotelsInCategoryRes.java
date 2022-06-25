package com.example.demo.src.hotel.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetAllHotelsInCategoryRes {
    private int hotelId;
    private List<String> hotelInstructionImgs;
    private String region;
    private String distance;
    private String baseDate;
    private String fee;
    private float avgGrade;
}
