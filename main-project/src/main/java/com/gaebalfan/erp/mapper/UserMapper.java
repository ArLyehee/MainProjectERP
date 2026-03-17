package com.gaebalfan.erp.mapper;

import com.gaebalfan.erp.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {

    void insertUser(User user);

    Optional<User> findById(@Param("userId") Long userId);

    Optional<User> findByUsername(@Param("username") String username);

    List<User> findAll();

    void updateUser(User user);

    void updateStatus(@Param("userId") Long userId, @Param("status") String status);

    void updatePassword(@Param("userId") Long userId, @Param("password") String password);

    int countByUsername(@Param("username") String username);
}
