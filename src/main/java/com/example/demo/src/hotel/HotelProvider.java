package com.example.demo.src.hotel;

import com.example.demo.config.BaseException;
import com.example.demo.src.hotel.model.GetCategoryRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

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
            List<GetCategoryRes> getCategoryRes = hotelDao.getAllCategories();
            return getCategoryRes;
        }catch(Exception exception){
            logger.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
