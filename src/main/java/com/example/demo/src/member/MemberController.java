package com.example.demo.src.member;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.member.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins="*", allowedHeaders = "*")
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
    @GetMapping("/{memberIdx}") // (GET) 127.0.0.1:9000/members/:memberIdx
    public BaseResponse<GetMemberInfoRes> getMembersByIdx(@PathVariable("memberIdx") int memberIdx) {
        try{

            GetMemberInfoRes getMembersImgRes = memberProvider.getMembersByIdx(memberIdx);
            return new BaseResponse<>(getMembersImgRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /*@ResponseBody
    @GetMapping("/{memberIdx}/profile") // (GET) 127.0.0.1:9000/member/img
    public BaseResponse<GetMemberProfileRes> getMembersImg(@PathVariable("memberIdx") int memberIdx) {
        try{

            GetMemberImgRes getMembersImgRes = memberProvider.getMembersImgByIdx(memberIdx);
            return new BaseResponse<>(getMembersImgRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }*/


}
