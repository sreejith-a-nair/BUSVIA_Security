package com.busvia.service;

import com.busvia.config.JwtRequestFilter;
import com.busvia.entity.UserInfo;
import com.busvia.model.UserRequest;
import com.busvia.repository.UserInfoRepo;
import com.busvia.utils.AuthUtil;
import com.busvia.utils.EmailUtils;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
    import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceAuthImp implements UserServiceAuth {

    @Autowired
    UserInfoRepo userInfoRepo;
    @Autowired
    JwtRequestFilter jwtFilter;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    EmailUtils emailUtils;

    @Autowired
    JavaMailSender mailSender;

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {

        try {
            String userEmail = jwtFilter.getCurrentUSer();

            Optional<UserInfo> user = userInfoRepo.findByEmail(userEmail);
            UserInfo userObj;
            if(user.isPresent()){
                userObj = user.get();
            }else {
//                throw new UserPrincipalNotFoundException("User with email " + userEmail + " not found");
                return AuthUtil.getResponseEntity("User not found enter valid password", HttpStatus.INTERNAL_SERVER_ERROR);
            }


            String oldPassword = requestMap.get("oldPassword");
//            String storedHashedPassword = userObj.getPassword();


            if (passwordEncoder.matches(oldPassword, userObj.getPassword())) {
                System.out.println("Passords are matched >>>>>>>>"+oldPassword);

                String newPassword = requestMap.get("newPassword");
                System.out.println("Passords are matched >>>>>>>>OLD "+oldPassword +"NEW "+newPassword);
                userObj.setPassword(passwordEncoder.encode(newPassword));
                userInfoRepo.save(userObj);
                return AuthUtil.getResponseEntity("Password changed successfully", HttpStatus.OK);
            } else {
                return AuthUtil.getResponseEntity("Incorrect old password", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return AuthUtil.getResponseEntity("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> changePasswordUser(Map<String, String> requestMap) {

        try {

             String userMail = requestMap.get("email");
            System.out.println("email :"+ userMail);
            Optional<UserInfo> user = userInfoRepo.findByEmail(userMail);
            System.out.println("user "+ user);
            UserInfo userObj;
            if(user.isPresent()){
                userObj = user.get();
            }else {
                return AuthUtil.getResponseEntity("User not found enter valid email", HttpStatus.INTERNAL_SERVER_ERROR);
            }


            if (!userMail.isEmpty()) {

                String newPassword = requestMap.get("newPassword");
                userObj.setPassword(passwordEncoder.encode(newPassword));
                userInfoRepo.save(userObj);
                return AuthUtil.getResponseEntity("Password changed successfully", HttpStatus.OK);
            } else {
                return AuthUtil.getResponseEntity("Incorrect old password", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return AuthUtil.getResponseEntity("Invalid email  ,Enter registered email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<String> forgetPassword(Map<String, String> requestMap) {

        try {
            Optional<UserInfo> userInfo = userInfoRepo.findByEmail(requestMap.get("email"));
            if (userInfo.isPresent()) {
                UserInfo userObj = userInfo.get();
                System.out.println("work 2 " + userObj.getEmail());
                String emailId = userObj.getEmail();
                String url = "http://localhost:4200/customer/change-pswd";
                String subject = "Reset Password - BusVia System";
                String body = "Click the link reset your password : " + url;
                boolean emailSent   =  sendMail(emailId, subject, body);

                if (emailSent) {
//                    return ResponseEntity.ok("Email sent successfully!");
                    System.out.println("success");
                    return ResponseEntity.status(HttpStatus.OK).body("Email sent successfully!");
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email. invalid email enter registered mail ");
                }
            } else {
                System.out.println("User with email " + requestMap.get("email") + " not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid email. Please enter a registered email.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send password reset email. Please try again later.");
        }
    }


    @Override
    public boolean sendMail(String toEmail, String subject, String body) {

        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("anairsreejith1998@gmail.com");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            System.out.println("mail send success");
            return true;
        }catch (MailException ex){
            ex.printStackTrace();
            System.out.println("Failed to send email: " + ex.getMessage());
            return false;
        }



    }

    @Override
    public UserInfo findByEmail(String email) {
       return   userInfoRepo.findByEmail(email).orElseThrow();
    }

    @Override
    public boolean SendOtp(String emailId, String subject, String body) {
        System.out.println("Send otp work ! ");
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("anairsreejith1998@gmail.com");
            message.setTo(emailId);
            message.setSubject(subject);
            message.setText(body);

//            mailSender.send(message);
            System.out.println("mail send success");
            return true;
        }catch (MailException ex){
            ex.printStackTrace();
            System.out.println("Failed to send otp email: " + ex.getMessage());
            return false;
        }

    }


}

