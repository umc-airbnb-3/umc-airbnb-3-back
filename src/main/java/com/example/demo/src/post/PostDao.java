package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.post.model.GetPostImgRes;
import com.example.demo.src.post.model.GetPostsRes;
import com.example.demo.src.post.model.PostImgUrlReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PostDao {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetPostsRes> selectUserPosts(int userIdx) {
        String selectUserPostsQuery = "select p.postIdx, u.userIdx, u.nickName, u.profileImgUrl, p.content,\n" +
                "       IF(postLikeCount is null, 0, postLikeCount) as postLikeCount,\n" +
                "       IF(commentCount is null, 0, commentCount) as commentCount,\n" +
                "       CASE\n" +
                "        WHEN TIMESTAMPDIFF(SECOND, p.updatedAt, CURRENT_TIMESTAMP) < 60\n" +
                "        THEN CONCAT(TIMESTAMPDIFF(SECOND, p.updatedAt, CURRENT_TIMESTAMP), '초 전')\n" +
                "        WHEN TIMESTAMPDIFF(MINUTE, p.updatedAt, CURRENT_TIMESTAMP) < 60\n" +
                "        THEN CONCAT(TIMESTAMPDIFF(MINUTE, p.updatedAt, CURRENT_TIMESTAMP), '분 전')\n" +
                "        WHEN TIMESTAMPDIFF(DAY, p.updatedAt, CURRENT_TIMESTAMP) < 365\n" +
                "        THEN CONCAT(TIMESTAMPDIFF(DAY, p.updatedAt, CURRENT_TIMESTAMP), '일 전')\n" +
                "        ELSE CONCAT(TIMESTAMPDIFF(YEAR, p.updatedAt, CURRENT_TIMESTAMP), '년 전')\n" +
                "        END as updatedAt,\n" +
                "        IF(mpl.userIdx is null, 'N', 'Y') as likeOrNot\n" +
                "FROM Follow as f\n" +
                "    left join (SELECT userIdx, nickName, profileImgUrl\n" +
                "               FROM User) as u on u.userIdx = f.followeeIdx\n" +
                "    left join (select userIdx, postIdx, content, updatedAt, status\n" +
                "               from Post\n" +
                "               where status='ACTIVE') as p on p.userIdx = f.followeeIdx\n" +
                "    left join (select postIdx, COUNT(postLikeIdx) as postLikeCount, status\n" +
                "               from PostLike\n" +
                "               where status='ACTIVE'\n" +
                "               group by postIdx) as pl on pl.postIdx = p.postIdx\n" +
                "    left join (select postIdx, COUNT(commentIdx) as commentCount, status\n" +
                "               from Comment\n" +
                "               where status='ACTIVE'\n" +
                "               group by postIdx) as c on c.postIdx = p.postIdx\n" +
                "    left join (select userIdx as myLikeCount, postIdx, userIdx\n" +
                "               from PostLike\n" +
                "               where status ='ACTIVE') as mpl on mpl.postIdx = p.postIdx and mpl.userIdx = f.followerIdx\n" +
                "where f.followerIdx = ? and f.status='ACTIVE';";
        int selectUserPostsParam = userIdx;
        return this.jdbcTemplate.query(selectUserPostsQuery,
                (rs,rowNum)->new GetPostsRes(
                        rs.getInt("postIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("content"),
                        rs.getInt("postLikeCount"),
                        rs.getInt("commentCount"),
                        rs.getString("updatedAt"),
                        rs.getString("likeOrNot"),
                        this.jdbcTemplate.query("SELECT pi.postImgUrlIdx, pi.imgUrl\n" +
                                        "FROM Post as p left join PostImgUrl as pi on p.postIdx = pi.postIdx\n" +
                                        "where p.postIdx = ? and p.status ='ACTIVE';",
                                (rs2,rowNum2)->new GetPostImgRes(
                                        rs2.getInt("postImgUrlIdx"),
                                        rs2.getString("imgUrl")
                                ), rs.getInt("postIdx"))
                ), selectUserPostsParam);
    }

    public int checkUserExist(int userIdx) {
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ?);";
        int checkUserExistParam = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery, int.class, checkUserExistParam);
    }

    public int checkPostExist(int postIdx){
        String checkPostExistQuery = "SELECT EXISTS(SELECT postIdx from Post where postIdx = ?)";
        int checkPostExistParam = postIdx;
        return this.jdbcTemplate.queryForObject(checkPostExistQuery, int.class, checkPostExistParam);
    }

    public int insertPost(int userIdx, String content) {
        String insertPostQuery = "INSERT INTO Post(userIdx, content) VALUES (?,?)";
        Object[] insertPostParams=new Object[]{userIdx, content};
        this.jdbcTemplate.update(insertPostQuery, insertPostParams);

        String lastInsertIdxQuery="SELECT last_insert_id()"; // 자동으로 가장 마지막에 들어간 index 반환
        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery, int.class);
    }

    public int insertPostImg(int postIdx, PostImgUrlReq postImgUrlreq) {
        String insertPostImgQuery = "INSERT INTO PostImgUrl(postIdx, imgUrl) VALUES (?,?)";
        Object[] insertPostImgParams = new Object[]{postIdx, postImgUrlreq.getImgUrl()};
        this.jdbcTemplate.update(insertPostImgQuery, insertPostImgParams);

        String lastInsertIdxQuery="SELECT last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery, int.class);
    }

    public int updatePost(int postIdx, String content) {
        String updatePostQuery ="UPDATE Post SET content=? WHERE postIdx=?";
        Object[] updatePostParams = new Object[]{content, postIdx}; // content=? 가 먼저 있으니까 content 먼저 넣어줌
        return this.jdbcTemplate.update(updatePostQuery, updatePostParams);
    }

    public int checkUsersPost(int userIdx, int postIdx) {
        String checkUsersPostQuery = "SELECT EXISTS(SELECT * FROM Post WHERE userIdx = ? and postIdx = ?)";
        Object[] checkUsersPostParams = new Object[]{userIdx, postIdx};
        return this.jdbcTemplate.queryForObject(checkUsersPostQuery, int.class, checkUsersPostParams);
    }

    public int deletePost(int postIdx) {
        String deletePostQuery = "UPDATE Post SET status='INACTIVE' where postIdx = ?";
        int deletePostParam = postIdx;
        return this.jdbcTemplate.update(deletePostQuery, deletePostParam);
    }
}
