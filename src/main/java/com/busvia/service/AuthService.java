package com.busvia.service;

import com.busvia.config.AuthConfig;
import com.busvia.entity.UserInfo;
import com.busvia.model.UserRequest;
import com.busvia.repository.UserInfoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    @Autowired
    private  UserInfoRepo userInfoRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private  JwtService jwtService;

    public String saveUser(UserRequest userInfo){
  /* password encrypted in db*/
        UserInfo user = new UserInfo();
        user.setPassword(userInfo.getPassword());
        user.setEmail(userInfo.getEmail());
        user.setUuid(userInfo.getUuid());
        user.setRole(userInfo.getRole());
        user.setFirstName(userInfo.getFirstName());
        user.setBlock(true);
        userInfoRepo.save(user);
        return "Registration success";
    }
    public String updateUser(UserRequest userInfo){
        /* password encrypted in db*/
//        UserInfo user = new UserInfo();
        UserInfo user= userInfoRepo.findById( userInfo.getUuid())
                        .orElseThrow(()->new RuntimeException( "User not found in auth service "));
        System.out.println("User service uuid    : "+userInfo.getUuid());
        System.out.println("Auth service uuid   : "+user.getUuid());
        user.setEmail(userInfo.getEmail());
        user.setFirstName(userInfo.getFirstName());
        userInfoRepo.save(user);
        return "update in auth success";
    }

    public List<UserInfo> findAll() {
       return userInfoRepo.findAll();
    }


    public void blockUser(UserRequest userRequest) {
        UserInfo user =  userInfoRepo.findById(userRequest.getUuid())
                .orElseThrow(()->new RuntimeException( "User not found in auth service "));

        user.setBlock(false);
        UserInfo userBlockInfo=userInfoRepo.save(user);
        System.out.println("userBlockInfo : "+userBlockInfo);

    }

    public void unblockUser(UserRequest userRequest) {
        UserInfo user =  userInfoRepo.findById(userRequest.getUuid())
                .orElseThrow(()->new RuntimeException( "User not found in auth service "));

        user.setBlock(true);
        UserInfo userBlockInfo=userInfoRepo.save(user);
        System.out.println("user un-BlockInfo : "+userBlockInfo);

    }

    public UserInfo getUserByEmail(String email) {
        return userInfoRepo.findByEmail(email).orElseThrow(()->new RuntimeException( "User not found in by email in auth service "));
    }
}
