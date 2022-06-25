package com.example.demo.src.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUserFeedRes {
    
    @JsonProperty("isMyFeed")
    private boolean isMyFeed;

    private GetUserInfo getUserInfo;
    private List<GetUserPosts> getUserPosts;
}
