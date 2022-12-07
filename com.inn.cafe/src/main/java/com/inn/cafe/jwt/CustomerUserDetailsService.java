package com.inn.cafe.jwt;

import com.inn.cafe.dao.UserRepository;
import com.inn.cafe.pojo.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class CustomerUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private User user;

    public CustomerUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        this.user = this.userRepository.findByEmail(username);
        if (!Objects.isNull(this.user)) {
            return new org.springframework.security.core.userdetails.User(
                    this.user.getEmail(), this.user.getPassword(), new ArrayList<>());
        }

        throw new UsernameNotFoundException("User not found");
    }

    public User getUser() {
        return this.user;
    }
}
