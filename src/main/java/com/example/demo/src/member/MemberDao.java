package com.example.demo.src.member;


import com.example.demo.config.BaseException;
import com.example.demo.src.member.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class MemberDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public GetMemberImgRes getMembersByIdx(int memberIdx) {
        String getMembersImgByIdxQuery = "SELECT member_id, profile_img\n" +
                "FROM member\n" +
                "WHERE member_id = ?;";
        int getMembersImgParam = memberIdx;

        return this.jdbcTemplate.queryForObject(getMembersImgByIdxQuery,
                (rs, rowNum) -> new GetMemberImgRes(
                        rs.getInt("member_id"),
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
