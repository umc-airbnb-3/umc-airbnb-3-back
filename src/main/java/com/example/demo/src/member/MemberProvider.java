package com.example.demo.src.member;


import com.example.demo.config.BaseException;
import com.example.demo.src.member.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class MemberProvider {

    private final MemberDao memberDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public MemberProvider(MemberDao memberDao, JwtService jwtService) {
        this.memberDao = memberDao;
        this.jwtService = jwtService;
    }


    public GetMemberInfoRes getMembersByIdx(int memberIdx) throws BaseException{

        if(checkMemberExist(memberIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        try{
            GetMemberInfoRes getMembersInfoRes = memberDao.getMembersByIdx(memberIdx);
            return getMembersInfoRes;
        }
        catch (Exception exception){
            logger.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkMemberExist(int memberIdx) throws BaseException {
        try {
            return memberDao.checkMemberExist(memberIdx);
        } catch (Exception exception) {
            logger.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
