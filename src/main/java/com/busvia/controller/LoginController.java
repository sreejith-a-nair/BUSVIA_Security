package com.busvia.controller;

import com.busvia.entity.UserInfo;
import com.busvia.model.LoginRequest;
import com.busvia.model.LoginResponse;
import com.busvia.model.UserRequest;
import com.busvia.service.AuthService;
import com.busvia.service.JwtService;
import com.busvia.service.UserServiceAuth;
import com.busvia.utils.AuthUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/auth")
public class LoginController {

     @Autowired
     private  UserDetailsService userDetailsService;
     @Autowired
     private  JwtService jwtService;
     @Autowired
     AuthenticationManager authenticationManager;

     @Autowired
     UserServiceAuth userServiceAuth;

     @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) throws
            IndexOutOfBoundsException {
        System.out.println("login.. work: " + loginRequest);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        }
        catch (BadCredentialsException e) {
//            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Login failed due to bad credentials");
            System.out.println("Login failed due to bad credentials");
//            return null;
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
        System.out.println("mail id"+loginRequest.getEmail());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());


        final String jwt = jwtService.generateToken(userDetails.getUsername(),userDetails.getAuthorities().toString());
        System.out.println("ROLE,,,,,"+userDetails.getAuthorities().toString());
        System.out.println("JWT * "+ jwt);

//        return new LoginResponse(jwt);
        return ResponseEntity.ok(new LoginResponse(jwt));
}


    @PostMapping("/changePassword")
    ResponseEntity<String> changePassword(@RequestBody Map<String , String> requestMap){
        System.out.println("CHANGE PASSWORD METHOD WORKING *****:  "+requestMap);
        try{
            return userServiceAuth.changePassword(requestMap);
        }catch (Exception ex){
            ex.printStackTrace();
            return AuthUtil.getResponseEntity("Something went wrong !" , HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/changePasswordUser")
    ResponseEntity<String> changePasswordUser(@RequestBody Map<String , String> requestMap){
        System.out.println("CHANGE PASSWORD METHOD WORKING *****:  "+requestMap);
        try{
            return userServiceAuth.changePasswordUser(requestMap);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return AuthUtil.getResponseEntity("Something went wrong !" , HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> requestMap) {
        try {
            ResponseEntity<String> response = userServiceAuth.forgetPassword(requestMap);
            if (response.getStatusCode() == HttpStatus.OK) {
                String successMessage = response.getBody();
                System.out.println("Success: " + successMessage);
                return ResponseEntity.status(HttpStatus.OK).body("{\"message\": \"" + successMessage + "\"}");
            } else {
                String errorMessage = response.getBody();
                System.out.println("Error: " + errorMessage);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong!");
        }
    }


    @PostMapping("/register")
    public String addNewUser(@RequestBody UserRequest userRequest) {
        System.out.println("AUTH REGISTER WORK "+userRequest);
        if(userRequest!=null) {
            authService.saveUser(userRequest);
            return "User added successfully";
        }
        return  "Request is empty";
    }
    @PostMapping("/update")
    public String updateUser(@RequestBody UserRequest userRequest) {
        System.out.println("AUTH UPDATE WORK " + userRequest);
        if (userRequest != null) {
            authService.updateUser(userRequest);
            return "User updated successfully";
        }
        return "Request is empty";
    }



    @PostMapping("/blockUser")
    public String blockUser(@RequestBody UserRequest userRequest) {
        try {
            authService.blockUser(userRequest);
            return"User blocked successfully in auth service";
        } catch (Exception e) {
            return "Failed to block user: " + e.getMessage();
        }
    }

    @PostMapping("/unblockUser")
    public String unblockUser(@RequestBody UserRequest userRequest) {
        try {
            authService.unblockUser(userRequest);
            return "User unblocked successfully  in auth service";
        } catch (Exception e) {
            return "Failed to unblock user: " + e.getMessage();
        }
    }

    @GetMapping("/getUserByEmail")
    public UserInfo getUserByEmail(@RequestParam String email) {
        System.out.println("Authority role is By Driver "+email);
        UserInfo  user = authService.getUserByEmail(email);
        return user;
    }


//    @GetMapping("/generateOTPAndSendEmail")
//    public ResponseEntity<String> generateOTPAndSendEmail(@RequestParam String email) {
//        try {
//            System.out.println("Email id generateOTPAndSendEmail: " + email);
//            UserInfo userInfo = userServiceAuth.findByEmail(email);
//            if (userInfo != null) {
//                String emailId = userInfo.getEmail();
//                int otp = generateRandom4DigitNumber();
//                System.out.println("Generated OTP: " + otp);
//
//                String subject = "Reset Password - BusVia System";
//                String body = "Your OTP for password reset is: " + otp;
//                boolean emailSent = userServiceAuth.SendOtp(emailId, subject, body);
//                if (emailSent) {
//                    System.out.println("Email sent successfully!");
//                    return ResponseEntity.ok(String.valueOf(otp));
//                } else {
//                    // Return an error response in case of email sending failure
//                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("email not send");
//                }
//            } else {
//                // Return 404 Not Found if user info is not found
//                return ResponseEntity.notFound().build();
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            // Return an error response in case of any exception
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("user not found in ths email ");
//        }
//    }
//    @GetMapping("/generateOTPAndSendEmail")
//    public String generateOTPAndSendEmail(@RequestParam String email) {
//        try {
//            System.out.println("Email id generateOTPAndSendEmail: " + email);
//            UserInfo userInfo = userServiceAuth.findByEmail(email);
//            if (userInfo != null) {
//                String emailId = userInfo.getEmail();
//                int otp = generateRandom4DigitNumber();
//                System.out.println("Generated OTP: " + otp);
//
//                String subject = "Reset Password - BusVia System";
//                String body = "Your OTP for password reset is: " + otp;
//                boolean emailSent = userServiceAuth.SendOtp(emailId, subject, body);
//                if (emailSent) {
//                    System.out.println("Email sent successfully!");
//                    String otpString = String.valueOf(otp);
//                    System.out.println(otpString);
//                    return String.valueOf(otp);
//                } else {
//                    return String.valueOf(otp);
//                    // Return an error response in case of email sending failure
////                    return "email not send";
//                }
//            } else {
//                // Return 404 Not Found if user info is not found
//                return "user not found in this email";
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            // Return an error response in case of any exception
//            return "error occurred";
//        }
//    }

    @GetMapping("/generateOTPAndSendEmail")
    public ResponseEntity<String> generateOTPAndSendEmail(@RequestParam String email) {
        try {
            UserInfo userInfo = userServiceAuth.findByEmail(email);
            if (userInfo != null) {
                String emailId = userInfo.getEmail();
                int otp = generateRandom4DigitNumber();
                String subject = "Reset Password - BusVia System";
                String body = "Your OTP for password reset is: " + otp;
                boolean emailSent = userServiceAuth.SendOtp(emailId, subject, body);
                if (emailSent) {
                    return ResponseEntity.ok(String.valueOf(otp));
                } else {
                    // Email sending failed
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Email sending failed");
                }
            } else {
                // User not found
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            // Handle any unexpected exception
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }
    private int generateRandom4DigitNumber() {
        // Generate a random 4-digit number between 1000 and 9999
        return new Random().nextInt(9000) + 1000;
    }


}
