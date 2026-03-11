package com.gaebalfan.erp.config;

import com.gaebalfan.erp.domain.User;
import com.gaebalfan.erp.mapper.UserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        return args -> {

            if (userMapper.countByUsername("admin") == 0) {

                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("1234"));
                admin.setName("관리자");
                admin.setRole("ADMIN");
                admin.setDepartment("관리팀");
                admin.setStatus("ACTIVE");

                userMapper.insertUser(admin);

                System.out.println("관리자 계정 생성 완료");
            }
        };
    }
}