package com.example.demo.src.ref.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.ref.post.model.GetPostsRes;
import com.example.demo.src.ref.post.model.PatchPostReq;
import com.example.demo.src.ref.post.model.PostPostReq;
import com.example.demo.src.ref.post.model.PostPostRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostProvider postProvider;

    private final PostService postService;

    private final JwtService jwtService;

    @Autowired
    public PostController(PostProvider postProvider, PostService postService, JwtService jwtService) {
        this.postProvider = postProvider;
        this.postService = postService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetPostsRes>> getPosts(@RequestParam int userIdx){
        try{
            List<GetPostsRes> getPostsRes = postProvider.retrievePosts(userIdx, userIdx);
            return new BaseResponse<>(getPostsRes);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostPostRes> createPost(@RequestBody PostPostReq postPostReq){
        try{
            // Controller가 형식적 validation
            if(postPostReq.getContent().length() > 450){
                return new BaseResponse<>(BaseResponseStatus.POST_POST_CONTENT_TO_LONG);
            }
            if(postPostReq.getPostImgUrls().size() < 1){
                return new BaseResponse<>(BaseResponseStatus.POST_POST_EMPTY_IMGURL);
            }
            // 생성하는 거니까 postService에게
            // 나중에 jwt로 userIdx를 편리하게 주기 위해 userIdx는 따로 넣어주자.
            PostPostRes postPostRes = postService.createPost(postPostReq.getUserIdx(), postPostReq);
            return new BaseResponse<>(postPostRes);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{postIdx}")
    public BaseResponse<String> modifyPost(@PathVariable("postIdx") int postIdx, @RequestBody PatchPostReq patchPostReq){
        try{
            // 형식적 validation -> 내용 길이
            if(patchPostReq.getContent().length() > 450){
                return new BaseResponse<>(BaseResponseStatus.POST_POST_CONTENT_TO_LONG);
            }
            // userIdx -> 나중에 jwt
            postService.modifyPost(patchPostReq.getUserIdx(), postIdx, patchPostReq);

            // 성공 시 String 출력
            String result = "게시물 수정을 완료하였습니다.";
            return new BaseResponse<>(result);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PatchMapping("/{postIdx}/status")
    public BaseResponse<String> deletePost(@PathVariable("postIdx") int postIdx){ // 따로 모델이 필요 없음.
        try{
            postService.deletePost(postIdx);

            String result = "게시물 삭제를 완료하였습니다.";
            return new BaseResponse<>(result);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
