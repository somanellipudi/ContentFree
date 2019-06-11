package com.bcp.contentfree.request;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Setter
@Getter
@ApiModel
public class ProjectAccessRequest {

    private String adminUser;

    @NotNull
    private String projectName;

    @NotNull
    private Set<String> userNameList;
}
