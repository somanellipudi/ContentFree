package com.bcp.contentfree.controller;

import com.bcp.contentfree.entity.Project;
import com.bcp.contentfree.service.ProjectService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Level;

@RestController
@RequestMapping("/content/project")
public class ProjectController {


    @Autowired
    ProjectService projectService;

    private XLogger xLogger = XLoggerFactory.getXLogger(getClass());


    @PostMapping("/addProject")
    public ResponseEntity<Object> addProject(@RequestBody Project project) {
        xLogger.info(Level.INFO + " : checking if the userName : {}, is already present", project.getProjectName());
        return projectService.addProjectService(project);
    }


/*    @PutMapping("/changeProjectAdmin")
    public ResponseEntity<Object> changeProjectAdmin(@RequestBody ChangeProjectAdminRequest changeProjectAdminRequest){
        xLogger.info(Level.INFO +" : entered change Project Admin for the ProjectName {}", changeProjectAdminRequest.getProjectName());
        return projectService.changeProjectAdminService(changeProjectAdminRequest);
    }*/
}
