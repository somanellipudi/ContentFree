package com.bcp.contentfree.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;

import java.util.List;

@Data
@DynamoDBTable(tableName = "ContentFreeUser")
@Setter
@Getter
@NoArgsConstructor
public class User extends BaseEntity {

    @NonNull
    @DynamoDBHashKey(attributeName = "userName")
    private String userName;

    @NonNull
    private String password;

    private String phoneNumber;

    @NonNull
    private String emailId;

    private List<String> adminProjectNames;

    private List<String> accessProjectNames;


}
