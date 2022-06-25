package com.example.demo.src.ref.user;


import com.example.demo.config.BaseException;
import com.example.demo.src.ref.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetUserRes> getUsers() {
        String getUsersQuery = "select userIdx,name,nickName,email from User";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email")
                ));
    }

    public GetUserRes getUsersByEmail(String email) {
        String getUsersByEmailQuery = "select userIdx,name,nickName,email from User where email=?";
        String getUsersByEmailParams = email;
        return this.jdbcTemplate.queryForObject(getUsersByEmailQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email")),
                getUsersByEmailParams);
    }


    public GetUserRes getUsersByIdx(int userIdx) throws BaseException {
        String getUsersByIdxQuery = "select userIdx,name,nickName,email from User where userIdx=?";
        int getUsersByIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUsersByIdxQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email")),
                getUsersByIdxParams);


        // return this.jdbcTemplate.queryForObject(getUsersByIdxQuery,
        //         (rs, rowNum) -> new GetUserRes(
        //                 rs.getInt("userIdx"),
        //                 rs.getString("name"),
        //                 rs.getString("nickName"),
        //                 rs.getString("email")),
        //         getUsersByIdxParams);
    }


    public GetUserInfo selectUserInfo(int userIdx) {
        String selectUserQuery = "select u.userIdx as userIdx,\n" +
                "       u.nickName as nickName,\n" +
                "       u.name as name,\n" +
                "       u.profileImgUrl as profileImgUrl,\n" +
                "       u.website as website,\n" +
                "       u.introduction as introduction,\n" +
                "       IF(postCount is null, 0, postCount) as postCount,\n" +
                "       IF(followerCount is null, 0, followerCount) as followerCount,\n" +
                "       IF(followingCount is null, 0, followingCount) as followingCount\n" +
                "from User as u\n" +
                "    left join (select userIdx, count(postIdx) as postCount\n" +
                "               from Post where status='ACTIVE'\n" +
                "                         group by userIdx) p on p.userIdx = u.userIdx\n" +
                "    left join (select followerIdx, count(followeeIdx) as followerCount\n" +
                "               from Follow where status='ACTIVE'\n" +
                "                           group by followerIdx) follower on follower.followerIdx = u.userIdx\n" +
                "    left join (select followeeIdx, count(followerIdx) as followingCount\n" +
                "               from Follow where status='ACTIVE'\n" +
                "                           group by followeeIdx) following on following.followeeIdx = u.userIdx\n" +
                "where u.userIdx = ? and u.status = 'ACTIVE';";
        int selectUserParam = userIdx;
        return this.jdbcTemplate.queryForObject(selectUserQuery,
                (rs, rowNum) -> new GetUserInfo(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("website"),
                        rs.getString("introduction"),
                        rs.getInt("followerCount"),
                        rs.getInt("followingCount"),
                        rs.getInt("postCount")
                        ), selectUserParam);
    }

    public List<GetUserPosts> getUserPost(int userIdx) {
        String getUserPostQuery = "select p.postIdx as postIdx, pi.imgUrl as postImgUrl\n" +
                "from Post as p\n" +
                "    join PostImgUrl as pi on pi.postIdx = p.postIdx and pi.status = 'ACTIVE'\n" +
                "    join User as u on u.userIdx = p.userIdx\n" +
                "where p.status = 'ACTIVE' and u.userIdx = ?\n" +
                "group by p.postIdx\n" +
                "HAVING min(pi.postImgUrlIdx)\n" +
                "order by p.postIdx;";
        int getUserPostParam = userIdx;
        return this.jdbcTemplate.query(getUserPostQuery,
                (rs, rowNum)->new GetUserPosts(
                        rs.getInt("postIdx"),
                        rs.getString("postImgUrl")
                ), getUserPostParam);
    }

    public int createUser(PostUserReq postUserReq) {
        String createUserQuery = "insert into User (name, nickName, phone, email, password) VALUES (?,?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getName(), postUserReq.getNickName(), postUserReq.getPhone(), postUserReq.getEmail(), postUserReq.getPassword()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    public int checkEmail(String email) {
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    public int modifyUserName(PatchUserReq patchUserReq) {
        String modifyUserNameQuery = "update User set nickName = ? where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getNickName(), patchUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery, modifyUserNameParams);
    }

    public DeleteUserRes deleteUserByIdx(DeleteUserReq deleteUserReq) throws BaseException {
        String deleteUserByIdxQuery = "update User set status = 'DELETED' where userIdx = ? ";
        Object[] deleteUserParams = new Object[]{deleteUserReq.getUserIdx()};
        this.jdbcTemplate.update(deleteUserByIdxQuery, deleteUserParams);

        String deletedUserInfoQuery = "select name, nickName, phone, email, pwd from User where userIdx = ? ";
        return this.jdbcTemplate.queryForObject(deletedUserInfoQuery,
                (rs, rowNum) -> new DeleteUserRes(
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("pwd")),
                deleteUserParams);
    }

    public int checkUserExist(int userIdx){
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ?);";
        int checkUserExistParam = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParam);
    }
}
