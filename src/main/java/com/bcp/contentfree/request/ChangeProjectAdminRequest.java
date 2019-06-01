package com.bcp.contentfree.request;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ApiModel
public class ChangeProjectAdminRequest {

    private String projectName;
    private String oldUserName;
    private String oldUserPassword;
    private String newUserName;
}
