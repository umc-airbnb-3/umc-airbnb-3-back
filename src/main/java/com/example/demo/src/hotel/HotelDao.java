package com.example.demo.src.hotel;

import com.example.demo.src.hotel.model.GetAllHotelsInCategoryRes;
import com.example.demo.src.hotel.model.GetCategoryRes;
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

    public int checkCategory(int categoryId) {
        String checkCategoryQuery = "SELECT EXISTS(SELECT category_id from category where category_id = ?)";
        int checkCategoryParam = categoryId;
        return this.jdbcTemplate.queryForObject(checkCategoryQuery,
                int.class,
                checkCategoryParam);
    }

    public List<GetAllHotelsInCategoryRes> findAllHotelsInCategory(int categoryId) {
        String findAllHotelsInCategoryQuery = "SELECT h.hotel_id,\n" +
                "       CONCAT(SUBSTRING_INDEX(h.address, ',', 2), ', ', h.country) as region,\n" +
                "       FORMAT(ST_DISTANCE_SPHERE(POINT(126, 37), h.location) * 0.001, 0) as distance,\n" +
                "       IF (MONTH(h.start_date)=MONTH(h.end_date),\n" +
                "           CONCAT(CAST(MONTH(h.start_date) AS CHAR(2)), 'm ', CAST(DAY(h.start_date) AS CHAR(2)),'d', '~', CAST(DAY(h.end_date) AS CHAR(2)), 'd'),\n" +
                "           CONCAT(CAST(MONTH(h.start_date) AS CHAR(2)), 'm ', CAST(DAY(h.start_date) AS CHAR(2)), 'd', ' ~ ', CAST(MONTH(h.end_date) AS CHAR(2)), 'm', CAST(DAY(h.end_date) AS CHAR), 'd')) as base_date,\n" +
                "        FORMAT(h.hotel_fee+h.extra_fee,0) as fee,\n" +
                "        TRUNCATE(AVG((hr.cleanliness + hr.accuracy + hr.communication\n" +
                "                                   + hr.location + hr.checkin + hr.satisfaction)/6),2) as avg_grade\n" +
                "from hotel as h\n" +
                "left join hotel_review as hr on h.hotel_id = hr.hotel_id\n" +
                "where h.hotel_id=?\n" +
                "GROUP BY h.hotel_id;";
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
