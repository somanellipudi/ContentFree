package com.bcp.contentfree.service;

import com.bcp.contentfree.entity.Project;
import com.bcp.contentfree.entity.User;
import com.bcp.contentfree.repositories.ProjectRepository;
import com.bcp.contentfree.repositories.UserRepository;
import com.bcp.contentfree.response.BaseResponse;
import io.swagger.annotations.ApiModel;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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


    public ResponseEntity<Object> addUserService(User user) {
        BaseResponse baseResponse = new BaseResponse();
        if (userRepository.findAUser(user.getUserName()) == null) {
            if(user.getAccessProjectNames() != null){
                for(String projectName : user.getAccessProjectNames()){
                    Project project = projectRepository.findAProject(projectName);
                    if(project == null){
                        xLogger.error(Level.WARNING +" : project doesnt exists with the name {}", projectName);
                        baseResponse.setResponseCode("93");
                        baseResponse.setResponseMessage("project not found");
                        return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
                    } else{
                        projectService.addUserToProject(user, project, ACCESS);
                    }
                }
            }

            if(user.getAdminProjectNames() != null){
                for(String projectName : user.getAdminProjectNames()){
                    Project project = projectRepository.findAProject(projectName);
                    if(project == null){
                        xLogger.error(Level.WARNING +" : Project doesnt exists with the name {}", projectName);
                        baseResponse.setResponseCode("93");
                        baseResponse.setResponseMessage("project not found");
                        return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
                    } else{
                        projectService.addUserToProject(user, project,ADMIN);
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

        if(user.getAccessProjectNames() != null){
            for(String projectName : user.getAccessProjectNames()){
                Project project = projectRepository.findAProject(projectName);
                if(project == null){
                    xLogger.error(Level.WARNING +" : project doesnt exists with the name {}", projectName);
                    baseResponse.setResponseCode("93");
                    baseResponse.setResponseMessage("Access project not found");
                    return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
                } else{
                    projectService.addUserToProject(user, project, ACCESS);
                }
            }
        }

        if(user.getAdminProjectNames() != null){
            for(String projectName : user.getAdminProjectNames()){
                Project project = projectRepository.findAProject(projectName);
                if(project == null){
                    xLogger.error(Level.WARNING +" : Project doesnt exists with the name {}", projectName);
                    baseResponse.setResponseCode("93");
                    baseResponse.setResponseMessage("Admin project not found");
                    return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
                } else{
                    projectService.addUserToProject(user, project,ADMIN);
                }
            }
        }
        userRepository.updateUser(user);
        baseResponse.setResponseCode("0");
        baseResponse.setResponseMessage("User Data Updated");
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }
}
