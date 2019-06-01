package com.bcp.contentfree.service;

import com.bcp.contentfree.entity.Project;
import com.bcp.contentfree.entity.User;
import com.bcp.contentfree.repositories.ProjectRepository;
import com.bcp.contentfree.repositories.UserRepository;
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

@Service
public class ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    UserRepository userRepository;

    private XLogger xLogger = XLoggerFactory.getXLogger(getClass());

    /**
     * @param project
     * @return
     */
    public ResponseEntity<Object> addProjectService(Project project) {
        xLogger.info(Level.INFO + " : checking if the userName : {}, is already present", project.getProjectName());

        User adminUser = userRepository.findAUser(project.getProjectAdminUserName());
        Project oldProject = projectRepository.findAProject(project.getProjectName());
        BaseResponse baseResponse = new BaseResponse();
        if (adminUser == null) {
            xLogger.error(Level.WARNING + " : admin user not found userName : {}", project.getProjectAdminUserName());
            baseResponse.setResponseCode("97");
            baseResponse.setResponseMessage("Admin User Not Found");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        } else if (oldProject != null) {
            xLogger.error(Level.WARNING + " : {} Project is already exists", oldProject.getProjectName());
            baseResponse.setResponseCode("96");
            baseResponse.setResponseMessage(" Project already exists with name");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        } else if (project.getProjectUserNames() != null) {
            for (String userName : project.getProjectUserNames()) {
                User accessUser = userRepository.findAUser(userName);

                if (accessUser == null) {
                    xLogger.error(Level.WARNING + " : access user not found userName : {}", userName);
                    baseResponse.setResponseCode("95");
                    baseResponse.setResponseMessage("Access User Not Found");
                    return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
                } else {
                    if (accessUser.getUserName().equals(adminUser.getUserName())) {
                        xLogger.warn(Level.WARNING + " : This is the admin user, so not adding again into accessUsers userName : {}", accessUser.getUserName());
                        baseResponse.setResponseCode("94");
                        baseResponse.setResponseMessage("Admin User is also added in Access user");
                        return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
                    } else {
                        xLogger.info(Level.INFO + " : adding {} as access Project for the userName {}", project.getProjectName(), accessUser.getUserName());
                        List<String> accessProjectNames = accessUser.getAccessProjectNames();

                        if (accessProjectNames != null) {
                            xLogger.info(Level.INFO + " : admin projects for the  user {} are {}", accessUser.getUserName(), accessProjectNames);
                            accessProjectNames.add(project.getProjectName());
                            accessUser.setAccessProjectNames(accessProjectNames);
                        } else {
                            xLogger.info(Level.INFO + " : there are no admin projects yet for the userName : {}", adminUser.getUserName());
                            List<String> newAccessProjects = new ArrayList<>();
                            newAccessProjects.add(project.getProjectName());
                            accessUser.setAccessProjectNames(newAccessProjects);
                        }
                        xLogger.info(Level.INFO + " : updating the access User Data to add project");
                        userRepository.updateUser(accessUser);
                    }
                }
            }
        }

        List<String> adminProjects = adminUser.getAdminProjectNames();
        if (adminProjects != null) {
            xLogger.info(Level.INFO + " : admin projects for the admin user {} are {}", project.getProjectAdminUserName(), adminProjects);
            adminProjects.add(project.getProjectName());
            adminUser.setAdminProjectNames(adminProjects);
        } else {
            xLogger.info(Level.INFO + " : there are no admin projects yet for the userName : {}", adminUser.getUserName());
            List<String> newAdminProjects = new ArrayList<>();
            newAdminProjects.add(project.getProjectName());
            adminUser.setAdminProjectNames(newAdminProjects);
        }
        xLogger.info(Level.INFO + " : updating the user");
        userRepository.updateUser(adminUser);

        projectRepository.insertProject(project);
        baseResponse.setResponseMessage("Added New Project");
        baseResponse.setResponseCode("0");
        return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    /**
     *
     * @param changeProjectAdminRequest
     * @return
     */
/*    public ResponseEntity<Object> changeProjectAdminService(ChangeProjectAdminRequest changeProjectAdminRequest){



    }*/
}
