package com.example.demo.src.ref.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUserInfo {
    private int userIdx;
    private String name;
    private String nickName;
    private String profileImgUrl;
    private String website;
    private String introduction;
    private int followerCount;
    private int followingCount;
    private int postCount;
}
