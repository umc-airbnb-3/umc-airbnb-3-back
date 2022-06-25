package com.example.demo.src.ref.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchPostReq {
    /*
    인스타그램 -> 게시물의 사진 수정은 불가능, 내용만 수정 가능
     */
    private int userIdx;
    private String content;
}
