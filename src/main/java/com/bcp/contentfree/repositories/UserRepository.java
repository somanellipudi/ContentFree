package com.bcp.contentfree.repositories;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.bcp.contentfree.entity.User;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.logging.Level;

@Repository
public class UserRepository {

    @Autowired
    public DynamoDBMapper mapper;

    private XLogger xLogger = XLoggerFactory.getXLogger(getClass());

    public void insertUser(User user) {
        mapper.save(user);
    }

    public void updateUser(User user) {
        try {
            mapper.save(user);
        } catch (ConditionalCheckFailedException e) {
            xLogger.error(Level.SEVERE + ": error is {}", e.getCause());
        }
    }

    public User findAUser(String userName) {
        xLogger.info(Level.INFO + " : querying the db to find the user with userName : {}", userName);
        return mapper.load(User.class, userName);
    }

    public void deleteUser(User user){
        xLogger.info(Level.INFO +  " deleting the userName : {}", user.getUserName());
         mapper.delete(user);
    }

    public List<User> getAllUsers(){
        DynamoDBScanExpression dynamoDBScanExpression = new DynamoDBScanExpression();
        return mapper.scan(User.class, dynamoDBScanExpression);
    }


}
