package com.bcp.contentfree.service;

import com.bcp.contentfree.entity.Project;
import com.bcp.contentfree.entity.User;
import com.bcp.contentfree.repositories.ProjectRepository;
import com.bcp.contentfree.repositories.UserRepository;
import com.bcp.contentfree.request.ChangePasswordRequest;
import com.bcp.contentfree.request.DeleteUserRequest;
import com.bcp.contentfree.request.RevokeProjectAccessRequest;
import com.bcp.contentfree.response.BaseResponse;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static com.bcp.contentfree.constant.Constant.ACCESS;
import static com.bcp.contentfree.constant.Constant.ADMIN;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectService projectService;


    private XLogger xLogger = XLoggerFactory.getXLogger(getClass());


    public ResponseEntity<Object> getUserByUserNameService(String userName) {
        User user = userRepository.findAUser(userName);
        if (user == null) {
            xLogger.error(Level.WARNING + " : No user Found with the UserName, {}", userName);
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setResponseMessage("No user Found");
            baseResponse.setResponseCode("98");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        }
        xLogger.info(Level.INFO + ": user found {}", user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<Object> changeUserPasswordService(ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findAUser(changePasswordRequest.getUserName());
        if (user == null) {
            xLogger.error(Level.WARNING + " : No user Found with the UserName for changepassword, {}", changePasswordRequest.getUserName());
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setResponseMessage("No user Found");
            baseResponse.setResponseCode("98");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        } else if (!user.getPassword().equals(changePasswordRequest.getOldPassword())) {
            xLogger.info(Level.WARNING + " : password didnt match for userName : {}", changePasswordRequest.getUserName());
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setResponseMessage("Old Password is wrong");
            baseResponse.setResponseCode("89");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        } else {
            xLogger.info(Level.INFO + " : changing the password for the UserName : {}", changePasswordRequest.getUserName());
            user.setPassword(changePasswordRequest.getNewPassword());
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setResponseCode("0");
            baseResponse.setResponseMessage("Password changed");
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);
        }

    }


    public ResponseEntity<Object> addUserService(User user) {
        BaseResponse baseResponse = new BaseResponse();
        if (userRepository.findAUser(user.getUserName()) == null) {
            if (user.getAccessProjectNames() != null) {
                for (String projectName : user.getAccessProjectNames()) {
                    Project project = projectRepository.findAProject(projectName);
                    if (project == null) {
                        xLogger.error(Level.WARNING + " : project doesnt exists with the name {}", projectName);
                        baseResponse.setResponseCode("93");
                        baseResponse.setResponseMessage("project not found");
                        return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
                    } else {
                        projectService.addUserToProject(user, project, ACCESS);
                    }
                }
            }

            if (user.getAdminProjectNames() != null) {
                for (String projectName : user.getAdminProjectNames()) {
                    Project project = projectRepository.findAProject(projectName);
                    if (project == null) {
                        xLogger.error(Level.WARNING + " : Admin Project doesnt exists with the name {}", projectName);
                        baseResponse.setResponseCode("93");
                        baseResponse.setResponseMessage("Admin project not found");
                        return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
                    } else {
                        projectService.addUserToProject(user, project, ADMIN);
                    }
                }
            }

            userRepository.insertUser(user);
            baseResponse.setResponseCode("0");
            baseResponse.setResponseMessage("User Added");
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);
        } else {
            xLogger.error(Level.WARNING + " : UserName already exists");
            baseResponse.setResponseCode("99");
            baseResponse.setResponseMessage("UserName already Present");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<Object> editUserService(User user) {
        User user1 = userRepository.findAUser(user.getUserName());
        BaseResponse baseResponse = new BaseResponse();
        if (user1 == null) {
            xLogger.error(Level.WARNING + ": No user Found with the UserName, {}", user.getUserName());
            baseResponse.setResponseMessage("No user Found");
            baseResponse.setResponseCode("98");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        }
        xLogger.info(Level.INFO + ": user found with UserName : {}", user1.getUserName());

        if (user.getAccessProjectNames() != null) {
            for (String projectName : user.getAccessProjectNames()) {
                Project project = projectRepository.findAProject(projectName);
                if (project == null) {
                    xLogger.error(Level.WARNING + " : project doesnt exists with the name {}", projectName);
                    baseResponse.setResponseCode("93");
                    baseResponse.setResponseMessage("Access project not found");
                    return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
                } else {
                    projectService.addUserToProject(user, project, ACCESS);
                }
            }
        }

        if (user.getAdminProjectNames() != null) {
            for (String projectName : user.getAdminProjectNames()) {
                Project project = projectRepository.findAProject(projectName);
                if (project == null) {
                    xLogger.error(Level.WARNING + " : Project doesnt exists with the name {}", projectName);
                    baseResponse.setResponseCode("93");
                    baseResponse.setResponseMessage("Admin project not found");
                    return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
                } else {
                    projectService.addUserToProject(user, project, ADMIN);
                }
            }
        }
        userRepository.updateUser(user);
        baseResponse.setResponseCode("0");
        baseResponse.setResponseMessage("User Data Updated");
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }


    public ResponseEntity<Object> deleteUser(DeleteUserRequest deleteUserRequest) {
        xLogger.info(Level.INFO + " : deleting userName : {}", deleteUserRequest.getUserName());

        User user = userRepository.findAUser(deleteUserRequest.getUserName());

        BaseResponse baseResponse = new BaseResponse();
        if (user == null) {
            xLogger.error(Level.WARNING + ": No user Found with the UserName {}", deleteUserRequest.getUserName());
            baseResponse.setResponseMessage("No user Found");
            baseResponse.setResponseCode("98");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        if (!user.getPassword().equals(deleteUserRequest.getPassword())) {
            xLogger.info(Level.WARNING + " : password didnt match for userName  : {}", deleteUserRequest.getUserName());
            baseResponse.setResponseMessage("Password is wrong");
            baseResponse.setResponseCode("89");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        if (user.getAdminProjectNames() != null) {
            xLogger.info(Level.WARNING + " : user still have alive admin projects  : {}", deleteUserRequest.getUserName());
            baseResponse.setResponseMessage("Admin Projects still there");
            baseResponse.setResponseCode("88");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        }
        if (user.getAccessProjectNames() != null) {
            for (String projectName : user.getAccessProjectNames()) {
                projectService.revokeProjectAccessService(new RevokeProjectAccessRequest(user.getUserName(), projectName));
            }
        }

        xLogger.info(Level.INFO + " DELETING USERNAME : {}", user.getUserName());
        userRepository.deleteUser(user);
        baseResponse.setResponseMessage("success");
        baseResponse.setResponseCode("0");
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);


    }

    public ResponseEntity<Object> getAllProjectsUserAccess(String userName) {
        xLogger.info(Level.INFO + " : getting all the project of the userName : {} ", userName);
        User user = userRepository.findAUser(userName);
        BaseResponse baseResponse = new BaseResponse();
        if (user == null) {
            xLogger.error(Level.WARNING + ": No user Found with the UserName, {}", userName);
            baseResponse.setResponseMessage("No user Found");
            baseResponse.setResponseCode("98");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        }
        List<Project> projects = new ArrayList<>();
        if (user.getAccessProjectNames() != null) {
            for (String projectName : user.getAccessProjectNames()) {
                projects.add(projectRepository.findAProject(projectName));
            }
            xLogger.info(Level.INFO + " : projects for userName {} are {}", userName, projects);
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } else {

            baseResponse.setResponseMessage("No Projects the user have access to");
            baseResponse.setResponseCode("00");
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);
        }

    }

    public ResponseEntity<Object> getAllUsers() {
        List<User> users = userRepository.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
