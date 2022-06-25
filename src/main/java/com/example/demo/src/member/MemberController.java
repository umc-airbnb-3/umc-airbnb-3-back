package com.example.demo.src.member;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.member.MemberProvider;
import com.example.demo.src.member.MemberService;
import com.example.demo.src.member.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.POST_USERS_EMPTY_EMAIL;
import static com.example.demo.config.BaseResponseStatus.POST_USERS_INVALID_EMAIL;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/members")
public class MemberController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final MemberProvider memberProvider;
    @Autowired
    private final MemberService memberService;
    @Autowired
    private final JwtService jwtService;


    public MemberController(MemberProvider memberProvider, MemberService memberService, JwtService jwtService){

        this.memberProvider = memberProvider;
        this.memberService = memberService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("/{memberIdx}/img") // (GET) 127.0.0.1:9000/member/img
    public BaseResponse<GetMemberImgRes> getMembersImg(@PathVariable("memberIdx") int memberIdx) {
        try{

            GetMemberImgRes getMembersImgRes = memberProvider.getMembersImgByIdx(memberIdx);
            return new BaseResponse<>(getMembersImgRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
