package com.bcp.contentfree.controller;

import com.bcp.contentfree.entity.User;
import com.bcp.contentfree.request.ChangePasswordRequest;
import com.bcp.contentfree.request.DeleteUserRequest;
import com.bcp.contentfree.service.UserService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.logging.Level;


@RestController
@RequestMapping("/content/user")
public class UserController {

    @Autowired
    UserService userService;

    private XLogger xLogger = XLoggerFactory.getXLogger(getClass());

    @GetMapping("/getAllUsers")
    public ResponseEntity<Object> getAllUsers() {
        xLogger.info(Level.INFO + " : getting all the users");
        return userService.getAllUsers();
    }

    @GetMapping("/getAllProjectsUserAccess")
    public ResponseEntity<Object> getAllProjectsUserAccess(String userName) {
        xLogger.info(Level.INFO + " : getting all the project of the userName : {} ", userName);
        return userService.getAllProjectsUserAccess(userName);
    }

    @PostMapping("/addUser")
    public ResponseEntity<Object> addUser(@RequestBody User user) {
        xLogger.info(Level.INFO + " : checking if the userName : {}, is already present", user.getUserName());
        return userService.addUserService(user);
    }

    @GetMapping("/getUser/{userName}")
    public ResponseEntity<Object> getUserByUserName(@PathVariable String userName) {
        xLogger.info(Level.INFO + ": checking if the userName : {}, is already present", userName);
        return userService.getUserByUserNameService(userName);
    }

    @PutMapping("/editUser")
    public ResponseEntity<Object> editUser(@RequestBody @Valid User user) {
        xLogger.info(Level.INFO + " : checking if the userName : {}, is already present", user.getUserName());
        return userService.editUserService(user);
    }

    @PutMapping("/changePassword")
    public ResponseEntity<Object> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        xLogger.info(Level.INFO + " : change password controller for UserName : {}", changePasswordRequest.getUserName());
        return userService.changeUserPasswordService(changePasswordRequest);
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<Object> deleteUser(@RequestBody @Valid DeleteUserRequest deleteUserRequest) {
        xLogger.info(Level.INFO + " : entered delete User for UserName : {}", deleteUserRequest.getUserName());
        return userService.deleteUser(deleteUserRequest);
    }
}
