package com.example.demo.src.hotel;

import com.example.demo.src.hotel.model.GetAllHotelsInCategoryRes;
import com.example.demo.src.hotel.model.GetCategoryRes;
import com.example.demo.src.hotel.model.GetHotelAmenityRes;
import com.example.demo.src.hotel.model.GetHotelInfoRes;
import com.example.demo.src.hotel.model.GetHotelRoomRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class HotelDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetCategoryRes> findAllCategories() {
        String getCategoriesQuery = "SELECT category_id, category_name, category_img\n" +
                "from category;";
        return this.jdbcTemplate.query(getCategoriesQuery,
                (rs, rowNum) -> new GetCategoryRes(
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("category_img")
                ));
    }

    public GetHotelInfoRes getHotelInfo(int hotelIdx) {
        String getHotelInfoQuery = "SELECT hotel.hotel_id, hotel_title, CONCAT(address, ', ', country) as location,\n" +
                "       CONCAT(member.last_name, member.first_name) as owner_name, max_capacity, count(hotel_room.bed_cnt) as bedroom_cnt,\n" +
                "       sum(hotel_room.bed_cnt) as bed_cnt, bathroom_cnt, start_date as check_in, end_date as check_out, hotel_fee, extra_fee, DATEDIFF(end_date,start_date) as accomodation_day,\n" +
                "       hotel_fee * DATEDIFF(end_date,start_date) as hotel_fee_sum,\n" +
                "       hotel_fee * DATEDIFF(end_date,start_date) + extra_fee as total_fee, hotel_introduction\n" +
                "FROM hotel\n" +
                "LEFT JOIN member ON hotel.owner_id = member.member_id\n" +
                "LEFT JOIN hotel_room ON hotel.hotel_id = hotel_room.hotel_id\n" +
                "WHERE hotel.hotel_id = ?;";

        int getHotelInfoParam = hotelIdx;
        return this.jdbcTemplate.queryForObject(getHotelInfoQuery,
                (rs, rowNum) -> new GetHotelInfoRes(
                        rs.getInt("hotel.hotel_id"),
                        rs.getString("hotel_title"),
                        rs.getString("location"),
                        rs.getString("owner_name"),
                        rs.getInt("max_capacity"),
                        rs.getInt("bedroom_cnt"),
                        rs.getInt("bed_cnt"),
                        rs.getInt("bathroom_cnt"),
                        rs.getString("check_in"),
                        rs.getString("check_out"),
                        rs.getInt("hotel_fee"),
                        rs.getInt("extra_fee"),
                        rs.getInt("accomodation_day"),
                        rs.getInt("hotel_fee_sum"),
                        rs.getInt("total_fee"),
                        rs.getString("hotel_introduction"))
                , getHotelInfoParam);
    }

    public List<String> getHotelImage(int hotelIdx) {
        String getHotelImageQuery = "SELECT img\n" +
                "FROM hotel_introduction_img\n" +
                "WHERE hotel_introduction_img.hotel_id = ?;";

        int getHotelImageParam = hotelIdx;
        return this.jdbcTemplate.query(getHotelImageQuery,
                (rs, rowNum) -> rs.getString("img"),
                getHotelImageParam
                );
    }

    public List<GetHotelRoomRes> getHotelRoom(int hotelIdx) {
        String getHotelRoomQuery = "SELECT hotel_id, bed_cnt, img\n" +
                "FROM hotel_room\n" +
                "WHERE hotel_id = ?;";

        int getHotelRoomParam = hotelIdx;
        return this.jdbcTemplate.query(getHotelRoomQuery,
                (rs, rowNum) -> new GetHotelRoomRes(
                        rs.getInt("hotel_id"),
                        rs.getInt("bed_cnt"),
                        rs.getString("img")
                ),getHotelRoomParam);
    }

    public List<GetHotelAmenityRes> getHotelAmenity(int hotelIdx) {
        String getHotelAmenityQuery = "SELECT hotel_id, img, hotel_amenity_introduction\n" +
                "FROM hotel_amenity\n" +
                "WHERE hotel_amenity.hotel_id = ?;";

        int getHotelAmenityParam = hotelIdx;
        return this.jdbcTemplate.query(getHotelAmenityQuery,
                (rs, rowNum) -> new GetHotelAmenityRes(
                        rs.getInt("hotel_id"),
                        rs.getString("img"),
                        rs.getString("hotel_amenity_introduction")
                ),getHotelAmenityParam);
    }


    public int checkCategory(int categoryId) {
        String checkCategoryQuery = "SELECT EXISTS(SELECT category_id from category where category_id = ?)";
        int checkCategoryParam = categoryId;
        return this.jdbcTemplate.queryForObject(checkCategoryQuery,
                int.class,
                checkCategoryParam);
    }

    public List<GetAllHotelsInCategoryRes> findAllHotelsInCategory(int categoryId) {
        String findAllHotelsInCategoryQuery = "SELECT cl.category_id, hotel_info.hotel_id, region, distance, base_date, fee, avg_grade\n" +
                "from categoryList as cl\n" +
                "left join (SELECT h.hotel_id,\n" +
                "                  CONCAT(SUBSTRING_INDEX(h.address, ',', 2), ', ', h.country) as region,\n" +
                "                  FORMAT(ST_DISTANCE_SPHERE(POINT(126, 37), h.location) * 0.001, 0) as distance,\n" +
                "                  IF (MONTH(h.start_date)=MONTH(h.end_date),\n" +
                "                     CONCAT(MONTH(h.start_date), 'm ', DAY(h.start_date),'d', '~', DAY(h.end_date), 'd'),\n" +
                "                     CONCAT(MONTH(h.start_date), 'm ', DAY(h.start_date), 'd', ' ~ ', MONTH(h.end_date), 'm', DAY(h.end_date), 'd')) as base_date,\n" +
                "                  FORMAT(h.hotel_fee+h.extra_fee,0) as fee,\n" +
                "                  TRUNCATE(AVG((hr.cleanliness + hr.accuracy + hr.communication\n" +
                "                                             + hr.location + hr.checkin + hr.satisfaction)/6),2) as avg_grade\n" +
                "            FROM hotel as h\n" +
                "            left join hotel_review as hr on h.hotel_id = hr.hotel_id\n" +
                "            GROUP BY h.hotel_id) as hotel_info on hotel_info.hotel_id = cl.hotel_id\n" +
                "where cl.category_id=?;";
        int findAllHotelsInCategoryParam = categoryId;

        String findAllHotelIntroductionImagesQuery = "SELECT img\n" +
                "from hotel_introduction_img\n" +
                "where hotel_id =?;";
        int findAllHotelIntroductionImagesParam = categoryId;

        return this.jdbcTemplate.query(findAllHotelsInCategoryQuery,
                (rs, rowNum)-> new GetAllHotelsInCategoryRes(
                        rs.getInt("hotel_id"),
                        this.jdbcTemplate.query(findAllHotelIntroductionImagesQuery,
                                (rs2, rowNum2) -> rs2.getString("img"),
                                findAllHotelIntroductionImagesParam),
                        // 이미지 리스트
                        rs.getString("region"),
                        rs.getString("distance") + "km 거리",
                        rs.getString("base_date").replace("m", "월").replace("d", "일"),
                        "₩"+rs.getString("fee")+" /박",
                        rs.getFloat("avg_grade")
                ), findAllHotelsInCategoryParam);
    }
}
