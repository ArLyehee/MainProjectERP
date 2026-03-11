package com.gaebalfan.erp.security;

import com.gaebalfan.erp.domain.User;
import com.gaebalfan.erp.mapper.UserMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ErpUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    public ErpUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public ErpUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        return new ErpUserDetails(
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
