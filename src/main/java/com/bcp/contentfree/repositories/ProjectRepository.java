package com.bcp.contentfree.repositories;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.bcp.contentfree.entity.Project;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.logging.Level;

@Repository
public class ProjectRepository {

    @Autowired
    public DynamoDBMapper mapper;

    private XLogger xLogger = XLoggerFactory.getXLogger(getClass());

    public Project findAProject(String projectName) {
        xLogger.info(Level.INFO + " : querying the db to find the user with projectName : {}", projectName);
        return mapper.load(Project.class, projectName);
    }

    public void insertProject(Project project) {
        xLogger.info(Level.INFO + " inserting the project into the database, with ProjectName : {} ", project.getProjectName());
        mapper.save(project);
    }

    public void updateProject(Project project) {
        try {
            mapper.save(project);
        } catch (ConditionalCheckFailedException e) {
            xLogger.error(Level.SEVERE + ": error is {}", e.getCause());
        }
    }


}
