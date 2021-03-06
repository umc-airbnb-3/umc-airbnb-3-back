package com.example.demo.src.hotel;

import com.example.demo.config.BaseException;
import com.example.demo.src.hotel.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.HOTELS_EMPTY_CATEGORY_ID;

@Service
public class HotelProvider {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HotelDao hotelDao;
    private final JwtService jwtService;

    @Autowired
    public HotelProvider(HotelDao hotelDao, JwtService jwtService) {
        this.hotelDao = hotelDao;
        this.jwtService = jwtService;
    }

    public List<GetCategoryRes> getAllCategories() throws BaseException {
        try{
            List<GetCategoryRes> getCategoryRes = hotelDao.findAllCategories();
            return getCategoryRes;
        }catch(Exception exception){
            logger.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetAllHotelsInCategoryRes> getAllHotelsInCategory(int categoryId) throws BaseException {
        // categoryId 존재하지 앖으면 에러
        if(checkCategoryExists(categoryId)==0){
            throw new BaseException(HOTELS_EMPTY_CATEGORY_ID);
        }

        try{
            logger.warn(Integer.toString(checkCategoryExists(categoryId)));
            List<GetAllHotelsInCategoryRes> getAllHotelsInCategoryRes = hotelDao.findAllHotelsInCategory(categoryId);
            return getAllHotelsInCategoryRes;
        }catch(Exception exception){
            logger.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    private int checkCategoryExists(int categoryId) throws BaseException {
        try {
            return hotelDao.checkCategory(categoryId);
        } catch(Exception exception){
            logger.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetHotelFeedRes getHotelInfoRes(int hotelIdx) throws BaseException {
        try{

            GetHotelInfoRes getHotelInfoRes = hotelDao.getHotelInfo(hotelIdx);
            List<String> hotelImage = hotelDao.getHotelImage(hotelIdx);
            List<GetHotelRoomRes> getHotelRoomRes = hotelDao.getHotelRoom(hotelIdx);
            List<GetHotelAmenityRes> getHotelAmenityRes = hotelDao.getHotelAmenity(hotelIdx);

            return new GetHotelFeedRes(getHotelInfoRes, hotelImage, getHotelRoomRes, getHotelAmenityRes);

        } catch(Exception exception){
            logger.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
