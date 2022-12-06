package com.inn.cafe.service.user;

import com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.dao.UserRepository;
import com.inn.cafe.pojo.User;
import com.inn.cafe.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        if (validateSignUpMap(requestMap)) {
            var user = this.userRepository.findByEmail(requestMap.get("email"));
            if (Objects.isNull(user)) {
                this.userRepository.save(this.getUserFromMap(requestMap));
                return CafeUtils.getResponseEntity(CafeConstants.SUCCESSFUL_REGISTRATION, HttpStatus.OK);
            }

            return CafeUtils.getResponseEntity(CafeConstants.EMAIL_EXISTS, HttpStatus.BAD_REQUEST);
        }

        return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
    }

    private boolean validateSignUpMap(Map<String, String> requestMap) {
        return requestMap.containsKey("name")
                && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email")
                && requestMap.containsKey("password");
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        var user = new User();
        user.setName(requestMap.get("name"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setStatus("false");
        user.setRole("user");

        return user;
    }
}
