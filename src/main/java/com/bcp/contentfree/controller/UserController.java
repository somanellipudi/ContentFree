package com.bcp.contentfree.controller;

import com.bcp.contentfree.entity.User;
import com.bcp.contentfree.repositories.UserRepository;
import com.bcp.contentfree.response.BaseResponse;
import com.bcp.contentfree.service.UserService;
import io.swagger.annotations.ApiModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;


@RestController
@RequestMapping("/content/user")
public class UserController {

    @Autowired
    UserService userService;

    private XLogger xLogger = XLoggerFactory.getXLogger(getClass());

    @PostMapping("/addUser")
    public ResponseEntity<Object> addUser(@RequestBody User user) {
        xLogger.info(Level.INFO + " : checking if the userName : {}, is already present", user.getUserName());
        return userService.addUserService(user);
    }

    @GetMapping("/getUser/{userName}")
    public ResponseEntity<Object> getUserByUserName(@PathVariable String userName){
        xLogger.info(Level.INFO+": checking if the userName : {}, is already present", userName);
       return userService.getUserByUserNameService(userName);
    }

    @PutMapping("/editUser")
    public ResponseEntity<Object> editUser(@RequestBody User user){
        xLogger.info(Level.INFO +" : checking if the userName : {}, is already present", user.getUserName());
        return userService.editUserService(user);
    }
}
