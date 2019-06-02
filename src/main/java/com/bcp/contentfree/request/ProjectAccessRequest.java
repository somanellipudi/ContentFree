package com.bcp.contentfree.request;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Setter
@Getter
@ApiModel
public class ProjectAccessRequest {

    @NonNull
    private String projectName;

    @NonNull
    private Set<String> userNameList;
}
