package com.bcp.contentfree.service;


import com.bcp.contentfree.entity.Project;
import com.bcp.contentfree.entity.User;
import com.bcp.contentfree.repositories.ProjectRepository;
import com.bcp.contentfree.repositories.UserRepository;
import com.bcp.contentfree.request.ChangeProjectAdminRequest;
import com.bcp.contentfree.request.ProjectAccessRequest;
import com.bcp.contentfree.request.RevokeProjectAccessRequest;
import com.bcp.contentfree.response.BaseResponse;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import static com.bcp.contentfree.constant.Constant.ACCESS;
import static com.bcp.contentfree.constant.Constant.ADMIN;

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
                        Set<String> accessProjectNames = accessUser.getAccessProjectNames();

                        if (accessProjectNames != null) {
                            xLogger.info(Level.INFO + " : admin projects for the  user {} are {}", accessUser.getUserName(), accessProjectNames);
                            accessProjectNames.add(project.getProjectName());
                            accessUser.setAccessProjectNames(accessProjectNames);
                        } else {
                            xLogger.info(Level.INFO + " : there are no admin projects yet for the userName : {}", adminUser.getUserName());
                            Set<String> newAccessProjects = new HashSet<>();
                            newAccessProjects.add(project.getProjectName());
                            accessUser.setAccessProjectNames(newAccessProjects);
                        }
                        xLogger.info(Level.INFO + " : updating the access User Data to add project");
                        userRepository.updateUser(accessUser);
                    }
                }
            }
        }
        makeUserAnAdminToProjectWithoutPassword(adminUser, project);
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
    public ResponseEntity<Object> changeProjectAdminService(ChangeProjectAdminRequest changeProjectAdminRequest){
        xLogger.info(Level.INFO + " : checking if project name : {} exists and its admin", changeProjectAdminRequest.getProjectName());
        Project project = projectRepository.findAProject(changeProjectAdminRequest.getProjectName());

        BaseResponse baseResponse = new BaseResponse();

        if(project == null){
            xLogger.error(Level.WARNING +" : project doesnt exists with the name {}", changeProjectAdminRequest.getProjectName());
            baseResponse.setResponseCode("93");
            baseResponse.setResponseMessage("project not found");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        } else if(!changeProjectAdminRequest.getOldAdminUserName().equals(project.getProjectAdminUserName())){
            xLogger.error(Level.WARNING + ": current admin of the project is different than {}", changeProjectAdminRequest.getOldAdminUserName());
            baseResponse.setResponseCode("92");
            baseResponse.setResponseMessage("Wrong Admin User");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        } else if(!userRepository.findAUser(project.getProjectAdminUserName()).getPassword().equals(changeProjectAdminRequest.getOldUserPassword())){
            xLogger.info(Level.WARNING + " : password of the admin {} is not matching", changeProjectAdminRequest.getOldAdminUserName());
            baseResponse.setResponseCode("91");
            baseResponse.setResponseMessage("password mismatch");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        } else if(userRepository.findAUser(changeProjectAdminRequest.getNewAdminUserName()) == null){
            xLogger.error(Level.WARNING + " : new UserName {} doesnt exists", changeProjectAdminRequest.getNewAdminUserName());
            baseResponse.setResponseCode("90");
            baseResponse.setResponseMessage("New Admin User Not Found");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        else{
            xLogger.info(Level.INFO + " : changing the project {} Admin User from {} ", changeProjectAdminRequest.getProjectName(), changeProjectAdminRequest.getOldAdminUserName());
            project.setProjectAdminUserName(changeProjectAdminRequest.getNewAdminUserName());
            projectRepository.updateProject(project);
            User newAdminUser = userRepository.findAUser(changeProjectAdminRequest.getNewAdminUserName());
            makeUserAnAdminToProjectWithoutPassword(newAdminUser, project);
            if(project.getProjectUserNames() != null){
                for(String projectName : project.getProjectUserNames()){
                    if(projectName.equals(project.getProjectName())){
                        revokeProjectAccessService(new RevokeProjectAccessRequest(newAdminUser.getUserName(), projectName));
                    }
                }
            }
            xLogger.info(Level.INFO + " : project {} new Admin is {}", project.getProjectName(), project.getProjectAdminUserName());
            baseResponse.setResponseCode("0");
            baseResponse.setResponseMessage("Admin changed");
            return new ResponseEntity<>(baseResponse, HttpStatus.OK);

        }

    }

    private void makeUserAnAdminToProjectWithoutPassword(User user, Project project){
        Set<String> adminProjects = user.getAdminProjectNames();
        if (adminProjects != null) {
            xLogger.info(Level.INFO + " : admin projects for the admin user {} are {}", project.getProjectAdminUserName(), adminProjects);
            adminProjects.add(project.getProjectName());
            user.setAdminProjectNames(adminProjects);
        } else {
            xLogger.info(Level.INFO + " : there are no admin projects yet for the userName : {}", user.getUserName());
            Set<String> newAdminProjects = new HashSet<>();
            newAdminProjects.add(project.getProjectName());
            user.setAdminProjectNames(newAdminProjects);
        }
        xLogger.info(Level.INFO + " : updating the user");
        userRepository.updateUser(user);

    }

    private List<String> checkUsersExists(Set<String> userNames){
        xLogger.info(Level.INFO + " : checking if the users present");
        List<String> userNotExistsList = new ArrayList<>();
        for(String userName : userNames){
            User user = userRepository.findAUser(userName);
            if(user == null){
                userNotExistsList.add(userName);
            }
        }
        return userNotExistsList;
    }


    public boolean addUserToProject(User user, Project project, String role){
        if(project == null){
            xLogger.error(Level.WARNING +" : project doesnt exists");
            return false;
        }
        if(user == null){
            xLogger.error(Level.WARNING + " : user doesnt exists");
            return false;
        }
        if(role.equals(ACCESS)){
            if(project.getProjectUserNames() != null){
                for(String userName : project.getProjectUserNames()){
                    if(user.getUserName().equals(userName)){
                        xLogger.error(Level.WARNING + " : User already have access");
                        return false;
                    }
                }

                project.getProjectUserNames().add(user.getUserName());
            } else{
                Set<String> projectUserNames = new HashSet<>();
                projectUserNames.add(user.getUserName());
                project.setProjectUserNames(projectUserNames);
            }


        } else if(role.equals(ADMIN)){
            project.setProjectAdminUserName(user.getUserName());
        }

        projectRepository.updateProject(project);
        return true;
    }



    public ResponseEntity<Object> giveProjectAccessService(ProjectAccessRequest projectAccessRequest) {
        xLogger.info(Level.INFO + " : Giving Access to Project {} to the userNames {}", projectAccessRequest.getProjectName(), projectAccessRequest.getUserNameList());

        Project project = projectRepository.findAProject(projectAccessRequest.getProjectName());
        BaseResponse baseResponse = new BaseResponse();
        if(project == null){
            xLogger.error(Level.WARNING +" : project doesnt exists with the name {}", projectAccessRequest.getProjectName());
            baseResponse.setResponseCode("93");
            baseResponse.setResponseMessage("project not found");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        }
        List<String> notFoundUsers = checkUsersExists(projectAccessRequest.getUserNameList());
       if(notFoundUsers == null){
           xLogger.error(Level.WARNING + " : access user not found userName : {}", notFoundUsers);
           baseResponse.setResponseCode("95");
           baseResponse.setResponseMessage("Access User Not Found");
           return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
       }

        for(String userName : projectAccessRequest.getUserNameList()){
            if(project.getProjectAdminUserName().equals(userName)){
                baseResponse.setResponseCode("94");
                baseResponse.setResponseMessage("Admin User is also added in Access user");
                return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
            }
        }

       Set<String> projectUsers =  project.getProjectUserNames();

       if(projectUsers != null){
           xLogger.info(Level.INFO + " : projectName {} already some users, adding these {} users also", project.getProjectName(), projectAccessRequest.getUserNameList());
           projectUsers.addAll(projectAccessRequest.getUserNameList());
       } else{

           xLogger.info(Level.INFO + " : projectName {} doesnt have any user to it, now adding {} to it", project.getProjectName(), projectAccessRequest.getUserNameList());
           project.setProjectUserNames(projectAccessRequest.getUserNameList());
       }

       projectRepository.updateProject(project);
       for(String userName : projectAccessRequest.getUserNameList()){
           User user =  userRepository.findAUser(userName);
           if(user.getAccessProjectNames() != null){

               for(String projectName : user.getAccessProjectNames()){
                   if(projectName.equals(projectAccessRequest.getProjectName())){
                       user.getAccessProjectNames().remove(projectAccessRequest.getProjectName());
                   }
               }
               user.getAccessProjectNames().add(projectAccessRequest.getProjectName());
           } else{
               Set<String> projectList = new HashSet<>();
               projectList.add(projectAccessRequest.getProjectName());
               user.setAccessProjectNames(projectList);
           }
           xLogger.info(Level.INFO + " : updating the userName {}", user.getUserName());
           userRepository.updateUser(user);
       }

       baseResponse.setResponseCode("0");
       baseResponse.setResponseMessage("users added to project");
       return new ResponseEntity<>(baseResponse, HttpStatus.OK);
    }

    public ResponseEntity<Object> revokeProjectAccessService(RevokeProjectAccessRequest revokeProjectAccessRequest) {

        xLogger.info(Level.INFO + " : revoke project access controller for ProjectName {} for UserName {}", revokeProjectAccessRequest.getProjectName(), revokeProjectAccessRequest.getUserName());

        Project project = projectRepository.findAProject(revokeProjectAccessRequest.getProjectName());

        BaseResponse baseResponse = new BaseResponse();
        if(project == null){
            xLogger.error(Level.WARNING +" : project doesnt exists with the ProjectName {}", revokeProjectAccessRequest.getProjectName());
            baseResponse.setResponseCode("93");
            baseResponse.setResponseMessage("project not found");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.findAUser(revokeProjectAccessRequest.getUserName());

        if(user == null){
            xLogger.error(Level.WARNING + ": No user Found with the UserName : {}", revokeProjectAccessRequest.getUserName());
            baseResponse.setResponseMessage("No user Found");
            baseResponse.setResponseCode("98");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        if(user.getAccessProjectNames() != null){

            for(String projectName : user.getAccessProjectNames()){
                if(projectName.equals(revokeProjectAccessRequest.getProjectName())){
                    xLogger.info(Level.INFO + " : revoking the access to the userName : {}, ProjectName : {}", user.getUserName(), project.getProjectName());
                    project.getProjectUserNames().remove(user.getUserName());
                    projectRepository.updateProject(project);


                    baseResponse.setResponseCode("success");
                    baseResponse.setResponseCode("0");
                    return new ResponseEntity<>(baseResponse, HttpStatus.OK);
                }
            }


            xLogger.error(Level.WARNING + ": User don't have access to that project : {}", revokeProjectAccessRequest.getUserName());
            baseResponse.setResponseMessage("No Access Found");
            baseResponse.setResponseCode("88");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);


        }

        xLogger.info(Level.INFO + " : userName dont have access to any Project ", user.getUserName());
        baseResponse.setResponseMessage("No Access any Project");
        baseResponse.setResponseCode("87");
        return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);

    }

    public ResponseEntity<Object> getProjectService(String projectName) {

        xLogger.info(Level.INFO + " find project with projectName : {}", projectName);
        Project project = projectRepository.findAProject(projectName);
        BaseResponse baseResponse = new BaseResponse();

        if(project == null){
            xLogger.error(Level.WARNING +" : project doesnt exists with the ProjectName  {}", projectName);
            baseResponse.setResponseCode("93");
            baseResponse.setResponseMessage("project not found");
            return new ResponseEntity<>(baseResponse, org.springframework.http.HttpStatus.BAD_REQUEST);
        }else {
            return new ResponseEntity<>(project, HttpStatus.OK);
        }

    }

    public ResponseEntity<Object> getAllProjectsService() {

        xLogger.info(Level.INFO + " : getting all the projects");
        List<Project> projects = projectRepository.findAllProjects();
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }
}
