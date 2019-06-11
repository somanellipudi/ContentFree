package com.bcp.contentfree.controller;

import com.bcp.contentfree.entity.Project;
import com.bcp.contentfree.request.ChangeProjectAdminRequest;
import com.bcp.contentfree.request.ProjectAccessRequest;
import com.bcp.contentfree.request.RevokeProjectAccessRequest;
import com.bcp.contentfree.service.ProjectService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.logging.Level;

@RestController
@RequestMapping("/content/project")
public class ProjectController {


    @Autowired
    ProjectService projectService;

    private XLogger xLogger = XLoggerFactory.getXLogger(getClass());


    @GetMapping("/getAllProjects")
    public ResponseEntity<Object> getAllProjects() {
        xLogger.info(Level.INFO + " : getting all the projects");
        return projectService.getAllProjectsService();
    }

    @PostMapping("/addProject")
    public ResponseEntity<Object> addProject(@RequestBody @Valid Project project) {
        xLogger.info(Level.INFO + " : checking if the userName : {}, is already present", project.getProjectName());
        return projectService.addProjectService(project);
    }

    @GetMapping("/getProject/{projectName}")
    public ResponseEntity<Object> getProject(@PathVariable String projectName) {
        xLogger.info(Level.INFO + " entered get Project for projectName : {}", projectName);
        return projectService.getProjectService(projectName);
    }

    @PutMapping("/changeProjectAdmin")
    public ResponseEntity<Object> changeProjectAdmin(@RequestBody @Valid ChangeProjectAdminRequest changeProjectAdminRequest) {
        xLogger.info(Level.INFO + " : entered change Project Admin for the ProjectName {}", changeProjectAdminRequest.getProjectName());
        return projectService.changeProjectAdminService(changeProjectAdminRequest);
    }


    @PutMapping("/giveProjectAccess")
    public ResponseEntity<Object> giveProjectAccess(@RequestBody @Valid ProjectAccessRequest projectAccessRequest) {
        xLogger.info(Level.INFO + " : Entered project Access giving controller for the ProjectName : {} ", projectAccessRequest.getProjectName());
        return projectService.giveProjectAccessService(projectAccessRequest);
    }

    @PutMapping("/revokeProjectAccess")
    public ResponseEntity<Object> revokeProjectAccess(@RequestBody @Valid RevokeProjectAccessRequest revokeProjectAccessRequest) {
        xLogger.info(Level.INFO + " : Entered Project revoke controller for the ProjectName : {} and UserName : {}", revokeProjectAccessRequest.getProjectName(), revokeProjectAccessRequest.getUserName());
        return projectService.revokeProjectAccessService(revokeProjectAccessRequest);
    }
}
