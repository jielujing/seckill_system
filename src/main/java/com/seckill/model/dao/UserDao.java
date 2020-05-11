package com.seckill.model.dao;

import com.seckill.model.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @author Dell
 * @create 2019-07-18 19:18
 */
@Mapper
@Repository
public interface UserDao {
    @Select("SELECT * FROM  user WHERE id=#{id} ")
    User selectUserById(@Param("id") long id);

    @Update("update user set password = #{password} where id = #{id}")
    void updateUserPassword(User user);

    @Insert("insert into user values(#{id},#{nickName},#{password},#{salt},#{head},#{registerDate},#{lastLoginDate},#{loginCount})")
    int insertUser(User user);
}
