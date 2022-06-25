package com.example.demo.src.ref.user;


import com.example.demo.config.BaseException;
import com.example.demo.src.ref.user.model.GetUserInfo;
import com.example.demo.src.ref.user.model.GetUserFeedRes;
import com.example.demo.src.ref.user.model.GetUserPosts;
import com.example.demo.src.ref.user.model.GetUserRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }


    public GetUserRes getUsersByEmail(String email) throws BaseException{
        try{
            GetUserRes getUsersRes = userDao.getUsersByEmail(email);
            return getUsersRes;
        }
        catch (Exception exception) {
            logger.warn(exception.getMessage());
            throw new BaseException(FIND_USER_FAIL);
        }
    }


    public GetUserRes getUsersByIdx(int userIdx) throws BaseException{
        try{
            GetUserRes getUserRes = userDao.getUsersByIdx(userIdx);
            return getUserRes;
        }
        catch (Exception exception) {
            logger.warn(exception.getMessage());
            throw new BaseException(FIND_USER_FAIL);
        }
    }


    public int checkEmail(String email) throws BaseException{
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            logger.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public GetUserFeedRes retrieveUserFeed(int userIdxByJwt, int userIdx) throws BaseException {
        boolean isMyFeed = true;

        if(checkUserExist(userIdx)==0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        try{
            if(userIdxByJwt != userIdx){
                isMyFeed=false;
            }
            GetUserInfo getUserInfo = userDao.selectUserInfo(userIdx);
            List<GetUserPosts> getUserPosts = userDao.getUserPost(userIdx);
            return new GetUserFeedRes(isMyFeed, getUserInfo, getUserPosts);

        }catch(Exception exception){
            logger.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUserExist(int userIdx) throws BaseException {
        try{
            return userDao.checkUserExist(userIdx);
        }catch(Exception exception){
            logger.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
