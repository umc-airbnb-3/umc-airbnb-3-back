package com.example.demo.src.hotel;

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

    public List<GetCategoryRes> getAllCategories() {
        String getCategoriesQuery = "SELECT category_id, category_name, category_img\n" +
                "from category;";
        return this.jdbcTemplate.query(getCategoriesQuery,
                (rs, rowNum) -> new GetCategoryRes(
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("category_img")
                ));
    }
}
