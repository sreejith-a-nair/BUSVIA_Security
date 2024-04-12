package com.busvia.service;


import com.busvia.entity.UserInfo;
import com.busvia.model.UserRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface UserServiceAuth {
    ResponseEntity<String> changePassword(Map<String,String> requestMap);

    ResponseEntity<String> changePasswordUser(Map<String,String> requestMap);

    ResponseEntity<String> forgetPassword(Map<String, String> requestMap);

    boolean sendMail(String toEmail,
                  String subject,
                  String body);


    UserInfo findByEmail(String email);

    boolean SendOtp(String emailId, String subject, String body);

}
