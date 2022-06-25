package com.example.demo.src.hotel;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.hotel.model.GetCategoryRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HotelProvider hotelProvider;

    private final HotelService hotelService;

    private final JwtService jwtService;

    @Autowired
    public HotelController(HotelProvider hotelProvider, HotelService hotelService, JwtService jwtService) {
        this.hotelProvider = hotelProvider;
        this.hotelService = hotelService;
        this.jwtService = jwtService;
    }

    /**
     * 호텔 카테고리 전체 조회 API
     * [GET] /hotels/categories
     * @return BaseResponse<GetCategoriesRes>
     */
    @ResponseBody
    @GetMapping("/categories")
    public BaseResponse<List<GetCategoryRes>> getCategories(){
        try{
            List<GetCategoryRes> getCategoryRes = hotelProvider.getAllCategories();
            return new BaseResponse<>(getCategoryRes);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
