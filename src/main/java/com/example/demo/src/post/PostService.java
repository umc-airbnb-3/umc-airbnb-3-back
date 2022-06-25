package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.post.model.PatchPostReq;
import com.example.demo.src.post.model.PostImgUrlReq;
import com.example.demo.src.post.model.PostPostReq;
import com.example.demo.src.post.model.PostPostRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class PostService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostProvider postProvider;
    private final PostDao postDao;
    private final JwtService jwtService;

    @Autowired
    public PostService(PostProvider postProvider, PostDao postDao, JwtService jwtService) {
        this.postProvider = postProvider;
        this.postDao = postDao;
        this.jwtService = jwtService;
    }

    public PostPostRes createPost(int userIdx, PostPostReq postPostReq) throws BaseException {
        try {
            int postIdx = postDao.insertPost(userIdx, postPostReq.getContent());
            for (PostImgUrlReq postImgUrl : postPostReq.getPostImgUrls()) {
                postDao.insertPostImg(postIdx, postImgUrl);
            }
            return new PostPostRes(postIdx);
        } catch (Exception exception) {
            logger.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyPost(int userIdx, int postIdx, PatchPostReq patchPostReq) throws BaseException {
        if (postProvider.checkUserExist(userIdx) == 0) {
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if (postProvider.checkPostExist(postIdx) == 0) {
            throw new BaseException(POSTS_EMPTY_POST_ID);
        }
        if (postProvider.checkUsersPost(userIdx, postIdx) == 0) {
            throw new BaseException(POSTS_NOT_USERS_POST);
        }
        try {
            // query문은 update 사용하므로 Dao의 메소드 이름도 update
            // userIdx는 Dao단까지 들어갈 필요는 없음
            int result = postDao.updatePost(postIdx, patchPostReq.getContent());
            if (result == 0) {
                throw new BaseException(MODIFY_POST_FAIL);
            }
        } catch (Exception exception) {
            logger.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deletePost(int postIdx) throws BaseException {

        if (postProvider.checkPostExist(postIdx) == 0) {
            throw new BaseException(POSTS_EMPTY_POST_ID);
        }
        try {
            int result = postDao.deletePost(postIdx);
            if (result == 0) {
                throw new BaseException(DELETE_POST_FAIL);
            }
        } catch (Exception exception) {
            logger.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
