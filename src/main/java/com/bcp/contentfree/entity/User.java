package com.bcp.contentfree.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@DynamoDBTable(tableName = "ContentFreeUser")
@Setter
@Getter
@NoArgsConstructor
public class User extends BaseEntity {

    @NotNull
    @DynamoDBHashKey(attributeName = "userName")
    private String userName;

    @NotNull
    private String password;

    private String phoneNumber;

    @NotNull
    private String emailId;

    private Set<String> adminProjectNames;

    private Set<String> accessProjectNames;


}
