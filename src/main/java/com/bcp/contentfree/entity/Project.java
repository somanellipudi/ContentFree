package com.bcp.contentfree.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;

import java.util.List;

@Data
@DynamoDBTable(tableName = "ContentFreeProject")
@Setter
@Getter
@NoArgsConstructor
public class Project extends BaseEntity {

    @NonNull
    @DynamoDBHashKey(attributeName = "projectName")
    private String projectName;

    private String projectDescription;

    @NonNull
    private String projectAdminUserName;

    private List<String> projectUserNames;


}
