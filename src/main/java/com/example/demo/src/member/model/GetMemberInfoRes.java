package com.example.demo.src.member.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetMemberInfoRes {
    private int memberId;
    private String firstName;
    private String lastName;
    private int idCheck;
    private int emailCheck;
    private int phoneCheck;
    private int superHost;
    private String introduction;
    private String location;
    private String language;
    private String company;
    private String profileImg;
}
