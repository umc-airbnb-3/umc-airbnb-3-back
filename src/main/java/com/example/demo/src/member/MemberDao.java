package com.example.demo.src.member;


import com.example.demo.src.member.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class MemberDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public GetMemberInfoRes getMembersByIdx(int memberIdx) {
        String getMembersImgByIdxQuery = "SELECT member_id, first_name, last_name, id_check, email_check, phone_check, super_host,introduction, location, language, company, profile_img\n" +
                "FROM member\n" +
                "WHERE member_id = ?;";
        int getMembersImgParam = memberIdx;

        return this.jdbcTemplate.queryForObject(getMembersImgByIdxQuery,
                (rs, rowNum) -> new GetMemberInfoRes(
                        rs.getInt("member_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("id_check"),
                        rs.getInt("email_check"),
                        rs.getInt("phone_check"),
                        rs.getInt("super_host"),
                        rs.getString("introduction"),
                        rs.getString("location"),
                        rs.getString("language"),
                        rs.getString("company"),
                        rs.getString("profile_img")),

                        getMembersImgParam);
    }

    public int checkMemberExist(int memberIdx) {
        String checkUserExistQuery = "SELECT EXISTS(SELECT member_id FROM member WHERE member_id = ?)";
        int checkMemberExistParams = memberIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkMemberExistParams);

    }
}
