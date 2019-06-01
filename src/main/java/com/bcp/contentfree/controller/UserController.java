package com.bcp.contentfree.controller;

import com.bcp.contentfree.entity.User;
import com.bcp.contentfree.repositories.UserRepository;
import com.bcp.contentfree.response.BaseResponse;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;


@RestController
@RequestMapping("/content/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    private XLogger xLogger = XLoggerFactory.getXLogger(getClass());




    @PostMapping("/addUser")
    public ResponseEntity<Object> addUser(@RequestBody User user) {
        xLogger.info(Level.INFO + " : checking if the userName : {}, is already present", user.getUserName());
        BaseResponse baseResponse = new BaseResponse();
        if(userRepository.findAUser(user.getUserName()) == null){
            userRepository.insertUser(user);
            baseResponse.setResponseCode("0");
            baseResponse.setResponseMessage("User Added");
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);
        } else{
            xLogger.error(Level.WARNING +" : UserName already exists");
            baseResponse.setResponseCode("99");
            baseResponse.setResponseMessage("UserName already Present");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/getUser")
    public ResponseEntity<Object> getUserByUserName(String userName){
        xLogger.info(Level.INFO+": checking if the userName : {}, is already present", userName);
        User user = userRepository.findAUser(userName);
        if(user == null){
            xLogger.error(Level.WARNING +" : No user Found with the UserName, {}", userName);
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setResponseMessage("No user Found");
            baseResponse.setResponseCode("98");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        }
        xLogger.info(Level.INFO +": user found {}", user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/editUser")
    public ResponseEntity<Object> editUser(@RequestBody User user){
        xLogger.info(Level.INFO +" : checking if the userName : {}, is already present", user.getUserName());
        User user1 = userRepository.findAUser(user.getUserName());
        BaseResponse baseResponse = new BaseResponse();
        if(user1 == null){
            xLogger.error(Level.WARNING +": No user Found with the UserName, {}", user.getUserName());
            baseResponse.setResponseMessage("No user Found");
            baseResponse.setResponseCode("98");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        }
        xLogger.info(Level.INFO +": user found with UserName : {}", user1.getUserName());
        userRepository.updateUser(user);
        baseResponse.setResponseCode("0");
        baseResponse.setResponseMessage("User Data Updated");
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }
}
