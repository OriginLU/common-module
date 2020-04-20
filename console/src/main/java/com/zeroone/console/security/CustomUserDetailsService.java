package com.zeroone.console.security;

import com.zeroone.console.entity.User;
import com.zeroone.console.security.utils.PasswordEncoderHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Charles
 * @since 2020-02-01
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = new User(username, PasswordEncoderHelper.encode("123456"));
        return new CustomUserDetail(user);
    }

    public void updateUserPassword(String username, String password) {

    }
}
