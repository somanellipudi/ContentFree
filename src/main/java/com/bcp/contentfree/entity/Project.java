package com.bcp.contentfree.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Data
@DynamoDBTable(tableName = "ContentFreeProject")
@Setter
@Getter
@NoArgsConstructor
public class Project extends BaseEntity {

    @NotNull
    @DynamoDBHashKey(attributeName = "projectName")
    private String projectName;

    private String projectDescription;

    @NotNull
    private String projectAdminUserName;

    private Set<String> projectUserNames;


}
